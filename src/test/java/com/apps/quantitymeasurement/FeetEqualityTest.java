package com.apps.quantitymeasurement;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.apps.quantitymeasurement.Feet;


public class FeetEqualityTest {

	
	@Test
	void testFeetEquality_SameValue() {
		Feet f1 = new Feet(1.0);
		Feet f2 = new Feet(1.0);
		assertTrue(f1.equals(f2));
		
	}
	
	@Test
	void testFeetEquality_DifferenceValue() {
		Feet f1 = new Feet(1.0);
		Feet f2 = new Feet(2.0);
		assertFalse(f1.equals(f2));
		
	}
	
	@Test
	void testFeetEquality_NullComparison() {
		Feet f1 = new Feet(1.0);
		assertFalse(f1.equals(null));
	}
	
	@Test
	void testFeetEquality_DifferentClass() {
		Feet f1 = new Feet(1.0);
		String str = "1.0";
		assertFalse(f1.equals(str));
	}
	
	
	@Test
	void testFeetEquality_SameReference() {
		Feet f1 = new Feet(1.0);
		assertTrue(f1.equals(f1));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
