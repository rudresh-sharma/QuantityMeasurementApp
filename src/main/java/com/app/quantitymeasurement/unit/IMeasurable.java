package com.app.quantitymeasurement.unit;

public interface IMeasurable {

	double getConversionFactor();

	double convertToBaseUnit(double value);

	double convertFromBaseUnit(double baseValue);

	String getUnitName();

	default String getMeasurementType() {
		return getClass().getSimpleName().replace("Unit", "").toUpperCase();
	}

	SupportsArithmetic supportsArithmetic = () -> true;

	default boolean supportsArithmetic() {
		return supportsArithmetic.isSupported();
	}

	default void validateOperationSupport(String operation) {
	}

	static IMeasurable from(String measurementType, String unitName) {
		if (measurementType == null || unitName == null) {
			throw new IllegalArgumentException("Measurement type and unit name cannot be null");
		}

		String normalizedMeasurement = measurementType.trim().toUpperCase();
		String normalizedUnit = unitName.trim().toUpperCase();

		switch (normalizedMeasurement) {
		case "LENGTH":
			return LengthUnit.valueOf(normalizedUnit);
		case "WEIGHT":
			return WeightUnit.valueOf(normalizedUnit);
		case "VOLUME":
			return VolumeUnit.valueOf(normalizedUnit);
		case "TEMPERATURE":
			return TemperatureUnit.valueOf(normalizedUnit);
		default:
			throw new IllegalArgumentException("Unsupported measurement type: " + measurementType);
		}
	}
}
