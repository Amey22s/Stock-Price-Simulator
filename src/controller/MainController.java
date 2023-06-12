package controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import model.AdvancedModelInterface;
import model.BaseModelInterface;
import view.BaseViewInterface;


/**
 * Controller class to implement functionalities of MainControllerInterface.
 * Its task is to take user inputs and delegate it to respective controller.
 */

public class MainController implements BaseControllerInterface {

  private final BaseModelInterface baseModel;
  private final AdvancedModelInterface advanceModel;
  private final BaseViewInterface view;
  private final Readable in;
  private final Appendable out;

  /**
   * Parameterized constructor that initialises model, view, Readable and Appendable classes.
   *
   * @param baseModel    object of interface AdvancedModelInterface passed by main.
   * @param advanceModel object of interface AdvancedModelInterface passed by main.
   * @param view         object of interface BaseViewInterface passed by main.
   * @param in           object of Readable class to take in inputs passed by main.
   * @param out          object of Appendable class to print outputs passed by main.
   */
  public MainController(BaseModelInterface baseModel, AdvancedModelInterface advanceModel,
                        BaseViewInterface view, Readable in,
                        Appendable out) {
    this.baseModel = baseModel;
    this.advanceModel = advanceModel;
    this.view = view;
    this.in = in;
    this.out = out;

  }

  protected MainController(BaseModelInterface baseModel, BaseViewInterface view,
                           Readable in, Appendable out) {
    this.baseModel = baseModel;
    this.view = view;
    this.in = in;
    this.out = out;
    this.advanceModel = null;
  }

  protected MainController(AdvancedModelInterface advancedModel, BaseViewInterface view,
                           Readable in, Appendable out) {
    this.advanceModel = advancedModel;
    this.view = view;
    this.in = in;
    this.out = out;
    this.baseModel = null;
  }

  /**
   * entry point of the application where the main inputs from the user is taken.
   * the controller present 2 options to the user ie to work with either flexible portfolios or
   * inflexible portfolios and then transfers the control to the respective controller.
   *
   * @throws IOException when there is an exception thrown by Appendable class.
   */
  @Override
  public void goController() throws IOException {
    Scanner sc = new Scanner(this.in);

    String user;
    String pwd;

    do {
      out.append("\nEnter username: ");
      user = sc.next();

      out.append("\nEnter password: ");
      pwd = sc.next();
    }
    while (!authenticateCredentials(user, pwd));

    String input;
    int choice = 0;

    do {
      try {
        view.printMainControllerMenu(this.out);
        input = sc.next();
        choice = Integer.parseInt(input);
        switch (choice) {
          case 1:
            BaseControllerInterface stockController = new InflexibleController(baseModel, view,
                    in, out);
            stockController.goController();
            break;
          case 2:
            BaseControllerInterface flexibleController = new
                    FlexibleController(advanceModel, view, in, out);
            flexibleController.goController();
            break;
          case 3:
            break;
          default:
            out.append("!!! Invalid input !!!\n");
        }
      } catch (Exception e) {
        view.printStatus("\nInvalid input please provide valid input.\n"
                + e.getMessage(), this.out);
      }
    }
    while (choice != 3);
    sc.close();
  }

  /**
   * gets the ticker symbol and quantity of shares from the user.
   * validates symbol and quantity and accordingly returns portfolio.
   *
   * @return map of portfolio with symbol and quantity.
   */
  Map<String, Double> getInput(Scanner sc, List<String> symbolList) throws IOException {
    String symbol;
    Double quantity;
    HashMap<String, Double> pf = new HashMap<>();
    do {
      try {
        out.append("Enter stock symbol: ");
        symbol = sc.next();

        if (!symbolList.contains(symbol)) {
          view.printStatus("\nPlease enter a valid ticker symbol.\n", this.out);
          out.append("\nPress Y to continue, N to exit: \n");
          continue;
        }
      } catch (Exception e) {
        view.printStatus("\nUnable to access stock list for verification.\n", this.out);
        out.append("\nPress Y to continue, N to exit: \n");
        continue;
      }

      try {
        out.append("\nEnter stock quantity: ");
        quantity = 1.0 * Integer.parseInt(sc.next());

        if (quantity <= 0) {
          view.printStatus("\nPlease enter a positive integer", this.out);
          out.append("\nPress Y to continue, N to exit: \n");
          continue;
        }
      } catch (NumberFormatException e) {
        view.printStatus("\nPlease enter a valid integer\n", this.out);
        out.append("\nPress Y to continue, N to exit: \n");
        continue;
      }

      if (pf.containsKey(symbol)) {
        pf.put(symbol, pf.get(symbol) + quantity);
        out.append("\nPress Y to continue, N to exit: \n");
        continue;
      }

      pf.put(symbol, quantity);
      out.append("\nPress Y to continue, N to exit: \n");
    }
    while (!sc.next().equalsIgnoreCase("N"));

    return pf;
  }

  /**
   * asks model for portfolio list of that user.
   * asks view to display appropriate status.
   *
   * @param sc Scanner object passed by goController().
   * @return true if portfolios are present else false.
   */
  int showPortfolioOptions(Scanner sc, List<String> files) throws IOException {

    if (files.size() == 0) {
      view.printStatus("\nNo portfolios present, Please create one", this.out);
      return 0;
    } else {
      view.printPortfolioOptions(files, this.out);

      int pfNumber = 0;

      try {
        String input = sc.next();
        pfNumber = Integer.parseInt(input);

        if (pfNumber > files.size() || pfNumber < 0) {
          return -1;
        }
      } catch (Exception e) {
        view.printStatus("Please enter a valid positive portfolio number", this.out);
      }
      return pfNumber;
    }
  }


  /**
   * authenticates date passed in the argument.
   *
   * @param date input date in yyyy-mm-dd format.
   * @return true if valid date is passed else false.
   */
  boolean authDate(String date) {
    int year = Integer.parseInt(date.substring(0, 4));
    int month = Integer.parseInt(date.substring(5, 7));
    int day = Integer.parseInt(date.substring(8, 10));

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    Date currDate = Calendar.getInstance().getTime();

    try {
      Date passedDate = df.parse(date);
      return year <= 2022 && year >= 1999 && month <= 12 && month >= 01 && day <= 31 && day >= 01
              && passedDate.before(currDate);

    } catch (ParseException e) {
      throw new RuntimeException("Could not parse the date correctly" + e.getMessage());
    }
  }

  /**
   * authenticate user credentials.
   * for this assignment we have taken username as admin and password as Asdf@1234.
   *
   * @param user username of the user.
   * @param pwd  password of the user.
   * @return true if username and password are correct else false.
   */
  public boolean authenticateCredentials(String user, String pwd) throws IOException {
    if (baseModel.authenticateCredentials(user, pwd) &&
            advanceModel.authenticateCredentials(user, pwd)) {
      view.printStatus("Successfully logged in.\n!!! Welcome !!!", this.out);
      return true;
    } else {
      view.printAuthError(this.out);
      return false;
    }
  }

  /**
   * check whether the passed day is weekend or not
   * stock market is closed on weekends therefore user cannot operate in these days.
   *
   * @param date input date in yyyy-mm-dd format.
   * @return true is day is weekend else false.
   */
  boolean authDayWeekend(String date) {

    try {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date weekend = dateFormat.parse(date);

      Calendar cal = Calendar.getInstance();
      cal.setTime(weekend);

      int day = cal.get(Calendar.DAY_OF_WEEK);
      return day == Calendar.SUNDAY || day == Calendar.SATURDAY;
    } catch (ParseException e) {
      throw new RuntimeException("Date cannot be parsed");
    }
  }
}