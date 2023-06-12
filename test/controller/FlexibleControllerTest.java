package controller;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import model.AdvancedModelInterface;
import view.StockView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Junit tests for standalone testing for controller.
 * MockModel is used to check whether controller sends accurate data to model.
 */
public class FlexibleControllerTest {

  AdvancedModelInterface model;
  BaseControllerInterface sc;
  StringBuilder log;

  @Before
  public void setUp() throws IOException {

    log = new StringBuilder();
    model = new MockModel(log);

  }


  @Test
  public void checkTransact() {

    String symbol = "GOOG";
    int quantity = 10;
    int pfNumber = 1;
    double commission = 10.0;
    String date = "2022-02-02";

    Readable in = new StringReader("2 " + pfNumber + " " + date + " " + commission + " " +
            symbol + " " + quantity + " n 8");

    Appendable out = new StringBuilder();

    sc = new FlexibleController(model, new StockView(), in, out);

    try {
      sc.goController();
      assertEquals("pfNumber passed is " + pfNumber
                      + "\nCommission passed is "
                      + commission + "\nDate passed is " + date + "\noperation passed is b" +
                      "\nsymbol passed is " + symbol + " : quantity passed is " + (double) quantity,
              log.toString());
    } catch (IOException e) {
      fail();
    }
  }


  @Test
  public void checkCalculateCostBasis() {

    int pfNumber = 1;
    String date = "2022-02-02";
    int commission = 10;

    Readable in = new StringReader("4 " + pfNumber + " " + date + " " + commission + " 8");

    Appendable out = new StringBuilder();

    sc = new FlexibleController(model, new StockView(), in, out);

    try {
      sc.goController();
      assertEquals("pfNumber passed is " + pfNumber
              + "\nDate passed is " + date, log.toString());
    } catch (IOException e) {
      fail();
    }
  }

  @Test
  public void checkCalculateValuation() {

    int pfNumber = 1;
    String date = "2022-02-02";

    Readable in = new StringReader("5 " + pfNumber + " " + date + " 8");

    Appendable out = new StringBuilder();

    sc = new FlexibleController(model, new StockView(), in, out);

    try {
      sc.goController();
      assertEquals("pfNumber passed is " + pfNumber
              + "\nDate passed is " + date, log.toString());
    } catch (IOException e) {
      fail();
    }
  }

  @Test
  public void checkExamineFlexPortfolio() {

    int pfNumber = 1;
    String date = "2022-02-02";

    Readable in = new StringReader("1 " + pfNumber + " " + date + " 8");

    Appendable out = new StringBuilder();

    sc = new FlexibleController(model, new StockView(), in, out);

    try {
      sc.goController();
      assertEquals("pfNumber passed is " + pfNumber
              + "\nDate passed is " + date, log.toString());
    } catch (IOException e) {
      fail();
    }

  }

  @Test
  public void checkSaveFlexPortfolioFile() {
    String sourceFilePath = "temp_res_flex/admin/admin1.json";
    Readable in = new StringReader("7 " + sourceFilePath + " 8");
    Appendable out = new StringBuilder();

    sc = new FlexibleController(model, new StockView(), in, out);

    try {
      sc.goController();
      assertEquals("sourceFile path passed is " + sourceFilePath, log.toString());
    } catch (IOException e) {
      fail();
    }

  }
}