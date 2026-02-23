package com.apps.quantitymeasurement;

public class QuantityMeasurementApp {

	public static <U extends IMeasurable> boolean demonstrateEquality(Quantity<U> q1, Quantity<U> q2) {
		return q1.equals(q2);
	}

	public static <U extends IMeasurable> boolean demonstrateComparison(double value1, U unit1, double value2,
			U unit2) {

		Quantity<U> q1 = new Quantity<>(value1, unit1);
		Quantity<U> q2 = new Quantity<>(value2, unit2);

		boolean result = q1.equals(q2);

		System.out.println("quantities are equal : " + result);
		return result;
	}

	public static <U extends IMeasurable> double demonstrateConversion(double value, U from, U to) {

		double result = Quantity.convert(value, from, to);

		System.out.println(value + " " + from.getUnitName() + " = " + result + " " + to.getUnitName());

		return result;
	}

	public static <U extends IMeasurable> Quantity<U> demonstrateAddition(Quantity<U> q1, Quantity<U> q2) {

		Quantity<U> result = q1.add(q2);

		System.out.println("Addition : " + result);

		return result;
	}

	public static <U extends IMeasurable> Quantity<U> demonstrateAddition(Quantity<U> q1, Quantity<U> q2,
			U targetUnit) {

		Quantity<U> result = q1.add(q2, targetUnit);

		System.out.println("Addition : " + result);

		return result;
	}

	public static void main(String[] args) {

	    // Demonstration equality between two quantities
	    Quantity<WeightUnit> weightInGrams =
	            new Quantity<>(1000.0, WeightUnit.GRAM);

	    Quantity<WeightUnit> weightInKilograms =
	            new Quantity<>(1.0, WeightUnit.KILOGRAM);

	    boolean areEqual =
	            demonstrateEquality(weightInGrams, weightInKilograms);

	    System.out.println("Are weights equal? " + areEqual);


	    // Demonstration conversion
	    double convertedValue =
	            demonstrateConversion(1000.0,
	                                  WeightUnit.GRAM,
	                                  WeightUnit.KILOGRAM);

	    System.out.println("Converted Weight: "
	            + convertedValue + " "
	            + WeightUnit.KILOGRAM.getUnitName());


	    // Demonstration addition (result in first unit)
	    Quantity<WeightUnit> weightInPounds =
	            new Quantity<>(2.20462, WeightUnit.POUND);

	    Quantity<WeightUnit> sumWeight =
	            demonstrateAddition(weightInKilograms, weightInPounds);

	    System.out.println("Sum Weight: "
	            + sumWeight.getValue() + " "
	            + sumWeight.getUnit());


	    // Demonstration addition in specific target unit
	    Quantity<WeightUnit> sumWeightInGrams =
	            demonstrateAddition(weightInKilograms,
	                                weightInPounds,
	                                WeightUnit.GRAM);

	    System.out.println("Sum Weight in Grams: "
	            + sumWeightInGrams.getValue() + " "
	            + sumWeightInGrams.getUnit());
	}
}