package com.app.quantitymeasurement.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.app.quantitymeasurement.controller.QuantityMeasurementController;
import com.app.quantitymeasurement.model.QuantityDTO;
import com.app.quantitymeasurement.quantity.Quantity;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurement.repository.QuantityMeasurementDatabaseRepository;
import com.app.quantitymeasurement.service.QuantityMeasurementServiceImpl;
import com.app.quantitymeasurement.unit.LengthUnit;
import com.app.quantitymeasurement.util.ApplicationConfig;

public class QuantityMeasurementIntegrationTest {

	@Test
	void shouldRunEndToEndWithDatabasePersistenceAndFixtures() {
		String databaseUrl = "jdbc:h2:mem:quantitymeasurement-integration-" + UUID.randomUUID()
				+ ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
		System.setProperty("app.database.url", databaseUrl);
		System.setProperty("app.database.username", "sa");
		System.setProperty("app.database.password", "");
		System.setProperty("app.database.pool.size", "3");

		IQuantityMeasurementRepository repository = new QuantityMeasurementDatabaseRepository(new ApplicationConfig());
		try {
			executeScript(databaseUrl, "test-db-schema.sql");
			executeScript(databaseUrl, "test-data.sql");

			QuantityMeasurementController controller = new QuantityMeasurementController(
					new QuantityMeasurementServiceImpl(repository));

			Quantity<LengthUnit> additionResult = controller.addQuantities(new Quantity<>(1.0, LengthUnit.FEET),
					new Quantity<>(12.0, LengthUnit.INCHES), LengthUnit.FEET);
			boolean comparisonResult = controller.compareQuantities(new QuantityDTO(1.0, "FEET", "LENGTH"),
					new QuantityDTO(12.0, "INCHES", "LENGTH"));

			assertEquals(new Quantity<>(2.0, LengthUnit.FEET), additionResult);
			assertTrue(comparisonResult);
			assertEquals(3, repository.getTotalCount());
			assertEquals(2, repository.getMeasurementsByOperation("COMPARE").size());
			assertFalse(controller.getMeasurementHistory().isEmpty());
		} finally {
			repository.releaseResources();
		}
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
