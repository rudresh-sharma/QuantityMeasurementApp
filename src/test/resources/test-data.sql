INSERT INTO quantity_measurement_history (
    created_at, operation, measurement_type,
    left_value, left_unit, left_measurement_type,
    right_value, right_unit, right_measurement_type,
    target_unit, result_value, result_unit, result_measurement_type,
    scalar_result, success, error_message
) VALUES (
    TIMESTAMP '2026-03-12 10:00:00', 'COMPARE', 'LENGTH',
    1.0, 'FEET', 'LENGTH',
    12.0, 'INCHES', 'LENGTH',
    NULL, NULL, NULL, NULL,
    1.0, TRUE, NULL
);
