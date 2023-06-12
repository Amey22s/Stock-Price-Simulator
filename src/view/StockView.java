package view;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import model.PortfolioInterface;

/**
 * StockView class that implements BaseViewInterface and
 * performs various display operations in this application.
 */
public class StockView implements BaseViewInterface {

  /**
   * displays the login menu where user credentials are asked to enter.
   *
   * @param out displays relevant message to the user.
   * @throws IOException when there is an exception thrown by Appendable class.
   */
  public void printStarter(Appendable out) throws IOException {
    out.append("\n" + "*".repeat(100) + "\n"
            + "1. Create Account\n"
            + "2. Login \n"
            + "\n" + "*".repeat(100) + "\n");
  }

  /**
   * displays a menu to the user to perform functionalities.
   * functionalities include creating portfolio, view portfolios,
   * get portfolio value at particular date and upload portfolio file.
   *
   * @param out displays relevant message to the user.
   * @throws IOException when there is an exception thrown by Appendable class.
   */
  public void printMenu(Appendable out) throws IOException {
    out.append("\n" + "*".repeat(100) + "\n"
            + "1. Create Portfolio\n"
            + "2. Examine Portfolio\n"
            + "3. Get Portfolio at a specific date\n"
            + "4. Upload custom portfolio\n"
            + "5. Exit\n"
            + "\n" + "*".repeat(100) + "\n");
    out.append("Choose the operation to be performed next:\n");
  }

  /**
   * displays message when invalid user credentials are entered.
   *
   * @param out displays relevant message to the user.
   * @throws IOException when there is an exception thrown by Appendable class.
   */
  public void printAuthError(Appendable out) throws IOException {
    out.append("\n" + "*".repeat(100) + "\n"
            + "Invalid credentials. Please login again\n"
            + "\n" + "*".repeat(100) + "\n");
  }

  /**
   * displays valuation of each stock in a portfolio on a given date.
   * the columns shown are stock symbol, quantity, price and total value.
   *
   * @param portfolio portfolio interface object based on particular portfolio number.
   * @param out       displays relevant message to the user.
   */
  public void printPortfolioValuation(PortfolioInterface portfolio, Appendable out) throws
          IOException {

    out.append("\n" + "*".repeat(100) + "\n");
    out.append(String.format("%5s%20s%20s%25s\n", "Company", "Quantity", "Stock Price", "Value"));
    double sum = 0;

    for (int i = 0; i < portfolio.getSize(); i++) {
      double value = portfolio.getPrice().get(i) * portfolio.getQuantity().get(i);
      if (value < 0) {
        printStatus("No data found for this date, please check if share market was trading " +
                "on that particular date.", out);
        break;
      }
      sum += value;
      out.append(String.format("%5s%20s%20s%30s\n", portfolio.getSymbol().get(i),
              portfolio.getQuantity().get(i), "$" + portfolio.getPrice().get(i),
              String.format("$%.2f", value)));
    }

    out.append("\nTotal valuation of portfolio: \t$" + String.format("%.2f", sum) + "\n");

    out.append("\n" + "*".repeat(100) + "\n");

  }

  /**
   * displays composition of a portfolio.
   * the columns shown are stock symbol and its quantity of shares.
   *
   * @param portfolios portfolio interface object based on a particular portfolio number.
   * @param out        displays relevant message to the user.
   * @throws IOException when there is an exception thrown by Appendable class.
   */

  public void printPortfolio(PortfolioInterface portfolios, Appendable out) throws IOException {

    out.append("\n" + "*".repeat(100) + "\n");
    out.append(String.format("%5s%20s\n", "Company", "Quantity"));

    for (int i = 0; i < portfolios.getSize(); i++) {

      out.append(String.format("%5s%20s\n", portfolios.getSymbol().get(i),
              String.format("%.2f", portfolios.getQuantity().get(i))));
    }
    out.append("\n" + "*".repeat(100) + "\n");
  }


