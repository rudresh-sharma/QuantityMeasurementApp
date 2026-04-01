package com.app.quantitymeasurementapp.service;

import com.app.quantitymeasurementapp.unit.LengthUnit;
import com.app.quantitymeasurementapp.unit.VolumeUnit;
import com.app.quantitymeasurementapp.unit.WeightUnit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MeasurementCalculatorTest {

    @Test
    void convertShouldPreserveSmallVolumeResults() {
        double result = MeasurementCalculator.convert(1.0, VolumeUnit.MILLILITRE, VolumeUnit.LITRE);

        assertEquals(0.001, result, 1e-9);
    }

    @Test
    void convertShouldSupportNewLengthUnits() {
        double result = MeasurementCalculator.convert(1.0, LengthUnit.METER, LengthUnit.CENTIMETER);

        assertEquals(100.0, result, 1e-9);
    }

    @Test
    void convertShouldSupportNewWeightUnits() {
        double result = MeasurementCalculator.convert(1.0, WeightUnit.TONNE, WeightUnit.KILOGRAM);

        assertEquals(1000.0, result, 1e-9);
    }

    @Test
    void convertShouldSupportNewVolumeUnits() {
        double result = MeasurementCalculator.convert(1.0, VolumeUnit.CUBIC_METER, VolumeUnit.LITRE);

        assertEquals(1000.0, result, 1e-9);
    }
}
