package com.apps.quantitymeasurement;

import java.util.Objects;

public class Length {

	private final double value;
	private final LengthUnit unit;

	public enum LengthUnit {

		FEET(1.0),
		INCHES(1.0 / 12.0), 
		YARDS(3.0), 
		CENTIMETERS(0.393701 / 12.0);

		private final double toFeetFactor;

		LengthUnit(double toFeetFactor) {
			this.toFeetFactor = toFeetFactor;
		}

		public double toFeet(double value) {
			return value * toFeetFactor;
		}
	}

	public Length(double value, LengthUnit unit) {
		if (!Double.isFinite(value)) {
			throw new IllegalArgumentException("Value must be a finite number");
		}
		if (unit == null) {
			throw new IllegalArgumentException("Unit must not be null");
		}
		this.value = value;
		this.unit = unit;
	}
	
	public double getValue() {
		return value;
	}
	public LengthUnit getUnit() {
		return unit;
	}

	private double toBaseUnit() {
		return unit.toFeet(value);
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null  || getClass() != obj.getClass())
			return false;
	
		Length other = (Length) obj;

		return Double.compare(this.toBaseUnit(), other.toBaseUnit()) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(toBaseUnit());
	}
	
	
	
	
	   public static void main(String[] args) {

	        Length length1 = new Length(1.0, Length.LengthUnit.FEET);
	        Length length2 = new Length(12.0, Length.LengthUnit.INCHES);
	        System.out.println("Are lengths equal? " + length1.equals(length2)); // Should print true

	        Length length3 = new Length(1.0, Length.LengthUnit.YARDS);
	        Length length4 = new Length(36.0, Length.LengthUnit.INCHES);
	        System.out.println("Are lengths equal? " + length3.equals(length4)); // Should print true

	        Length length5 = new Length(100.0, Length.LengthUnit.CENTIMETERS);
	        Length length6 = new Length(39.3701, Length.LengthUnit.INCHES);
	        System.out.println("Are lengths equal? " + length5.equals(length6)); // Should print true
	    }
	   
	  
}