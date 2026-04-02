package com.app.quantitymeasurementservice.unit;

public enum VolumeUnit implements IMeasurable {
    LITRE(1.0), MILLILITRE(0.001), GALLON(3.78541), QUART(0.946353),
    PINT(0.473176), CUP(0.236588), FLUID_OUNCE(0.0295735), CUBIC_METER(1000.0);

    private final double conversionFactor;
    VolumeUnit(double conversionFactor) { this.conversionFactor = conversionFactor; }
    public double getConversionFactor() { return conversionFactor; }
    public double convertToBaseUnit(double value) { return value * conversionFactor; }
    public double convertFromBaseUnit(double value) { return value / conversionFactor; }
    public String getUnitName() { return this.name(); }
    public String getMeasurementType() { return this.getClass().getSimpleName(); }
}
