package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * class to implement StrategyInterface and its functionalities. Its object holds all the
 * details associated with a strategy.
 */
class Strategy implements StrategyInterface {

  private final double totalCost;
  private final String endDate;
  private final int period;
  private final double commission;
  private final Map<String, Double> weightMap;
  private final List<String> pfList;

  /**
   * default constructor that initialises all the fields of this class namely totalCost,
   * endDate, period, commission, weightMap and pfList.
   */
  Strategy() {
    this.totalCost = 0;
    this.endDate = "";
    this.period = 0;
    this.commission = 0;
    this.weightMap = new HashMap<>();
    this.pfList = new ArrayList<>();
  }

  /**
   * parameterized constructor that initialises all the fields of this class namely totalCost,
   * endDate, period, commission, weightMap and pfList with the values passed as argument.
   */
  Strategy(Map<String, Double> weightMap, String endDate, double totalCost,
           double commission, int period) {

    this.totalCost = totalCost;
    this.endDate = endDate;
    this.period = period;
    this.commission = commission;
    this.weightMap = weightMap;
    this.pfList = new ArrayList<>();
  }

  @Override
  public List<String> getPfList() {
    return pfList;
  }

  @Override
  public double getTotalCost() {
    return totalCost;
  }

  @Override
  public String getEndDate() {
    return endDate;
  }

  @Override
  public int getPeriod() {
    return period;
  }

  @Override
  public double getCommission() {
    return commission;
  }

  @Override
  public Map<String, Double> getWeightMap() {
    return weightMap;
  }

}
