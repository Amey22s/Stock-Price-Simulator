package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import model.AdvancedModelInterface;
import model.BaseModelInterface;
import model.PortfolioInterface;

/**
 * MockModel is used to check whether controller sends accurate data to model.
 * We maintain a log to check data integrity between model and controller.
 */
class MockModel implements BaseModelInterface, AdvancedModelInterface {

  private final StringBuilder log;

  MockModel(StringBuilder log) {
    this.log = log;
  }

  @Override
  public PortfolioInterface getPortFolioAtDate(int pfNumber, String date) throws IOException {
    log.setLength(0);
    log.append("pfNumber passed is " + pfNumber + "\ndate passed is " + date);
    return null;
  }

  @Override
  public String createInflexiblePortfolio(Map<String, Double> stockMap) {
    log.setLength(0);
    for (Map.Entry<String, Double> entry : stockMap.entrySet()) {
      log.append("symbol passed is " + entry.getKey() + " : quantity passed is "
              + entry.getValue());
    }
    return null;
  }


  @Override
  public boolean authenticateCredentials(String user, String pwd) {
    log.append("User passed is " + user + ". pwd passed is " + pwd);
    return true;
  }


  @Override
  public List<String> getPortFolioList() {
    return new ArrayList<>(List.of("admin1.txt"));
  }

  @Override
  public List<String> getTickerList() throws IOException {
    return new ArrayList<>(List.of("GOOG"));
  }

  @Override
  public String createCostAveragePortfolio(Map<String, Double> weightMap, double totalCost,
                                           int pfNumber, double commission, String startDate,
                                           String endDate, int period, char op) {
    return null;
  }

  @Override
  public String createWeightedPortfolio(Map<String, Double> weightMap, double totalCost,
                                        int pfNumber, double commission, String startDate) {
    return null;
  }


  @Override
  public String getUser() {
    return null;
  }

  @Override
  public String getPwd() {
    return null;
  }

  /**
   * returns the portfolio object of the portfolio currently being worked on.
   *
   * @return portfolio object.
   */
  public PortfolioInterface getPortfolio() {
    return null;
  }

  /**
   * returns the properties object currently being used.
   *
   * @return properties object.
   */
  public Properties getProperties() {
    return new Properties();
  }

  @Override
  public String savePortfolioFile(String sourceFile) {
    log.setLength(0);
    log.append("sourceFile path passed is " + sourceFile);
    return null;
  }


  @Override
  public PortfolioInterface examinePortfolio(int pfNumber) throws IOException {
    log.setLength(0);
    log.append("pfnumber passed is " + pfNumber);
    return null;
  }

  @Override
  public String transact(Map<String, Double> stockMap, int pfNumber, double commission,
                         String date, char op) {

    log.setLength(0);
    log.append("pfNumber passed is " + pfNumber);
    log.append("\nCommission passed is " + commission);
    log.append("\nDate passed is " + date);
    log.append("\noperation passed is " + op);
    for (Map.Entry<String, Double> entry : stockMap.entrySet()) {
      log.append("\nsymbol passed is " + entry.getKey() + " : quantity passed is "
              + entry.getValue());
    }
    return "admin2.json";
  }

  @Override
  public double calculateCostBasis(int pfNumber, String date) {
    log.setLength(0);
    log.append("pfNumber passed is " + pfNumber);
    log.append("\nDate passed is " + date);
    return 0;
  }

  @Override
  public double calculateValuation(int pfNumber, String date) {
    log.setLength(0);
    log.append("pfNumber passed is " + pfNumber);
    log.append("\nDate passed is " + date);
    return 0;
  }

  @Override
  public PortfolioInterface examineFlexPortfolio(int pfNumber, String date) throws IOException {
    log.setLength(0);
    log.append("pfNumber passed is " + pfNumber);
    log.append("\nDate passed is " + date);
    return null;
  }


  @Override
  public Map<String, String> getPerformance(String startDate, String endDate, int pfNumber)
          throws IOException {
    log.setLength(0);
    log.append("pfNumber passed is " + pfNumber);
    log.append("Start date passed is " + startDate);
    log.append("End date passed is " + endDate);
    return null;
  }

  @Override
  public String saveFlexPortfolioFile(String sourceFile) {
    log.setLength(0);
    log.append("sourceFile path passed is " + sourceFile);
    return null;
  }
}