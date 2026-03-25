package com.app.quantitymeasurement.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.app.quantitymeasurement.model.QuantityDTO;
import com.app.quantitymeasurement.quantity.Quantity;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurement.repository.QuantityMeasurementCacheRepository;
import com.app.quantitymeasurement.service.QuantityMeasurementServiceImpl;
import com.app.quantitymeasurement.unit.LengthUnit;

public class QuantityMeasurementControllerTest {

	@Test
	void shouldDelegateAdditionThroughServiceLayer() {
		IQuantityMeasurementRepository repository = new QuantityMeasurementCacheRepository(tempCacheFile());
		QuantityMeasurementController controller = new QuantityMeasurementController(
				new QuantityMeasurementServiceImpl(repository));

		Quantity<LengthUnit> result = controller.addQuantities(new Quantity<>(1.0, LengthUnit.FEET),
				new Quantity<>(12.0, LengthUnit.INCHES), LengthUnit.FEET);

		assertEquals(new Quantity<>(2.0, LengthUnit.FEET), result);
		assertEquals(1, controller.getMeasurementHistory().size());
		assertEquals("ADD", controller.getMeasurementHistory().get(0).getOperation());
	}

	@Test
	void shouldSupportDtoBasedComparison() {
		IQuantityMeasurementRepository repository = new QuantityMeasurementCacheRepository(tempCacheFile());
		QuantityMeasurementController controller = new QuantityMeasurementController(
				new QuantityMeasurementServiceImpl(repository));

		boolean result = controller.compareQuantities(new QuantityDTO(1.0, "FEET", "LENGTH"),
				new QuantityDTO(12.0, "INCHES", "LENGTH"));

		assertTrue(result);
		assertFalse(controller.getMeasurementHistory().isEmpty());
	}

	private Path tempCacheFile() {
		try {
			Path path = Files.createTempFile("quantitymeasurement-controller-", ".ser");
			Files.deleteIfExists(path);
			return path;
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
}
