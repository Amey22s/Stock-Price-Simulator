package controller;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import model.AdvancedModelInterface;
import model.PortfolioInterface;
import view.BaseViewInterface;

/**
 * This class implements FlexibleControllerInterface.
 * Its task is to take user inputs and provides user data to model for further processing
 * and tells view what to display.
 */
class FlexibleController extends MainController implements BaseControllerInterface {

  private final AdvancedModelInterface advancedModel;
  private final BaseViewInterface view;
  private final Readable in;
  private final Appendable out;

  /**
   * Parameterized constructor that initialises model, view, Readable and Appendable classes.
   *
   * @param advancedModel object of interface AdvancedModelInterface passed by main.
   * @param view          object of interface BaseViewInterface passed by main.
   * @param in            object of Readable class to take in inputs passed by main.
   * @param out           object of Appendable class to print outputs passed by main.
   */
  public FlexibleController(AdvancedModelInterface advancedModel,
                            BaseViewInterface view, Readable in, Appendable out) {
    super(advancedModel, view, in, out);
    this.advancedModel = advancedModel;
    this.view = view;
    this.in = in;
    this.out = out;

  }

  /**
   * takes in input from the user related to modifying portfolio.
   * asks the view to print starter menu and accordingly asks model to implement them.
   *
   * @throws IOException when there is an exception thrown by Appendable class.
   */
  @Override
  public void goController() throws IOException {


    Scanner sc = new Scanner(this.in);
    String input;
    int choice = 0;
    do {
      try {
        view.printFlexibleMenu(this.out);
        input = sc.next();
        choice = Integer.parseInt(input);
        switch (choice) {
          case 1:
            flexInputHelper('e', sc);
            break;
          case 2:
            String buyStr = transactHelper('b', sc);
            view.printStatus("Successfully bought desired stocks into your portfolio with name "
                    + buyStr, this.out);
            break;
          case 3:
            String sellStr = transactHelper('s', sc);
            view.printStatus("Successfully sold desired stocks from your portfolio with name "
                    + sellStr, this.out);
            break;
          case 4:
            flexInputHelper('c', sc);
            break;
          case 5:
            flexInputHelper('v', sc);
            break;
          case 6:
            flexInputHelper('p', sc);
            break;
          case 7:
            out.append("Please enter the local path to your custom portfolio file:\n");
            String sourceFile = sc.next();
            String filename = advancedModel.saveFlexPortfolioFile(sourceFile);
            view.printStatus("Successfully converted custom portfolio file to " + filename,
                    this.out);
            break;
          case 8:
            break;
          default:
            out.append("!!! Invalid input !!!\n");

        }
      } catch (Exception e) {
        view.printStatus("\nSomething went wrong please try again" +
                " with a different set of input.\n" + e.getMessage(), this.out);
      }
    }
    while (choice != 8);
  }


  private String transactHelper(char op, Scanner sc) throws IOException {
    out.append("\nEnter 0 to create a new portfolio\n");
    int pfNumber = showPortfolioOptions(sc, advancedModel.getPortFolioList());
    try {
      if (pfNumber > 0 || (pfNumber == 0 && op == 'b')) {
        out.append("Please enter the date at which you want to buy/sell stocks in a " +
                "given portfolio(Date format: yyyy-mm-dd):\n");
        String date = sc.next();
        out.append("Please enter commission percent at which you want to buy/sell stocks in a " +
                "given portfolio:\n");
        double commission = Double.parseDouble(sc.next());
        if (commission < 0) {
          throw new RuntimeException("Commission cannot be negative.");
        }

        if (!authDate(date) || authDayWeekend(date)) {
          throw new RuntimeException("Invalid Date passed");
        } else {
          Map<String, Double> pf = getInput(sc, advancedModel.getTickerList());
          return advancedModel.transact(pf, pfNumber, commission, date, op);
        }
      } else {
        throw new RuntimeException("Invalid portfolio number");
      }
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getMessage());
    }

  }

  private void flexInputHelper(char op, Scanner sc) throws IOException {
    int pfNumber = showPortfolioOptions(sc, advancedModel.getPortFolioList());
    try {
      if (pfNumber >= 0) {
        out.append("Please enter the date at which you want to perform operation on given " +
                "(Date format: yyyy-mm-dd):\n");
        String date = sc.next();
        if (!authDate(date) || authDayWeekend(date)) {
          throw new RuntimeException("Invalid Date passed");
        } else {
          switch (op) {
            case 'e':
              PortfolioInterface pf = advancedModel.examineFlexPortfolio(pfNumber, date);
              view.printPortfolio(pf, this.out);
              break;
            case 'c':
              double costBasis = advancedModel.calculateCostBasis(pfNumber, date);
              view.printStatus("Total cost basis of the given portfolio is $"
                      + String.format("%.2f", costBasis), this.out);
              break;
            case 'v':
              PortfolioInterface pf1 = advancedModel.examineFlexPortfolio(pfNumber, date);
              view.printPortfolio(pf1, this.out);
              double valuation = advancedModel.calculateValuation(pfNumber, date);
              view.printStatus("Total valuation of the above mentioned portfolio is $"
                      + String.format("%.2f", valuation), this.out);
              break;
            case 'p':
              out.append("Please enter the end date at which you want to perform operation" +
                      " on given (Date format: yyyy-mm-dd):\n");
              String endDate = sc.next();
              if (!authDate(endDate) || authDayWeekend(endDate)) {
                throw new RuntimeException("Invalid Date passed");
              }
              Map<String, String> graphMap = advancedModel.getPerformance(date, endDate, pfNumber);
              out.append("Performance of portfolio no. " + pfNumber + " from " + date + " to "
                      + endDate + "\n\n");
              view.printPerformanceScalingGraph(graphMap, this.out);
              break;
            default:
              throw new RuntimeException("Invalid Operation");
          }
        }
      } else {
        throw new RuntimeException("Invalid portfolio number");
      }
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

}
