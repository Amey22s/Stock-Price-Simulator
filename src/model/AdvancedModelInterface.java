package model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Interface for flexible model interface that holds functionalities related to
 * flexible stock model.
 */

public interface AdvancedModelInterface {

  /**
   * Performs operations necessary to add buy or sell transactions in flexible portfolio.
   * Is also used to create a completely new portfolio and then add bought stocks in it.
   * Function to add symbol and quantity of stocks into portfolio object.
   * Internally calls a function that creates a file under flex_portfolio folder for that username.
   *
   * @param stockMap   a hash map to store symbols and quantity as string and integer respectively.
   * @param pfNumber   portfolio number selected by user among the list of portfolios.
   * @param commission commission (in %) to be incurred on each transaction.
   * @param date       specified date in yyyy-mm-dd.
   * @param op         operation which was performed (buy / sell).
   * @return filename the portfolio in string format.
   */
  String transact(Map<String, Double> stockMap, int pfNumber, double commission,
                  String date, char op);

  /**
   * returns costBasis of a portfolio at a specified date.
   *
   * @param pfNumber portfolio number selected by user among the list of portfolios.
   * @param date     specified date in yyyy-mm-dd.
   * @return amount invested in the given portfolio by a specified date.
   */
  double calculateCostBasis(int pfNumber, String date);

  /**
   * returns valuation of a portfolio at a specified date.
   *
   * @param pfNumber portfolio number selected by user among the list of portfolios.
   * @param date     specified date in yyyy-mm-dd.
   * @return total valuation of a given portfolio at a specified date.
   */
  double calculateValuation(int pfNumber, String date);

  /**
   * gets the portfolio among the list of portfolio options based on the pfNumber passed.
   * displays the composition of a given portfolio at specified date.
   *
   * @param pfNumber portfolio number selected by user among the list of portfolios.
   * @param date     specified date in yyyy-mm-dd.
   * @return an object of PortfolioInterface.
   */
  PortfolioInterface examineFlexPortfolio(int pfNumber, String date) throws IOException;

  /**
   * plots a performance graph for a given portfolio between the two specified dates.
   * displays the composition of a given portfolio at specified date.
   *
   * @param pfNumber  portfolio number selected by user among the list of portfolios.
   * @param startDate it is the start date for the performance graph in yyyy-mm-dd format.
   * @param endDate   it is the end date for the performance graph in yyyy-mm-dd format.
   * @return map of time period and its valuation.
   */
  Map<String, String> getPerformance(String startDate, String endDate, int pfNumber)
          throws IOException;

  /**
   * Function used to get a custom portfolio file from the user and parse it to convert it
   * to a portfolio in application if valid.
   *
   * @param sourceFile path of the custom file given by the user.
   * @return filename of the portfolio file created in application.
   */
  String saveFlexPortfolioFile(String sourceFile);


  /**
   * authenticates the credentials of that particular user. Here we have considered just a single
   * user.Username is "admin" and password is "Asdf@1234".
   *
   * @param user name of the user i.e. admin.
   * @param pwd  password set by admin ie Asdf@1234.
   * @return true if the credentials matches as given by user else false.
   */
  boolean authenticateCredentials(String user, String pwd);

  /**
   * returns the username currently logged in.
   *
   * @return username in string format.
   */
  String getUser();

  /**
   * returns the password of the user currently logged in.
   *
   * @return password in string format.
   */
  String getPwd();


  /**
   * gets the list of portfolio files created by the user.
   *
   * @return the list of portfolio files.
   */

  List<String> getPortFolioList();

  /**
   * retrieves tickers from the list of tickers present in the csv file.
   *
   * @return list of valid tickers.
   */
  List<String> getTickerList() throws IOException;

  /**
   * create a new portfolio or add values to existing portfolios to implement dollar cost averaging
   * strategy depending on the parameter passed as argument and asks the view to display
   * the file name.
   *
   * @param weightMap  map of stock symbol and its corresponding weight in that strategy.
   * @param totalCost  total amount to be invested in each transaction in that strategy.
   * @param pfNumber   portfolio number selected by user among the list of portfolios.
   * @param commission commission (in %) to be incurred on each transaction.
   * @param startDate  it is the start date for the strategy in yyyy-mm-dd format.
   * @param endDate    it is the end date for the strategy in yyyy-mm-dd format.
   * @param period     the interval after which transaction should recur in that strategy.
   * @return name of the new portfolio file created or name of existing file which was updated.
   */

  String createCostAveragePortfolio(Map<String, Double> weightMap, double totalCost, int pfNumber,
                                    double commission, String startDate,
                                    String endDate, int period, char op);


  /**
   * creates a new portfolio or adds values to existing portfolio to implement passive investment
   * where each stock symbol is given a particular weight as per the user input and total cost
   * is divided among these symbols according to the weight distribution asks the view to display
   * the file name.
   *
   * @param weightMap  map of stock symbol and its corresponding weight in that investment.
   * @param totalCost  total amount to be invested in that investment.
   * @param pfNumber   portfolio number selected by user among the list of portfolios.
   * @param commission commission (in %) to be incurred.
   * @param date       it is the date for the investment to be made in yyyy-mm-dd format.
   * @return name of the new portfolio file created or name of existing file which was updated.
   */
  String createWeightedPortfolio(Map<String, Double> weightMap, double totalCost, int pfNumber,
                                 double commission, String date);
}
