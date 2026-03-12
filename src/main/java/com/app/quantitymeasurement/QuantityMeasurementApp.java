package com.app.quantitymeasurement;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.app.quantitymeasurement.controller.QuantityMeasurementController;
import com.app.quantitymeasurement.model.QuantityDTO;
import com.app.quantitymeasurement.model.QuantityMeasurementEntity;
import com.app.quantitymeasurement.quantity.Quantity;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurement.repository.QuantityMeasurementDatabaseRepository;
import com.app.quantitymeasurement.repository.QuantityMeasurementCacheRepository;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import com.app.quantitymeasurement.service.QuantityMeasurementServiceImpl;
import com.app.quantitymeasurement.unit.IMeasurable;
import com.app.quantitymeasurement.unit.LengthUnit;
import com.app.quantitymeasurement.unit.VolumeUnit;
import com.app.quantitymeasurement.unit.WeightUnit;
import com.app.quantitymeasurement.util.ApplicationConfig;

public class QuantityMeasurementApp {

	private static final Logger LOGGER = Logger.getLogger(QuantityMeasurementApp.class.getName());
	private static final IQuantityMeasurementRepository REPOSITORY = buildRepository();
	private static final QuantityMeasurementController CONTROLLER = buildController();

	private static QuantityMeasurementController buildController() {
		IQuantityMeasurementService service = new QuantityMeasurementServiceImpl(REPOSITORY);
		return new QuantityMeasurementController(service);
	}

	private static IQuantityMeasurementRepository buildRepository() {
		ApplicationConfig config = new ApplicationConfig();
		if ("cache".equalsIgnoreCase(config.getRepositoryType())) {
			LOGGER.info("Using cache repository");
			return new QuantityMeasurementCacheRepository();
		}
		LOGGER.info("Using database repository");
		return new QuantityMeasurementDatabaseRepository(config);
	}

	public static <U extends IMeasurable> boolean demonstrateEquality(Quantity<U> q1, Quantity<U> q2) {
		return CONTROLLER.compareQuantities(q1, q2);
	}

	public static <U extends IMeasurable> Quantity<U> demonstrateSubtraction(Quantity<U> q1, Quantity<U> q2) {

		Quantity<U> result = CONTROLLER.subtractQuantities(q1, q2);

		LOGGER.info("Subtraction Result: " + result);
		return result;
	}

	public static <U extends IMeasurable> Quantity<U> demonstrateSubtraction(Quantity<U> q1, Quantity<U> q2,
			U targetUnit) {

		Quantity<U> result = CONTROLLER.subtractQuantities(q1, q2, targetUnit);

		LOGGER.info("Subtraction Result: " + result);
		return result;
	}

	public static <U extends IMeasurable> double demonstrateDivision(Quantity<U> q1, Quantity<U> q2) {

		double result = CONTROLLER.divideQuantities(q1, q2);

		LOGGER.info("Division Result: " + result);
		return result;
	}

	public static <U extends IMeasurable> boolean demonstrateComparison(double value1, U unit1, double value2,
			U unit2) {

		QuantityDTO q1 = new QuantityDTO(value1, unit1.getUnitName(), unit1.getMeasurementType());
		QuantityDTO q2 = new QuantityDTO(value2, unit2.getUnitName(), unit2.getMeasurementType());

		boolean result = CONTROLLER.compareQuantities(q1, q2);

		LOGGER.info("quantities are equal : " + result);
		return result;
	}

	public static <U extends IMeasurable> double demonstrateConversion(double value, U from, U to) {

		Quantity<U> converted = CONTROLLER.convertQuantity(new Quantity<>(value, from), to);
		double result = converted.getValue();

		LOGGER.info(value + " " + from.getUnitName() + " = " + result + " " + to.getUnitName());

		return result;
	}

	public static <U extends IMeasurable> Quantity<U> demonstrateAddition(Quantity<U> q1, Quantity<U> q2) {

		Quantity<U> result = CONTROLLER.addQuantities(q1, q2);

		LOGGER.info("Addition : " + result);

		return result;
	}

	public static <U extends IMeasurable> Quantity<U> demonstrateAddition(Quantity<U> q1, Quantity<U> q2,
			U targetUnit) {

		Quantity<U> result = CONTROLLER.addQuantities(q1, q2, targetUnit);

		LOGGER.info("Addition : " + result);

		return result;
	}

	public static <U extends IMeasurable> void demonstrateConversion(Quantity<U> quantity, U targetUnit) {

		Quantity<U> converted = CONTROLLER.convertQuantity(quantity, targetUnit);

		LOGGER.info("Original: " + quantity);
		LOGGER.info("Converted: " + converted);
	}

	public static void deleteAllMeasurements() {
		REPOSITORY.deleteAllMeasurements();
	}

	public static List<QuantityMeasurementEntity> getAllMeasurements() {
		return REPOSITORY.getAllMeasurements();
	}

	public static Map<String, Integer> getPoolStatistics() {
		return REPOSITORY.getPoolStatistics();
	}

	public static void closeResources() {
		REPOSITORY.releaseResources();
	}

	public static void main(String[] args) {
		deleteAllMeasurements();

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

		LOGGER.info("Stored measurements: " + getAllMeasurements().size());
		LOGGER.info("Repository statistics: " + getPoolStatistics());
		closeResources();
	}
}
