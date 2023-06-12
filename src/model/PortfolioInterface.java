package model;

import java.util.List;

/**
 * Portfolio interface to make portfolio object secure.
 * Getters are public so any class can fetch the values of Portfolio object.
 */
public interface PortfolioInterface {

  /**
   * returns the ticker symbols.
   *
   * @return list of all the ticker symbols in a portfolio.
   */
  List<String> getSymbol();

  /**
   * returns the quantity of each ticker symbol.
   *
   * @return list of quantities of all ticker symbols in a portfolio.
   */
  List<Double> getQuantity();

  /**
   * returns the price of each ticker symbol.
   *
   * @return list of prices of all ticker symbols in a portfolio.
   */
  List<Double> getPrice();

  /**
   * returns the number of unique stocks in a portfolio.
   *
   * @return number of different companies in a portfolio.
   */
  int getSize();
}
