package com.paytm.sdechallenge.collections.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.paytm.sdechallenge.collections.MovingAverageCalculator;

/**
 * The Class MovingAverageCalculatorImpl, which implements MovingAverageCalculator interface.
 * 
 * Use of BigDecimal
 * 
 * 
 *  This class stores the elements using BigDecimal type. BigDecimal was chosen as it is the
 *  best choice in Java to perform arithmetic operations that require exact answers. Since most
 *  real world applications of Moving Average like financial transactions use inputs with
 *  decimal points and expect precise outputs, data types like double, float etc cannot be
 *  considered to store data and calculate results, as decimal operations with them yield
 *  unpredictable results. 
 * 
 * 
 * Storing Data
 * 
 * 
 *  elements - The class stores all elements inserted as a list of BigDecimals.
 *  
 *  window - The window size to calculate the Moving Average. Must be initialized using the available constructors
 *  and can be updated using the setter method 
 *  
 *  windowElements - A FIFO queue is used to hold all elements that fall inside the window (last N elements)
 *  for moving average calculation. This queue is updated whenever an element is inserted, by removing the first
 *  element and adding the new element to the last. This ensures that the correct elements are present inside the
 *  window after each insertion.
 *  
 *  windowSum - The sum of all elements inside the window is calculated after every insertion and stored, so that
 *  the moving average calculation can be done without any iteration.
 * 
 * 
 * Scale and RoundingMode
 * 
 * 
 * 	Scale and Rounding mode are two options from the BigDecimal class used to ensure accuracy of the results.
 * 
 *  scale - This defines the number of decimal points required in the moving average value returned. Rounding off
 *  is done for values with number of decimal points exceeding scale. Trailing zeroes are added to values when number
 *  of decimal points are less than scale. The default scale is set to 5, but can be set using the available constructors
 *  or the setter method. 
 *  NOTE: Scale is not applied to elements on addition. The scale is applied only to the result that is returned
 *  from the moving average calculation.
 *  eg : val1 = 2.266, val2 = 2.266, scale = 2, window = 2
 *  	 The scale is applied, when the moving average is calculated
 *       ie, scale of 2 is applied on the result((2.266 + 2.266) / 2) 2.266 to return the result 2.27
 *       
 *  roundingMode - This defines the mode of rounding to be chosen to limit the decimal points to the scale.
 *  The default mode chosen is HALF_DOWN. Other rounding modes can be set using the available constructors
 *  or the setter method. 
 *  https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/math/RoundingMode.html
 *  
 *  
 * Moving Average Calculation
 *  
 * 
 *  When each element is added, the windowSum is calculated by adding the newly added element and popping the first
 *  element in the windowElements.
 *  When the moving average method is called, the windowSum is calculated by dividing windowElements with the
 *  window size and then setting scale
 *  When the window size is updated, a recalculation is done to update windowElements and windowSum
 *   	 
 * 
 * Complexities
 * 
 *  Time Complexity:
 *  The add, movingAverage, get, getAll, size, isEmpty methods have time complexity O(1)
 *  The setWindow method, used to update the window size has a complexity of O(n), where n is the window size
 *  
 *  Space Complexity:
 *  The add, movingAverage, setWindow methods have complexity O(n), where n is the window size
 *  The get, getAll, size, isEmpty methods have complexity O(1)
 *   
 */
public class MovingAverageCalculatorImpl implements MovingAverageCalculator {
	
	/** The list storing all elements. */
	private List<BigDecimal> elements = new ArrayList<>();
	
	/** The window size to calculate moving average. */
	private int window;
	
	/** A FIFO queue storing all elements currently in the window for moving average calculation. */
	private Queue<BigDecimal> windowElements = new LinkedList<>();

	/** The scale, which is the number of decimal places the moving average will have. */
	private int scale = 5;
	
	/** The rounding mode to be used to round moving average to the exact decimal points required.
	 *	https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/math/RoundingMode.html
	 */
	private RoundingMode roundingMode = RoundingMode.HALF_DOWN;
	
	/** The sum of elements in the window at any point. */
	private BigDecimal windowSum = BigDecimal.ZERO;
	
	/** Error message when Window Size is Invalid. */
	private static String WINDOW_INVALID = "Window size is invalid : %s";
	
	/** Error message when Scale is Invalid. */
	private static String SCALE_INVALID = "Scale is invalid : %s";
	
	/** Error message when Rounding Mode is null. */
	private static String ROUNDING_MODE_NULL = "Rounding Mode is null";
	
	/** Error message when Rounding Mode is UNESCESSARY. 
	 *	Rounding Mode cannot be UNESCESSARY as division with BigDecimals always require a RoundingMode
	 */
	private static String ROUNDING_MODE_CANT_BE_UNESCESSARY = "Rounding Mode cant be UNESCESSARY";

	/**
	 * Instantiates a new moving average calculator impl.
	 *
	 * @param window the window size
	 */
	public MovingAverageCalculatorImpl(int window) {
		validateWindow(window);		
		this.window = window;
	}
		
