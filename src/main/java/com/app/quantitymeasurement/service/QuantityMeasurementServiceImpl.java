package com.app.quantitymeasurement.service;

import java.util.List;

import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.model.QuantityDTO;
import com.app.quantitymeasurement.model.QuantityMeasurementEntity;
import com.app.quantitymeasurement.model.QuantityModel;
import com.app.quantitymeasurement.quantity.Quantity;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurement.unit.IMeasurable;

public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

	private final IQuantityMeasurementRepository repository;

	public QuantityMeasurementServiceImpl(IQuantityMeasurementRepository repository) {
		this.repository = repository;
	}

	@Override
	public boolean compare(QuantityDTO first, QuantityDTO second) {
		Quantity<IMeasurable> left = toQuantity(first);
		Quantity<IMeasurable> right = toQuantity(second);

		try {
			boolean result = left.equals(right);
			repository.saveMeasurement(new QuantityMeasurementEntity("COMPARE", toModel(left), toModel(right),
					Double.valueOf(result ? 1.0 : 0.0)));
			return result;
		} catch (RuntimeException exception) {
			handleFailure("COMPARE", left, right, null, exception);
			throw wrap(exception);
		}
	}

	@Override
	public QuantityDTO convert(QuantityDTO source, String targetUnit) {
		Quantity<IMeasurable> quantity = toQuantity(source);
		IMeasurable target = IMeasurable.from(source.getMeasurementType(), targetUnit);

		try {
			Quantity<IMeasurable> converted = quantity.convertTo(target);
			repository.saveMeasurement(
					new QuantityMeasurementEntity("CONVERT", toModel(quantity), null, target.getUnitName(), toModel(converted)));
			return toDto(converted);
		} catch (RuntimeException exception) {
			handleFailure("CONVERT", quantity, null, targetUnit, exception);
			throw wrap(exception);
		}
	}

	@Override
	public QuantityDTO add(QuantityDTO first, QuantityDTO second) {
		return add(first, second, first.getUnit());
	}

	@Override
	public QuantityDTO add(QuantityDTO first, QuantityDTO second, String targetUnit) {
		Quantity<IMeasurable> left = toQuantity(first);
		Quantity<IMeasurable> right = toQuantity(second);
		IMeasurable target = IMeasurable.from(first.getMeasurementType(), targetUnit);

		try {
			Quantity<IMeasurable> result = left.add(right, target);
			repository.saveMeasurement(
					new QuantityMeasurementEntity("ADD", toModel(left), toModel(right), target.getUnitName(), toModel(result)));
			return toDto(result);
		} catch (RuntimeException exception) {
			handleFailure("ADD", left, right, targetUnit, exception);
			throw wrap(exception);
		}
	}

	@Override
	public QuantityDTO subtract(QuantityDTO first, QuantityDTO second) {
		return subtract(first, second, first.getUnit());
	}

	@Override
	public QuantityDTO subtract(QuantityDTO first, QuantityDTO second, String targetUnit) {
		Quantity<IMeasurable> left = toQuantity(first);
		Quantity<IMeasurable> right = toQuantity(second);
		IMeasurable target = IMeasurable.from(first.getMeasurementType(), targetUnit);

		try {
			Quantity<IMeasurable> result = left.subtract(right, target);
			repository.saveMeasurement(new QuantityMeasurementEntity("SUBTRACT", toModel(left), toModel(right),
					target.getUnitName(), toModel(result)));
			return toDto(result);
		} catch (RuntimeException exception) {
			handleFailure("SUBTRACT", left, right, targetUnit, exception);
			throw wrap(exception);
		}
	}

	@Override
	public double divide(QuantityDTO first, QuantityDTO second) {
		Quantity<IMeasurable> left = toQuantity(first);
		Quantity<IMeasurable> right = toQuantity(second);

		try {
			double result = left.divide(right);
			repository.saveMeasurement(
					new QuantityMeasurementEntity("DIVIDE", toModel(left), toModel(right), Double.valueOf(result)));
			return result;
		} catch (RuntimeException exception) {
			handleFailure("DIVIDE", left, right, null, exception);
			throw wrap(exception);
		}
	}

	@Override
	public List<QuantityMeasurementEntity> getMeasurementHistory() {
		return repository.getAllMeasurements();
	}

	private Quantity<IMeasurable> toQuantity(QuantityDTO dto) {
		if (dto == null) {
			throw new QuantityMeasurementException("Quantity input cannot be null");
		}

		IMeasurable unit = IMeasurable.from(dto.getMeasurementType(), dto.getUnit());
		return new Quantity<>(dto.getValue(), unit);
	}

	private QuantityModel<IMeasurable> toModel(Quantity<IMeasurable> quantity) {
		return new QuantityModel<>(quantity.getValue(), quantity.getUnit());
	}

	private QuantityDTO toDto(Quantity<IMeasurable> quantity) {
		return new QuantityDTO(quantity.getValue(), quantity.getUnit().getUnitName(),
				quantity.getUnit().getMeasurementType());
	}

	private void handleFailure(String operation, Quantity<IMeasurable> left, Quantity<IMeasurable> right,
			String targetUnit, RuntimeException exception) {
		repository.saveMeasurement(new QuantityMeasurementEntity(operation, left == null ? null : toModel(left),
				right == null ? null : toModel(right), targetUnit, exception.getMessage()));
	}

	private QuantityMeasurementException wrap(RuntimeException exception) {
		if (exception instanceof QuantityMeasurementException) {
			return (QuantityMeasurementException) exception;
		}
		return new QuantityMeasurementException(exception.getMessage(), exception);
	}
}
