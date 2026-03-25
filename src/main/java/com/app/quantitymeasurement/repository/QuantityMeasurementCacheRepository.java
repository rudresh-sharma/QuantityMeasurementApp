package com.app.quantitymeasurement.repository;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.model.QuantityMeasurementEntity;

public class QuantityMeasurementCacheRepository implements IQuantityMeasurementRepository {

	private final List<QuantityMeasurementEntity> cache = new ArrayList<>();
	private final Path storagePath;

	public QuantityMeasurementCacheRepository() {
		this(Path.of("quantity-measurement-history.ser"));
	}

	public QuantityMeasurementCacheRepository(Path storagePath) {
		this.storagePath = storagePath;
		loadHistory();
	}

	@Override
	public synchronized void saveMeasurement(QuantityMeasurementEntity entity) {
		cache.add(entity);
		writeEntity(entity);
	}

	@Override
	public synchronized List<QuantityMeasurementEntity> getAllMeasurements() {
		return new ArrayList<>(cache);
	}

	@Override
	public synchronized List<QuantityMeasurementEntity> getMeasurementsByOperation(String operation) {
		String normalizedOperation = normalize(operation);
		return cache.stream()
				.filter(entity -> normalize(entity.getOperation()).equals(normalizedOperation))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public synchronized List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType) {
		String normalizedType = normalize(measurementType);
		return cache.stream()
				.filter(entity -> normalize(entity.getMeasurementType()).equals(normalizedType))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public synchronized long getTotalCount() {
		return cache.size();
	}

	@Override
	public synchronized void deleteAllMeasurements() {
		cache.clear();
		try {
			Files.deleteIfExists(storagePath);
		} catch (IOException exception) {
			throw new QuantityMeasurementException("Failed to delete measurement history", exception);
		}
	}

	private void loadHistory() {
		if (!Files.exists(storagePath)) {
			return;
		}

		FileInputStream fileInputStream = null;
		ObjectInputStream inputStream = null;
		try {
			fileInputStream = new FileInputStream(storagePath.toFile());
			inputStream = new ObjectInputStream(fileInputStream);
			while (true) {
				Object record = inputStream.readObject();
				if (record instanceof QuantityMeasurementEntity) {
					cache.add((QuantityMeasurementEntity) record);
				}
			}
		} catch (EOFException ignored) {
		} catch (IOException | ClassNotFoundException exception) {
			closeQuietly(inputStream);
			closeQuietly(fileInputStream);
			recoverCorruptedHistory(exception);
		} finally {
			closeQuietly(inputStream);
			closeQuietly(fileInputStream);
		}
	}

	private void recoverCorruptedHistory(Exception cause) {
		Path corruptedBackup = storagePath.resolveSibling(storagePath.getFileName() + ".corrupted");
		try {
			Files.move(storagePath, corruptedBackup, StandardCopyOption.REPLACE_EXISTING);
			cache.clear();
		} catch (IOException backupException) {
			backupException.addSuppressed(cause);
			throw new QuantityMeasurementException("Failed to load measurement history", backupException);
		}
	}

	private void writeEntity(QuantityMeasurementEntity entity) {
		boolean append = Files.exists(storagePath) && storagePath.toFile().length() > 0;

		try (ObjectOutputStream outputStream = append
				? new AppendableObjectOutputStream(new FileOutputStream(storagePath.toFile(), true))
				: new ObjectOutputStream(new FileOutputStream(storagePath.toFile(), true))) {
			outputStream.writeObject(entity);
		} catch (IOException exception) {
			throw new QuantityMeasurementException("Failed to save measurement history", exception);
		}
	}

	private String normalize(String value) {
		return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
	}

	private void closeQuietly(ObjectInputStream inputStream) {
		if (inputStream == null) {
			return;
		}
		try {
			inputStream.close();
		} catch (IOException ignored) {
		}
	}

	private void closeQuietly(FileInputStream inputStream) {
		if (inputStream == null) {
			return;
		}
		try {
			inputStream.close();
		} catch (IOException ignored) {
		}
	}
}
