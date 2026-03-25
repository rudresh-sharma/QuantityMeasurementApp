package com.app.quantitymeasurement.repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.app.quantitymeasurement.exception.DatabaseException;
import com.app.quantitymeasurement.model.QuantityMeasurementEntity;
import com.app.quantitymeasurement.model.QuantityModel;
import com.app.quantitymeasurement.unit.IMeasurable;
import com.app.quantitymeasurement.util.ApplicationConfig;
import com.app.quantitymeasurement.util.ConnectionPool;

public class QuantityMeasurementDatabaseRepository implements IQuantityMeasurementRepository {

	private static final String INSERT_SQL = """
			INSERT INTO quantity_measurement_history (
				created_at, operation, measurement_type,
				left_value, left_unit, left_measurement_type,
				right_value, right_unit, right_measurement_type,
				target_unit, result_value, result_unit, result_measurement_type,
				scalar_result, success, error_message
			) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
			""";

	private static final String SELECT_ALL_SQL = "SELECT * FROM quantity_measurement_history ORDER BY id";
	private static final String SELECT_BY_OPERATION_SQL = "SELECT * FROM quantity_measurement_history WHERE UPPER(operation) = ? ORDER BY id";
	private static final String SELECT_BY_TYPE_SQL = "SELECT * FROM quantity_measurement_history WHERE UPPER(measurement_type) = ? ORDER BY id";
	private static final String COUNT_SQL = "SELECT COUNT(*) FROM quantity_measurement_history";
	private static final String DELETE_ALL_SQL = "DELETE FROM quantity_measurement_history";

	private final ConnectionPool connectionPool;

	public QuantityMeasurementDatabaseRepository() {
		this(new ApplicationConfig());
	}

	public QuantityMeasurementDatabaseRepository(ApplicationConfig config) {
		this.connectionPool = new ConnectionPool(config);
		initializeSchema();
	}

