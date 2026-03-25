package com.app.quantitymeasurement.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.app.quantitymeasurement.model.QuantityMeasurementEntity;
import com.app.quantitymeasurement.model.QuantityModel;
import com.app.quantitymeasurement.unit.LengthUnit;

public class QuantityMeasurementCacheRepositoryTest {

	@Test
	void shouldReloadSerializedHistoryAndFilterResults() {
		Path storagePath = tempHistoryFile("reload");
		QuantityMeasurementCacheRepository repository = new QuantityMeasurementCacheRepository(storagePath);
		repository.saveMeasurement(successEntity("ADD"));
		repository.saveMeasurement(successEntity("COMPARE"));

		QuantityMeasurementCacheRepository reloadedRepository = new QuantityMeasurementCacheRepository(storagePath);

		assertEquals(2, reloadedRepository.getTotalCount());
		assertEquals(1, reloadedRepository.getMeasurementsByOperation("ADD").size());
		assertEquals(2, reloadedRepository.getMeasurementsByType("LENGTH").size());
	}

	@Test
	void shouldDeleteAllMeasurementsAndRecoverCorruptedFile() throws Exception {
		Path storagePath = tempHistoryFile("corrupted");
		Files.writeString(storagePath, "broken-history", StandardCharsets.UTF_8);

		QuantityMeasurementCacheRepository repository = new QuantityMeasurementCacheRepository(storagePath);

		assertTrue(repository.getAllMeasurements().isEmpty());
		assertTrue(Files.exists(storagePath.resolveSibling(storagePath.getFileName() + ".corrupted")));

		repository.saveMeasurement(successEntity("ADD"));
		assertEquals(1, repository.getTotalCount());

		repository.deleteAllMeasurements();
		assertEquals(0, repository.getTotalCount());
		assertFalse(Files.exists(storagePath));
	}

	private QuantityMeasurementEntity successEntity(String operation) {
		return new QuantityMeasurementEntity(operation, new QuantityModel<>(1.0, LengthUnit.FEET),
				new QuantityModel<>(2.0, LengthUnit.INCHES), LengthUnit.FEET.getUnitName(),
				new QuantityModel<>(3.0, LengthUnit.FEET));
	}

	private Path tempHistoryFile(String suffix) {
		try {
			Path path = Files.createTempFile("quantitymeasurement-cache-" + suffix, ".ser");
			Files.deleteIfExists(path);
			return path;
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
}
