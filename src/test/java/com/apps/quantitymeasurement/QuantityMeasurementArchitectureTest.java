package com.apps.quantitymeasurement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.apps.quantitymeasurement.controller.QuantityMeasurementController;
import com.apps.quantitymeasurement.exception.QuantityMeasurementException;
import com.apps.quantitymeasurement.model.QuantityDTO;
import com.apps.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.apps.quantitymeasurement.repository.QuantityMeasurementCacheRepository;
import com.apps.quantitymeasurement.service.IQuantityMeasurementService;
import com.apps.quantitymeasurement.service.QuantityMeasurementServiceImpl;

public class QuantityMeasurementArchitectureTest {

	@Test
	void controllerShouldDelegateAdditionThroughServiceLayer() {
		QuantityMeasurementController controller = buildController(tempHistoryFile("add"));

		Quantity<LengthUnit> result = controller.addQuantities(new Quantity<>(1.0, LengthUnit.FEET),
				new Quantity<>(12.0, LengthUnit.INCHES), LengthUnit.FEET);

		assertEquals(new Quantity<>(2.0, LengthUnit.FEET), result);
		assertEquals(1, controller.getMeasurementHistory().size());
		assertEquals("ADD", controller.getMeasurementHistory().get(0).getOperation());
	}

	@Test
	void serviceShouldConvertUsingDtoContract() {
		IQuantityMeasurementService service = buildService(tempHistoryFile("convert"));

		QuantityDTO result = service.convert(new QuantityDTO(1.0, "FEET", "LENGTH"), "INCHES");

		assertEquals(12.0, result.getValue(), 0.01);
		assertEquals("INCHES", result.getUnit());
		assertEquals("LENGTH", result.getMeasurementType());
	}

	@Test
	void serviceShouldCaptureFailureInHistory() {
		IQuantityMeasurementService service = buildService(tempHistoryFile("failure"));

		assertThrows(QuantityMeasurementException.class,
				() -> service.add(new QuantityDTO(0.0, "CELSIUS", "TEMPERATURE"),
						new QuantityDTO(10.0, "CELSIUS", "TEMPERATURE")));

		assertEquals(1, service.getMeasurementHistory().size());
		assertFalse(service.getMeasurementHistory().get(0).isSuccess());
		assertTrue(service.getMeasurementHistory().get(0).getErrorMessage().contains("Temperature"));
	}

	@Test
	void repositoryShouldReloadSerializedHistory() {
		Path storagePath = tempHistoryFile("repository");
		IQuantityMeasurementService service = buildService(storagePath);
		service.compare(new QuantityDTO(1.0, "FEET", "LENGTH"), new QuantityDTO(12.0, "INCHES", "LENGTH"));

		QuantityMeasurementCacheRepository reloadedRepository = new QuantityMeasurementCacheRepository(storagePath);

		assertEquals(1, reloadedRepository.getAllMeasurements().size());
		assertTrue(reloadedRepository.getAllMeasurements().get(0).isSuccess());
	}

	@Test
	void repositoryShouldRecoverFromCorruptedHistoryFile() throws Exception {
		Path storagePath = tempHistoryFile("corrupted");
		Files.writeString(storagePath, "broken-history", StandardCharsets.UTF_8);

		QuantityMeasurementCacheRepository repository = new QuantityMeasurementCacheRepository(storagePath);

		assertTrue(repository.getAllMeasurements().isEmpty());
		assertTrue(Files.exists(storagePath.resolveSibling(storagePath.getFileName() + ".corrupted")));
		assertFalse(Files.exists(storagePath));
	}

	private QuantityMeasurementController buildController(Path storagePath) {
		return new QuantityMeasurementController(buildService(storagePath));
	}

	private IQuantityMeasurementService buildService(Path storagePath) {
		IQuantityMeasurementRepository repository = new QuantityMeasurementCacheRepository(storagePath);
		return new QuantityMeasurementServiceImpl(repository);
	}

	private Path tempHistoryFile(String suffix) {
		try {
			Path path = Files.createTempFile("quantity-measurement-" + suffix, ".ser");
			Files.deleteIfExists(path);
			return path;
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
}
