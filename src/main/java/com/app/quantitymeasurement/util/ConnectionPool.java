package com.app.quantitymeasurement.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.app.quantitymeasurement.exception.DatabaseException;

public class ConnectionPool {

	private final BlockingQueue<Connection> availableConnections;
	private final List<Connection> managedConnections = new ArrayList<>();
	private final AtomicInteger activeConnections = new AtomicInteger();
	private final int poolSize;

	public ConnectionPool(ApplicationConfig config) {
		try {
			Class.forName(config.getDatabaseDriver());
		} catch (ClassNotFoundException exception) {
			throw new DatabaseException("Failed to load database driver", exception);
		}

		this.poolSize = config.getPoolSize();
		this.availableConnections = new LinkedBlockingQueue<>(poolSize);

		for (int index = 0; index < poolSize; index++) {
			Connection connection = createPhysicalConnection(config);
			managedConnections.add(connection);
			availableConnections.offer(connection);
		}
	}

	public Connection getConnection() {
		Connection physicalConnection = availableConnections.poll();
		if (physicalConnection == null) {
			throw new DatabaseException("No database connections are currently available");
		}

		activeConnections.incrementAndGet();
		return createPooledConnection(physicalConnection);
	}

	public void shutdown() {
		for (Connection connection : managedConnections) {
			try {
				connection.close();
			} catch (SQLException exception) {
				throw new DatabaseException("Failed to close database connection", exception);
			}
		}
		managedConnections.clear();
		availableConnections.clear();
	}

	public int getActiveCount() {
		return activeConnections.get();
	}

	public int getIdleCount() {
		return availableConnections.size();
	}

	public int getTotalCount() {
		return poolSize;
	}

	private Connection createPhysicalConnection(ApplicationConfig config) {
		try {
			return DriverManager.getConnection(config.getDatabaseUrl(), config.getDatabaseUsername(),
					config.getDatabasePassword());
		} catch (SQLException exception) {
			throw new DatabaseException("Failed to create database connection", exception);
		}
	}

	private Connection createPooledConnection(Connection physicalConnection) {
		InvocationHandler handler = new PooledConnectionHandler(physicalConnection);
		return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class<?>[] { Connection.class },
				handler);
	}

	private final class PooledConnectionHandler implements InvocationHandler {
		private final Connection delegate;
		private boolean returnedToPool;

		private PooledConnectionHandler(Connection delegate) {
			this.delegate = delegate;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if ("close".equals(method.getName())) {
				if (!returnedToPool) {
					returnConnection(delegate);
					returnedToPool = true;
				}
				return null;
			}

			if ("isClosed".equals(method.getName())) {
				return returnedToPool || delegate.isClosed();
			}

			return method.invoke(delegate, args);
		}
	}

	private void returnConnection(Connection connection) {
		try {
			if (!connection.getAutoCommit()) {
				connection.setAutoCommit(true);
			}
		} catch (SQLException exception) {
			throw new DatabaseException("Failed to reset database connection state", exception);
		} finally {
			activeConnections.decrementAndGet();
			availableConnections.offer(connection);
		}
	}
}
