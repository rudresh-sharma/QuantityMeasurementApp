package com.app.quantitymeasurement.model;

import java.io.Serializable;
import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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
