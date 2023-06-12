package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * FlexibleStockModel class that implements AdvanceModelInterface and extends
 * InflexibleStockModel class. It performs various operations on flexible portfolio.
 * It implements all the features of this application while performing functionalities on
 * flexible portfolio.
 */

public class FlexibleStockModel extends AbstractStockModel implements AdvancedModelInterface {
  private final ReadWriteInterface<Portfolio> fileParser;
  private final Properties properties;
  private final PerformanceGraphInterface performanceGraph;
  private final Portfolio portfolio;
  private final CostAveragingInterface costAverageCalculator;
  private String path;


  /**
   * non parameterized constructor that initializes the instance of ReadWrite class,
   * Portfolio class, PerformanceGraph and Properties class.
   */
  public FlexibleStockModel() throws IOException {
    this.properties = new Properties();
    InputStream input = new FileInputStream("src/config.properties");
    properties.load(input);

    this.portfolio = new Portfolio();
    properties.setProperty("resource_file", "flex_portfolios");
    this.fileParser = new JsonReadWrite(properties);
    this.performanceGraph = new PerformanceGraph();
    this.costAverageCalculator = new CostAveraging();
    this.path = "flex_portfolios";

    populatePortfolios();
  }

  private void populatePortfolios() {
    List<String> pfList = getPortFolioList();
    List<String> strategyFileList = costAverageCalculator.getStrategyList();
    Strategy dummyStrategy = new Strategy();
    ReadWriteInterface<Strategy> strategyReadWriteInterface = new StrategyReadWrite();
    for (int i = 0; i < strategyFileList.size(); i++) {
      path = "strategy";
      StrategyInterface currentStrategy = strategyReadWriteInterface.readFromFile(
              getCorrectFile(i + 1, strategyFileList),
              dummyStrategy);
      path = "flex_portfolios";
      for (String s : currentStrategy.getPfList()) {
        if (pfList.contains(s)) {
          int pfNumber = pfList.indexOf(s) + 1;
          double totalCost = currentStrategy.getTotalCost();
          String endDate = currentStrategy.getEndDate();
          int period = currentStrategy.getPeriod();
          double commission = currentStrategy.getCommission();
          Map<String, Double> weightMap = currentStrategy.getWeightMap();
          createCostAveragePortfolio(weightMap, totalCost, pfNumber,
                  commission, getStartDate(pfNumber),
                  endDate, period, 'o');
        }
      }
    }

  }

  private String getStartDate(int pfNumber) {
    File tempFile = getCorrectFile(pfNumber, getPortFolioList());
    ReadWriteInterface<Portfolio> tempParser = getCorrectFileParser();
    Portfolio tempPf = new Portfolio();
    tempPf.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    return tempParser.readFromFile(tempFile, tempPf).getDate();
  }

  @Override
  String getPath() {
    return path;
  }

  @Override
  ReadWriteInterface getCorrectFileParser() {
    return new JsonReadWrite(properties);
  }


  /**
   * Performs operations necessary to add buy or sell transactions in flexible portfolio.
   * Is also used to create a completely new portfolio and then add bought stocks in it.
   * Function to add symbol and quantity of stocks into portfolio object.
   * Internally calls a function that creates a file under flex_portfolio folder for that username.
   *
   * @param stockMap a hash map to store symbols and quantity as string and integer respectively.
   * @param pfNumber portfolio number selected by user among the list of portfolios.
   * @param date     specified date in yyyy-mm-dd.
   * @param op       operation which was performed (buy / sell).
   * @return filename the portfolio in string format.
   */

  @Override
  public String transact(Map<String, Double> stockMap, int pfNumber, double commission,
                         String date, char op) {

    if (pfNumber < 0) {
      throw new RuntimeException("No such portfolio exists. " +
              "Please enter a valid portfolio number.");
    }

    String jsonFilename;
    Portfolio portfolio = createPortfolio(stockMap);

    BaseStockPriceInterface stockPrice = getStockPrice();

    List<Double> price = new ArrayList<>();
    for (int i = 0; i < portfolio.getSize(); i++) {
      if (portfolio.getQuantity().get(i) < 0) {
        price.add(-1.0);
        continue;
      }

      double cost = stockPrice.getStockPriceByApi(portfolio.getSymbol().get(i), date, properties);
      double priceAfterCommission = (cost * commission / 100.0);
      if (op == 'b') {
        priceAfterCommission += cost;
      }
      price.add(priceAfterCommission);
    }

    portfolio.setPrice(price);
    portfolio.setDate(date);
    portfolio.setOperation(op);

    if (pfNumber == 0) {
      List<String> files = getPortFolioList();
      jsonFilename = createPortfolioFile(".json", fileParser, portfolio, files);
    } else {

      try {
        List<String> files = getPortFolioList();
        File file = getCorrectFile(pfNumber, files);
        fileParser.writeToFile(file, portfolio);
        jsonFilename = getUser() + pfNumber;
      } catch (RuntimeException e) {
        throw new RuntimeException(e.getMessage());
      }
    }

    return jsonFilename + getCorrectFileType();
  }

