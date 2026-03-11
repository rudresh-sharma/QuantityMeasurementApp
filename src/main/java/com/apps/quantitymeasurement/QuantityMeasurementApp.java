package com.apps.quantitymeasurement;

import com.apps.quantitymeasurement.controller.QuantityMeasurementController;
import com.apps.quantitymeasurement.model.QuantityDTO;
import com.apps.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.apps.quantitymeasurement.repository.QuantityMeasurementCacheRepository;
import com.apps.quantitymeasurement.service.IQuantityMeasurementService;
import com.apps.quantitymeasurement.service.QuantityMeasurementServiceImpl;

public class QuantityMeasurementApp {

	private static final QuantityMeasurementController CONTROLLER = buildController();

	private static QuantityMeasurementController buildController() {
		IQuantityMeasurementRepository repository = new QuantityMeasurementCacheRepository();
		IQuantityMeasurementService service = new QuantityMeasurementServiceImpl(repository);
		return new QuantityMeasurementController(service);
	}

	public static <U extends IMeasurable> boolean demonstrateEquality(Quantity<U> q1, Quantity<U> q2) {
		return CONTROLLER.compareQuantities(q1, q2);
	}

	public static <U extends IMeasurable> Quantity<U> demonstrateSubtraction(Quantity<U> q1, Quantity<U> q2) {

		Quantity<U> result = CONTROLLER.subtractQuantities(q1, q2);

		System.out.println("Subtraction Result: " + result);
		return result;
	}

	public static <U extends IMeasurable> Quantity<U> demonstrateSubtraction(Quantity<U> q1, Quantity<U> q2,
			U targetUnit) {

		Quantity<U> result = CONTROLLER.subtractQuantities(q1, q2, targetUnit);

		System.out.println("Subtraction Result: " + result);
		return result;
	}

	public static <U extends IMeasurable> double demonstrateDivision(Quantity<U> q1, Quantity<U> q2) {

		double result = CONTROLLER.divideQuantities(q1, q2);

		System.out.println("Division Result: " + result);
		return result;
	}

	public static <U extends IMeasurable> boolean demonstrateComparison(double value1, U unit1, double value2,
			U unit2) {

		QuantityDTO q1 = new QuantityDTO(value1, unit1.getUnitName(), unit1.getMeasurementType());
		QuantityDTO q2 = new QuantityDTO(value2, unit2.getUnitName(), unit2.getMeasurementType());

		boolean result = CONTROLLER.compareQuantities(q1, q2);

		System.out.println("quantities are equal : " + result);
		return result;
	}

	public static <U extends IMeasurable> double demonstrateConversion(double value, U from, U to) {

		Quantity<U> converted = CONTROLLER.convertQuantity(new Quantity<>(value, from), to);
		double result = converted.getValue();

		System.out.println(value + " " + from.getUnitName() + " = " + result + " " + to.getUnitName());

		return result;
	}

	public static <U extends IMeasurable> Quantity<U> demonstrateAddition(Quantity<U> q1, Quantity<U> q2) {

		Quantity<U> result = CONTROLLER.addQuantities(q1, q2);

		System.out.println("Addition : " + result);

		return result;
	}

	public static <U extends IMeasurable> Quantity<U> demonstrateAddition(Quantity<U> q1, Quantity<U> q2,
			U targetUnit) {

		Quantity<U> result = CONTROLLER.addQuantities(q1, q2, targetUnit);

		System.out.println("Addition : " + result);

		return result;
	}

	public static <U extends IMeasurable> void demonstrateConversion(Quantity<U> quantity, U targetUnit) {

		Quantity<U> converted = CONTROLLER.convertQuantity(quantity, targetUnit);

		System.out.println("Original: " + quantity);
		System.out.println("Converted: " + converted);
	}

	public static void main(String[] args) {

		demonstrateComparison(1.0, LengthUnit.FEET, 12.0, LengthUnit.INCHES);

		demonstrateComparison(1.0, LengthUnit.YARDS, 3.0, LengthUnit.FEET);

		demonstrateComparison(1.0, LengthUnit.YARDS, 36.0, LengthUnit.INCHES);

		demonstrateComparison(1.0, LengthUnit.CENTIMETERS, 0.393701, LengthUnit.INCHES);

		demonstrateComparison(2.0, LengthUnit.YARDS, 6.0, LengthUnit.FEET);

		demonstrateConversion(1.0, LengthUnit.FEET, LengthUnit.INCHES);

		demonstrateConversion(3.0, LengthUnit.YARDS, LengthUnit.FEET);

		demonstrateConversion(2.54, LengthUnit.CENTIMETERS, LengthUnit.INCHES);

		demonstrateAddition(new Quantity<>(1.0, LengthUnit.FEET), new Quantity<>(12.0, LengthUnit.INCHES));

		demonstrateAddition(new Quantity<>(1.0, LengthUnit.FEET), new Quantity<>(12.0, LengthUnit.INCHES),
				LengthUnit.YARDS);

		demonstrateComparison(1.0, WeightUnit.KILOGRAM, 1000.0, WeightUnit.GRAM);

		demonstrateComparison(1.0, WeightUnit.KILOGRAM, 2.20462, WeightUnit.POUND);

		demonstrateComparison(500.0, WeightUnit.GRAM, 0.5, WeightUnit.KILOGRAM);

		demonstrateConversion(1.0, WeightUnit.KILOGRAM, WeightUnit.GRAM);

		demonstrateConversion(2.0, WeightUnit.POUND, WeightUnit.KILOGRAM);

		demonstrateConversion(500.0, WeightUnit.GRAM, WeightUnit.POUND);

		demonstrateAddition(new Quantity<>(1.0, WeightUnit.KILOGRAM), new Quantity<>(2.0, WeightUnit.KILOGRAM));

		demonstrateAddition(new Quantity<>(1.0, WeightUnit.KILOGRAM), new Quantity<>(1000.0, WeightUnit.GRAM),
				WeightUnit.GRAM);

		Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
		Quantity<VolumeUnit> v2 = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
		Quantity<VolumeUnit> v3 = new Quantity<>(1.0, VolumeUnit.GALLON);

		demonstrateEquality(v1, v2);
		demonstrateConversion(v1, VolumeUnit.MILLILITRE);

		demonstrateAddition(v1, v2, VolumeUnit.LITRE);

		Quantity<VolumeUnit> x1 = new Quantity<>(5.0, VolumeUnit.LITRE);

		Quantity<VolumeUnit> x2 = new Quantity<>(500.0, VolumeUnit.MILLILITRE);

		demonstrateSubtraction(x1, x2);
		demonstrateDivision(x1, x2);
	}
}
