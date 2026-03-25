package com.app.quantitymeasurement.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.app.quantitymeasurement.model.QuantityMeasurementEntity;
import com.app.quantitymeasurement.model.QuantityModel;
import com.app.quantitymeasurement.unit.LengthUnit;

public class QuantityMeasurementEntityTest {

	@Test
	void shouldCreateSuccessfulQuantityResultEntity() {
		QuantityMeasurementEntity entity = new QuantityMeasurementEntity("ADD", new QuantityModel<>(1.0, LengthUnit.FEET),
				new QuantityModel<>(12.0, LengthUnit.INCHES), LengthUnit.FEET.getUnitName(),
				new QuantityModel<>(2.0, LengthUnit.FEET));

		assertNotNull(entity.getCreatedAt());
		assertEquals("ADD", entity.getOperation());
		assertTrue(entity.isSuccess());
		assertEquals("LENGTH", entity.getMeasurementType());
	}

	@Test
	void shouldCreateFailureEntity() {
		QuantityMeasurementEntity entity = new QuantityMeasurementEntity("SUBTRACT",
				new QuantityModel<>(1.0, LengthUnit.FEET), new QuantityModel<>(2.0, LengthUnit.INCHES), "FEET",
				"Temperature does not support arithmetic");

		assertFalse(entity.isSuccess());
		assertEquals("Temperature does not support arithmetic", entity.getErrorMessage());
	}
}
