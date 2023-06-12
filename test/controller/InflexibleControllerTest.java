package controller;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import model.BaseModelInterface;
import view.StockView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Junit tests for standalone testing for controller.
 * MockModel is used to check whether controller sends accurate data to model.
 */
public class InflexibleControllerTest {

  BaseModelInterface model;
  BaseControllerInterface sc;
  StringBuilder log;

  @Before
  public void setUp() throws IOException {

    log = new StringBuilder();
    model = new MockModel(log);

  }

  @Test
  public void checkGetPortfolioAtDate() throws IOException {
    int pfNumber = 1;
    String date = "2022-10-12";

    Readable in = new StringReader("admin Asdf@1234 3 " + pfNumber + " " + date + " 5");
    Appendable out = new StringBuilder();

    sc = new InflexibleController(model, new StockView(), in, out);

    try {
      sc.goController();
      assertEquals("pfNumber passed is " + pfNumber + "\ndate passed is "
              + date, log.toString());
    } catch (IOException e) {
      fail();
    }

  }

  @Test
  public void checkCreatePortfolio() {

    String symbol = "GOOG";
    int quantity = 10;

    Readable in = new StringReader("1 " + symbol + " " + quantity + " n 5");
    Appendable out = new StringBuilder();

    sc = new InflexibleController(model, new StockView(), in, out);

    try {
      sc.goController();
      assertEquals("symbol passed is " + symbol + " : quantity passed is "
              + (double) quantity, log.toString());
    } catch (IOException e) {
      fail();
    }

  }

  @Test
  public void checkExaminePortFolio() {

    int pfNumber = 1;

    Readable in = new StringReader("admin Asdf@1234 2 " + pfNumber + " 5");
    Appendable out = new StringBuilder();

    sc = new InflexibleController(model, new StockView(), in, out);

    try {
      sc.goController();
      assertEquals("pfnumber passed is " + pfNumber, log.toString());
    } catch (IOException e) {
      fail();
    }

  }


  @Test
  public void checkSavePortfolioFile() {

    String sourceFilePath = "temp_res/admin/admin1.txt";
    Readable in = new StringReader("admin Asdf@1234 4 " + sourceFilePath + " 5");
    Appendable out = new StringBuilder();

    sc = new InflexibleController(model, new StockView(), in, out);

    try {
      sc.goController();
      assertEquals("sourceFile path passed is " + sourceFilePath, log.toString());
    } catch (IOException e) {
      fail();
    }

  }
}