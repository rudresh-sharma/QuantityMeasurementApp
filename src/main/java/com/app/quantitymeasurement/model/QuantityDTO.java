package com.app.quantitymeasurement.model;

import java.io.Serializable;

public class QuantityDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private double value;
	private String unit;
	private String measurementType;

	public QuantityDTO() {
	}

	public QuantityDTO(double value, String unit, String measurementType) {
		this.value = value;
		this.unit = unit;
		this.measurementType = measurementType;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getMeasurementType() {
		return measurementType;
	}

	public void setMeasurementType(String measurementType) {
		this.measurementType = measurementType;
	}
}
