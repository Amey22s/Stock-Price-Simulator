package controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

import model.AdvancedModelInterface;
import model.FlexibleStockModel;
import view.StockView;

import static org.junit.Assert.assertEquals;

/**
 * Junit tests for integration testing between model, view and Flexible controller.
 */
public class FlexibleControllerIntegrationTest {
  private Properties properties;

  private AdvancedModelInterface model;
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
    properties.setProperty("resource_file", properties.getProperty("flex_resource_file"));

    FileOutputStream output = new FileOutputStream("src/config.properties");

    properties.store(output, "Flex controller test start");

    String codePath = properties.getProperty("resource_file");
    String testPath = properties.getProperty("path_for_flex_test");
    String codeDump = properties.getProperty("path_for_dump");

    new File(codeDump).mkdir();

    model = new FlexibleStockModel();

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
            properties.getProperty("path_for_flex_test_savePortfolio");

    copyFolder(Path.of(codeDump), Path.of(codePath), StandardCopyOption.REPLACE_EXISTING);

    deleteDirectory(new File(codeDump));

    InputStream input = new FileInputStream("src/config.properties");
    properties.load(input);
    properties.setProperty("resource_file", "portfolios");

    FileOutputStream output = new FileOutputStream("src/config.properties");

    properties.store(output, "flex controller test end");

  }

  /**
   * display screen when MainController is run and the user selects option 1 that is
   * Flexible Portfolio.
   */
  @Test
  public void flexibleControllerStarterScreenTest() throws IOException {
    Readable in = new StringReader("8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("1. Examine portfolio", out.toString().split("\n")[2]);
    assertEquals("2. Buy Stock", out.toString().split("\n")[3]);
    assertEquals("3. Sell Stock", out.toString().split("\n")[4]);
    assertEquals("4. Calculate Cost Basis", out.toString().split("\n")[5]);
    assertEquals("5. Calculate Portfolio value at a specific date",
            out.toString().split("\n")[6]);
    assertEquals("6. Performance Graph", out.toString().split("\n")[7]);
    assertEquals("7. Upload custom portfolio", out.toString().split("\n")[8]);
    assertEquals("8. Exit", out.toString().split("\n")[9]);
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
  }

  /**
   * display screen when choice entered is invalid ie 5 < ch <1.
   */
  @Test
  public void choiceIsInvalidInputTestForFlexController() throws IOException {
    Readable in = new StringReader("9 8");
    Appendable out = new StringBuilder();

    Readable in2 = new StringReader("-2 8");
    Appendable out2 = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("!!! Invalid input !!!", out.toString().split("\n")[13]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[16]);

    BaseControllerInterface sc2 = new FlexibleController(
            model, new StockView(), in2, out2);
    sc2.goController();
    assertEquals("!!! Invalid input !!!", out2.toString().split("\n")[13]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[16]);
  }

  /**
   * display screen when username enters choice number 1.
   */
  @Test
  public void examinePortfolioScreenTest() throws IOException {
    Readable in = new StringReader("1 1 2022-11-14 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[18]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 1 and examines portfolio no 1.
   */
  @Test
  public void examinePortfolioValidTest() throws IOException {
    Readable in = new StringReader("1 1 2022-11-14 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[18]);
    assertEquals("Please enter the date at which you want to perform operation on given" +
            " (Date format: yyyy-mm-dd):", out.toString().split("\n")[21]);
    assertEquals(String.format("%5s%20s", "Company", "Quantity"),
            out.toString().split("\n")[24]);
    assertEquals(String.format("%5s%20s", "V", "120.00"),
            out.toString().split("\n")[25]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 1 but enters invalid portfolio number.
   */
  @Test
  public void examinePortfolioInvalidInputTest() throws IOException {
    Readable in = new StringReader("1 3 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[18]);
    assertEquals("Invalid portfolio number", out.toString().split("\n")[25]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[29]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 1 but enters invalid portfolio number.
   * negative pf.
   */
  @Test
  public void examinePortfolioInvalidNegInputTest() throws IOException {
    Readable in = new StringReader("1 -2 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[18]);
    assertEquals("Invalid portfolio number", out.toString().split("\n")[25]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[29]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 1 but enters valid portfolio number
   * but invalid date.
   */
  @Test
  public void examinePortfolioInvalidDateTest() throws IOException {
    Readable in = new StringReader("1 1 2022-11-20 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[18]);
    assertEquals("Something went wrong please try again with a different set of input.",
            out.toString().split("\n")[25]);
    assertEquals("Invalid Date passed", out.toString().split("\n")[26]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[30]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 2 and wants to buy a stock in a
   * new portfolio.
   */
  @Test
  public void buyValidStockTestForNewPortfolio() throws IOException {
    Readable in = new StringReader("2 0 2022-11-16 10 VZ 10 N 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Enter 0 to create a new portfolio"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[19]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[20]);
    assertEquals("Please enter the date at which you want to buy/sell stocks in " +
            "a given portfolio(Date format: yyyy-mm-dd):", out.toString().split("\n")[23]);
    assertEquals("Please enter commission percent at which you want to buy/sell " +
            "stocks in a given portfolio:", out.toString().split("\n")[24]);
    assertEquals("Enter stock symbol: ", out.toString().split("\n")[25]);
    assertEquals("Enter stock quantity: ", out.toString().split("\n")[26]);
    assertEquals("Press Y to continue, N to exit: ", out.toString().split("\n")[27]);
    assertEquals("Successfully bought desired stocks into your portfolio " +
            "with name admin2.json", out.toString().split("\n")[30]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[34]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 2 and wants to buy a stock in an
   * existing portfolio.
   */
  @Test
  public void buyValidStockTestForExistingPortfolio() throws IOException {
    Readable in = new StringReader("2 1 2022-11-16 5 V 50 N 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Enter 0 to create a new portfolio"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[19]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[20]);
    assertEquals("Please enter the date at which you want to buy/sell stocks in " +
            "a given portfolio(Date format: yyyy-mm-dd):", out.toString().split("\n")[23]);
    assertEquals("Enter stock symbol: ", out.toString().split("\n")[25]);
    assertEquals("Enter stock quantity: ", out.toString().split("\n")[26]);
    assertEquals("Press Y to continue, N to exit: ", out.toString().split("\n")[27]);
    assertEquals("Successfully bought desired stocks into your portfolio " +
            "with name admin1.json", out.toString().split("\n")[30]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[34]);

    deleteFile();
  }

  /**
   * display screen when username enters choice number 1 and examines portfolio no 1 after buy.
   */
  @Test
  public void examinePortfolioAfterBuyValidTest() throws IOException {
    Readable in = new StringReader("2 1 2022-11-16 10 V 50 N 1 1 2022-11-16 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Enter 0 to create a new portfolio"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[19]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[20]);
    assertEquals("Please enter the date at which you want to buy/sell stocks in " +
            "a given portfolio(Date format: yyyy-mm-dd):", out.toString().split("\n")[23]);
    assertEquals("Enter stock symbol: ", out.toString().split("\n")[25]);
    assertEquals("Enter stock quantity: ", out.toString().split("\n")[26]);
    assertEquals("Press Y to continue, N to exit: ", out.toString().split("\n")[27]);
    assertEquals("Successfully bought desired stocks into your portfolio " +
            "with name admin1.json", out.toString().split("\n")[30]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[34]);
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[44]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[49]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[50]);
    assertEquals("Please enter the date at which you want to perform operation on given" +
            " (Date format: yyyy-mm-dd):", out.toString().split("\n")[53]);
    assertEquals(String.format("%5s%20s", "Company", "Quantity"),
            out.toString().split("\n")[56]);
    assertEquals(String.format("%5s%20s", "V", "170.00"),
            out.toString().split("\n")[57]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[62]);

    deleteFile();
  }

  /**
   * choice 2 but user enters invalid pf number.
   */
  @Test
  public void buyStockInvalidPfNumber() throws IOException {
    Readable in = new StringReader("2 3 N 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Enter 0 to create a new portfolio"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[19]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[20]);
    assertEquals("Invalid portfolio number", out.toString().split("\n")[27]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[31]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 2 and wants to buy a stock in an
   * existing portfolio but invalid date.
   */
  @Test
  public void buyStockInvalidDate() throws IOException {
    Readable in = new StringReader("2 1 2022-11-12 5 2 1 2022/11/12 5 2 1 2022-12-03 5 8 ");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Enter 0 to create a new portfolio"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[19]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[20]);
    assertEquals("Please enter the date at which you want to buy/sell stocks in " +
            "a given portfolio(Date format: yyyy-mm-dd):", out.toString().split("\n")[23]);
    assertEquals("Something went wrong please try again with a different set of input.",
            out.toString().split("\n")[28]);
    assertEquals("Invalid Date passed", out.toString().split("\n")[29]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[33]);
    assertEquals("Could not parse the date correctlyUnparseable date: \"2022/11/12\"",
            out.toString().split("\n")[60]);
    assertEquals("Invalid Date passed", out.toString().split("\n")[91]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 2 and wants to sell a stock in an
   * existing portfolio.
   */
  @Test
  public void sellValidStockTestForExistingPortfolio() throws IOException {
    Readable in = new StringReader("3 1 2015-02-05 5 V 10 N 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Enter 0 to create a new portfolio"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[19]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[20]);
    assertEquals("Please enter the date at which you want to buy/sell stocks in " +
            "a given portfolio(Date format: yyyy-mm-dd):", out.toString().split("\n")[23]);
    assertEquals("Successfully sold desired stocks from your portfolio with name" +
            " admin1.json", out.toString().split("\n")[30]);
    deleteFile();
  }

  /**
   * choice 2 but user enters invalid pf number.
   */
  @Test
  public void sellStockInvalidPfNumber() throws IOException {
    Readable in = new StringReader("3 3 N 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Enter 0 to create a new portfolio"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[19]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[20]);
    assertEquals("Invalid portfolio number", out.toString().split("\n")[27]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[31]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 2 and wants to buy a stock in an
   * existing portfolio but invalid date.
   */
  @Test
  public void sellStockInvalidDate() throws IOException {
    Readable in = new StringReader("3 1 2022-11-12 5 3 1 2022/11/12 5 3 1 2022-12-03 5 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Enter 0 to create a new portfolio"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[19]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[20]);
    assertEquals("Please enter the date at which you want to buy/sell stocks in " +
            "a given portfolio(Date format: yyyy-mm-dd):", out.toString().split("\n")[23]);
    assertEquals("Something went wrong please try again with a different set of input.",
            out.toString().split("\n")[28]);
    assertEquals("Invalid Date passed", out.toString().split("\n")[29]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[33]);
    assertEquals("Could not parse the date correctlyUnparseable date: \"2022/11/12\"",
            out.toString().split("\n")[60]);
    assertEquals("Invalid Date passed", out.toString().split("\n")[91]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 3 and wants to buy a stock in an
   * existing portfolio but invalid date.
   */
  @Test
  public void sellStockInvalidDateBeforeBuying() throws IOException {
    Readable in = new StringReader("3 1 2015-02-01 5 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Enter 0 to create a new portfolio"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[19]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[20]);
    assertEquals("Please enter the date at which you want to buy/sell stocks in " +
            "a given portfolio(Date format: yyyy-mm-dd):", out.toString().split("\n")[23]);
    assertEquals("Something went wrong please try again with a different set of input.",
            out.toString().split("\n")[28]);
    assertEquals("Invalid Date passed", out.toString().split("\n")[29]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[33]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 3 and wants to buy a stock in an
   * existing portfolio but invalid quantity that is more than buy.
   */
  @Test
  public void sellStockInvalidQuantity() throws IOException {
    Readable in = new StringReader("3 1 2015-02-05 5 V 30 N 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Enter 0 to create a new portfolio"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[19]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[20]);
    assertEquals("Please enter the date at which you want to buy/sell stocks in " +
            "a given portfolio(Date format: yyyy-mm-dd):", out.toString().split("\n")[23]);
    assertEquals("Something went wrong please try again with a different set of input.",
            out.toString().split("\n")[31]);
    assertEquals("Invalid transaction." +
            " Quantity of selling shares is greater than quantity" +
            " of buying shares at this specific date", out.toString().split("\n")[32]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[36]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 4 and wants to calculate cost basis
   * with valid date and commission number.
   */
  @Test
  public void calculateCostBasisValid() throws IOException {
    Readable in = new StringReader("4 1 2022-11-16 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[15]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[18]);
    assertEquals("Please enter the date at which you want to perform operation on " +
            "given (Date format: yyyy-mm-dd):", out.toString().split("\n")[21]);
    assertEquals("Total cost basis of the given portfolio is $14063.80",
            out.toString().split("\n")[24]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[28]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 4 and wants to calculate cost basis
   * when date is before the first buying date, cost basis should be 0.
   */
  @Test
  public void calculateCostBasisDateBeforeFirstBuy() throws IOException {
    Readable in = new StringReader("4 1 2015-02-02 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[15]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[18]);
    assertEquals("Please enter the date at which you want to perform operation on " +
            "given (Date format: yyyy-mm-dd):", out.toString().split("\n")[21]);
    assertEquals("Total cost basis of the given portfolio is $0.00",
            out.toString().split("\n")[24]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[28]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 4 and wants to calculate cost basis
   * when date is invalid.
   */
  @Test
  public void calculateCostBasisInvalidDate() throws IOException {
    Readable in = new StringReader("4 1 2022-12-03 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[15]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[18]);
    assertEquals("Please enter the date at which you want to perform operation on " +
            "given (Date format: yyyy-mm-dd):", out.toString().split("\n")[21]);
    assertEquals("Invalid Date passed", out.toString().split("\n")[26]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[30]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 2 and wants to buy stocks
   * but commission is negative.
   */
  @Test
  public void calculateBuyWithNegativeCommission() throws IOException {
    Readable in = new StringReader("2 1 2015-02-05 -5 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Enter 0 to create a new portfolio"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[19]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[20]);
    assertEquals("Please enter the date at which you want to buy/sell stocks in " +
            "a given portfolio(Date format: yyyy-mm-dd):", out.toString().split("\n")[23]);
    assertEquals("Something went wrong please try again with a different set of input.",
            out.toString().split("\n")[28]);
    assertEquals("Commission cannot be negative.", out.toString().split("\n")[29]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[33]);
    deleteFile();
  }


  /**
   * display screen when username enters choice number 3 and wants to sell stocks
   * but commission is negative.
   */
  @Test
  public void calculateSellWithNegativeCommission() throws IOException {
    Readable in = new StringReader("3 1 2015-02-05 -5 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);
    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Enter 0 to create a new portfolio"),
            out.toString().split("\n")[14]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[19]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[20]);
    assertEquals("Please enter the date at which you want to buy/sell stocks in " +
            "a given portfolio(Date format: yyyy-mm-dd):", out.toString().split("\n")[23]);
    assertEquals("Something went wrong please try again with a different set of input.",
            out.toString().split("\n")[28]);
    assertEquals("Commission cannot be negative.", out.toString().split("\n")[29]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[33]);
    deleteFile();
  }

  /**
   * choice 3 but user enters invalid pf number.
   */
  @Test
  public void costBasisInvalidPfNumber() throws IOException {
    Readable in = new StringReader("4 3 N 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[15]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[18]);
    assertEquals("Invalid portfolio number", out.toString().split("\n")[25]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[29]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 5 and wants to calculate valuation
   * of a portfolio at a specific date.
   */
  @Test
  public void calculateValuationValid() throws IOException {
    Readable in = new StringReader("5 1 2022-11-16 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[15]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[18]);
    assertEquals("Please enter the date at which you want to perform operation on " +
            "given (Date format: yyyy-mm-dd):", out.toString().split("\n")[21]);
    assertEquals(String.format("%5s%20s", "Company", "Quantity"),
            out.toString().split("\n")[24]);
    assertEquals(String.format("%5s%20s", "V", "120.00"),
            out.toString().split("\n")[25]);
    assertEquals("Total valuation of the above mentioned portfolio is $25216.80",
            out.toString().split("\n")[30]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[34]);
    deleteFile();
  }

  /**
   * choice 5 but user enters invalid pf number.
   */
  @Test
  public void pfValueInvalidPfNumber() throws IOException {
    Readable in = new StringReader("5 3 N 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[15]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[18]);
    assertEquals("Invalid portfolio number", out.toString().split("\n")[25]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[29]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 5 and wants to calculate cost basis
   * when date is invalid.
   */
  @Test
  public void pfValueInvalidDate() throws IOException {
    Readable in = new StringReader("5 1 2022-12-03 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[15]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[18]);
    assertEquals("Please enter the date at which you want to perform operation on " +
            "given (Date format: yyyy-mm-dd):", out.toString().split("\n")[21]);
    assertEquals("Invalid Date passed", out.toString().split("\n")[26]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[30]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 5 and wants to calculate pf value
   * when date is before the first buying date, pfValue should be 0.
   */
  @Test
  public void pfValueDateBeforeFirstBuy() throws IOException {
    Readable in = new StringReader("5 1 2015-02-02 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[15]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[18]);
    assertEquals("Please enter the date at which you want to perform operation on " +
            "given (Date format: yyyy-mm-dd):", out.toString().split("\n")[21]);
    assertEquals("Total valuation of the above mentioned portfolio is $0.00",
            out.toString().split("\n")[29]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[33]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 6 and wants to display the performance
   * of a portfolio between a given range of date.
   */
  @Test
  public void pfPerformanceValid() throws IOException {
    Readable in = new StringReader("6 1 2015-02-03 2017-04-14 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();

    String[] res = out.toString().split("\n");
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[15]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[18]);
    assertEquals("Please enter the date at which you want to perform operation on " +
            "given (Date format: yyyy-mm-dd):", out.toString().split("\n")[21]);
    assertEquals("Please enter the end date at which you want to perform operation on " +
            "given (Date format: yyyy-mm-dd):", out.toString().split("\n")[22]);
    assertEquals("Performance of portfolio no. 1 from 2015-02-03 to 2017-04-14",
            out.toString().split("\n")[23]);

    StringBuilder expected = new StringBuilder();
    for (int i = 25; i < 53; i++) {
      expected.append(res[i]).append("\n");
    }

    assertEquals("Feb 2015 : *****\nMar 2015 : *\nApr 2015 : **\nMay 2015 : **\n" +
                    "Jun 2015 : **\nJul 2015 : **\nAug 2015 : **\nSep 2015 : **\nOct 2015 : **" +
                    "\nNov 2015 : **\nDec 2015 : **\nJan 2016 : **\nFeb 2016 : **\nMar 2016 : **" +
                    "\nApr 2016 : **\nMay 2016 : **\nJun 2016 : **\nJul 2016 : **\nAug 2016 : **" +
                    "\nSep 2016 : **\nOct 2016 : **\nNov 2016 : **\nDec 2016 : **\nJan 2017 : **" +
                    "\nFeb 2017 : **\nMar 2017 : **\nApr 2017 : " +
                    "*********\nScale =  *   : $1308.20\n"
            , expected.toString());
    assertEquals("1. Examine portfolio", out.toString().split("\n")[55]);
    deleteFile();
  }

  /**
   * choice 5 but user enters invalid pf number.
   */
  @Test
  public void pfPerformanceInvalidPfNumber() throws IOException {
    Readable in = new StringReader("6 3 N 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[15]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[18]);
    assertEquals("Invalid portfolio number", out.toString().split("\n")[25]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[29]);
    deleteFile();
  }

  /**
   * display screen when username enters choice number 5 and wants to calculate cost basis
   * when date is invalid.
   */
  @Test
  public void pfPerformanceInvalidDate() throws IOException {
    Readable in = new StringReader("6 1 2015-02-03 2022-12-03 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[12]);
    assertEquals(String.format("Please provide a portfolio number from the options below:"),
            out.toString().split("\n")[15]);
    assertEquals(String.format("%10s%20s", "Portfolio Number", "Filename"),
            out.toString().split("\n")[17]);
    assertEquals(String.format("%5s%33s", "1", "admin1.json"),
            out.toString().split("\n")[18]);
    assertEquals("Please enter the date at which you want to perform operation on " +
            "given (Date format: yyyy-mm-dd):", out.toString().split("\n")[21]);
    assertEquals("Please enter the end date at which you want to perform operation on " +
            "given (Date format: yyyy-mm-dd):", out.toString().split("\n")[22]);
    assertEquals("Invalid Date passed", out.toString().split("\n")[27]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[31]);
    deleteFile();
  }

  //*************************************************************************************

  /**
   * display screen when choice entered is invalid ie 8 < ch < 1.
   */
  @Test
  public void choiceIsInvalidInputTest() throws IOException {
    Readable in = new StringReader(" 9 8");
    Appendable out = new StringBuilder();

    Readable in2 = new StringReader("-2 8");
    Appendable out2 = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);

    sc.goController();
    assertEquals("!!! Invalid input !!!", out.toString().split("\n")[13]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[16]);

    BaseControllerInterface sc2 = new FlexibleController(model, new StockView(), in2, out2);
    sc2.goController();
    assertEquals("!!! Invalid input !!!", out2.toString().split("\n")[13]);
    assertEquals("1. Examine portfolio", out.toString().split("\n")[16]);
  }

  @Test
  public void invalidSourceFilePathTest() throws IOException {
    String sourceFile = "/test.txt";
    testDirForUpload = properties.getProperty("path_for_flex_test_savePortfolio");

    Readable in = new StringReader("7 " + testDirForUpload + sourceFile + " 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");

    assertEquals("Something went wrong please try again with a different set of input.\n" +
                    "Error occurred while reading from a file\n" +
                    "test_res_flex/filesForSavePortfolio/test.txt (No such file or directory)",
            res[17]
                    + "\n" + res[18] + "\n" + res[19]);
    assertEquals("1. Examine portfolio", res[23]);


  }

  @Test
  public void singleStockLegitTest() throws IOException {
    String sourceFile = "/solo_legit.json";
    testDirForUpload = properties.getProperty("path_for_flex_test_savePortfolio");


    Readable in = new StringReader("7 " + testDirForUpload + sourceFile + " 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");

    assertEquals("Successfully converted custom portfolio file to admin2", res[16]);
    assertEquals("1. Examine portfolio", res[20]);

    deleteFile();

  }

  @Test
  public void multipleStocksLegitTest() throws IOException {
    String sourceFile = "/Multi_legit.json";
    testDirForUpload = properties.getProperty("path_for_flex_test_savePortfolio");


    Readable in = new StringReader("7 " + testDirForUpload + sourceFile + " 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);
    sc.goController();
    String[] res = out.toString().split("\n");
    assertEquals("Successfully converted custom portfolio file to admin2", res[16]);
    assertEquals("1. Examine portfolio", res[20]);

    deleteFile();
  }

  @Test
  public void buySellLegitTest() throws IOException {
    String sourceFile = "/solo_legit_buysell.json";
    testDirForUpload = properties.getProperty("path_for_flex_test_savePortfolio");


    Readable in = new StringReader("7 " + testDirForUpload + sourceFile + " 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);
    sc.goController();
    String[] res = out.toString().split("\n");
    assertEquals("Successfully converted custom portfolio file to admin2", res[16]);
    assertEquals("1. Examine portfolio", res[20]);

    deleteFile();

  }

  @Test
  public void openBraceMissingTest() throws IOException {
    String sourceFile = "/open_brace_missing.json";
    testDirForUpload = properties.getProperty("path_for_flex_test_savePortfolio");


    Readable in = new StringReader("7 " + testDirForUpload + sourceFile + " 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");

    assertEquals("Something went wrong please try again with a different set of input.\n" +
                    "Error occurred while reading from a file",
            res[17] + "\n" + res[18]);

    assertEquals("1. Examine portfolio", res[23]);

  }

  @Test
  public void sellBeforeBuyTest() throws IOException {
    String sourceFile = "/sell_before_buy.json";
    testDirForUpload = properties.getProperty("path_for_flex_test_savePortfolio");


    Readable in = new StringReader("7 " + testDirForUpload + sourceFile + " 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");


    assertEquals("Something went wrong please try again with a different set of input.\n" +
                    "Error occurred while reading from a file\n" +
                    "Invalid Transaction. " +
                    "Kindly make sure that the selling date is not before the buying " +
                    "date. Moreover make sure the stock ticker is present in the " +
                    "current portfolio",
            res[17] + "\n" + res[18] + "\n" + res[19]);

    assertEquals("1. Examine portfolio", res[23]);

  }

  @Test
  public void sellMoreThanBuyTest() throws IOException {
    String sourceFile = "/sell_more_than_buy.json";
    testDirForUpload = properties.getProperty("path_for_flex_test_savePortfolio");


    Readable in = new StringReader("7 " + testDirForUpload + sourceFile + " 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");


    assertEquals("Something went wrong please try again with a different set of input.\n" +
                    "Error occurred while reading from a file\n" +
                    "Invalid transaction. " +
                    "Quantity of selling shares is greater than quantity of buying shares " +
                    "at this specific date",
            res[17] + "\n" + res[18] + "\n" + res[19]);

    assertEquals("1. Examine portfolio", res[23]);

  }

  @Test
  public void sellStockNotPresentTest() throws IOException {
    String sourceFile = "/sell_stock_not_present.json";
    testDirForUpload = properties.getProperty("path_for_flex_test_savePortfolio");


    Readable in = new StringReader("7 " + testDirForUpload + sourceFile + " 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");


    assertEquals("Something went wrong please try again with a different set of input.\n" +
                    "Error occurred while reading from a file\n" +
                    "Invalid Transaction. " +
                    "Kindly make sure that the selling date is not before the buying " +
                    "date. Moreover make sure the stock ticker is present in the " +
                    "current portfolio",
            res[17] + "\n" + res[18] + "\n" + res[19]);

    assertEquals("1. Examine portfolio", res[23]);

  }

  @Test
  public void quantityMissingTest() throws IOException {
    String sourceFile = "/quantity_missing.json";
    testDirForUpload = properties.getProperty("path_for_flex_test_savePortfolio");


    Readable in = new StringReader("7 " + testDirForUpload + sourceFile + " 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");

    assertEquals("Something went wrong please try again with a different set of input.\n" +
                    "Error occurred while reading from a file",
            res[17] + "\n" + res[18]);

    assertEquals("1. Examine portfolio", res[23]);


  }

  @Test
  public void symbolMissingTest() throws IOException {
    String sourceFile = "/symbol_missing.json";
    testDirForUpload = properties.getProperty("path_for_flex_test_savePortfolio");


    Readable in = new StringReader("7 " + testDirForUpload + sourceFile + " 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");

    assertEquals("Something went wrong please try again with a different set of input.\n" +
                    "Error occurred while reading from a file",
            res[17] + "\n" + res[18]);

    assertEquals("1. Examine portfolio", res[23]);

  }

  @Test
  public void symbolOutsideBracesTest() throws IOException {
    String sourceFile = "/symbol_outside.json";
    testDirForUpload = properties.getProperty("path_for_flex_test_savePortfolio");


    Readable in = new StringReader("7 " + testDirForUpload + sourceFile + " 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");

    assertEquals("Something went wrong please try again with a different set of input.\n" +
                    "Error occurred while reading from a file",
            res[17] + "\n" + res[18]);

    assertEquals("1. Examine portfolio", res[23]);

  }

  @Test
  public void quantityOutsideBracesTest() throws IOException {
    String sourceFile = "/quantity_outside.json";
    testDirForUpload = properties.getProperty("path_for_flex_test_savePortfolio");


    Readable in = new StringReader("7 " + testDirForUpload + sourceFile + " 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");

    assertEquals("Something went wrong please try again with a different set of input.\n" +
                    "Error occurred while reading from a file",
            res[17] + "\n" + res[18]);

    assertEquals("1. Examine portfolio", res[23]);

  }

  @Test
  public void negativeQuantityTest() throws IOException {
    String sourceFile = "/negative_quantity.json";
    testDirForUpload = properties.getProperty("path_for_flex_test_savePortfolio");


    Readable in = new StringReader("7 " + testDirForUpload + sourceFile + " 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");


    assertEquals("Something went wrong please try again with a different set of input.\n" +
                    "Error occurred while reading from a file\n" +
                    "Invalid transaction. " +
                    "Quantity of selling shares is greater than quantity of buying shares " +
                    "at this specific date",
            res[17] + "\n" + res[18] + "\n" + res[19]);

    assertEquals("1. Examine portfolio", res[23]);

  }

  @Test
  public void decimalQuantityTest() throws IOException {
    String sourceFile = "/decimal_quantity.json";
    testDirForUpload = properties.getProperty("path_for_flex_test_savePortfolio");


    Readable in = new StringReader("7 " + testDirForUpload + sourceFile + " 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");


    assertEquals("Something went wrong please try again with a different set of input.\n" +
                    "Error occurred while reading from a file",
            res[17] + "\n" + res[18]);

    assertEquals("1. Examine portfolio", res[23]);

  }

  @Test
  public void invalidFileFormatTest() throws IOException {
    String sourceFile = "/dummy.txt";
    testDirForUpload = properties.getProperty("path_for_flex_test_savePortfolio");


    Readable in = new StringReader("7 " + testDirForUpload + sourceFile + " 8");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new FlexibleController(model, new StockView(), in, out);
    sc.goController();

    String[] res = out.toString().split("\n");


    assertEquals("Something went wrong please try again with a different set of input.\n" +
            "Error occurred while reading from a file", res[17]
            + "\n" + res[18]);
    assertEquals("1. Examine portfolio", res[23]);
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
