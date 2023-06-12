package model;

import java.util.List;
import java.util.Map;

/**
 * interface used to persist a strategy of investment by the user. This would populate itself
 * everytime when the program is run.
 */
interface StrategyInterface {

  /**
   * gives list of portfolios with which the strategy is associated.
   *
   * @return List of portfolios in string format.
   */
  List<String> getPfList();

  /**
   * gives the total amount to be invested in the portfolio.
   *
   * @return teh value in double format.
   */
  double getTotalCost();

  /**
   * gives the end date of when the strategy would end.
   *
   * @return the date in yyyy-MM-dd format.
   */
  String getEndDate();

  /**
   * gives the duration between two intervals - in days.
   *
   * @return the duration of days in integer format.
   */
  int getPeriod();

  /**
   * gives the commission that need to be added with each transaction in that strategy.
   *
   * @return the commission in double format.
   */

  double getCommission();

  /**
   * gives a map of symbols and weights in the strategy.
   *
   * @return map of symbols and their weights in key value pair.
   */
  Map<String, Double> getWeightMap();

}