  /**
   * returns costBasis of a portfolio at a specified date.
   *
   * @param pfNumber portfolio number selected by user among the list of portfolios.
   * @param date     specified date in yyyy-mm-dd.
   * @return amount invested in the given portfolio by a specified date.
   */

  @Override
  public double calculateCostBasis(int pfNumber, String date) {

    File file = getCorrectFile(pfNumber, getPortFolioList());
    Portfolio pf = new Portfolio();
    pf.setDate(date);
    pf = fileParser.readFromFile(file, pf);


    double costBasis = 0.0;

    for (int i = 0; i < pf.getSize(); i++) {
      costBasis += Math.abs(pf.getPrice().get(i) * pf.getQuantity().get(i));
    }
    return costBasis;
  }

  /**
   * returns valuation of a portfolio at a specified date.
   *
   * @param pfNumber portfolio number selected by user among the list of portfolios.
   * @param date     specified date in yyyy-mm-dd.
   * @return total valuation of a given portfolio at a specified date.
   */
  @Override
  public double calculateValuation(int pfNumber, String date) {
    Portfolio pf = new Portfolio();
    pf = removeDuplicates(pf);
    pf.setDate(date);

    try {
      setPortfolio(pf);
      PortfolioInterface portfolio = getPortFolioAtDate(pfNumber, date);

      double valuation = 0;
      for (int i = 0; i < portfolio.getSize(); i++) {
        if (portfolio.getPrice().get(i) >= 0) {
          valuation += portfolio.getPrice().get(i) * portfolio.getQuantity().get(i);
        }
      }
      return valuation;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * gets the portfolio among the list of portfolio options based on the pfNumber passed.
   * displays the composition of a given portfolio at specified date.
   *
   * @param pfNumber portfolio number selected by user among the list of portfolios.
   * @param date     specified date in yyyy-mm-dd.
   * @return an object of PortfolioInterface.
   */
  @Override
  public PortfolioInterface examineFlexPortfolio(int pfNumber, String date) throws IOException {
    Portfolio pf = getPortfolio();
    pf.setDate(date);
    pf = examinePortfolioHelper(pfNumber);

    pf = removeDuplicates(pf);
    pf.setDate(date);

    return pf;
  }

  /**
   * plots a performance graph for a given portfolio between the two specified dates.
   * displays the composition of a given portfolio at specified date.
   *
   * @param pfNumber  portfolio number selected by user among the list of portfolios.
   * @param startDate it is the start date for the performance graph in yyyy-mm-dd format.
   * @param endDate   it is the end date for the performance graph in yyyy-mm-dd format.
   * @return a map which has keys as the intervals scaled for Y-axis and number of stars
   */
  @Override
  public Map<String, String> getPerformance(String startDate, String endDate, int pfNumber)
          throws IOException {

    BaseStockPriceInterface sp = new StockPrice();
    Map<String, String> modelMap = performanceGraph.getTimeIntervalMap(startDate, endDate);
    Map<String, Double> viewMap = new LinkedHashMap<>();

    try {
      for (Map.Entry<String, String> entry : modelMap.entrySet()) {
        char ch = entry.getValue().charAt(0);
        if (ch == 'm') {
          viewMap.put(entry.getKey(), getMonthlyYearlyValuation(pfNumber,
                  entry.getValue().substring(1),
                  sp, ch));
        } else if (ch == 'd') {
          viewMap.put(entry.getKey(), calculateValuation(pfNumber,
                  entry.getValue().substring(1)));
        } else if (ch == 'y') {
          viewMap.put(entry.getKey(), getMonthlyYearlyValuation(pfNumber,
                  entry.getValue().substring(1),
                  sp, ch));
        }
      }
    } catch (ParseException e) {
      throw new RuntimeException(" error in getting yearly/monthly valuation" + e.getMessage());
    }

    return performanceGraph.graphScaling(viewMap);
  }

  /**
   * Function used to get a custom portfolio file from the user and parse it to convert it
   * to a portfolio in application if valid.
   *
   * @param sourceFile path of the custom file given by the user.
   * @return filename of the portfolio file created in application.
   */
  @Override
  public String saveFlexPortfolioFile(String sourceFile) {
    String custom_file_path = "";
    try {
      Portfolio pf = new Portfolio();
      properties.setProperty("custom_file", "true");
      super.setPortfolio(pf);
      String filename = super.createPortfolioFile(".json",
              fileParser, portfolio, getPortFolioList());
      custom_file_path = properties.getProperty("resource_file") + "/"
              + getUser() + "/" + filename + ".json";
      properties.setProperty("custom_file_path", custom_file_path);
      fileParser.readFromFile(new File(sourceFile), pf);
      properties.setProperty("custom_file", "false");
      return filename;
    } catch (Exception e) {
      File file = new File(custom_file_path);
      file.delete();
      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  public boolean authenticateCredentials(String user, String pwd) {
    return super.authenticateCredentials(user, pwd);
  }

  /**
   * returns the username currently logged in.
   *
   * @return username in string format.
   */
  @Override
  public String getUser() {
    return super.getUser();
  }

  /**
   * returns the password of the user currently logged in.
   *
   * @return password in string format.
   */
  @Override
  public String getPwd() {
    return super.getPwd();
  }

  /**
   * gets the list of portfolio files created by the user.
   *
   * @return the list of portfolio files.
   */
  @Override
  public List<String> getPortFolioList() {
    return super.getFileList();
  }

  @Override
  public List<String> getTickerList() throws IOException {
    return super.getTickerList();
  }

  @Override
  public String createCostAveragePortfolio(Map<String, Double> weightMap, double totalCost,
                                           int pfNumber, double commission, String startDate,
                                           String endDate, int period, char op) {
    try {
      List<String> intervals = costAverageCalculator.getDates(startDate, endDate, period);
      String fileName = createWeightedPortfolio(weightMap, totalCost, pfNumber,
              commission, startDate);
      List<String> pfList = getPortFolioList();
      if (pfNumber == 0) {
        for (int i = 0; i < pfList.size(); i++) {
          if (pfList.get(i).equals(fileName)) {
            pfNumber = i + 1;
          }
        }
      }

      for (String date : intervals) {
        fileName = createWeightedPortfolio(weightMap, totalCost, pfNumber, commission, date);
      }

      if (op == 'n') {
        costAverageCalculator.persistStrategy(weightMap, totalCost, fileName, commission, endDate,
                period);
      }

      return fileName;
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }

  }


  @Override
  public String createWeightedPortfolio(Map<String, Double> weightMap, double totalCost,
                                        int pfNumber, double commission, String startDate) {
    Map<String, Double> stockMap = costAverageCalculator.getStockMap(weightMap, totalCost,
            commission, startDate, properties);
    String fileName = transact(stockMap, pfNumber, commission, startDate, 'b');

    return fileName;
  }

  private Double getMonthlyYearlyValuation(int pfNumber, String date,
                                           BaseStockPriceInterface stockPrice, Character op)
          throws IOException, ParseException {
    String correctDate = "";
    String yearDate;
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    Date tempDate = Calendar.getInstance().getTime();
    String currentDate = df.format(tempDate);

    String dummyStock = getTickerList().get(0);

    if (op == 'm') {
      correctDate = stockPrice.getMonthEndDateByApi(dummyStock, date, properties);
    } else if (op == 'y') {
      if (date.substring(0, 5).equals(currentDate.substring(0, 5))) {
        correctDate = currentDate;
      } else {
        yearDate = (date.substring(0, 5)) + "12" + date.substring(7);
        correctDate = stockPrice.getMonthEndDateByApi(dummyStock, yearDate, properties);
      }

    }

    PortfolioInterface portfolio = examineFlexPortfolio(pfNumber, correctDate);

    double sum = 0.0;

    for (int i = 0; i < portfolio.getSize(); i++) {
      sum += stockPrice.getStockPriceByApiMonthly(portfolio.getSymbol().get(i),
              correctDate, properties) * portfolio.getQuantity().get(i);
    }
    return sum;
  }


  private Portfolio removeDuplicates(Portfolio pf) {
    List<String> symbol = pf.getSymbol();
    List<Double> quantity = pf.getQuantity();

    List<String> final_symbol = new ArrayList<>();
    List<Double> final_quantity = new ArrayList<>();

    for (int i = 0; i < pf.getSize(); i++) {
      if (final_symbol.contains(symbol.get(i))) {
        int prev_index = final_symbol.indexOf(symbol.get(i));
        double prev_quantity = final_quantity.get(prev_index);
        double current_quantity = quantity.get(i);
        final_quantity.set(prev_index, prev_quantity + current_quantity);
      } else {
        final_symbol.add(symbol.get(i));
        final_quantity.add(quantity.get(i));
      }
    }
    return new Portfolio(final_symbol, final_quantity, new ArrayList<>());
  }

}


