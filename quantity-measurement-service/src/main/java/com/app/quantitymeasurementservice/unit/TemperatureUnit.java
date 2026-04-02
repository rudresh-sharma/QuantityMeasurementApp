package com.app.quantitymeasurementservice.unit;

import java.util.function.Function;

public enum TemperatureUnit implements IMeasurable {
    CELSIUS(c -> c),
    FAHRENHEIT(f -> (f - 32) * 5 / 9),
    KELVIN(k -> k - 273.15);

    private final Function<Double, Double> conversionToBase;
    TemperatureUnit(Function<Double, Double> conversionToBase) { this.conversionToBase = conversionToBase; }
    public String getUnitName() { return name(); }
    public double getConversionFactor() { return 1.0; }
    public double convertToBaseUnit(double value) { return conversionToBase.apply(value); }
    public double convertFromBaseUnit(double baseValue) {
        return switch (this) {
            case FAHRENHEIT -> (baseValue * 9 / 5) + 32;
            case KELVIN -> baseValue + 273.15;
            default -> baseValue;
        };
    }

    private final SupportsArithmetic supportsArithmetic = () -> false;
    @Override public boolean supportsArithmetic() { return supportsArithmetic.isSupported(); }
    @Override public void validateOperationSupport(String operation) {
        throw new UnsupportedOperationException(
                "Temperature does not support operation: " + operation + " on absolute values.");
    }
    public String getMeasurementType() { return this.getClass().getSimpleName(); }
}
