package controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.AdvancedModelInterface;
import model.PortfolioInterface;
import view.SwingViewInterface;

/**
 * this class implements the functionalities of SwingControllerInterface. The functionalities
 * include authenticateCredentials, examine portfolio, create/buy flexible portfolio,
 * create/buy weighted portfolio, create/buy dollar cost portfolios, calculate cost basis,
 * calculate portfolio valuation at a specific date and persists a file uploaded by the user.
 * The implementation is done by the user which is sent by controller and the result is then
 * forwarded to the view to display results.
 */
public class SwingController implements SwingControllerInterface {

  private final AdvancedModelInterface advanceModel;
  private final SwingViewInterface swingView;

  /**
   * Parameterized constructor that initialises model and swing view.
   * it also initialises the add features method of SwingViewInterface.
   *
   * @param advanceModel object of interface AdvancedModelInterface.
   * @param swingView    object of interface SwingViewInterface.
   */
  public SwingController(AdvancedModelInterface advanceModel,
                         SwingViewInterface swingView) {
    this.advanceModel = advanceModel;
    this.swingView = swingView;

    swingView.addFeatures(this);
  }

  @Override
  public void authenticateCredentialsView(String user, String pwd) {
    String message;
    try {
      if (advanceModel.authenticateCredentials(user, pwd)) {
        message = "Login Successful!!";
        swingView.printStatus(swingView.getPanel("flexMenu"), message);
        swingView.resetFields();
        swingView.switchScreen(swingView.getPanel("flexMenu"));
      } else {
        message = "Invalid Credentials!!";
        swingView.printStatus(swingView.getPanel("login page"), message);
        swingView.resetFields();
      }
    } catch (Exception e) {
      throw new RuntimeException("Something went wrong \t" + e.getMessage());
    }
  }

  private boolean authDate(String date) {

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    Date currDate = Calendar.getInstance().getTime();

    try {
      Date passedDate = df.parse(date);
      return passedDate.before(currDate);

    } catch (ParseException e) {
      throw new RuntimeException("Could not parse the date correctly" + e.getMessage());
    }
  }

  private boolean authDayWeekend(String date) {

    int year = Integer.parseInt(date.substring(0, 4));
    int month = Integer.parseInt(date.substring(5, 7));
    int day = Integer.parseInt(date.substring(8, 10));

    try {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date weekend = dateFormat.parse(date);

      Calendar cal = Calendar.getInstance();
      cal.setTime(weekend);

      int weekEnd = cal.get(Calendar.DAY_OF_WEEK);
      return (year > 2022 || year < 2014 || month > 12 || month < 01 || day > 31 || day < 01
              || weekEnd == Calendar.SUNDAY || weekEnd == Calendar.SATURDAY);
    } catch (ParseException e) {
      throw new RuntimeException("Date cannot be parsed");
    }
  }

  private boolean checkDollarCostEndDate(String endDate) {
    int year = Integer.parseInt(endDate.substring(0, 4));
    int month = Integer.parseInt(endDate.substring(5, 7));
    int day = Integer.parseInt(endDate.substring(8, 10));

    return (year < 2014 || month > 12 || month < 01 || day > 31 || day < 01);
  }

