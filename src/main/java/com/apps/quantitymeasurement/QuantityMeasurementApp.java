package com.apps.quantitymeasurement;

public class QuantityMeasurementApp {

    public static boolean demonstrateLengthEquality(Length l1, Length l2) { 
        return l1.equals(l2);
    }

    public static boolean demonstrateLengthComparison(double value1,
                                                      Length.LengthUnit unit1,
                                                      double value2,
                                                      Length.LengthUnit unit2) {

        Length l1 = new Length(value1, unit1);
        Length l2 = new Length(value2, unit2);

        boolean result = l1.equals(l2);

        System.out.println("lengths are equal : " + result);
        return result;
    }

    public static double demonstrateLengthConversion(double value,
                                                     Length.LengthUnit from,
                                                     Length.LengthUnit to) {

        double result = Length.convert(value, from, to);

        System.out.println("Input: convert(" + value + ", "
                + from + ", " + to + ") → Output: " + result);

        return result;
    }

    public static Length demonstrateLengthConversion(Length length,
                                                     Length.LengthUnit toUnit) {

        Length result = length.convertTo(toUnit);

        System.out.println("Input: convert(" + length.getValue() + ", "
                + length.getUnit() + ", " + toUnit + ") → Output: "
                + result.getValue());

        return result;
    }
    
    
	public static Length demonstrateLengthAddition(Length length1, Length length2) {

		Length result = length1.add(length2);

		System.out.println("Input: add(Quantity(" + length1 + "), Quantity("+length2+ "))");
		System.out.println("Quantity(" + result+")"); 
		
		return result;
	}
    
	 public static void main(String[] args) {

	        demonstrateLengthAddition(new Length(1.0, Length.LengthUnit.FEET),
	                      new Length(2.0, Length.LengthUnit.FEET));

	        demonstrateLengthAddition(new Length(1.0, Length.LengthUnit.FEET),
	                      new Length(12.0, Length.LengthUnit.INCHES));

	        demonstrateLengthAddition(new Length(12.0, Length.LengthUnit.INCHES),
	                      new Length(1.0, Length.LengthUnit.FEET));

	        demonstrateLengthAddition(new Length(1.0, Length.LengthUnit.YARDS),
	                      new Length(3.0, Length.LengthUnit.FEET));

	        demonstrateLengthAddition(new Length(36.0, Length.LengthUnit.INCHES),
	                      new Length(1.0, Length.LengthUnit.YARDS));

	        demonstrateLengthAddition(new Length(2.54, Length.LengthUnit.CENTIMETERS),
	                      new Length(1.0, Length.LengthUnit.INCHES));

	        demonstrateLengthAddition(new Length(5.0, Length.LengthUnit.FEET),
	                      new Length(0.0, Length.LengthUnit.INCHES));

	        demonstrateLengthAddition(new Length(5.0, Length.LengthUnit.FEET),
	                      new Length(-2.0, Length.LengthUnit.FEET));
	    }

}