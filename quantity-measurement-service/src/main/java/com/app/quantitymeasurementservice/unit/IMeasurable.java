package com.app.quantitymeasurementservice.unit;

public interface IMeasurable {

    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
    String getMeasurementType();

    static IMeasurable getUnitByName(String unitName, String measurementType) {
        return switch (measurementType) {
            case "LengthUnit" -> LengthUnit.valueOf(unitName);
            case "WeightUnit" -> WeightUnit.valueOf(unitName);
            case "VolumeUnit" -> VolumeUnit.valueOf(unitName);
            case "TemperatureUnit" -> TemperatureUnit.valueOf(unitName);
            default -> throw new IllegalArgumentException("Unknown measurement type: " + measurementType);
        };
    }

    SupportsArithmetic supportsArithmetic = () -> true;

    default boolean supportsArithmetic() {
        return supportsArithmetic.isSupported();
    }

    default void validateOperationSupport(String operation) {
    }
}