	/**
	 * Instantiates a new moving average calculator impl.
	 *
	 * @param window the window size
	 * @param scale the scale
	 */
	public MovingAverageCalculatorImpl(int window, int scale) {
		validateWindow(window);
		validateScale(scale);

		this.window = window;	
		this.scale = scale;
	}
	
	/**
	 * Instantiates a new moving average calculator impl.
	 *
	 * @param window the window
	 * @param roundingMode the rounding mode
	 */
	public MovingAverageCalculatorImpl(int window, RoundingMode roundingMode) {
		validateWindow(window);
		validateRoundingMode(roundingMode);
		
		this.window = window;
		this.roundingMode = roundingMode;
	}

	/**
	 * Instantiates a new moving average calculator impl.
	 *
	 * @param window the window
	 * @param scale the scale
	 * @param roundingMode the rounding mode
	 */
	public MovingAverageCalculatorImpl(int window, int scale, RoundingMode roundingMode) {
		validateWindow(window);
		validateScale(scale);
		validateRoundingMode(roundingMode);
		
		this.window = window;
		
		this.scale = scale;
		this.roundingMode = roundingMode;
	}

	/**
	 * Adds the new element.
	 *
	 * @param element the BigDecimal element
	 */
	@Override
	public void add(BigDecimal element) {
		//Subtraction is required only if window size >= number of elements currently inserted
		if(elements.size() >= window) {
			windowSum = windowSum.subtract(windowElements.poll());
		}
		elements.add(element);
		windowElements.add(element);
		windowSum = windowSum.add(element);
	}

	/**
	 * Calculates the Moving average.
	 *
	 * @return the big decimal Moving Average, with decimal places equal to the scale set
	 */
	@Override
	public BigDecimal movingAverage() {
		BigDecimal movingAverage;
		if(!isEmpty()) {
			//If number of elements less that window size, MA = sum/num of elements
			int size = elements.size() >= window ? window : elements.size();
			movingAverage = windowSum.divide(new BigDecimal(size), scale, roundingMode);
		} else {
			//Return 0, which is the initial value of windowSum
			movingAverage = windowSum.setScale(scale);
		}
		return movingAverage;
	}

	/**
	 * Size method.
	 *
	 * @return the int size of elements
	 */
	@Override
	public int size() {
		return elements.size();
	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	@Override
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	/**
	 * Gets the element at a given index.
	 *
	 * @param index the index
	 * @return the big decimal value
	 */
	@Override
	public BigDecimal get(int index) {
		if (index < 0 || index >= elements.size()) {
			throw new IndexOutOfBoundsException();
		}
		return elements.get(index);
	}

	/**
	 * Gets the list of elements.
	 *
	 * @return the list of bigdecimal elements
	 */
	@Override
	public List<BigDecimal> getAll() {
		return elements;
	}	
	
	/**
	 * Gets the scale.
	 *
	 * @return the scale
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * Sets the scale.
	 *
	 * @param scale the new scale
	 */
	public void setScale(int scale) {
		this.scale = scale;
	}

	/**
	 * Gets the rounding mode.
	 *
	 * @return the rounding mode
	 */
	public RoundingMode getRoundingMode() {
		return roundingMode;
	}

	/**
	 * Sets the rounding mode.
	 *
	 * @param roundingMode the new rounding mode
	 */
	public void setRoundingMode(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
	}
	
	/**
	 * Gets the window size.
	 *
	 * @return the window size
	 */
	public int getWindow() {
		return window;
	}
	
	/**
	 * Sets the window.
	 *
	 * @param window the new window
	 */
	public void setWindow(int window) {
		validateWindow(window);
		//resets window, windowElements and windowSum
		this.window = window;
		windowElements.clear();
		windowSum = BigDecimal.ZERO;
		//if window greater than number of elements, add all elements to windowElements 
		int startIndex = window < elements.size() ? elements.size() - window : 0;
		for(int i = startIndex; i < elements.size(); ++i) {
			windowSum = windowSum.add(elements.get(i));
		}
	}
	
	/**
	 * Validate window.
	 *
	 * @param window the window size
	 */
	private void validateWindow(int window) {
		if(window <= 0) {
			throw new IllegalArgumentException(String.format(WINDOW_INVALID, window));
		}
	}
	
	/**
	 * Validate scale.
	 *
	 * @param scale the scale
	 */
	private void validateScale(int scale) {
		if(scale < 0) {
			throw new IllegalArgumentException(String.format(SCALE_INVALID, scale));
		}
	}

	/**
	 * Validate rounding mode.
	 *
	 * @param roundingMode the rounding mode
	 */
	private void validateRoundingMode(RoundingMode roundingMode) {
		if(null == roundingMode) {
			throw new IllegalArgumentException(ROUNDING_MODE_NULL);
		}
		if(roundingMode == RoundingMode.UNNECESSARY) {
			throw new IllegalArgumentException(String.format(ROUNDING_MODE_CANT_BE_UNESCESSARY, roundingMode.toString()));
		}
	}

}
