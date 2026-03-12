package com.app.quantitymeasurement.repository;

import java.util.List;
import java.util.Map;

import com.app.quantitymeasurement.model.QuantityMeasurementEntity;

public interface IQuantityMeasurementRepository {

	void saveMeasurement(QuantityMeasurementEntity entity);

	List<QuantityMeasurementEntity> getAllMeasurements();

	List<QuantityMeasurementEntity> getMeasurementsByOperation(String operation);

	List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType);

	long getTotalCount();

	void deleteAllMeasurements();

	default Map<String, Integer> getPoolStatistics() {
		return Map.of();
	}

	default void releaseResources() {
	}
}
