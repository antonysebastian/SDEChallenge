package com.paytm.SDEChallenge;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import com.paytm.sdechallenge.collections.impl.MovingAverageCalculatorImpl;

import junit.framework.TestCase;

/**
 * Unit tests for MovingAverageCalculatorImplTest.
 */
public class MovingAverageCalculatorImplTest extends TestCase {

	public void test_Constructor_With_WindowSize() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(2);
		assertNotNull(maCalculator);
		assertTrue(maCalculator.getAll().isEmpty());
		assertEquals(5,  maCalculator.getScale());
		assertEquals(RoundingMode.HALF_DOWN,  maCalculator.getRoundingMode());
		assertEquals(2, maCalculator.getWindow());
		assertEquals(new BigDecimal("0.00000"), maCalculator.movingAverage());
	}
	
	public void test_Constructor_With_WindowSizeInvalid() {
		try {
			new MovingAverageCalculatorImpl(-1);
			fail("Expected exception when window size less than or equal to 0");
		} catch (IllegalArgumentException e) {
			//pass
		}
	}
	
	public void testConstructor_With_Window_Scale_And_RoundingMode() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(5, 2, RoundingMode.HALF_UP);
		assertNotNull(maCalculator);
		assertTrue(maCalculator.getAll().isEmpty());
		assertEquals(2,  maCalculator.getScale());
		assertEquals(RoundingMode.HALF_UP,  maCalculator.getRoundingMode());
		assertEquals(5, maCalculator.getWindow());
		assertEquals(new BigDecimal("0.00"), maCalculator.movingAverage());
	}
	
	public void testConstructor_With_InvalidWindow_Scale_And_RoundingMode() {
		try {
			new MovingAverageCalculatorImpl(0, 2, RoundingMode.HALF_UP);
			fail("Expected exception when window size less than or equal to 0");
		} catch (IllegalArgumentException e) {
			//pass
		}
	}
	
	public void testConstructor_With_Window_Invalid_Scale_And_RoundingMode() {
		try {
			new MovingAverageCalculatorImpl(3, -1, RoundingMode.HALF_UP);
			fail("Expected exception when scale less than 0");
		} catch (IllegalArgumentException e) {
			//pass
		}
	}
	
	public void testConstructor_With_Window_Scale_And_Null_RoundingMode() {
		try {
			new MovingAverageCalculatorImpl(3, 2, null);
			fail("Expected exception when rounding mode is null");
		} catch (IllegalArgumentException e) {
			//pass
		}
	}
	
	public void testConstructor_With_Window_Scale_And_RoundingMode_Unescessary() {
		try {
			new MovingAverageCalculatorImpl(3, 2, RoundingMode.UNNECESSARY);
			fail("Expected exception when rounding mode is RoundingMode.UNNECESSARY");
		} catch (IllegalArgumentException e) {
			//pass
		}
	}
	
	public void testConstructor_With_Window_And_Scale() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(5, 2);
		assertNotNull(maCalculator);
		assertTrue(maCalculator.getAll().isEmpty());
		assertEquals(2,  maCalculator.getScale());
		assertEquals(RoundingMode.HALF_DOWN,  maCalculator.getRoundingMode());
		assertEquals(5, maCalculator.getWindow());
		assertEquals(new BigDecimal("0.00"), maCalculator.movingAverage());
	}
	
	public void testConstructor_With_InvalidWindow_And_Scale() {
		try {
			new MovingAverageCalculatorImpl(0, 2);
			fail("Expected exception when window size less than or equal to 0");
		} catch (IllegalArgumentException e) {
			//pass
		}
	}
	
	public void testConstructor_With_Invalid_And_Invalid_Scale() {
		try {
			new MovingAverageCalculatorImpl(3, -1);
			fail("Expected exception when scale less than 0");
		} catch (IllegalArgumentException e) {
			//pass
		}
	}
	
	public void testConstructor_With_Window_And_RoundingMode() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(5, RoundingMode.HALF_UP);
		assertNotNull(maCalculator);
		assertTrue(maCalculator.getAll().isEmpty());
		assertEquals(5,  maCalculator.getScale());
		assertEquals(RoundingMode.HALF_UP,  maCalculator.getRoundingMode());
		assertEquals(5, maCalculator.getWindow());
		assertEquals(new BigDecimal("0.00000"), maCalculator.movingAverage());
	}

	public void testConstructor_With_Window_And_Null_RoundingMode() {
		try {
			new MovingAverageCalculatorImpl(3, null);
			fail("Expected exception when rounding mode is null");
		} catch (IllegalArgumentException e) {
			//pass
		}
	}
	
	public void testConstructor_With_Window_And_RoundingMode_Unescessary() {
		try {
			new MovingAverageCalculatorImpl(3, 2, RoundingMode.UNNECESSARY);
			fail("Expected exception when rounding mode is RoundingMode.UNNECESSARY");
		} catch (IllegalArgumentException e) {
			//pass
		}
	}

	public void test_Add_When_WindowSize_NotReached() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(5);
		maCalculator.add(new BigDecimal(10));
		assertEquals(1,  maCalculator.getAll().size());
		assertEquals(new BigDecimal("10"),  maCalculator.getAll().get(0));
		assertEquals(new BigDecimal("10.00000"), maCalculator.movingAverage());
	}
	
	public void test_Add() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(2);
		maCalculator.add(new BigDecimal(10));
		maCalculator.add(new BigDecimal(10));
		maCalculator.add(new BigDecimal(5));
		assertEquals(3,  maCalculator.getAll().size());
		assertEquals(new BigDecimal("5"),  maCalculator.getAll().get(2));
		assertEquals(new BigDecimal("7.50000"), maCalculator.movingAverage());
	}
	
	public void test_MovingAverage_WhenWindowSize_NotReached() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(5);
		maCalculator.add(new BigDecimal(5));
		assertEquals(new BigDecimal("5.00000"), maCalculator.movingAverage());
	}
	
	public void test_MovingAverage_WhenEmpty() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(5);
		assertEquals(new BigDecimal("0.00000"), maCalculator.movingAverage());
	}
	
	public void test_MovingAverage() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(2);
		maCalculator.add(new BigDecimal(5));
		maCalculator.add(new BigDecimal(10));
		assertEquals(new BigDecimal("7.50000"), maCalculator.movingAverage());
		maCalculator.add(new BigDecimal(2));
		assertEquals(new BigDecimal("6.00000"), maCalculator.movingAverage());
	}
	
	public void test_Deafult_Scale_And_Rounding() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(2);
		maCalculator.add(new BigDecimal("5.444449"));
		maCalculator.add(new BigDecimal("5.444449"));
		assertEquals(new BigDecimal("5.44445"), maCalculator.movingAverage());
	}
	
	public void test_Custom_Scale_And_Rounding() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(2, 2, RoundingMode.HALF_UP);
		maCalculator.add(new BigDecimal("5.445"));
		maCalculator.add(new BigDecimal("5.440"));
		assertEquals(new BigDecimal("5.44"), maCalculator.movingAverage());
	}
	
	public void test_Size() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(2);
		assertEquals(0,  maCalculator.size());
		maCalculator.add(new BigDecimal(10));
		assertEquals(1,  maCalculator.size());
	}
	
	public void test_IsEmpty() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(2);
		assertTrue(maCalculator.isEmpty());
		maCalculator.add(new BigDecimal(10));
		assertFalse(maCalculator.isEmpty());
	}
	
	public void test_Get_Throws_Exception_When_IndexInvalid() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(2);
		
		try {
			maCalculator.get(-1);
			fail("Exception expected when index less than 0");
		} catch (IndexOutOfBoundsException ex) {
			//pass
		}
		
		try {
			maCalculator.get(0);
			fail("Exception expected when index = count of elements");
		} catch (IndexOutOfBoundsException ex) {
			//pass
		}
		
		try {
			maCalculator.get(1);
			fail("Exception expected when index > count of elements");
		} catch (IndexOutOfBoundsException ex) {
			//pass
		}
	}
	
	public void test_Get() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(2);
		maCalculator.add(new BigDecimal(10));
		assertEquals(new BigDecimal(10),  maCalculator.get(0));
	}
	
	public void test_GetAll() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(2);
		maCalculator.add(new BigDecimal(10));
		maCalculator.add(new BigDecimal(5));
		assertEquals(2,  maCalculator.getAll().size());
		assertTrue(maCalculator.getAll() instanceof ArrayList<?>);
		assertEquals(new BigDecimal(10),  maCalculator.get(0));
		assertEquals(new BigDecimal(5),  maCalculator.get(1));
	}
	
	public void test_Get_Window() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(2);
		assertEquals(2, maCalculator.getWindow());
	}
	
	public void test_Set_New_Window_LessThan_CurrentSize() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(3);
		maCalculator.add(new BigDecimal(10));
		maCalculator.add(new BigDecimal(10));
		maCalculator.add(new BigDecimal(5));
		maCalculator.setWindow(2);
		assertEquals(2, maCalculator.getWindow());
		assertEquals(new BigDecimal("7.50000"), maCalculator.movingAverage());
	}
	
	public void test_Set_New_Window_GreaterThan_CurrentSize() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(2);
		maCalculator.add(new BigDecimal(0));
		maCalculator.add(new BigDecimal(10));
		maCalculator.add(new BigDecimal(5));
		maCalculator.setWindow(5);
		assertEquals(5, maCalculator.getWindow());
		assertEquals(new BigDecimal("5.00000"), maCalculator.movingAverage());
	}
	
	public void test_Get_RoundingMode() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(2, RoundingMode.CEILING);
		assertEquals(RoundingMode.CEILING, maCalculator.getRoundingMode());
	}
	
	public void test_Set_RoundingMode() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(2, RoundingMode.CEILING);
		maCalculator.setRoundingMode(RoundingMode.HALF_DOWN);
		assertEquals(RoundingMode.HALF_DOWN, maCalculator.getRoundingMode());
	}
	
	public void test_Get_Scale() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(2, 5);
		assertEquals(5, maCalculator.getScale());
	}
	
	public void test_Set_Scale() {
		MovingAverageCalculatorImpl maCalculator = new MovingAverageCalculatorImpl(2, 5);
		maCalculator.setScale(3);
		assertEquals(3, maCalculator.getScale());
	}
}
