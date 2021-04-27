package com.paytm.sdechallenge.collections;

import java.math.BigDecimal;
import java.util.List;

/**
 * The Interface MovingAverageCalculator.
 * 
 * This interface provides methods for storing data elements and calculating moving average of the last n
 * elements at any given point in time.
 * 
 * The data type BigDecimal is used to store elements and to calculate moving average.
 * BigDecimal was chosen as it is the best choice in Java to perform arithmetic operations
 * that require exact answers. Since most real world applications of Moving Average like financial
 * transactions use inputs with decimal points and expect precise outputs, data types like double,
 * float etc cannot be considered to store data and calculate results, as decimal operations with them
 * yield unpredictable results. 
 * 
 */
public interface MovingAverageCalculator {
	
	/**
	 * Adds the new element.
	 *
	 * @param element the BigDecimal element
	 */
	public void add(BigDecimal element);
	
	/**
	 * Calculates the Moving average.
	 *
	 * @return the big decimal moving average
	 */
	public BigDecimal movingAverage();
		
	/**
	 * Size.
	 *
	 * @return the int number of elements
	 */
	public int size();
	
	/**
	 * Checks if empty.
	 *
	 * @return true, if empty
	 */
	public boolean isEmpty();
	
	/**
	 * Gets the element at the given index.
	 *
	 * @param index the index
	 * @return the big decimal value
	 */
	public BigDecimal get(int index);
		
	/**
	 * Gets the list of all elements.
	 *
	 * @return the List containing all elements
	 */
	public List<BigDecimal> getAll();	
}
