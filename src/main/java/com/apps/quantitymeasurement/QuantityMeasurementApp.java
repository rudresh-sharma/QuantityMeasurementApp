package com.apps.quantitymeasurement;

public class QuantityMeasurementApp {

    // 1️⃣ Equality using Length objects
    public static boolean demonstrateLengthEquality(Length l1, Length l2) { 
        return l1.equals(l2);
    }

    // 2️⃣ Equality using raw values
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

    // 3️⃣ Conversion using static convert()
    public static double demonstrateLengthConversion(double value,
                                                     Length.LengthUnit from,
                                                     Length.LengthUnit to) {

        double result = Length.convert(value, from, to);

        System.out.println("Input: convert(" + value + ", "
                + from + ", " + to + ") → Output: " + result);

        return result;
    }

    // 4️⃣ Conversion using object method
    public static Length demonstrateLengthConversion(Length length,
                                                     Length.LengthUnit toUnit) {

        Length result = length.convertTo(toUnit);

        System.out.println("Input: convert(" + length.getValue() + ", "
                + length.getUnit() + ", " + toUnit + ") → Output: "
                + result.getValue());

        return result;
    }

    // 5️⃣ Main method (Your Required Example Output)
    public static void main(String[] args) {

        demonstrateLengthConversion(1.0,
                Length.LengthUnit.FEET,
                Length.LengthUnit.INCHES);

        demonstrateLengthConversion(3.0,
                Length.LengthUnit.YARDS,
                Length.LengthUnit.FEET);

        demonstrateLengthConversion(36.0,
                Length.LengthUnit.INCHES,
                Length.LengthUnit.YARDS);

        demonstrateLengthConversion(1.0,
                Length.LengthUnit.CENTIMETERS,
                Length.LengthUnit.INCHES);

        demonstrateLengthConversion(0.0,
                Length.LengthUnit.FEET,
                Length.LengthUnit.INCHES);
    }
}