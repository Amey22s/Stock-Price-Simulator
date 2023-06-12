package controller;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import model.BaseModelInterface;
import model.PortfolioInterface;
import view.BaseViewInterface;


/**
 * Controller class to implement functionalities of BaseControllerInterface.
 * Its task is to take user inputs and provides user data to model for further processing
 * and tells view what to display.
 */
class InflexibleController extends MainController implements BaseControllerInterface {

  private final BaseModelInterface baseModel;
  private final BaseViewInterface view;
  private final Readable in;
  private final Appendable out;

  /**
   * Parameterized constructor that initialises model, view, Readable and Appendable classes.
   *
   * @param baseModel object of interface BaseModelInterface passed by main.
   * @param view      object of interface BaseViewInterface passed by main.
   * @param in        object of Readable class to take in inputs passed by main.
   * @param out       object of Appendable class to print outputs passed by main.
   */
  public InflexibleController(BaseModelInterface baseModel, BaseViewInterface view, Readable in,
                              Appendable out) {

    super(baseModel, view, in, out);
    this.baseModel = baseModel;
    this.view = view;
    this.in = in;
    this.out = out;
  }

  /**
   * entry point of the application where all the inputs from the user are taken.
   * asks the view to print starter menu and accordingly asks model to implement them.
   *
   * @throws IOException when there is an exception thrown by Appendable class.
   */

  @Override
  public void goController() throws IOException {

    Scanner sc = new Scanner(this.in);

    String input = "";
    int choice = 0;
    do {
      try {
        view.printMenu(this.out);
        input = sc.next();
        choice = Integer.parseInt(input);
        switch (choice) {
          case 1:
            Map<String, Double> pf = getInput(sc, baseModel.getTickerList());
            try {
              if (pf.size() != 0) {
                String pfName = baseModel.createInflexiblePortfolio(pf);
                view.printStatus("Successfully created your portfolio with name "
                        + pfName, this.out);
              }
            } catch (RuntimeException e) {
              view.printStatus(e.getMessage(), this.out);
            }
            break;
          case 2:
            int pfNumber = showPortfolioOptions(sc, baseModel.getPortFolioList());
            if (pfNumber > 0) {
              examinePortfolio(pfNumber);
            } else {
              out.append("Invalid portfolio number");
            }
            break;
          case 3:
            pfNumber = showPortfolioOptions(sc, baseModel.getPortFolioList());
            try {
              if (pfNumber > 0) {
                out.append("Please enter the date at which you want to see the value " +
                        "of given portfolio(Date format: yyyy-mm-dd):\n");
                String date = sc.next();
                if (!authDate(date) || authDayWeekend(date)) {
                  view.printStatus("Invalid Date passed", this.out);
                  break;
                } else {
                  displayPortfolioValue(pfNumber, date);
                }
              } else {
                out.append("Invalid portfolio number");
              }
            } catch (Exception e) {
              view.printStatus("Invalid Date format.Please enter the date in yyyy-mm-dd",
                      this.out);
            }
            break;
          case 4:
            out.append("Please enter the local path to your custom portfolio file:\n");
            String sourceFile = sc.next();
            try {
              String filename = baseModel.savePortfolioFile(sourceFile);
              view.printStatus("Successfully converted custom portfolio file to " + filename,
                      this.out);
            } catch (Exception e) {
              view.printStatus(e.getMessage(), this.out);
            }
            break;
          case 5:
            break;
          default:
            out.append("!!! Invalid input !!!\n");
        }
      } catch (Exception e) {
        view.printStatus("\nInvalid input please provide valid input.\n"
                + e.getMessage(), this.out);
      }
    }
    while (choice != 5);

  }


  /**
   * asks model for a particular portfolio based on the portfolio number provided by the user.
   * asks view to display appropriate status based on models return.
   *
   * @param pfNumber portfolio number associated with a user's list of portfolios.
   */

  private void examinePortfolio(int pfNumber) throws IOException {
    PortfolioInterface pf;
    try {
      pf = baseModel.examinePortfolio(pfNumber);
      view.printPortfolio(pf, this.out);
    } catch (Exception e) {
      view.printStatus("\nInvalid portfolio number", this.out);
    }
  }

  /**
   * asks model to return a portfolio value at a specified date.
   * asks view to display the total valuation of the stocks in portfolio.
   *
   * @param pfNumber  portfolio number associated with a user's list of portfolios.
   * @param inputDate specified date in yyyy-mm-dd format.
   */
  private void displayPortfolioValue(int pfNumber, String inputDate) throws IOException {

    PortfolioInterface pf;

    try {
      pf = baseModel.getPortFolioAtDate(pfNumber, inputDate);
      view.printPortfolioValuation(pf, this.out);
    } catch (Exception e) {
      view.printStatus(e.getMessage(), this.out);
    }
  }
}
