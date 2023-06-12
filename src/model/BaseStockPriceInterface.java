package model;

import java.util.Properties;

/**
 * This interface represents all the operations related to price of a stock.
 * It is also package private that is accessible to only classes present in model package.
 */


interface BaseStockPriceInterface {
  /**
   * retrieves the stock price for the current date by calling API.
   *
   * @param symbol     ticker symbol.
   * @param currDate   current date.
   * @param properties config properties to retrieve the api key.
   * @return the current price in double format.
   */
  Double getStockPriceByApi(String symbol, String currDate, Properties properties);

  /**
   * retrieves the stock price for a particular date by looking up the cache that is stored in
   * cache object of class Cache.
   *
   * @param symbol     ticker symbol.
   * @param inputDate  specified date as entered by the user.
   * @param properties config properties to retrieve Stock_Database.csv and Stock_Ticker.csv files.
   * @return the price of the stock at that particular date in double format.
   */
  Double getStockPrice(String symbol, String inputDate, Properties properties);

  /**
   * retrieves the stock price for the last working day of the month
   * by calling API for monthly price of stock.
   *
   * @param symbol     ticker symbol.
   * @param currDate   current date.
   * @param properties config properties to retrieve the api key.
   * @return the current price in double format.
   */
  Double getStockPriceByApiMonthly(String symbol, String currDate, Properties properties);

  /**
   * gets the last working day of the month.
   *
   * @param symbol     ticker symbol.
   * @param currDate   current date.
   * @param properties config properties to retrieve the api key.
   * @return the date in string format.
   */
  String getMonthEndDateByApi(String symbol, String currDate, Properties properties);

}
