package com.apps.quantitymeasurement;

public class QuantityMeasurementApp {

	public static boolean demonstrateLengthEquality(Length l1, Length l2) {
		return l1.equals(l2);
	}

	public static boolean demonstrateLengthComparison(double value1, Length.LengthUnit unit1, double value2,Length.LengthUnit unit2) {

		Length l1 = new Length(value1, unit1);
		Length l2 = new Length(value2, unit2);

		boolean result = l1.equals(l2);

		System.out.println("Input: Quantity(" + l1.getValue() + ",\"" + l1.getUnit() +"\") and Quantity("+ l2.getValue() + ",\"" + l2.getUnit() + "\")");
		System.out.println("Output: Equal(" + l1.equals(l2) + ")");		return result;
	}

	
	    // Main method
	    public static void main(String[] args) {

	        // Demonstrate Feet and Inches comparison
	        demonstrateLengthComparison(1.0, Length.LengthUnit.FEET,
	                                    12.0, Length.LengthUnit.INCHES);

	        // Demonstrate Yards and Inches comparison
	        demonstrateLengthComparison(1.0, Length.LengthUnit.YARDS,
	                                    36.0, Length.LengthUnit.INCHES);

	        // Demonstrate Centimeters and Inches comparison
	        demonstrateLengthComparison(100.0, Length.LengthUnit.CENTIMETERS,
	                                    39.3701, Length.LengthUnit.INCHES);

	        // Demonstrate Feet and Yards comparison
	        demonstrateLengthComparison(1.0, Length.LengthUnit.YARDS,
	        							3.0, Length.LengthUnit.FEET
	                                    );

	        // Demonstrate Centimeters and Feet comparison
	        demonstrateLengthComparison(30.48, Length.LengthUnit.CENTIMETERS,
	                                    1.0, Length.LengthUnit.FEET);
	    }
	}
