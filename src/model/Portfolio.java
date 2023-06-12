package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Independent class to store symbol, quantity and price of a stock.
 * Setters of this class are package private and are only available to
 * model package.
 */
class Portfolio implements PortfolioInterface {

  private final List<String> symbol;
  private final List<Double> quantity;
  private List<Double> price;
  private String date;
  private char operation;

  /**
   * Non Parameterized constructor to initialize symbol, quantity and price variables.
   */
  Portfolio() {
    this.symbol = new ArrayList<>();
    this.quantity = new ArrayList<>();
    this.price = new ArrayList<>();
  }

  /**
   * Parameterized constructor to initialize symbol, quantity and price variables.
   */
  Portfolio(List<String> symbol, List<Double> quantity, List<Double> price) {
    this.symbol = symbol;
    this.quantity = quantity;
    this.price = price;
  }


  /**
   * returns a list of symbols in the list.
   */
  public List<String> getSymbol() {
    return symbol;
  }


  /**
   * returns a list of shares quantity in the list.
   */
  public List<Double> getQuantity() {
    return quantity;
  }

  /**
   * returns a list of prices of shares in the list.
   */
  public List<Double> getPrice() {
    return price;
  }

  /**
   * sets the price of stocks.
   */
  void setPrice(List<Double> price) {
    this.price = price;
  }

  /**
   * returns the length of symbol list.
   */
  public int getSize() {
    return symbol.size();
  }

  /**
   * returns the date in string format.
   */
  String getDate() {
    return date;
  }

  /**
   * sets the date passed as a string.
   *
   * @param date in string format.
   */
  void setDate(String date) {
    this.date = date;
  }

  /**
   * returns the character for buy or sell ie either 'b' or 's'.
   *
   * @return char value.
   */
  char getOperation() {
    return operation;
  }

  /**
   * sets the operation to either 'b' that is buy or 's' that is sell.
   *
   * @param operation b or s in char.
   */
  void setOperation(char operation) {
    this.operation = operation;
  }
}
