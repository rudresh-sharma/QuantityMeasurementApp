package com.app.quantitymeasurementservice.service;

import com.app.quantitymeasurementservice.dto.QuantityDTO;
import com.app.quantitymeasurementservice.dto.QuantityMeasurementDTO;
import com.app.quantitymeasurementservice.exception.QuantityMeasurementException;
import com.app.quantitymeasurementservice.model.QuantityMeasurementEntity;
import com.app.quantitymeasurementservice.repository.QuantityMeasurementRepository;
import com.app.quantitymeasurementservice.service.MeasurementCalculator.MeasurementResult;
import com.app.quantitymeasurementservice.unit.IMeasurable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementServiceImpl.class);

    private final QuantityMeasurementRepository repository;

    public QuantityMeasurementServiceImpl(QuantityMeasurementRepository repository) {
        this.repository = repository;
    }

    @Override
    public QuantityMeasurementDTO compareQuantities(QuantityDTO quantity1, QuantityDTO quantity2) {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        try {
            validateInputs(quantity1, quantity2);
            validateSameCategory(quantity1, quantity2);

            IMeasurable unit1 = IMeasurable.getUnitByName(quantity1.getUnit(), quantity1.getMeasurementType());
            IMeasurable unit2 = IMeasurable.getUnitByName(quantity2.getUnit(), quantity2.getMeasurementType());

            boolean isEqual = MeasurementCalculator.areEqual(quantity1.getValue(), unit1, quantity2.getValue(), unit2);

            populateEntity(entity, quantity1, quantity2, "compare");
            entity.setResultString(String.valueOf(isEqual));
            entity.setError(false);
            repository.save(entity);
            return QuantityMeasurementDTO.fromEntity(entity);
        } catch (Exception e) {
            persistError(entity, quantity1, quantity2, "compare", e);
            throw new QuantityMeasurementException("compare Error: " + e.getMessage(), e);
        }
    }

    @Override
    public QuantityMeasurementDTO convertQuantity(QuantityDTO quantity1, QuantityDTO quantity2) {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        try {
            if (quantity1 == null) {
                throw new IllegalArgumentException("Source quantity cannot be null");
            }
            if (quantity2 == null) {
                throw new IllegalArgumentException("Target quantity cannot be null");
            }

            IMeasurable sourceUnit = IMeasurable.getUnitByName(quantity1.getUnit(), quantity1.getMeasurementType());
            IMeasurable targetUnit = IMeasurable.getUnitByName(quantity2.getUnit(), quantity2.getMeasurementType());

            double convertedValue = MeasurementCalculator.convert(quantity1.getValue(), sourceUnit, targetUnit);

            populateEntity(entity, quantity1, quantity2, "convert");
            entity.setResultValue(convertedValue);
            entity.setResultUnit(targetUnit.getUnitName());
            entity.setResultMeasurementType(targetUnit.getMeasurementType());
            entity.setError(false);
            repository.save(entity);
            return QuantityMeasurementDTO.fromEntity(entity);
        } catch (Exception e) {
            persistError(entity, quantity1, quantity2, "convert", e);
            throw new QuantityMeasurementException("convert Error: " + e.getMessage(), e);
        }
    }

    @Override
    public QuantityMeasurementDTO addQuantities(QuantityDTO quantity1, QuantityDTO quantity2) {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        try {
            validateInputs(quantity1, quantity2);
            validateSameCategory(quantity1, quantity2);

            IMeasurable unit1 = IMeasurable.getUnitByName(quantity1.getUnit(), quantity1.getMeasurementType());
            IMeasurable unit2 = IMeasurable.getUnitByName(quantity2.getUnit(), quantity2.getMeasurementType());

            MeasurementResult result = MeasurementCalculator.add(
                    quantity1.getValue(), unit1, quantity2.getValue(), unit2, unit1);

            populateEntity(entity, quantity1, quantity2, "add");
            entity.setResultValue(result.value());
            entity.setResultUnit(result.unit().getUnitName());
            entity.setResultMeasurementType(result.unit().getMeasurementType());
            entity.setError(false);
            repository.save(entity);
            return QuantityMeasurementDTO.fromEntity(entity);
        } catch (Exception e) {
            persistError(entity, quantity1, quantity2, "add", e);
            throw new QuantityMeasurementException("add Error: " + e.getMessage(), e);
        }
    }

    @Override
    public QuantityMeasurementDTO subtractQuantities(QuantityDTO quantity1, QuantityDTO quantity2) {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        try {
            validateInputs(quantity1, quantity2);
            validateSameCategory(quantity1, quantity2);

            IMeasurable unit1 = IMeasurable.getUnitByName(quantity1.getUnit(), quantity1.getMeasurementType());
            IMeasurable unit2 = IMeasurable.getUnitByName(quantity2.getUnit(), quantity2.getMeasurementType());

            MeasurementResult result = MeasurementCalculator.subtract(
                    quantity1.getValue(), unit1, quantity2.getValue(), unit2, unit1);

            populateEntity(entity, quantity1, quantity2, "subtract");
            entity.setResultValue(result.value());
            entity.setResultUnit(result.unit().getUnitName());
            entity.setResultMeasurementType(result.unit().getMeasurementType());
            entity.setError(false);
            repository.save(entity);
            return QuantityMeasurementDTO.fromEntity(entity);
        } catch (Exception e) {
            persistError(entity, quantity1, quantity2, "subtract", e);
            throw new QuantityMeasurementException("subtract Error: " + e.getMessage(), e);
        }
    }

    @Override
    public QuantityMeasurementDTO divideQuantities(QuantityDTO quantity1, QuantityDTO quantity2) {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        try {
            validateInputs(quantity1, quantity2);
            validateSameCategory(quantity1, quantity2);

            IMeasurable unit1 = IMeasurable.getUnitByName(quantity1.getUnit(), quantity1.getMeasurementType());
            IMeasurable unit2 = IMeasurable.getUnitByName(quantity2.getUnit(), quantity2.getMeasurementType());

            double result = MeasurementCalculator.divide(quantity1.getValue(), unit1, quantity2.getValue(), unit2);

            populateEntity(entity, quantity1, quantity2, "divide");
            entity.setResultValue(result);
            entity.setError(false);
            repository.save(entity);
            return QuantityMeasurementDTO.fromEntity(entity);
        } catch (ArithmeticException e) {
            persistError(entity, quantity1, quantity2, "divide", e);
            throw e;
        } catch (Exception e) {
            persistError(entity, quantity1, quantity2, "divide", e);
            throw new QuantityMeasurementException("divide Error: " + e.getMessage(), e);
        }
    }

    @Override
    public List<QuantityMeasurementDTO> getHistoryByOperation(String operation) {
        return QuantityMeasurementDTO.fromEntityList(repository.findByOperation(operation));
    }

    @Override
    public List<QuantityMeasurementDTO> getHistoryByMeasurementType(String measurementType) {
        return QuantityMeasurementDTO.fromEntityList(repository.findByThisMeasurementType(measurementType));
    }

    @Override
    public long getCountByOperation(String operation) {
        return repository.countByOperationAndIsErrorFalse(operation);
    }

    @Override
    public List<QuantityMeasurementDTO> getErrorHistory() {
        return QuantityMeasurementDTO.fromEntityList(repository.findByIsErrorTrue());
    }

    private void validateInputs(QuantityDTO quantity1, QuantityDTO quantity2) {
        if (quantity1 == null || quantity2 == null) {
            throw new IllegalArgumentException("Quantities cannot be null");
        }
    }

    private void validateSameCategory(QuantityDTO quantity1, QuantityDTO quantity2) {
        if (!quantity1.getMeasurementType().equals(quantity2.getMeasurementType())) {
            throw new IllegalArgumentException("Cannot perform arithmetic between different measurement categories: "
                    + quantity1.getMeasurementType() + " and " + quantity2.getMeasurementType());
        }
    }

    private void populateEntity(QuantityMeasurementEntity entity, QuantityDTO q1, QuantityDTO q2, String operation) {
        if (q1 != null) {
            entity.setThisValue(q1.getValue());
            entity.setThisUnit(q1.getUnit());
            entity.setThisMeasurementType(q1.getMeasurementType());
        }
        if (q2 != null) {
            entity.setThatValue(q2.getValue());
            entity.setThatUnit(q2.getUnit());
            entity.setThatMeasurementType(q2.getMeasurementType());
        }
        entity.setOperation(operation);
    }

    private void persistError(QuantityMeasurementEntity entity, QuantityDTO q1, QuantityDTO q2, String operation, Exception e) {
        try {
            populateEntity(entity, q1, q2, operation);
            entity.setError(true);
            entity.setErrorMessage(e.getMessage());
            repository.save(entity);
            logger.warn("Operation {} failed and was persisted as error: {}", operation, e.getMessage());
        } catch (Exception ignored) {
            logger.warn("Failed to persist operation error for {}", operation);
        }
    }
}
