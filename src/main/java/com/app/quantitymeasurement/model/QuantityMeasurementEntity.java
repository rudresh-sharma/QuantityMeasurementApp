package com.app.quantitymeasurement.model;

import java.io.Serializable;
import java.time.Instant;

public class QuantityMeasurementEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
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

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public QuantityModel<?> getLeftOperand() {
		return leftOperand;
	}

	public void setLeftOperand(QuantityModel<?> leftOperand) {
		this.leftOperand = leftOperand;
	}

	public QuantityModel<?> getRightOperand() {
		return rightOperand;
	}

	public void setRightOperand(QuantityModel<?> rightOperand) {
		this.rightOperand = rightOperand;
	}

	public String getTargetUnit() {
		return targetUnit;
	}

	public void setTargetUnit(String targetUnit) {
		this.targetUnit = targetUnit;
	}

	public QuantityModel<?> getQuantityResult() {
		return quantityResult;
	}

	public void setQuantityResult(QuantityModel<?> quantityResult) {
		this.quantityResult = quantityResult;
	}

	public Double getScalarResult() {
		return scalarResult;
	}

	public void setScalarResult(Double scalarResult) {
		this.scalarResult = scalarResult;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMeasurementType() {
		if (leftOperand != null && leftOperand.getUnit() != null) {
			return leftOperand.getUnit().getMeasurementType();
		}
		if (rightOperand != null && rightOperand.getUnit() != null) {
			return rightOperand.getUnit().getMeasurementType();
		}
		if (quantityResult != null && quantityResult.getUnit() != null) {
			return quantityResult.getUnit().getMeasurementType();
		}
		return null;
	}
}
