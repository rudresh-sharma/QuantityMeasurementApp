package com.apps.quantitymeasurement.repository;

import java.util.List;

import com.apps.quantitymeasurement.model.QuantityMeasurementEntity;

public interface IQuantityMeasurementRepository {

	void saveMeasurement(QuantityMeasurementEntity entity);

	List<QuantityMeasurementEntity> getAllMeasurements();
}
