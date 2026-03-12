package com.app.quantitymeasurement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.model.QuantityDTO;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurement.repository.QuantityMeasurementCacheRepository;
import com.app.quantitymeasurement.repository.QuantityMeasurementDatabaseRepository;
import com.app.quantitymeasurement.util.ApplicationConfig;

public class QuantityMeasurementServiceImplTest {

	@Test
	void shouldConvertUsingDtoContract() {
		IQuantityMeasurementRepository repository = new QuantityMeasurementCacheRepository(tempCacheFile());
		IQuantityMeasurementService service = new QuantityMeasurementServiceImpl(repository);

		QuantityDTO result = service.convert(new QuantityDTO(1.0, "FEET", "LENGTH"), "INCHES");

		assertEquals(12.0, result.getValue(), 0.01);
		assertEquals("INCHES", result.getUnit());
		assertEquals("LENGTH", result.getMeasurementType());
	}

	@Test
	void shouldCaptureFailureInHistory() {
		IQuantityMeasurementRepository repository = new QuantityMeasurementCacheRepository(tempCacheFile());
		IQuantityMeasurementService service = new QuantityMeasurementServiceImpl(repository);

		assertThrows(QuantityMeasurementException.class,
				() -> service.add(new QuantityDTO(0.0, "CELSIUS", "TEMPERATURE"),
						new QuantityDTO(10.0, "CELSIUS", "TEMPERATURE")));

		assertEquals(1, service.getMeasurementHistory().size());
		assertFalse(service.getMeasurementHistory().get(0).isSuccess());
		assertTrue(service.getMeasurementHistory().get(0).getErrorMessage().contains("Temperature"));
	}

	@Test
	void shouldSupportBothRepositoryImplementations() {
		IQuantityMeasurementRepository cacheRepository = new QuantityMeasurementCacheRepository(tempCacheFile());
		IQuantityMeasurementRepository databaseRepository = buildDatabaseRepository();

		try {
			IQuantityMeasurementService cacheService = new QuantityMeasurementServiceImpl(cacheRepository);
			IQuantityMeasurementService databaseService = new QuantityMeasurementServiceImpl(databaseRepository);

			cacheService.compare(new QuantityDTO(1.0, "FEET", "LENGTH"), new QuantityDTO(12.0, "INCHES", "LENGTH"));
			databaseService.compare(new QuantityDTO(1.0, "FEET", "LENGTH"), new QuantityDTO(12.0, "INCHES", "LENGTH"));

			assertEquals(1, cacheRepository.getTotalCount());
			assertEquals(1, databaseRepository.getTotalCount());
		} finally {
			databaseRepository.releaseResources();
		}
	}

	private IQuantityMeasurementRepository buildDatabaseRepository() {
		String databaseName = "quantitymeasurement-service-" + UUID.randomUUID();
		System.setProperty("app.database.url",
				"jdbc:h2:mem:" + databaseName + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
		System.setProperty("app.database.username", "sa");
		System.setProperty("app.database.password", "");
		System.setProperty("app.database.pool.size", "3");
		return new QuantityMeasurementDatabaseRepository(new ApplicationConfig());
	}

	private Path tempCacheFile() {
		try {
			Path path = Files.createTempFile("quantitymeasurement-service-", ".ser");
			Files.deleteIfExists(path);
			return path;
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
}
