package com.app.quantitymeasurement.repository;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class AppendableObjectOutputStream extends ObjectOutputStream {

	public AppendableObjectOutputStream(OutputStream outputStream) throws IOException {
		super(outputStream);
	}

	@Override
	protected void writeStreamHeader() throws IOException {
		reset();
	}
}