	@Override
	public void saveMeasurement(QuantityMeasurementEntity entity) {
		try (Connection connection = connectionPool.getConnection()) {
			connection.setAutoCommit(false);
			try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
				bindEntity(statement, entity);
				statement.executeUpdate();
				connection.commit();
			} catch (SQLException exception) {
				connection.rollback();
				throw exception;
			}
		} catch (SQLException exception) {
			throw new DatabaseException("Failed to save measurement to database", exception);
		}
	}

	@Override
	public List<QuantityMeasurementEntity> getAllMeasurements() {
		try (Connection connection = connectionPool.getConnection();
				PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SQL);
				ResultSet resultSet = statement.executeQuery()) {
			return mapAll(resultSet);
		} catch (SQLException exception) {
			throw new DatabaseException("Failed to fetch measurements from database", exception);
		}
	}

	@Override
	public List<QuantityMeasurementEntity> getMeasurementsByOperation(String operation) {
		try (Connection connection = connectionPool.getConnection();
				PreparedStatement statement = connection.prepareStatement(SELECT_BY_OPERATION_SQL)) {
			statement.setString(1, normalize(operation));
			try (ResultSet resultSet = statement.executeQuery()) {
				return mapAll(resultSet);
			}
		} catch (SQLException exception) {
			throw new DatabaseException("Failed to fetch measurements by operation", exception);
		}
	}

	@Override
	public List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType) {
		try (Connection connection = connectionPool.getConnection();
				PreparedStatement statement = connection.prepareStatement(SELECT_BY_TYPE_SQL)) {
			statement.setString(1, normalize(measurementType));
			try (ResultSet resultSet = statement.executeQuery()) {
				return mapAll(resultSet);
			}
		} catch (SQLException exception) {
			throw new DatabaseException("Failed to fetch measurements by type", exception);
		}
	}

	@Override
	public long getTotalCount() {
		try (Connection connection = connectionPool.getConnection();
				PreparedStatement statement = connection.prepareStatement(COUNT_SQL);
				ResultSet resultSet = statement.executeQuery()) {
			resultSet.next();
			return resultSet.getLong(1);
		} catch (SQLException exception) {
			throw new DatabaseException("Failed to count measurements", exception);
		}
	}

	@Override
	public void deleteAllMeasurements() {
		try (Connection connection = connectionPool.getConnection();
				PreparedStatement statement = connection.prepareStatement(DELETE_ALL_SQL)) {
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new DatabaseException("Failed to delete measurements", exception);
		}
	}

	@Override
	public Map<String, Integer> getPoolStatistics() {
		return Map.of("active", connectionPool.getActiveCount(), "idle", connectionPool.getIdleCount(), "total",
				connectionPool.getTotalCount());
	}

	@Override
	public void releaseResources() {
		connectionPool.shutdown();
	}

	private void initializeSchema() {
		try (Connection connection = connectionPool.getConnection(); Statement statement = connection.createStatement()) {
			statement.execute(loadSchemaScript());
		} catch (SQLException exception) {
			throw new DatabaseException("Failed to initialize database schema", exception);
		}
	}

	private void bindEntity(PreparedStatement statement, QuantityMeasurementEntity entity) throws SQLException {
		statement.setTimestamp(1, Timestamp.from(entity.getCreatedAt() == null ? Instant.now() : entity.getCreatedAt()));
		statement.setString(2, entity.getOperation());
		statement.setString(3, entity.getMeasurementType());

		bindQuantity(statement, 4, entity.getLeftOperand());
		bindQuantity(statement, 7, entity.getRightOperand());

		statement.setString(10, entity.getTargetUnit());
		bindQuantity(statement, 11, entity.getQuantityResult());
		if (entity.getScalarResult() == null) {
			statement.setNull(14, java.sql.Types.DOUBLE);
		} else {
			statement.setDouble(14, entity.getScalarResult());
		}
		statement.setBoolean(15, entity.isSuccess());
		statement.setString(16, entity.getErrorMessage());
	}

	private void bindQuantity(PreparedStatement statement, int startIndex, QuantityModel<?> quantity) throws SQLException {
		if (quantity == null || quantity.getUnit() == null) {
			statement.setNull(startIndex, java.sql.Types.DOUBLE);
			statement.setNull(startIndex + 1, java.sql.Types.VARCHAR);
			statement.setNull(startIndex + 2, java.sql.Types.VARCHAR);
			return;
		}

		statement.setDouble(startIndex, quantity.getValue());
		statement.setString(startIndex + 1, quantity.getUnit().getUnitName());
		statement.setString(startIndex + 2, quantity.getUnit().getMeasurementType());
	}

	private List<QuantityMeasurementEntity> mapAll(ResultSet resultSet) throws SQLException {
		List<QuantityMeasurementEntity> entities = new ArrayList<>();
		while (resultSet.next()) {
			entities.add(mapRow(resultSet));
		}
		return entities;
	}

	private QuantityMeasurementEntity mapRow(ResultSet resultSet) throws SQLException {
		QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
		entity.setId(resultSet.getLong("id"));
		entity.setCreatedAt(resultSet.getTimestamp("created_at").toInstant());
		entity.setOperation(resultSet.getString("operation"));
		entity.setLeftOperand(readQuantity(resultSet, "left"));
		entity.setRightOperand(readQuantity(resultSet, "right"));
		entity.setTargetUnit(resultSet.getString("target_unit"));
		entity.setQuantityResult(readQuantity(resultSet, "result"));
		double scalar = resultSet.getDouble("scalar_result");
		entity.setScalarResult(resultSet.wasNull() ? null : scalar);
		entity.setSuccess(resultSet.getBoolean("success"));
		entity.setErrorMessage(resultSet.getString("error_message"));
		return entity;
	}

	private QuantityModel<?> readQuantity(ResultSet resultSet, String prefix) throws SQLException {
		double value = resultSet.getDouble(prefix + "_value");
		if (resultSet.wasNull()) {
			return null;
		}

		String unitName = resultSet.getString(prefix + "_unit");
		String measurementType = resultSet.getString(prefix + "_measurement_type");
		IMeasurable unit = IMeasurable.from(measurementType, unitName);
		return new QuantityModel<>(value, unit);
	}

	private String loadSchemaScript() {
		try (InputStream stream = getClass().getClassLoader().getResourceAsStream("db/schema.sql")) {
			if (stream == null) {
				throw new DatabaseException("Database schema resource db/schema.sql was not found");
			}
			return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException exception) {
			throw new DatabaseException("Failed to load database schema", exception);
		}
	}

	private String normalize(String value) {
		return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
	}
}
