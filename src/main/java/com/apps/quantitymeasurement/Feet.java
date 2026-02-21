package com.apps.quantitymeasurement;

import java.util.Objects;

public class Feet {
	public 	final double value;

	public Feet(double value) {
		this.value = value;
	}
	
	
	@Override
	public boolean equals(Object obj) {

	    if (this == obj) return true;

	    if (obj == null || getClass() != obj.getClass()) return false;

	    Feet other = (Feet) obj;

	    return Double.compare(this.value, other.value) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
	
	
}