  /**
   * displays list of all portfolios for a given user.
   * columns include the portfolio number and portfolio name.
   *
   * @param fileNames list of portfolios
   * @param out       displays relevant message to the user.
   * @throws IOException when there is an exception thrown by Appendable class.
   */
  public void printPortfolioOptions(List<String> fileNames, Appendable out) throws IOException {

    out.append("\n" + "*".repeat(100) + "\n"
            + "Please provide a portfolio number from the options below:\n\n");

    out.append(String.format("%10s%20s\n", "Portfolio Number", "Filename"));
    for (int i = 0; i < fileNames.size(); i++) {
      out.append(String.format("%5s%33s\n", i + 1, fileNames.get(i)));
    }

    out.append("\n" + "*".repeat(100) + "\n");
  }

  /**
   * displays custom statuses as returned by functions in controller.
   *
   * @param str message that needs to be displayed.
   * @param out displays relevant message to the user.
   * @throws IOException when there is an exception thrown by Appendable class.
   */
  public void printStatus(String str, Appendable out) throws IOException {
    out.append("\n" + "*".repeat(100) + "\n"
            + str
            + "\n" + "*".repeat(100) + "\n");
  }

  /**
   * displays a menu to the user to perform functionalities for flexible portfolio.
   * functionalities include buying, selling portfolios.
   * calculate cost basis and get portfolio value at particular date.
   *
   * @param out displays relevant message to the user.
   * @throws IOException when there is an exception thrown by Appendable class.
   */

  @Override
  public void printFlexibleMenu(Appendable out) throws IOException {
    out.append("\n" + "*".repeat(100) + "\n"
            + "1. Examine portfolio\n"
            + "2. Buy Stock\n"
            + "3. Sell Stock\n"
            + "4. Calculate Cost Basis\n"
            + "5. Calculate Portfolio value at a specific date\n"
            + "6. Performance Graph\n"
            + "7. Upload custom portfolio\n"
            + "8. Exit\n"
            + "\n" + "*".repeat(100) + "\n");
    out.append("Choose the operation to be performed next:\n");
  }

  /**
   * displays a menu to the user to choose between flexible or inflexible portfolio.
   * the user may select 1 to navigate to inflexible portfolio option
   * or select option 2 to navigate to flexible one.
   *
   * @param out displays relevant message to the user.
   * @throws IOException when there is an exception thrown by Appendable class.
   */

  @Override
  public void printMainControllerMenu(Appendable out) throws IOException {
    out.append("\n" + "*".repeat(100) + "\n"
            + "\n1. Inflexible Portfolio\n"
            + "2. Flexible Portfolio\n"
            + "3. Exit\n"
            + "\n" + "*".repeat(100) + "\n");
    out.append("Choose the operation to be performed next:\n");

  }

  /**
   * displays a performance graph (bar graph) for the portfolio
   * the user has asked to monitor between the dates entered by the user.
   * the graph is scaled on both X-axis and Y-axis.
   * The title of the barchart specifies the name of the portfolio X, and the time range Y to Z.
   * The number of lines is decided by the timespan desired. It is always between 5 and 30 lines.
   * The number of asterisks on each line is a measure of the
   * value of the portfolio at that timestamp. It is no more than 50 asterisks.
   * The end of the bar chart shows the scale in terms of
   * how many dollars are represented by each asterisk.
   * It also displays whether the scale is absolute or relative.
   *
   * @param graphMap a TreeMap that has the time frame(months/year/days) as keys
   *                 and the valuation as its values.
   * @param out      displays relevant message to the user.
   * @throws IOException when there is an exception thrown by Appendable class.
   */
  public void printPerformanceScalingGraph(Map<String, String> graphMap, Appendable out)
          throws IOException {

    for (Map.Entry<String, String> entry : graphMap.entrySet()) {
      out.append(entry.getKey() + " : " + entry.getValue() + "\n");
    }
  }


}
