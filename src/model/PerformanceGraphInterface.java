package model;

import java.util.Map;

/**
 * Interface that is used to perform preprocessing operations on portfolio objects to.
 * It is also package private that is accessible to only classes present in model package.
 */
public interface PerformanceGraphInterface {

  /**
   * This function is used to convert the duration between start and end date
   * into equally distributed time intervals.
   *
   * @param startDate date passed by user from where performance graph begins.
   * @param endDate   date passed by user till where performance graph is to be displayed.
   * @return a map of values where keys are the values to be displayed on X-axis in the graph.
   */
  Map<String, String> getTimeIntervalMap(String startDate, String endDate);

  /**
   * This function is used to convert values of valuation of a portfolio from double format
   * to string of asterisks. Number of asterisks depends on the valuation at that point in the graph
   * and is scaled accordingly in this function. The decision of using relative or absolute scale
   * is also taken in this function.
   *
   * @param map map containing time intervals for X-axis and their respective valuation values in
   *            double format.
   * @return a map of time intervals for X-axis and variable number of asterisks depending on
   */
  Map<String, String> graphScaling(Map<String, Double> map);
}
