package com.app.quantitymeasurementservice.dto;

import com.app.quantitymeasurementservice.unit.IMeasurable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuantityDTO {

    public interface IMeasurableUnit {
        String getUnitName();
        String getMeasurementType();
    }

    public enum LengthUnit implements IMeasurableUnit {
        KILOMETER, METER, CENTIMETER, MILLIMETER, MILE, YARD, FOOT, INCH;
        public String getUnitName() { return this.name(); }
        public String getMeasurementType() { return this.getClass().getSimpleName(); }
    }

    public enum VolumeUnit implements IMeasurableUnit {
        LITRE, MILLILITRE, GALLON, QUART, PINT, CUP, FLUID_OUNCE, CUBIC_METER;
        public String getUnitName() { return this.name(); }
        public String getMeasurementType() { return this.getClass().getSimpleName(); }
    }

    public enum WeightUnit implements IMeasurableUnit {
        KILOGRAM, GRAM, MILLIGRAM, POUND, OUNCE, TONNE;
        public String getUnitName() { return this.name(); }
        public String getMeasurementType() { return this.getClass().getSimpleName(); }
    }

    public enum TemperatureUnit implements IMeasurableUnit {
        CELSIUS, FAHRENHEIT, KELVIN;
        public String getUnitName() { return this.name(); }
        public String getMeasurementType() { return this.getClass().getSimpleName(); }
    }

    @NotNull
    private double value;

    @NotEmpty
    private String unit;

    @NotEmpty
    @Pattern(regexp = "LengthUnit|VolumeUnit|WeightUnit|TemperatureUnit",
            message = "measurementType must be one of: LengthUnit, VolumeUnit, WeightUnit, TemperatureUnit")
    private String measurementType;

    public QuantityDTO(double value, IMeasurableUnit unit) {
        this.value = value;
        this.unit = unit.getUnitName();
        this.measurementType = unit.getMeasurementType();
    }

    public QuantityDTO(double value, String unit, String measurementType) {
        this.value = value;
        this.unit = unit;
        this.measurementType = measurementType;
    }

    public boolean isValidUnit() {
        try {
            IMeasurable.getUnitByName(this.unit, this.measurementType);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
