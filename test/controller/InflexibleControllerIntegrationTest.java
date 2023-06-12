package controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;

import model.BaseModelInterface;
import model.InflexibleStockModel;
import view.StockView;

import static org.junit.Assert.assertEquals;

/**
 * Junit tests for integration testing between model, view and Inflexible controller.
 */
public class InflexibleControllerIntegrationTest {

  private Properties properties;

  private BaseModelInterface model;
  private String testDirForUpload;

  private static void deleteDirectory(File file) {
    for (File subfile : file.listFiles()) {

      if (subfile.isDirectory()) {
        deleteDirectory(subfile);
      }
      subfile.delete();
    }
  }

  @Before
  public void setUp() throws IOException {

    properties = new Properties();
    InputStream input = new FileInputStream("src/config.properties");

    properties.load(input);

    String codePath = properties.getProperty("resource_file");
    String testPath = properties.getProperty("path_for_test");
    String codeDump = properties.getProperty("path_for_dump");

    new File(codeDump).mkdir();

    model = new InflexibleStockModel();

    model.authenticateCredentials("admin", "Asdf@1234");

    copyFolder(Path.of(codePath), Path.of(codeDump), StandardCopyOption.REPLACE_EXISTING);
    deleteDirectory(new File(codePath + "/admin"));
    copyFolder(Path.of(testPath), Path.of(codePath), StandardCopyOption.REPLACE_EXISTING);

  }

  @After

  public void cleanUp() throws IOException {

    String codePath = properties.getProperty("resource_file");
    String codeDump = properties.getProperty("path_for_dump");
    String savePortfolioTestPath = codePath + "/" +
            properties.getProperty("path_for_test_savePortfolio");

    copyFolder(Path.of(codeDump), Path.of(codePath), StandardCopyOption.REPLACE_EXISTING);

    deleteDirectory(new File(codeDump));

  }

