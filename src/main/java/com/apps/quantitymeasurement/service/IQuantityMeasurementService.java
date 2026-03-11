package com.apps.quantitymeasurement.service;

import java.util.List;

import com.apps.quantitymeasurement.model.QuantityDTO;
import com.apps.quantitymeasurement.model.QuantityMeasurementEntity;

public interface IQuantityMeasurementService {

	boolean compare(QuantityDTO first, QuantityDTO second);

	QuantityDTO convert(QuantityDTO source, String targetUnit);

	QuantityDTO add(QuantityDTO first, QuantityDTO second);

	QuantityDTO add(QuantityDTO first, QuantityDTO second, String targetUnit);

	QuantityDTO subtract(QuantityDTO first, QuantityDTO second);

	QuantityDTO subtract(QuantityDTO first, QuantityDTO second, String targetUnit);

	double divide(QuantityDTO first, QuantityDTO second);

	List<QuantityMeasurementEntity> getMeasurementHistory();
}
