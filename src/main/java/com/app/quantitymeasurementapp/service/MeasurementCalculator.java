package com.app.quantitymeasurementapp.service;

import com.app.quantitymeasurementapp.unit.IMeasurable;

final class MeasurementCalculator {

    private static final double EPSILON = 1e-6;

    private MeasurementCalculator() {
    }

    static boolean areEqual(double leftValue, IMeasurable leftUnit, double rightValue, IMeasurable rightUnit) {
        validatePair(leftValue, leftUnit, rightValue, rightUnit);
        return Math.abs(toBaseValue(leftValue, leftUnit) - toBaseValue(rightValue, rightUnit)) < EPSILON;
    }

    static double convert(double value, IMeasurable sourceUnit, IMeasurable targetUnit) {
        validateValueAndUnit(value, sourceUnit, "Source unit cannot be null");
        validateUnit(targetUnit, "Target unit cannot be null");
        ensureCompatible(sourceUnit, targetUnit);
        return targetUnit.convertFromBaseUnit(toBaseValue(value, sourceUnit));
    }

    static MeasurementResult add(double leftValue, IMeasurable leftUnit, double rightValue, IMeasurable rightUnit,
                                 IMeasurable targetUnit) {
        validateArithmeticOperands(leftValue, leftUnit, rightValue, rightUnit, targetUnit, "ADD");
        double result = toBaseValue(leftValue, leftUnit) + toBaseValue(rightValue, rightUnit);
        return new MeasurementResult(targetUnit.convertFromBaseUnit(result), targetUnit);
    }

    static MeasurementResult subtract(double leftValue, IMeasurable leftUnit, double rightValue, IMeasurable rightUnit,
                                      IMeasurable targetUnit) {
        validateArithmeticOperands(leftValue, leftUnit, rightValue, rightUnit, targetUnit, "SUBTRACT");
        double result = toBaseValue(leftValue, leftUnit) - toBaseValue(rightValue, rightUnit);
        return new MeasurementResult(roundToTwoDecimals(targetUnit.convertFromBaseUnit(result)), targetUnit);
    }

    static double divide(double leftValue, IMeasurable leftUnit, double rightValue, IMeasurable rightUnit) {
        validatePair(leftValue, leftUnit, rightValue, rightUnit);
        leftUnit.validateOperationSupport("DIVIDE");
        rightUnit.validateOperationSupport("DIVIDE");

        double divisor = toBaseValue(rightValue, rightUnit);
        if (Math.abs(divisor) < EPSILON) {
            throw new ArithmeticException("Division by zero");
        }

        return toBaseValue(leftValue, leftUnit) / divisor;
    }

    private static void validateArithmeticOperands(double leftValue, IMeasurable leftUnit, double rightValue,
                                                   IMeasurable rightUnit, IMeasurable targetUnit, String operation) {
        validatePair(leftValue, leftUnit, rightValue, rightUnit);
        validateUnit(targetUnit, "Target unit cannot be null");
        ensureCompatible(leftUnit, targetUnit);
        leftUnit.validateOperationSupport(operation);
        rightUnit.validateOperationSupport(operation);
    }

    private static void validatePair(double leftValue, IMeasurable leftUnit, double rightValue, IMeasurable rightUnit) {
        validateValueAndUnit(leftValue, leftUnit, "Unit cannot be null");
        validateValueAndUnit(rightValue, rightUnit, "Unit cannot be null");
        ensureCompatible(leftUnit, rightUnit);
    }

    private static void validateValueAndUnit(double value, IMeasurable unit, String unitMessage) {
        validateUnit(unit, unitMessage);
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Value must be finite");
        }
    }

    private static void validateUnit(IMeasurable unit, String message) {
        if (unit == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private static void ensureCompatible(IMeasurable leftUnit, IMeasurable rightUnit) {
        if (!leftUnit.getClass().equals(rightUnit.getClass())) {
            throw new IllegalArgumentException("Incompatible measurement categories");
        }
    }

    private static double toBaseValue(double value, IMeasurable unit) {
        return unit.convertToBaseUnit(value);
    }

    private static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    static final class MeasurementResult {
        private final double value;
        private final IMeasurable unit;

        private MeasurementResult(double value, IMeasurable unit) {
            this.value = value;
            this.unit = unit;
        }

        double value() {
            return value;
        }

        IMeasurable unit() {
            return unit;
        }
    }
}