  private boolean checkSymbol(Map<String, Double> map) {
    try {

      List<String> keys = new ArrayList<>(map.keySet());
      List<String> keyList = advanceModel.getTickerList();

      for (String key : keys) {
        if (!keyList.contains(key)) {
          return false;
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Please check your input " + e.getMessage());
    }
    return true;
  }

  private boolean checkQuantity(Map<String, Double> map) {
    try {
      List<Double> values = new ArrayList<Double>(map.values());

      for (Double value : values) {
        if (value <= 0) {
          return false;
        }
      }
    } catch (NumberFormatException e) {
      throw new RuntimeException("check quantity not working.");
    }
    return true;
  }

  private boolean checkWeight(Map<String, Double> map) {

    double sum = 0.0;
    int sumFlag = 0;
    int valueFlag = 0;
    try {
      List<Double> values = new ArrayList<Double>(map.values());

      for (Double value : values) {
        if (value <= 0) {
          valueFlag = 1;
          break;
        }
        sum += value;
      }
      if (sum != 100.0) {
        sumFlag = 1;
      }
      if (valueFlag == 1 || sumFlag == 1) {
        return false;
      }
    } catch (NumberFormatException e) {
      throw new RuntimeException("check weight not working.");
    }
    return true;
  }

  @Override
  public void displayPortfolios(String operation) {
    swingView.switchScreen(swingView.getPanel(operation));
    swingView.populatePortfolioList(advanceModel.getPortFolioList());
  }


  @Override
  public void examinePortfolio(int pfNumber, String date) {
    String message;
    Map<String, Double> examineMap;
    try {
      if (!authDate(date) || authDayWeekend(date)) {
        message = "Invalid Date passed";
        swingView.printStatus(swingView.getPanel("examine"), message);
      } else {
        PortfolioInterface pf = advanceModel.examineFlexPortfolio(pfNumber, date);
        examineMap = getExaminePortfolioValueMap(pf);
        swingView.examineDisplay(examineMap);
      }
    } catch (Exception e) {
      message = "Please check your input  " + e.getMessage();
      swingView.printStatus(swingView.getPanel("examine"), message);
    }
  }

  private Map<String, Double> getExaminePortfolioValueMap(PortfolioInterface pf) {
    Map<String, Double> map = new LinkedHashMap<>();

    for (int i = 0; i < pf.getSize(); i++) {
      map.put(pf.getSymbol().get(i), pf.getQuantity().get(i));
    }

    return map;
  }

  @Override
  public void buyStocks(Map<String, Double> stockMap, int pfNumber, String commission,
                        String date) {
    String message;
    try {
      if (!authDate(date) || authDayWeekend(date)) {
        message = "Invalid Date passed";
      } else if (!checkSymbol(stockMap)) {
        message = "Invalid Symbol ";
      } else if (!checkQuantity(stockMap)) {
        message = "Invalid Quantity ";
      } else if (Integer.parseInt(commission) <= 0) {
        message = "Invalid Commission Amount ";
      } else {

        String fileName = advanceModel.transact(stockMap, pfNumber, Double.parseDouble(commission),
                date, 'b');
        message = "Your file is stored in " + fileName;
      }
    } catch (Exception e) {
      message = "Please check your input  " + e.getMessage();
    }
    if (pfNumber != 0) {
      swingView.printStatus(swingView.getPanel("Buy Flexible Portfolio"), message);
    } else {
      swingView.printStatus(swingView.getPanel("Create Flexible Portfolio"), message);
    }
  }

  @Override
  public void sellStocks(Map<String, Double> stockMap, int pfNumber, String commission,
                         String date) {
    String message;
    try {
      if (!authDate(date) || authDayWeekend(date)) {
        message = "Invalid Date passed";
      } else if (!checkSymbol(stockMap)) {
        message = "Invalid Symbol ";
      } else if (!checkQuantity(stockMap)) {
        message = "Invalid Quantity ";
      } else if (Integer.parseInt(commission) <= 0) {
        message = "Invalid Commission Amount ";
      } else {
        String fileName = advanceModel.transact(stockMap, pfNumber, Double.parseDouble(commission),
                date, 's');
        message = "Your file is stored in " + fileName;
      }
    } catch (Exception e) {
      message = "Please check your input " + e.getMessage();
    }
    swingView.printStatus(swingView.getPanel("sell"), message);
  }

  @Override
  public void createWeightedPortfolio(Map<String, Double> weightMap, String viewTotalCost,
                                      int pfNumber, String viewCommission, String startDate) {
    String message;
    try {
      double totalCost = Double.parseDouble(viewTotalCost);
      double commission = Double.parseDouble(viewCommission);
      if (!authDate(startDate) || authDayWeekend(startDate)) {
        message = "Invalid Date passed";
      } else if (Integer.parseInt(viewCommission) <= 0) {
        message = "Invalid Commission Amount ";
      } else if (!checkSymbol(weightMap)) {
        message = "Invalid Symbol ";
      } else if (!checkQuantity(weightMap)) {
        message = "Invalid Quantity ";
      } else if (!checkWeight(weightMap)) {
        message = "Invalid Weight";
      } else if (totalCost <= 0.0) {
        message = "Please enter a positive total cost";
      } else {
        String fileName = advanceModel.createWeightedPortfolio(weightMap, totalCost,
                pfNumber, commission, startDate);
        message = "Your file is stored in " + fileName;
      }
    } catch (Exception e) {
      message = "Please check your input " + e.getMessage();
    }
    if (pfNumber != 0) {
      swingView.printStatus(swingView.getPanel("Buy Weighted Portfolio"), message);
    } else {
      swingView.printStatus(swingView.getPanel("Create Weighted Portfolio"), message);
    }
  }

  @Override
  public void dollarCostAveraging(Map<String, Double> weightMap, String viewTotalCost, int pfNumber,
                                  String viewCommission, String startDate,
                                  String endDate, int period) {
    String message;
    try {
      if (endDate.equals("")) {
        endDate = "2100-12-31";
      }
      double totalCost = Double.parseDouble(viewTotalCost);
      double commission = Double.parseDouble(viewCommission);
      if (!authDate(startDate) || authDayWeekend(startDate)) {
        message = "Invalid start date passed";
      } else if (checkDollarCostEndDate(endDate)) {
        message = "Invalid end date passed";
      } else if (!checkSymbol(weightMap)) {
        message = "Invalid Symbol ";
      } else if (!checkQuantity(weightMap)) {
        message = "Invalid Quantity ";
      } else if (!checkWeight(weightMap)) {
        message = "Invalid Weight";
      } else if (Integer.parseInt(viewCommission) <= 0) {
        message = "Invalid Commission Amount ";
      } else if (totalCost <= 0.0) {
        message = "Please enter a positive total cost";
      } else if (period <= 0) {
        message = "Please enter a positive duration";
      } else {
        String fileName = advanceModel.createCostAveragePortfolio(weightMap, totalCost, pfNumber,
                commission, startDate, endDate, period, 'n');
        message = "Your file is stored in " + fileName;
      }
    } catch (Exception e) {
      message = "Please check your input " + e.getMessage();
    }
    if (pfNumber != 0) {
      swingView.printStatus(swingView.getPanel("Buy Dollar Cost Averaging"), message);
    } else {
      swingView.printStatus(swingView.getPanel("Create Dollar Cost Averaging"), message);
    }
  }

  @Override
  public void savePortfolio(String path) {
    String message;
    try {
      if (path.equals("")) {
        message = "Path cannot be blank.Please enter a valid path ";
      } else {
        String fileName = advanceModel.saveFlexPortfolioFile(path);
        message = "File with name " + fileName + " was successfully " +
                "created using given custom file.";
      }
    } catch (Exception e) {
      message = "Please check your input " + e.getMessage();
    }
    swingView.printStatus(swingView.getPanel("uploadFile"), message);
  }

  @Override
  public void getCostBasis(int pfNumber, String date) {

    String message;
    try {
      if (date.equals("")) {
        message = "Please enter the value for date.";
      } else if (!authDate(date) || authDayWeekend(date)) {
        message = "Invalid Date passed";
      } else {
        double costBasis = advanceModel.calculateCostBasis(pfNumber, date);
        message = "Total cost basis of the given portfolio is $" + String.format("%.2f", costBasis);
      }
    } catch (Exception e) {
      message = "Please check your input  " + e.getMessage();
    }
    swingView.printStatus(swingView.getPanel("costBasis"), message);
  }

  @Override
  public void getPfValue(int pfNumber, String date) {
    String message;
    try {
      if (date.equals("")) {
        message = "Please enter the value for date.";
      } else if (!authDate(date) || authDayWeekend(date)) {
        message = "Invalid Date passed";
      } else {
        double valuation = advanceModel.calculateValuation(pfNumber, date);
        message = "Total Valuation of the given portfolio is $" + String.format("%.2f", valuation);
      }
    } catch (Exception e) {
      message = "Please check your input " + e.getMessage();
    }
    swingView.printStatus(swingView.getPanel("getValue"), message);
  }

  @Override
  public void performanceChart(int pfNumber, String startDate, String endDate) {
    String message;
    try {
      if (startDate.equals("") || endDate.equals("")) {
        message = "Please enter the value for date.";
        swingView.printStatus(swingView.getPanel("performanceChart"), message);
      } else if (!authDate(startDate) || authDayWeekend(startDate)) {
        message = "Invalid Date passed";
        swingView.printStatus(swingView.getPanel("performanceChart"), message);
      } else if (!authDate(endDate) || authDayWeekend(endDate)) {
        message = "Invalid Date passed";
        swingView.printStatus(swingView.getPanel("performanceChart"), message);
      } else {
        Map<String, String> graphMap = advanceModel.getPerformance(startDate, endDate, pfNumber);
        swingView.populateGraphPoints(graphMap);
        message = "DPC:Performance of portfolio no. " + pfNumber + " from " + startDate + " to "
                + endDate;
        swingView.printStatus(swingView.getPanel("performanceChart"), message);
      }
    } catch (IOException e) {
      message = "Please check your input";
      swingView.printStatus(swingView.getPanel("performanceChart"), message);
    }
  }

}
