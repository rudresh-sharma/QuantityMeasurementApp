package com.app.quantitymeasurement.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.app.quantitymeasurement.exception.QuantityMeasurementException;

public class ApplicationConfig {

	private final Properties properties = new Properties();

	public ApplicationConfig() {
		loadFromClasspath();
		overrideFromSystemProperties();
	}

	public String getRepositoryType() {
		return get("app.repository.type", "database");
	}

	public String getDatabaseUrl() {
		return get("app.database.url", "jdbc:h2:./build/quantitymeasurement;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
	}

	public String getDatabaseUsername() {
		return get("app.database.username", "sa");
	}

	public String getDatabasePassword() {
		return get("app.database.password", "");
	}

	public String getDatabaseDriver() {
		return get("app.database.driver", "org.h2.Driver");
	}

	public int getPoolSize() {
		return Integer.parseInt(get("app.database.pool.size", "4"));
	}

	private String get(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	private void loadFromClasspath() {
		try (InputStream stream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
			if (stream != null) {
				properties.load(stream);
			}
		} catch (IOException exception) {
			throw new QuantityMeasurementException("Failed to load application configuration", exception);
		}
	}

	private void overrideFromSystemProperties() {
		for (String key : System.getProperties().stringPropertyNames()) {
			if (key.startsWith("app.")) {
				properties.setProperty(key, System.getProperty(key));
			}
		}
	}
}
