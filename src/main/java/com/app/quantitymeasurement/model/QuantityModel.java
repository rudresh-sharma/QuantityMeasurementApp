package com.app.quantitymeasurement.model;

import java.io.Serializable;

import com.app.quantitymeasurement.quantity.Quantity;
import com.app.quantitymeasurement.unit.IMeasurable;

public class QuantityModel<U extends IMeasurable> implements Serializable {

	private static final long serialVersionUID = 1L;

	private double value;
	private U unit;

	public QuantityModel() {
	}

	public QuantityModel(double value, U unit) {
		this.value = value;
		this.unit = unit;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public U getUnit() {
		return unit;
	}

	public void setUnit(U unit) {
		this.unit = unit;
	}

	public Quantity<U> toQuantity() {
		return new Quantity<>(value, unit);
	}

	public static <U extends IMeasurable> QuantityModel<U> fromQuantity(Quantity<U> quantity) {
		return new QuantityModel<>(quantity.getValue(), quantity.getUnit());
	}
}
