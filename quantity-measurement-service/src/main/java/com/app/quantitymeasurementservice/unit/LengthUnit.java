package com.app.quantitymeasurementservice.unit;

public enum LengthUnit implements IMeasurable {
    KILOMETER(39370.1), METER(39.3701), CENTIMETER(0.393701), MILLIMETER(0.0393701),
    MILE(63360.0), YARD(36.0), FOOT(12.0), INCH(1.0);

    private final double conversionFactor;
    LengthUnit(double conversionFactor) { this.conversionFactor = conversionFactor; }
    public double getConversionFactor() { return conversionFactor; }
    public double convertToBaseUnit(double value) { return value * conversionFactor; }
    public double convertFromBaseUnit(double baseValue) { return baseValue / conversionFactor; }
    public String getUnitName() { return this.name(); }
    public String getMeasurementType() { return this.getClass().getSimpleName(); }
}
