package model;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * Interface BaseModelInterface represents all the operations performed by model on a portfolio.
 * It implements all the functionalities needed for this application.
 */
public interface BaseModelInterface {

  /**
   * returns portfolio at a specified date.
   *
   * @param pfNumber portfolio number selected by user among the list of portfolios.
   * @param date     specified date in yyyy-mm-dd.
   * @return an object of type Portfolio Interface.
   * @throws IOException when there is an exception thrown while reading or writing files.
   */
  PortfolioInterface getPortFolioAtDate(int pfNumber, String date) throws IOException;

  /**
   * function to add symbol and quantity of stocks into portfolio object.
   * internally calls a function that creates a file under res folder for that username.
   *
   * @param stockMap a hash map to store symbols and quantity as string and integer respectively.
   * @return filename of the portfolio in string format.
   */
  String createInflexiblePortfolio(Map<String, Double> stockMap);

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
   * gets the list of portfolio files created by the user.
   *
   * @return the list of portfolio files.
   */

  List<String> getPortFolioList();

  /**
   * extracts the list of ticker symbols from csv file.
   *
   * @return list of ticker symbols.
   * @throws IOException when there is an exception thrown while reading csv file.
   */
  List<String> getTickerList() throws IOException;


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
   * it takes the file uploaded by the user then creates a portfolio and further
   * creates a file under res folder.
   *
   * @param sourceFile path of the uploaded file in string format.
   * @return returns the file name of created portfolio file in string format.
   */
  String savePortfolioFile(String sourceFile);


  /**
   * gets the portfolio among the list of portfolio options based on the pfNumber passed.
   *
   * @param pfNumber portfolio number selected by user among the list of portfolios.
   * @return an object of PortfolioInterface.
   */
  PortfolioInterface examinePortfolio(int pfNumber) throws IOException;

}
