package view;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import model.PortfolioInterface;

/**
 * Interface BaseViewInterface represents all the operations performed by view.
 * It displays results to the end user.
 */
public interface BaseViewInterface {

  /**
   * displays the login menu where user credentials are asked to enter.
   *
   * @param out displays relevant message to the user.
   * @throws IOException when there is an exception thrown by Appendable class.
   */
  void printStarter(Appendable out) throws IOException;

  /**
   * displays a menu to the user to perform functionalities.
   * functionalities include creating portfolio, view portfolios,
   * get portfolio value at particular date and upload portfolio file.
   *
   * @param out displays relevant message to the user.
   * @throws IOException when there is an exception thrown by Appendable class.
   */
  void printMenu(Appendable out) throws IOException;

  /**
   * displays message when invalid user credentials are entered.
   *
   * @param out displays relevant message to the user.
   * @throws IOException when there is an exception thrown by Appendable class.
   */
  void printAuthError(Appendable out) throws IOException;

  /**
   * displays valuation of each stock in a portfolio on a given date.
   * the columns shown are stock symbol, quantity, price and total value.
   *
   * @param portfolio portfolio interface based on particular portfolio number.
   * @param out       displays relevant message to the user.
   */
  void printPortfolioValuation(PortfolioInterface portfolio, Appendable out) throws IOException;

  /**
   * displays composition of a portfolio.
   * the columns shown are stock symbol and its quantity of shares.
   *
   * @param portfolio portfolio interface object based on a particular portfolio number.
   * @param out       displays relevant message to the user.
   * @throws IOException when there is an exception thrown by Appendable class.
   */
  void printPortfolio(PortfolioInterface portfolio, Appendable out) throws IOException;

  /**
   * displays list of all portfolios for a given user.
   * columns include the portfolio number and portfolio name.
   *
   * @param fileNames list of portfolios
   * @param out       displays relevant message to the user.
   * @throws IOException when there is an exception thrown by Appendable class.
   */
  void printPortfolioOptions(List<String> fileNames, Appendable out) throws IOException;

  /**
   * displays custom statuses as returned by functions in controller.
   *
   * @param str message that needs to be displayed.
   * @param out displays relevant message to the user.
   * @throws IOException when there is an exception thrown by Appendable class.
   */
  void printStatus(String str, Appendable out) throws IOException;

  /**
   * displays a menu to the user to perform functionalities for flexible portfolio.
   * functionalities include buying, selling portfolios.
   * calculate cost basis and get portfolio value at particular date.
   *
   * @param out displays relevant message to the user.
   * @throws IOException when there is an exception thrown by Appendable class.
   */
  void printFlexibleMenu(Appendable out) throws IOException;

  /**
   * displays a menu to the user to choose between flexible or inflexible portfolio.
   * the user may select 1 to navigate to inflexible portfolio option
   * or select option 2 to navigate to flexible one.
   *
   * @param out displays relevant message to the user.
   * @throws IOException when there is an exception thrown by Appendable class.
   */
  void printMainControllerMenu(Appendable out) throws IOException;

  /**
   * displays the performance of a portfolio over a period of time.
   * the range is given by the user that is start date and end date.
   *
   * @param graphMap a TreeMap that has the time frame(months/year/days) as keys
   *                 and the valuation as its values.
   * @param out      displays relevant message to the user.
   * @throws IOException when there is an exception thrown by Appendable class.
   */
  void printPerformanceScalingGraph(Map<String, String> graphMap, Appendable out)
          throws IOException;
}
