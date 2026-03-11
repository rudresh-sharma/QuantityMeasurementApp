package com.apps.quantitymeasurement.model;

import java.io.Serializable;
import java.time.Instant;

public class QuantityMeasurementEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private Instant createdAt;
	private String operation;
	private QuantityModel<?> leftOperand;
	private QuantityModel<?> rightOperand;
	private String targetUnit;
	private QuantityModel<?> quantityResult;
	private Double scalarResult;
	private boolean success;
	private String errorMessage;

	public QuantityMeasurementEntity() {
	}

	public QuantityMeasurementEntity(String operation, QuantityModel<?> leftOperand, QuantityModel<?> rightOperand,
			String targetUnit, QuantityModel<?> quantityResult) {
		this.createdAt = Instant.now();
		this.operation = operation;
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
		this.targetUnit = targetUnit;
		this.quantityResult = quantityResult;
		this.success = true;
	}

	public QuantityMeasurementEntity(String operation, QuantityModel<?> leftOperand, QuantityModel<?> rightOperand,
			Double scalarResult) {
		this.createdAt = Instant.now();
		this.operation = operation;
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
		this.scalarResult = scalarResult;
		this.success = true;
	}

	public QuantityMeasurementEntity(String operation, QuantityModel<?> leftOperand, QuantityModel<?> rightOperand,
			String targetUnit, String errorMessage) {
		this.createdAt = Instant.now();
		this.operation = operation;
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
		this.targetUnit = targetUnit;
		this.success = false;
		this.errorMessage = errorMessage;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public String getOperation() {
		return operation;
	}

	public QuantityModel<?> getLeftOperand() {
		return leftOperand;
	}

	public QuantityModel<?> getRightOperand() {
		return rightOperand;
	}

	public String getTargetUnit() {
		return targetUnit;
	}

	public QuantityModel<?> getQuantityResult() {
		return quantityResult;
	}

	public Double getScalarResult() {
		return scalarResult;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