  /**
   * display screen when MainController is run and the user selects option 1 that is
   * Inflexible Portfolio.
   */
  @Test
  public void inflexibleControllerStarterScreenTest() throws IOException {
    Readable in = new StringReader("5");
    Appendable out = new StringBuilder();


    BaseControllerInterface stockController = new InflexibleController(model,
            new StockView(), in, out);
    stockController.goController();
    assertEquals("1. Create Portfolio", out.toString().split("\n")[2]);
    assertEquals("2. Examine Portfolio", out.toString().split("\n")[3]);
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[9]);
  }

  /**
   * display screen when choice entered is invalid ie 5 < ch <1.
   */
  @Test
  public void choiceIsInvalidInputTest() throws IOException {
    Readable in = new StringReader(" 7 5");
    Appendable out = new StringBuilder();

    Readable in2 = new StringReader("-2 5");
    Appendable out2 = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("!!! Invalid input !!!", out.toString().split("\n")[10]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[13]);

    BaseControllerInterface sc2 = new InflexibleController(model, new StockView(), in2, out2);
    sc2.goController();
    assertEquals("!!! Invalid input !!!", out2.toString().split("\n")[10]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[13]);
  }

  /**
   * display screen when username enters choice number 1.
   */
  @Test
  public void createPortfolioScreenTest() throws IOException {
    Readable in = new StringReader("1 MSFT 10 N 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[9]);
    assertEquals("Enter stock symbol: ", out.toString().split("\n")[10]);
    assertEquals("Enter stock quantity: ", out.toString().split("\n")[11]);
    deleteFile();
  }

  /**
   * display screen when 1 portfolio is created with just 1 stock.
   */
  @Test
  public void createOnePortfolio() throws IOException {
    Readable in = new StringReader("1 MSFT 40 N 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();

    assertEquals("Enter stock symbol: ", out.toString().split("\n")[10]);
    assertEquals("Enter stock quantity: ", out.toString().split("\n")[11]);
    assertEquals("Press Y to continue, N to exit: ", out.toString().split("\n")[12]);
    assertEquals("Successfully created your portfolio with name admin2",
            out.toString().split("\n")[15]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[19]);
    deleteFile();
  }

  /**
   * display screen when 1 portfolio is created with multiple stocks.
   */
  @Test
  public void createMultiplePortfolio() throws IOException {
    Readable in = new StringReader("1 MSFT 40 Y TSLA 20 N 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();

    assertEquals("Enter stock symbol: ", out.toString().split("\n")[10]);
    assertEquals("Enter stock quantity: ", out.toString().split("\n")[11]);
    assertEquals("Press Y to continue, N to exit: ", out.toString().split("\n")[12]);
    assertEquals("Enter stock symbol: ", out.toString().split("\n")[13]);
    assertEquals("Enter stock quantity: ", out.toString().split("\n")[14]);
    assertEquals("Press Y to continue, N to exit: ", out.toString().split("\n")[15]);
    assertEquals("Successfully created your portfolio with name admin2",
            out.toString().split("\n")[18]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[22]);
    deleteFile();
  }

  /**
   * display screen when 1 portfolio is created with multiple stocks and same ticker.
   */
  @Test
  public void createMultiplePortfolioAndSameTicker() throws IOException {
    Readable in = new StringReader("1 MSFT 40 Y MSFT 20 N 2 2 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();

    assertEquals("Enter stock symbol: ", out.toString().split("\n")[10]);
    assertEquals("Enter stock quantity: ", out.toString().split("\n")[11]);
    assertEquals("Press Y to continue, N to exit: ", out.toString().split("\n")[12]);
    assertEquals("Enter stock symbol: ", out.toString().split("\n")[13]);
    assertEquals("Enter stock quantity: ", out.toString().split("\n")[14]);
    assertEquals("Press Y to continue, N to exit: ", out.toString().split("\n")[15]);
    assertEquals("Successfully created your portfolio with name admin2",
            out.toString().split("\n")[18]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[22]);
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[29]);
    assertEquals("Please provide a portfolio number from the options below:",
            out.toString().split("\n")[32]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[34]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[46]);
    deleteFile();
  }


  /**
   * display screen when user enters invalid ticker symbol.
   */
  @Test
  public void checkInvalidTickerSymbol() throws IOException {
    Readable in = new StringReader("1 Apple Y AAPL 30 N 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();

    assertEquals("Enter stock symbol: ", out.toString().split("\n")[10]);
    assertEquals("Please enter a valid ticker symbol.",
            out.toString().split("\n")[13]);
    assertEquals("Press Y to continue, N to exit: ",
            out.toString().split("\n")[17]);
    assertEquals("Enter stock symbol: ", out.toString().split("\n")[18]);
    assertEquals("Enter stock quantity: ", out.toString().split("\n")[19]);
    assertEquals("Press Y to continue, N to exit: ", out.toString().split("\n")[20]);
    assertEquals("Successfully created your portfolio with name admin2",
            out.toString().split("\n")[23]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[27]);
    deleteFile();
  }

  /**
   * display screen when user enters valid ticker symbol. but in lowercase.
   */
  @Test
  public void checkValidTickerSymbolInLowerCase() throws IOException {
    Readable in = new StringReader("1 aapl Y AAPL 30 N 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();

    assertEquals("Enter stock symbol: ", out.toString().split("\n")[10]);
    assertEquals("Please enter a valid ticker symbol.",
            out.toString().split("\n")[13]);
    assertEquals("Press Y to continue, N to exit: ",
            out.toString().split("\n")[17]);
    assertEquals("Enter stock symbol: ", out.toString().split("\n")[18]);
    assertEquals("Enter stock quantity: ", out.toString().split("\n")[19]);
    assertEquals("Press Y to continue, N to exit: ", out.toString().split("\n")[20]);
    assertEquals("Successfully created your portfolio with name admin2",
            out.toString().split("\n")[23]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[27]);
    deleteFile();
  }

  /**
   * display screen when user enters invalid ticker symbol inc numbers.
   */
  @Test
  public void checkInvalidTickerIncNumbers() throws IOException {
    Readable in = new StringReader("1 MS8FT Y AAPL 30 N 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();

    assertEquals("Enter stock symbol: ", out.toString().split("\n")[10]);
    assertEquals("Please enter a valid ticker symbol.",
            out.toString().split("\n")[13]);
    assertEquals("Press Y to continue, N to exit: ",
            out.toString().split("\n")[17]);
    assertEquals("Enter stock symbol: ", out.toString().split("\n")[18]);
    assertEquals("Enter stock quantity: ", out.toString().split("\n")[19]);
    assertEquals("Press Y to continue, N to exit: ", out.toString().split("\n")[20]);
    assertEquals("Successfully created your portfolio with name admin2",
            out.toString().split("\n")[23]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[27]);
    deleteFile();
  }

  /**
   * display screen when user enters invalid ticker symbol with decimals.
   */
  @Test
  public void checkInvalidTickerWithDecimal() throws IOException {
    Readable in = new StringReader("1 2.5 Y AAPL 30 N 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();

    assertEquals("Enter stock symbol: ", out.toString().split("\n")[10]);
    assertEquals("Please enter a valid ticker symbol.",
            out.toString().split("\n")[13]);
    assertEquals("Press Y to continue, N to exit: ",
            out.toString().split("\n")[17]);
    assertEquals("Enter stock symbol: ", out.toString().split("\n")[18]);
    assertEquals("Enter stock quantity: ", out.toString().split("\n")[19]);
    assertEquals("Press Y to continue, N to exit: ", out.toString().split("\n")[20]);
    assertEquals("Successfully created your portfolio with name admin2",
            out.toString().split("\n")[23]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[27]);
    deleteFile();
  }

  /**
   * display screen when user enters invalid quantity of shares.
   */
  @Test
  public void checkInvalidQuantity() throws IOException {
    Readable in = new StringReader("1 TSLA -25 Y TSLA 2s5 Y TSLA 2.5 Y " +
            "TSLA 25 N 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();

    assertEquals("Enter stock symbol: ", out.toString().split("\n")[10]);
    assertEquals("Enter stock quantity: ", out.toString().split("\n")[11]);
    assertEquals("Please enter a positive integer",
            out.toString().split("\n")[14]);
    assertEquals("Press Y to continue, N to exit: ",
            out.toString().split("\n")[17]);
    assertEquals("Enter stock symbol: ", out.toString().split("\n")[18]);
    assertEquals("Enter stock quantity: ", out.toString().split("\n")[19]);
    assertEquals("Please enter a valid integer",
            out.toString().split("\n")[22]);
    assertEquals("Press Y to continue, N to exit: ", out.toString().split("\n")[26]);
    assertEquals("Enter stock symbol: ", out.toString().split("\n")[27]);
    assertEquals("Enter stock quantity: ", out.toString().split("\n")[28]);
    assertEquals("Please enter a valid integer",
            out.toString().split("\n")[31]);
    assertEquals("Press Y to continue, N to exit: ", out.toString().split("\n")[35]);
    assertEquals("Enter stock symbol: ", out.toString().split("\n")[36]);
    assertEquals("Enter stock quantity: ", out.toString().split("\n")[37]);
    assertEquals("Successfully created your portfolio with name admin2",
            out.toString().split("\n")[41]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[45]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 2.
   */
  @Test
  public void examinePortfolioScreenTest() throws IOException {
    Readable in = new StringReader("2 1 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Please provide a portfolio number from the options below:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("%5s%33s", "1", "admin1.txt"),
            out.toString().split("\n")[15]);
    assertEquals(String.format("%5s%20s", "Company", "Quantity"),
            out.toString().split("\n")[20]);
    assertEquals(String.format("%5s%20s", "AAPL", "10.00"),
            out.toString().split("\n")[21]);
    assertEquals(String.format("%5s%20s", "TSLA", "15.00"),
            out.toString().split("\n")[22]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[27]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 2 but portfolio number > size of pf list.
   */
  @Test
  public void examinePortfolioWhenPfNumberIsGreaterThanSize() throws IOException {
    Readable in = new StringReader("2 3 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Please provide a portfolio number from the options below:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[14]);
    assertEquals("Invalid portfolio number",
            out.toString().split("\n")[18]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[20]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 2 but portfolio number is negative.
   */
  @Test
  public void examinePortfolioWithNegativePfNumber() throws IOException {
    Readable in = new StringReader("2 -6 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Please provide a portfolio number from the options below:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[14]);
    assertEquals("Invalid portfolio number",
            out.toString().split("\n")[18]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[20]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 2 but portfolio number is decimal.
   */
  @Test
  public void examinePortfolioWithDecimalPfNumber() throws IOException {
    Readable in = new StringReader("2 1.5 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Please provide a portfolio number from the options below:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[14]);
    assertEquals("Please enter a valid positive portfolio number",
            out.toString().split("\n")[20]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[24]);
    deleteFile();
  }


  /**
   * display screen when username enters choice number 2 but portfolio number is char.
   */
  @Test
  public void examinePortfolioWithCharPfNumber() throws IOException {
    Readable in = new StringReader("2 c 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Please provide a portfolio number from the options below:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[14]);
    assertEquals("Please enter a valid positive portfolio number",
            out.toString().split("\n")[20]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[24]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 3.
   */
  @Test
  public void portfolioValueAtDateScreenTest() throws IOException {
    Readable in = new StringReader("3 1 2022-11-01 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Please provide a portfolio number from the options below:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("%5s%33s", "1", "admin1.txt"),
            out.toString().split("\n")[15]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 3 but pfNumber > size.
   */
  @Test
  public void portfolioValueWithPfNumberGreaterThanSize() throws IOException {
    Readable in = new StringReader("3 3 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Please provide a portfolio number from the options below:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[14]);
    assertEquals("Invalid portfolio number",
            out.toString().split("\n")[18]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[20]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 3 but portfolio number is negative.
   */
  @Test
  public void portfolioValueWhenPfNumberIsNegative() throws IOException {
    Readable in = new StringReader("3 -6 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Please provide a portfolio number from the options below:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[14]);
    assertEquals("Invalid portfolio number",
            out.toString().split("\n")[18]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[20]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 3 but portfolio number is decimal.
   */
  @Test
  public void portfolioValueWithDecimalPfNumber() throws IOException {
    Readable in = new StringReader("3 1.5 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Please provide a portfolio number from the options below:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[14]);
    assertEquals("Please enter a valid positive portfolio number",
            out.toString().split("\n")[20]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[24]);
    deleteFile();
  }


  /**
   * display screen when username enters choice number 3 but portfolio number is char.
   */
  @Test
  public void portfolioValueWithCharPfNumber() throws IOException {
    Readable in = new StringReader("3 c 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Please provide a portfolio number from the options below:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[14]);
    assertEquals("Please enter a valid positive portfolio number",
            out.toString().split("\n")[20]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[24]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 3 and enters valid pfNumber and valid date.
   */
  @Test
  public void portfolioValueWhenPfNumberValid() throws IOException {
    Readable in = new StringReader("3 1 2022-11-01 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Please provide a portfolio number from the options below:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("%5s%33s", "1", "admin1.txt"),
            out.toString().split("\n")[15]);
    assertEquals("Please enter the date at which you want to see the value of " +
                    "given portfolio(Date format: yyyy-mm-dd):",
            out.toString().split("\n")[18]);
    assertEquals(String.format("%5s%20s%20s%25s", "Company", "Quantity", "Stock Price", "Value"),
            out.toString().split("\n")[21]);
    assertEquals(String.format("%5s%20s%20s%30s", "AAPL", "10.0", "$150.65", "$1506.50"),
            out.toString().split("\n")[22]);
    assertEquals(String.format("%5s%20s%20s%30s", "TSLA", "15.0", "$227.82", "$3417.30"),
            out.toString().split("\n")[23]);
    assertEquals("Total valuation of portfolio: \t$4923.80",
            out.toString().split("\n")[25]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[30]);

    deleteFile();
  }

  /**
   * display screen when username enters choice number 3 and enters valid pfNumber and valid date.
   * here date is few years back eg 2016-10-12
   */
  @Test
  public void portfolioValueWhenPfNumberValidOldDate() throws IOException {
    Readable in = new StringReader("3 1 2016-10-12 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Please provide a portfolio number from the options below:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("%5s%33s", "1", "admin1.txt"),
            out.toString().split("\n")[15]);
    assertEquals("Please enter the date at which you want to see the value of " +
                    "given portfolio(Date format: yyyy-mm-dd):",
            out.toString().split("\n")[18]);
    assertEquals(String.format("%5s%20s%20s%25s", "Company", "Quantity", "Stock Price", "Value"),
            out.toString().split("\n")[21]);
    assertEquals(String.format("%5s%20s%20s%30s", "AAPL", "10.0", "$117.34", "$1173.40"),
            out.toString().split("\n")[22]);
    assertEquals(String.format("%5s%20s%20s%30s", "TSLA", "15.0", "$201.51", "$3022.65"),
            out.toString().split("\n")[23]);
    assertEquals("Total valuation of portfolio: \t$4196.05",
            out.toString().split("\n")[25]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[30]);

    deleteFile();
  }

  /**
   * display screen when username enters choice number 3 and enters valid pfNumber and invalid date.
   * example 20221101.
   */
  @Test
  public void portfolioValueWhenDateInvalid() throws IOException {
    Readable in = new StringReader("3 1 20221101 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Please provide a portfolio number from the options below:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("%5s%33s", "1", "admin1.txt"),
            out.toString().split("\n")[15]);
    assertEquals("Please enter the date at which you want to see the value of " +
                    "given portfolio(Date format: yyyy-mm-dd):",
            out.toString().split("\n")[18]);
    assertEquals("Invalid Date format.Please enter the date in yyyy-mm-dd",
            out.toString().split("\n")[21]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[25]);

    deleteFile();
  }

  /**
   * display screen when username enters choice number 3 and enters valid pfNumber and invalid date.
   * example 2029-11-01.
   * here year > 2022.
   */
  @Test
  public void portfolioValueWhenYearGreaterThanCurrentYear() throws IOException {
    Readable in = new StringReader("3 1 2029-11-01 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Please provide a portfolio number from the options below:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("%5s%33s", "1", "admin1.txt"),
            out.toString().split("\n")[15]);
    assertEquals("Please enter the date at which you want to see the value of " +
                    "given portfolio(Date format: yyyy-mm-dd):",
            out.toString().split("\n")[18]);
    assertEquals("Invalid Date passed",
            out.toString().split("\n")[21]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[25]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 3 and enters valid pfNumber and invalid date.
   * example 2029-11-01.
   * here month > 12.
   */
  @Test
  public void portfolioValueWithInvalidMonth() throws IOException {
    Readable in = new StringReader("3 1 2029-15-01 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Please provide a portfolio number from the options below:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("%5s%33s", "1", "admin1.txt"),
            out.toString().split("\n")[15]);
    assertEquals("Please enter the date at which you want to see the value of " +
                    "given portfolio(Date format: yyyy-mm-dd):",
            out.toString().split("\n")[18]);
    assertEquals("Invalid Date passed",
            out.toString().split("\n")[21]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[25]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 3 and enters valid pfNumber and invalid date.
   * example 2029-11-33.
   * here day > 2022.
   */
  @Test
  public void portfolioValueWithInvalidDate() throws IOException {
    Readable in = new StringReader("3 1 2029-11-33 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Please provide a portfolio number from the options below:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("%5s%33s", "1", "admin1.txt"),
            out.toString().split("\n")[15]);
    assertEquals("Please enter the date at which you want to see the value of " +
                    "given portfolio(Date format: yyyy-mm-dd):",
            out.toString().split("\n")[18]);
    assertEquals("Invalid Date passed",
            out.toString().split("\n")[21]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[25]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 3 and enters valid pfNumber and valid date.
   * here the valuation is not present in the list since the stock market
   * might be closed for that date because of the weekend.
   */
  @Test
  public void portfolioValueWhenDateIsNotPresentInAPI() throws IOException {
    Readable in = new StringReader("3 1 2022-10-08 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Please provide a portfolio number from the options below:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("%5s%33s", "1", "admin1.txt"),
            out.toString().split("\n")[15]);
    assertEquals("Please enter the date at which you want to see the value of " +
                    "given portfolio(Date format: yyyy-mm-dd):",
            out.toString().split("\n")[18]);
    assertEquals("Invalid Date passed",
            out.toString().split("\n")[21]);
    assertEquals("1. Create Portfolio", out.toString().split("\n")[25]);
    deleteFile();
  }

  @Test
  public void invalidSourceFilePathTest() throws IOException {
    String sourceFile = "/test.txt";
    testDirForUpload = properties.getProperty("path_for_test_savePortfolio");

    Readable in = new StringReader("4 " + testDirForUpload + sourceFile + " 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");

    assertEquals("There was a error in converting a custom portfolio file.\n" +
            "An error occurred while reading from a file.\n" +
            "test_res/filesForSavePortfolio/test.txt (No such file or directory)", res[13]
            + "\n" + res[14] + "\n" + res[15]);
    assertEquals("1. Create Portfolio", res[19]);


  }

  @Test
  public void singleStockLegitTest() throws IOException {
    String sourceFile = "/solo_legit.txt";
    testDirForUpload = properties.getProperty("path_for_test_savePortfolio");

    Readable in = new StringReader("4 " + testDirForUpload + sourceFile + " 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");

    assertEquals("Successfully converted custom portfolio file to admin2", res[13]);
    assertEquals("1. Create Portfolio", res[17]);

    deleteFile();

  }

  @Test
  public void multipleStocksLegitTest() throws IOException {
    String sourceFile = "/Multi_legit.txt";
    testDirForUpload = properties.getProperty("path_for_test_savePortfolio");

    Readable in = new StringReader("4 " + testDirForUpload + sourceFile + " 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    String[] res = out.toString().split("\n");
    assertEquals("Successfully converted custom portfolio file to admin2", res[13]);
    assertEquals("1. Create Portfolio", res[17]);

    deleteFile();
  }

  @Test
  public void singleStockLegitWithSpaceTest() throws IOException {
    String sourceFile = "/solo_legit_space.txt";
    testDirForUpload = properties.getProperty("path_for_test_savePortfolio");

    Readable in = new StringReader("4 " + testDirForUpload + sourceFile + " 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    String[] res = out.toString().split("\n");
    assertEquals("Successfully converted custom portfolio file to admin2", res[13]);
    assertEquals("1. Create Portfolio", res[17]);

    deleteFile();

  }

  @Test
  public void multipleStocksLegitWithSpaceTest() throws IOException {
    String sourceFile = "/multi_legit_space.txt";
    testDirForUpload = properties.getProperty("path_for_test_savePortfolio");

    Readable in = new StringReader("4 " + testDirForUpload + sourceFile + " 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();
    String[] res = out.toString().split("\n");
    assertEquals("Successfully converted custom portfolio file to admin2", res[13]);
    assertEquals("1. Create Portfolio", res[17]);

    deleteFile();
  }

  @Test
  public void closeBraceMissingTest() throws IOException {
    String sourceFile = "/close_brace_missing.txt";
    testDirForUpload = properties.getProperty("path_for_test_savePortfolio");

    Readable in = new StringReader("4 " + testDirForUpload + sourceFile + " 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");


    assertEquals("There was a error in converting a custom portfolio file.\n" +
                    "An error occurred while reading from a file.\n" +
                    "Format of portfolio file passed was incorrect please follow given format to " +
                    "create a portfolio file.\n" +
                    " line 1 -> [comma separated list of ticker symbols]\n" +
                    " line 2 -> [comma separated list of quantity of stocks for " +
                    "respective ticker symbol]",
            res[13] + "\n" + res[14] + "\n" + res[15] + "\n" + res[16] + "\n" + res[17]);

    assertEquals("1. Create Portfolio", res[22]);

  }

  @Test
  public void openBraceMissingTest() throws IOException {
    String sourceFile = "/open_brace_missing.txt";
    testDirForUpload = properties.getProperty("path_for_test_savePortfolio");

    Readable in = new StringReader("4 " + testDirForUpload + sourceFile + " 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");

    assertEquals("There was a error in converting a custom portfolio file.\n" +
                    "An error occurred while reading from a file.\n" +
                    "Format of portfolio file passed was incorrect please follow given format " +
                    "to create a portfolio file.\n" +
                    " line 1 -> [comma separated list of ticker symbols]\n" +
                    " line 2 -> [comma separated list of quantity of stocks for " +
                    "respective ticker symbol]",
            res[13] + "\n" + res[14] + "\n" + res[15] + "\n" + res[16] + "\n" + res[17]);

    assertEquals("1. Create Portfolio", res[22]);

  }

  @Test
  public void quantityMissingTest() throws IOException {
    String sourceFile = "/quantity_missing.txt";
    testDirForUpload = properties.getProperty("path_for_test_savePortfolio");

    Readable in = new StringReader("4 " + testDirForUpload + sourceFile + " 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");

    assertEquals("There was a error in converting a custom portfolio file.\n" +
                    "An error occurred while reading from a file.\n" +
                    "Invalid quantity in quantity list",
            res[13] + "\n" + res[14] + "\n" + res[15]);

    assertEquals("1. Create Portfolio", res[19]);


  }

  @Test
  public void symbolMissingTest() throws IOException {
    String sourceFile = "/symbol_missing.txt";
    testDirForUpload = properties.getProperty("path_for_test_savePortfolio");

    Readable in = new StringReader("4 " + testDirForUpload + sourceFile + " 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");

    assertEquals("There was a error in converting a custom portfolio file.\n" +
                    "An error occurred while reading from a file.\n" +
                    "Invalid ticker symbol in symbol list.",
            res[13] + "\n" + res[14] + "\n" + res[15]);

    assertEquals("1. Create Portfolio", res[19]);

  }

  @Test
  public void symbolOutsideBracesTest() throws IOException {
    String sourceFile = "/symbol_outside.txt";
    testDirForUpload = properties.getProperty("path_for_test_savePortfolio");

    Readable in = new StringReader("4 " + testDirForUpload + sourceFile + " 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");

    assertEquals("There was a error in converting a custom portfolio file.\n" +
                    "An error occurred while reading from a file.\n" +
                    "Format of portfolio file passed was incorrect please follow given " +
                    "format to create a portfolio file.\n" +
                    " line 1 -> [comma separated list of ticker symbols]\n" +
                    " line 2 -> [comma separated list of quantity of stocks " +
                    "for respective ticker symbol]",
            res[13] + "\n" + res[14] + "\n" + res[15] + "\n" + res[16] + "\n" + res[17]);

    assertEquals("1. Create Portfolio", res[22]);

  }

  @Test
  public void quantityOutsideBracesTest() throws IOException {
    String sourceFile = "/quantity_outside.txt";
    testDirForUpload = properties.getProperty("path_for_test_savePortfolio");

    Readable in = new StringReader("4 " + testDirForUpload + sourceFile + " 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");

    assertEquals("There was a error in converting a custom portfolio file.\n" +
                    "An error occurred while reading from a file.\n" +
                    "Format of portfolio file passed was incorrect please follow " +
                    "given format to create a portfolio file.\n" +
                    " line 1 -> [comma separated list of ticker symbols]\n" +
                    " line 2 -> [comma separated list of quantity of stocks " +
                    "for respective ticker symbol]",
            res[13] + "\n" + res[14] + "\n" + res[15] + "\n" + res[16] + "\n" + res[17]);

    assertEquals("1. Create Portfolio", res[22]);

  }

  @Test
  public void negativeQuantityTest() throws IOException {
    String sourceFile = "/negative_quantity.txt";
    testDirForUpload = properties.getProperty("path_for_test_savePortfolio");

    Readable in = new StringReader("4 " + testDirForUpload + sourceFile + " 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");

    assertEquals("There was a error in converting a custom portfolio file.\n" +
                    "An error occurred while reading from a file.\n" +
                    "Invalid quantity in quantity list",
            res[13] + "\n" + res[14] + "\n" + res[15]);

    assertEquals("1. Create Portfolio", res[19]);

  }

  @Test
  public void decimalQuantityTest() throws IOException {
    String sourceFile = "/decimal_quantity.txt";
    testDirForUpload = properties.getProperty("path_for_test_savePortfolio");

    Readable in = new StringReader("4 " + testDirForUpload + sourceFile + " 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");

    assertEquals("There was a error in converting a custom portfolio file.\n" +
                    "An error occurred while reading from a file.\n" +
                    "Invalid quantity in quantity list",
            res[13] + "\n" + res[14] + "\n" + res[15]);

    assertEquals("1. Create Portfolio", res[19]);

  }

  @Test
  public void invalidFileFormatTest() throws IOException {
    String sourceFile = "/dummy.json";
    testDirForUpload = properties.getProperty("path_for_test_savePortfolio");

    Readable in = new StringReader("4 " + testDirForUpload + sourceFile + " 5");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new InflexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");

    assertEquals("There was a error in converting a custom portfolio file.\n" +
                    "An error occurred while reading from a file.\n" +
                    "Format of portfolio file passed was incorrect please follow " +
                    "given format to create a portfolio file.\n" +
                    " line 1 -> [comma separated list of ticker symbols]\n" +
                    " line 2 -> [comma separated list of quantity of stocks " +
                    "for respective ticker symbol]",
            res[13] + "\n" + res[14] + "\n" + res[15] + "\n" + res[16] + "\n" + res[17]);

    assertEquals("1. Create Portfolio", res[22]);
    deleteFile();
  }

  private void deleteFile() {

    String path = properties.getProperty("resource_file") + "/"
            + properties.getProperty("default_user") + "/";
    File file = new File(path + "admin2.txt");
    file.delete();
  }

  private void copyFolder(Path source, Path target, CopyOption... options)
          throws IOException {
    Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
              throws IOException {
        Files.createDirectories(target.resolve(source.relativize(dir)));
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
              throws IOException {
        Files.copy(file, target.resolve(source.relativize(file)), options);
        return FileVisitResult.CONTINUE;
      }
    });
  }


}