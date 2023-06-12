package controller;

import java.util.Map;

/**
 * Interface SwingControllerInterface represents all the operations performed by controller.
 * It delegates the operations to model and view.
 * Here the view is the Graphical user interface that is SwingView and the functions include the
 * respective functionalities required to do the same.
 */
public interface SwingControllerInterface {

  /**
   * authenticates credentials of the user. For this assignment we are dealing with just 1 user.
   *
   * @param user username is admin.
   * @param pwd  password is Asdf@1234.
   */
  void authenticateCredentialsView(String user, String pwd);

  /**
   * used to switch to the required screen depending upon the operation passed.
   * it also populates the portfolio list in the combo box.
   *
   * @param operation name of the panel in string format.
   */
  void displayPortfolios(String operation);

  /**
   * asks the model to get the composition of the portfolio that is list of symbols and their
   * respective quantities and asks the view to display it.
   *
   * @param pfNumber portfolio file name to be examined.
   * @param date     date in yyyy-MM-dd format.
   */
  void examinePortfolio(int pfNumber, String date);

  /**
   * asks the model to save the file passed by the admin as a portfolio file and asks the view
   * to display the status.
   *
   * @param path location of the file on the device in string format.
   */

  void savePortfolio(String path);

  /**
   * asks the model to calculate the cost basis of a given portfolio and asks view to display the
   * respective amount.
   *
   * @param pfNumber portfolio file name.
   * @param date     date in yyyy-MM-dd format.
   */

  void getCostBasis(int pfNumber, String date);

  /**
   * asks the model to calculate the total valuation of a given portfolio at a specific date
   * and asks view to display the respective amount.
   *
   * @param pfNumber portfolio file name.
   * @param date     date in yyyy-MM-dd format.
   */
  void getPfValue(int pfNumber, String date);

  /**
   * asks the model to get the time period and valuations in a map and then passes it to the view
   * to populate the chart and then display it.
   *
   * @param pfNumber  portfolio file name.
   * @param startDate start date in yyyy-MM-dd format.
   * @param endDate   end date in yyyy-MM-dd format.
   */
  void performanceChart(int pfNumber, String startDate, String endDate);

  /**
   * asks the model to either create a new portfolio or add values to existing portfolios depending
   * on the parameter passed as argument and asks view to display the file name.
   *
   * @param stockMap   map that has symbol list as keys and quantities as values.
   * @param pfNumber   portfolio file name if pfNumber > 0 else if pfNumber=0, then portfolio
   *                   is created.
   * @param commission commission cost that needs to be added in each transaction.
   * @param date       date in yyyy-MM-dd format.
   */

  void buyStocks(Map<String, Double> stockMap, int pfNumber, String commission, String date);

  /**
   * asks the model to either create a new portfolio or add values to existing portfolios to
   * implement dollar cost averaging strategy depending on the parameter passed as argument
   * and asks the view to display the file name.
   *
   * @param weightMap  map of symbols and weights as key value pairs.
   * @param totalCost  total cost that needs to be invested in the portfolio.
   * @param pfNumber   portfolio file name.
   * @param commission commission cost that needs to be added in each transaction.
   * @param startDate  start date of the transaction in yyyy-MM-dd format.
   * @param endDate    end date of the transaction in yyyy-MM-dd format.
   * @param period     interval of payments in days.
   */
  void dollarCostAveraging(Map<String, Double> weightMap, String totalCost, int pfNumber,
                           String commission, String startDate, String endDate, int period);

  /**
   * asks the model to either create a new portfolio or add  values to existing portfolios to
   * implement passive investment where each stock symbol is given a particular weight as per the
   * user and total cost is divided among these symbols according to the weight distribution
   * and asks the view to display the file name.
   *
   * @param weightMap  map of symbols and weights as key value pairs.
   * @param totalCost  total cost that needs to be invested in the portfolio.
   * @param pfNumber   portfolio file name.
   * @param commission commission cost that needs to be added in each transaction.
   * @param startDate  start date of the transaction in yyyy-MM-dd format.
   */
  void createWeightedPortfolio(Map<String, Double> weightMap, String totalCost, int pfNumber,
                               String commission, String startDate);

  /**
   * asks the user to sell stocks in existing portfolios depending
   * on the pfNumber passed as argument.
   *
   * @param stockMap   map that has symbol list as keys and quantities as values.
   * @param pfNumber   portfolio file name.
   * @param commission commission cost that needs to be added in each transaction.
   * @param date       date in yyyy-MM-dd format.
   */
  void sellStocks(Map<String, Double> stockMap, int pfNumber, String commission, String date);
}
