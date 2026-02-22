package com.apps.quantitymeasurement;

public class QuantityMeasurementApp {

	public static void main(String[] args) {

		Feet f1 = new Feet(12.0);
		Feet f2 = new Feet(12.0);
		
		Inches n1 = new Inches(1.0);
		Inches n2 = new Inches(1.0);

		System.out.println("Feet Equal: " + f1.equals(f2));
		System.out.println("Inches equal: " + n1.equals(n2));
	}
}