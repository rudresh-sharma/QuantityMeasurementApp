package com.app.quantitymeasurement.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.app.quantitymeasurement.model.QuantityMeasurementEntity;
import com.app.quantitymeasurement.model.QuantityModel;
import com.app.quantitymeasurement.unit.IMeasurable;
import com.app.quantitymeasurement.unit.LengthUnit;
import com.app.quantitymeasurement.unit.WeightUnit;
import com.app.quantitymeasurement.util.ApplicationConfig;

public class QuantityMeasurementDatabaseRepositoryTest {

	@Test
	void shouldPersistAndQueryMeasurements() {
		IQuantityMeasurementRepository repository = buildRepository();
		try {
			repository.saveMeasurement(successEntity("ADD", "LENGTH"));
			repository.saveMeasurement(successEntity("COMPARE", "LENGTH"));
			repository.saveMeasurement(successEntity("CONVERT", "WEIGHT"));

			assertEquals(3, repository.getTotalCount());
			assertEquals(1, repository.getMeasurementsByOperation("ADD").size());
			assertEquals(2, repository.getMeasurementsByType("LENGTH").size());
		} finally {
			repository.releaseResources();
		}
	}

	@Test
	void shouldExposePoolStatisticsAndDeleteAllMeasurements() {
		IQuantityMeasurementRepository repository = buildRepository();
		try {
			repository.saveMeasurement(successEntity("ADD", "LENGTH"));
			Map<String, Integer> statistics = repository.getPoolStatistics();

			assertEquals(1, repository.getTotalCount());
			assertTrue(statistics.containsKey("active"));
			assertTrue(statistics.containsKey("idle"));
			assertTrue(statistics.containsKey("total"));

			repository.deleteAllMeasurements();

			assertEquals(0, repository.getTotalCount());
		} finally {
			repository.releaseResources();
		}
	}

	@Test
	void shouldLoadSchemaAndFixturesFromTestResources() {
		IQuantityMeasurementRepository repository = buildRepository();
		try {
			executeScript(System.getProperty("app.database.url"), "test-db-schema.sql");
			executeScript(System.getProperty("app.database.url"), "test-data.sql");

			assertEquals(1, repository.getTotalCount());
			assertEquals(1, repository.getMeasurementsByOperation("COMPARE").size());
			assertEquals(1, repository.getMeasurementsByType("LENGTH").size());
		} finally {
			repository.releaseResources();
		}
	}

	private IQuantityMeasurementRepository buildRepository() {
		String databaseName = "quantitymeasurement-repository-" + UUID.randomUUID();
		System.setProperty("app.database.url",
				"jdbc:h2:mem:" + databaseName + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
		System.setProperty("app.database.username", "sa");
		System.setProperty("app.database.password", "");
		System.setProperty("app.database.pool.size", "2");
		return new QuantityMeasurementDatabaseRepository(new ApplicationConfig());
	}

	private QuantityMeasurementEntity successEntity(String operation, String measurementType) {
		IMeasurable unit = "WEIGHT".equals(measurementType) ? WeightUnit.KILOGRAM : LengthUnit.FEET;
		return new QuantityMeasurementEntity(operation, new QuantityModel<>(1.0, unit), new QuantityModel<>(2.0, unit),
				unit.getUnitName(), new QuantityModel<>(3.0, unit));
	}

	private void executeScript(String databaseUrl, String resourceName) {
		try (Connection connection = DriverManager.getConnection(databaseUrl, "sa", "");
				Statement statement = connection.createStatement()) {
			String sql = readResource(resourceName);
			for (String command : sql.split(";")) {
				String trimmed = command.trim();
				if (!trimmed.isEmpty()) {
					statement.execute(trimmed);
				}
			}
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private String readResource(String resourceName) {
		try (InputStream stream = getClass().getClassLoader().getResourceAsStream(resourceName)) {
			if (stream == null) {
				throw new IllegalStateException("Missing test resource: " + resourceName);
			}
			return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
}
