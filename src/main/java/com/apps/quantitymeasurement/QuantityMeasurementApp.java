package com.apps.quantitymeasurement;

public class QuantityMeasurementApp {

	public static boolean demonstrateLengthEquality(Length l1, Length l2) {
		return l1.equals(l2);
	}

	public static boolean demonstrateLengthComparison(double value1, Length.LengthUnit unit1, double value2,
			Length.LengthUnit unit2) {

		Length l1 = new Length(value1, unit1);
		Length l2 = new Length(value2, unit2);

		boolean result = l1.equals(l2);

		System.out.println("lengths are equal : " + result);
		return result;
	}

	public static double demonstrateLengthConversion(double value, Length.LengthUnit from, Length.LengthUnit to) {

		double result = Length.convert(value, from, to);

		System.out.println(value + " " + from + " = " + result + " " + to);

		return result;
	}

	public static Length demonstrateLengthAddition(Length l1, Length l2) {

		Length result = l1.add(l2);

		  System.out.println("Input: add(Quantity(" + l1 + "), "
		            + "Quantity(" + l2 + "), "
		            +  " → Output: Quantity(" + result + ")");
		return result;
	}

	public static Length demonstrateLengthAddition(Length l1, Length l2, Length.LengthUnit targetUnit) {

		Length result = l1.add(l2, targetUnit);

		  System.out.println("Input: add(Quantity(" + l1 + "), "
		            + "Quantity(" + l2 + "), "
		            + targetUnit + ") → Output: Quantity(" + result + ")");

		return result;
	}

	public static void main(String[] args) {

	    demonstrateLengthAddition(
	            new Length(1.0, Length.LengthUnit.FEET),
	            new Length(12.0, Length.LengthUnit.INCHES),
	            Length.LengthUnit.FEET);

	    demonstrateLengthAddition(
	            new Length(1.0, Length.LengthUnit.FEET),
	            new Length(12.0, Length.LengthUnit.INCHES),
	            Length.LengthUnit.INCHES);

	    demonstrateLengthAddition(
	            new Length(1.0, Length.LengthUnit.FEET),
	            new Length(12.0, Length.LengthUnit.INCHES),
	            Length.LengthUnit.YARDS);

	    demonstrateLengthAddition(
	            new Length(1.0, Length.LengthUnit.YARDS),
	            new Length(3.0, Length.LengthUnit.FEET),
	            Length.LengthUnit.YARDS);

	    demonstrateLengthAddition(
	            new Length(36.0, Length.LengthUnit.INCHES),
	            new Length(1.0, Length.LengthUnit.YARDS),
	            Length.LengthUnit.FEET);

	    demonstrateLengthAddition(
	            new Length(2.54, Length.LengthUnit.CENTIMETERS),
	            new Length(1.0, Length.LengthUnit.INCHES),
	            Length.LengthUnit.CENTIMETERS);

	    demonstrateLengthAddition(
	            new Length(5.0, Length.LengthUnit.FEET),
	            new Length(0.0, Length.LengthUnit.INCHES),
	            Length.LengthUnit.YARDS);

	    demonstrateLengthAddition(
	            new Length(5.0, Length.LengthUnit.FEET),
	            new Length(-2.0, Length.LengthUnit.FEET),
	            Length.LengthUnit.INCHES);
	}
}