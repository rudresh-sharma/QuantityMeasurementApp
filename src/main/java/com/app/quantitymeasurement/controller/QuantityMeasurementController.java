package com.app.quantitymeasurement.controller;

import java.util.List;

import com.app.quantitymeasurement.model.QuantityDTO;
import com.app.quantitymeasurement.model.QuantityMeasurementEntity;
import com.app.quantitymeasurement.quantity.Quantity;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import com.app.quantitymeasurement.unit.IMeasurable;

public class QuantityMeasurementController {

	private final IQuantityMeasurementService service;

	public QuantityMeasurementController(IQuantityMeasurementService service) {
		this.service = service;
	}

	public boolean compareQuantities(QuantityDTO first, QuantityDTO second) {
		return service.compare(first, second);
	}

	public QuantityDTO convertQuantity(QuantityDTO source, String targetUnit) {
		return service.convert(source, targetUnit);
	}

	public QuantityDTO addQuantities(QuantityDTO first, QuantityDTO second) {
		return service.add(first, second);
	}

	public QuantityDTO addQuantities(QuantityDTO first, QuantityDTO second, String targetUnit) {
		return service.add(first, second, targetUnit);
	}

	public QuantityDTO subtractQuantities(QuantityDTO first, QuantityDTO second) {
		return service.subtract(first, second);
	}

	public QuantityDTO subtractQuantities(QuantityDTO first, QuantityDTO second, String targetUnit) {
		return service.subtract(first, second, targetUnit);
	}

	public double divideQuantities(QuantityDTO first, QuantityDTO second) {
		return service.divide(first, second);
	}

	public List<QuantityMeasurementEntity> getMeasurementHistory() {
		return service.getMeasurementHistory();
	}

	public <U extends IMeasurable> boolean compareQuantities(Quantity<U> first, Quantity<U> second) {
		return service.compare(toDto(first), toDto(second));
	}

	public <U extends IMeasurable> Quantity<U> convertQuantity(Quantity<U> source, U targetUnit) {
		return fromDto(service.convert(toDto(source), targetUnit.getUnitName()));
	}

	public <U extends IMeasurable> Quantity<U> addQuantities(Quantity<U> first, Quantity<U> second) {
		return fromDto(service.add(toDto(first), toDto(second)));
	}

	public <U extends IMeasurable> Quantity<U> addQuantities(Quantity<U> first, Quantity<U> second, U targetUnit) {
		return fromDto(service.add(toDto(first), toDto(second), targetUnit.getUnitName()));
	}

	public <U extends IMeasurable> Quantity<U> subtractQuantities(Quantity<U> first, Quantity<U> second) {
		return fromDto(service.subtract(toDto(first), toDto(second)));
	}

	public <U extends IMeasurable> Quantity<U> subtractQuantities(Quantity<U> first, Quantity<U> second, U targetUnit) {
		return fromDto(service.subtract(toDto(first), toDto(second), targetUnit.getUnitName()));
	}

	public <U extends IMeasurable> double divideQuantities(Quantity<U> first, Quantity<U> second) {
		return service.divide(toDto(first), toDto(second));
	}

	private <U extends IMeasurable> QuantityDTO toDto(Quantity<U> quantity) {
		return new QuantityDTO(quantity.getValue(), quantity.getUnit().getUnitName(),
				quantity.getUnit().getMeasurementType());
	}

	@SuppressWarnings("unchecked")
	private <U extends IMeasurable> Quantity<U> fromDto(QuantityDTO dto) {
		U unit = (U) IMeasurable.from(dto.getMeasurementType(), dto.getUnit());
		return new Quantity<>(dto.getValue(), unit);
	}
}
