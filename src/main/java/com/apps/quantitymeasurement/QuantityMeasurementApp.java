package com.apps.quantitymeasurement;

public class QuantityMeasurementApp {

	public static boolean demonstrateLengthEquality(Length l1, Length l2) {
		return l1.equals(l2);
	}

	public static boolean demonstrateLengthComparison(double value1, LengthUnit unit1, double value2,
			LengthUnit unit2) {

		Length l1 = new Length(value1, unit1);
		Length l2 = new Length(value2, unit2);

		boolean result = l1.equals(l2);

		System.out.println("lengths are equal : " + result);
		return result;
	}

	public static double demonstrateLengthConversion(double value, LengthUnit from, LengthUnit to) {

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

	public static Length demonstrateLengthAddition(Length l1, Length l2, LengthUnit targetUnit) {

		Length result = l1.add(l2, targetUnit);

		   System.out.println("Input: add(Quantity(" + l1 + "), "
		            + "Quantity(" + l2 + "), "
		            +  " → Output: Quantity(" + result + ")");
		return result;
	}

	public static boolean demonstrateWeightEquality(Weight w1, Weight w2) {

	    boolean result = w1.equals(w2);

	    System.out.println("Input: Quantity(" + w1 + ").equals(Quantity(" + w2 + ")) → Output: " + result);

	    return result;
	}

	public static boolean demonstrateWeightComparison(double value1, WeightUnit unit1, double value2,
			WeightUnit unit2) {

		Weight w1 = new Weight(value1, unit1);
		Weight w2 = new Weight(value2, unit2);

		boolean result = w1.equals(w2);

		System.out.println("weights are equal : " + result);
		return result;
	}

	public static Weight demonstrateWeightConversion(Weight w, WeightUnit targetUnit) {

	    Weight result = w.convertTo(targetUnit);

	    System.out.println("Input: Quantity(" + w + ").convertTo(" + targetUnit + ") → Output: Quantity(" + result + ")");

	    return result;
	}

	public static Weight demonstrateWeightAddition(Weight w1, Weight w2) {

	    Weight result = w1.add(w2);

	    System.out.println("Input: Quantity(" + w1 + ").add(Quantity(" + w2 + ")) → Output: Quantity(" + result + ")");

	    return result;
	}

	public static Weight demonstrateWeightAddition(Weight w1, Weight w2, WeightUnit targetUnit) {

	    Weight result = w1.add(w2, targetUnit);

	    System.out.println("Input: Quantity(" + w1 + ").add(Quantity(" + w2 + "), " + targetUnit + ") → Output: Quantity(" + result + ")");

	    return result;
	}

	public static void main(String[] args) {

	    System.out.println("Equality Comparisons:\n");

	    demonstrateWeightEquality(
	            new Weight(1.0, WeightUnit.KILOGRAM),
	            new Weight(1.0, WeightUnit.KILOGRAM));

	    demonstrateWeightEquality(
	            new Weight(1.0, WeightUnit.KILOGRAM),
	            new Weight(1000.0, WeightUnit.GRAM));

	    demonstrateWeightEquality(
	            new Weight(2.0, WeightUnit.POUND),
	            new Weight(2.0, WeightUnit.POUND));

	    demonstrateWeightEquality(
	            new Weight(1.0, WeightUnit.KILOGRAM),
	            new Weight(2.20462, WeightUnit.POUND));

	    demonstrateWeightEquality(
	            new Weight(500.0, WeightUnit.GRAM),
	            new Weight(0.5, WeightUnit.KILOGRAM));

	    demonstrateWeightEquality(
	            new Weight(1.0, WeightUnit.POUND),
	            new Weight(453.592, WeightUnit.GRAM));

	    System.out.println("\nUnit Conversions:\n");

	    demonstrateWeightConversion(
	            new Weight(1.0, WeightUnit.KILOGRAM),
	            WeightUnit.GRAM);

	    demonstrateWeightConversion(
	            new Weight(2.0, WeightUnit.POUND),
	            WeightUnit.KILOGRAM);

	    demonstrateWeightConversion(
	            new Weight(500.0, WeightUnit.GRAM),
	            WeightUnit.POUND);

	    demonstrateWeightConversion(
	            new Weight(0.0, WeightUnit.KILOGRAM),
	            WeightUnit.GRAM);

	    System.out.println("\nAddition Operations (Implicit Target Unit):\n");

	    demonstrateWeightAddition(
	            new Weight(1.0, WeightUnit.KILOGRAM),
	            new Weight(2.0, WeightUnit.KILOGRAM));

	    demonstrateWeightAddition(
	            new Weight(1.0, WeightUnit.KILOGRAM),
	            new Weight(1000.0, WeightUnit.GRAM));

	    demonstrateWeightAddition(
	            new Weight(500.0, WeightUnit.GRAM),
	            new Weight(0.5, WeightUnit.KILOGRAM));

	    System.out.println("\nAddition Operations (Explicit Target Unit):\n");

	    demonstrateWeightAddition(
	            new Weight(1.0, WeightUnit.KILOGRAM),
	            new Weight(1000.0, WeightUnit.GRAM),
	            WeightUnit.GRAM);

	    demonstrateWeightAddition(
	            new Weight(1.0, WeightUnit.POUND),
	            new Weight(453.592, WeightUnit.GRAM),
	            WeightUnit.POUND);

	    demonstrateWeightAddition(
	            new Weight(2.0, WeightUnit.KILOGRAM),
	            new Weight(4.0, WeightUnit.POUND),
	            WeightUnit.KILOGRAM);

	    System.out.println("\nCategory Incompatibility:\n");

	    System.out.println("Input: Quantity(1.0, KILOGRAM).equals(Quantity(1.0, FOOT)) → Output: "
	            + new Weight(1.0, WeightUnit.KILOGRAM)
	                .equals(new Length(1.0, LengthUnit.FEET)));
	}
}