package model;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * An interface to help perform high level investment strategies on flexible portfolios.
 */

public interface CostAveragingInterface {

  /**
   * used to create a stock map of stock symbol and quantity of that stock to be bought using
   * weights of respective stocks and total amount to be invested given by the user.
   *
   * @param weightMap  map of stock symbol and its corresponding weight in that investment.
   * @param totalCost  total amount to be invested in that investment.
   * @param commission commission (in %) to be incurred.
   * @param date       it is the date for the investment to be made in yyyy-mm-dd format.
   * @param properties config properties to retrieve info needed to perform operation.
   * @return a map containing symbol and its respective quantity to be bought in this investment.
   */
  Map<String, Double> getStockMap(Map<String, Double> weightMap, double totalCost,
                                  double commission, String date, Properties properties);

  /**
   * used to get a list of dates when the investment is to be as per the strategy given by the user.
   *
   * @param startDate it is the start date for the strategy in yyyy-mm-dd format.
   * @param endDate   it is the end date for the strategy in yyyy-mm-dd format.
   * @param period    the interval after which transaction should recur in that strategy.
   * @return a list of dates when the strategic investment is to be made.
   */
  List<String> getDates(String startDate, String endDate, int period);

  /**
   * gives the list of all strategy file names.
   *
   * @return a list of files in string format.
   */
  List<String> getStrategyList();

  /**
   * function to save a particular strategy as defined by the user.
   *
   * @param weightMap  map of stock symbol and its corresponding weight in that investment.
   * @param totalCost  total amount to be invested in that investment.
   * @param pfFile     name of the pf file to which strategy is to be associated.
   * @param commission commission (in %) to be incurred.
   * @param endDate    it is the end date for the strategy in yyyy-mm-dd format.
   * @param period     the interval after which transaction should recur in that strategy.
   * @return name of the strategy file that gets created.
   */
  String persistStrategy(Map<String, Double> weightMap, double totalCost, String pfFile,
                         double commission, String endDate, int period);


}
