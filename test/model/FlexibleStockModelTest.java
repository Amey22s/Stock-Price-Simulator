package model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * JUnit test class to test Flexible stock model and its functionalities in isolation.
 */
public class FlexibleStockModelTest {

  private AdvancedModelInterface model;
  private Properties properties;

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

    properties.store(output, "model test start");

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
            properties.getProperty("path_for_test_savePortfolio");

    copyFolder(Path.of(codeDump), Path.of(codePath), StandardCopyOption.REPLACE_EXISTING);

    deleteDirectory(new File(codeDump));

    InputStream input = new FileInputStream("src/config.properties");
    properties.load(input);
    properties.setProperty("resource_file", "portfolios");

    FileOutputStream output = new FileOutputStream("src/config.properties");

    properties.store(output, "model test end");

  }

  @Test
  public void checkUserCredTest() {
    String user = "admin";
    String pwd = "Asdf@1234";
    assertTrue(model.authenticateCredentials(user, pwd));

    String user1 = "admin1";
    assertFalse(model.authenticateCredentials(user1, pwd));


    String pwd1 = "Asdf@123";
    assertFalse(model.authenticateCredentials(user, pwd1));
  }

  @Test
  public void checkBuyTransact() {

    Map<String, Double> map = new HashMap<>();
    map.put("AAPL", 10.0);
    map.put("TSLA", 15.0);

    String path = properties.getProperty("resource_file") + "/" +
            properties.getProperty("default_user") + "/";
    String pfName = model.transact(map, 0, 10, "2022-10-04", 'b');

    assertEquals(model.getUser() + "2.json", pfName);

    Map<String, Double> map1 = new HashMap<>();
    map1.put("V", 10.0);
    String pfName1 = model.transact(map1, 2, 10, "2022-11-01", 'b');

    assertEquals(pfName1, pfName);

    File file = new File(path + pfName1);
    file.delete();

  }

  @Test
  public void checkSellTransact() {

    Map<String, Double> map = new HashMap<>();
    map.put("V", 10.0);

    String path = properties.getProperty("resource_file") + "/" +
            properties.getProperty("default_user") + "/";
    try {
      String pfName = model.transact(map, 0, 10, "2014-10-09", 's');
      fail();
    } catch (RuntimeException e) {
      assertEquals("Invalid Transaction. " +
                      "Kindly make sure that the selling date is not before the buying date. " +
                      "Moreover make sure the stock ticker is present in the current portfolio",
              e.getMessage());
    }

    Map<String, Double> map1 = new HashMap<>();
    map1.put("V", 1000.0);
    try {
      String pfName = model.transact(map1, 1, 10, "2018-10-09", 's');
      fail();
    } catch (RuntimeException e) {
      assertEquals("Invalid transaction. " +
              "Quantity of selling shares is greater than quantity of buying shares" +
              " at this specific date", e.getMessage());
    }


    String pfName = model.transact(map, 1, 10, "2015-02-05", 's');
    assertEquals(model.getUser() + "1.json", pfName);

    File file = new File(path + pfName);
    file.delete();

  }

  @Test
  public void checkCalculateCostBasis() {
    int pfNumber = 1;
    int commission = 10;
    String date = "2022-11-14";

    Double costBasis = model.calculateCostBasis(pfNumber, date);

    assertEquals(14063.8, costBasis, 0.01);
  }

  @Test
  public void calculateValuation() {
    int pfNumber = 1;
    String date = "2022-11-14";

    Double costBasis = model.calculateValuation(pfNumber, date);

    assertEquals(24823.2, costBasis, 0.01);
  }

  @Test
  public void examineFlexPortfolio() {

    int pfNumber = 1;
    String date = "2022-11-14";
    List<String> expected_symbol = new ArrayList<>(List.of("V"));
    List<Double> expected_quantity = new ArrayList<>(List.of(120.0, 20.0));
    try {
      PortfolioInterface pf = model.examineFlexPortfolio(pfNumber, date);

      List<String> symbols = pf.getSymbol();
      List<Double> quantity = pf.getQuantity();

      for (int i = 0; i < pf.getSize(); i++) {
        assertEquals(expected_symbol.get(i), symbols.get(i));
        assertEquals(expected_quantity.get(i), quantity.get(i));
      }
    } catch (IOException e) {
      assertEquals("", e.getMessage());
      fail();
    }
  }

  @Test
  public void getPerformance() throws IOException {

    int pfNumber = 1;
    String start = "2015-02-02";
    String end = "2015-02-07";

    String expectedKey = start.substring(0, start.length() - 1);
    int i = 2;


    Map<String, String> map = model.getPerformance(start, end, pfNumber);
    for (Map.Entry<String, String> entry : map.entrySet()) {
      if (i == 8) {
        assertEquals("Scale =  *  ", entry.getKey());
        break;
      }
      assertEquals(expectedKey + i, entry.getKey());
      i++;
    }


  }

  @Test
  public void saveFlexPortfolioFile() {

    String path = properties.getProperty("resource_file") + "/" +
            properties.getProperty("default_user") + "/";
    String sourcePath = properties.getProperty("path_for_flex_test_savePortfolio")
            + "/multi_stocks_of_same_company.json";
    String actual_file_name = model.saveFlexPortfolioFile(sourcePath);
    assertEquals("admin2", actual_file_name);


    File file = new File(path + "admin2.json");
    file.delete();
  }

  @Test
  public void checkCreateWeightedPortfolio() {
    String path = properties.getProperty("resource_file") + "/" +
            properties.getProperty("default_user") + "/";

    Map<String, Double> map = new HashMap<>();
    map.put("MSFT", 50.0);
    map.put("V", 50.0);

    double totalCost = 2000;
    int pfNumber = 0;
    double commission = 10;
    String startDate = "2021-11-15";

    String fileName = model.createWeightedPortfolio(map, totalCost, pfNumber,
            commission, startDate);

    assertEquals(model.getUser() + "2.json", fileName);


    File file = new File(path + fileName);
    file.delete();
  }

  @Test
  public void checkBuyWeightedPortfolio() {
    String path = properties.getProperty("resource_file") + "/" +
            properties.getProperty("default_user") + "/";

    Map<String, Double> map = new HashMap<>();
    map.put("MSFT", 50.0);
    map.put("V", 50.0);

    double totalCost = 2000;
    int pfNumber = 1;
    double commission = 10;
    String startDate = "2021-11-15";

    String fileName = model.createWeightedPortfolio(map, totalCost, pfNumber,
            commission, startDate);

    assertEquals(model.getUser() + "1.json", fileName);


    File file = new File(path + fileName);
    file.delete();
  }

  @Test
  public void checkCostBasisAfterWeightedPortfolio() {
    String path = properties.getProperty("resource_file") + "/" +
            properties.getProperty("default_user") + "/";

    Map<String, Double> map = new HashMap<>();
    map.put("MSFT", 50.0);
    map.put("V", 50.0);

    double totalCost = 2000;
    int pfNumber = 0;
    double commission = 10;
    String startDate = "2021-11-15";

    String fileName = model.createWeightedPortfolio(map, totalCost, pfNumber,
            commission, startDate);

    assertEquals(model.getUser() + "2.json", fileName);

    double cb = model.calculateCostBasis(2, "2022-11-15");
    assertEquals(String.format("%.2f", cb), "2000.00");


    File file = new File(path + fileName);
    file.delete();
  }

  @Test
  public void checkValuationAfterWeightedPortfolio() {
    String path = properties.getProperty("resource_file") + "/" +
            properties.getProperty("default_user") + "/";

    Map<String, Double> map = new HashMap<>();
    map.put("MSFT", 50.0);
    map.put("V", 50.0);

    double totalCost = 2000;
    int pfNumber = 0;
    double commission = 10;
    String startDate = "2021-11-15";

    String fileName = model.createWeightedPortfolio(map, totalCost, pfNumber,
            commission, startDate);

    assertEquals(model.getUser() + "2.json", fileName);

    double val = model.calculateValuation(2, "2022-11-15");
    assertEquals(String.format("%.2f", val), "1553.74");


    File file = new File(path + fileName);
    file.delete();
  }

  @Test
  public void checkCreateDollarCostAveraging() {
    String path = properties.getProperty("resource_file") + "/" +
            properties.getProperty("default_user") + "/";

    Map<String, Double> map = new HashMap<>();
    map.put("MSFT", 50.0);
    map.put("V", 50.0);

    double totalCost = 2000;
    int pfNumber = 0;
    double commission = 10;
    String startDate = "2021-11-15";
    String endDate = "2022-11-14";
    int period = 120;

    String fileName = model.createCostAveragePortfolio(map, totalCost,
            pfNumber, commission, startDate, endDate, period,'n');

    assertEquals(model.getUser() + "2.json", fileName);

    File file = new File(path + fileName);
    file.delete();
  }

  @Test
  public void checkBuyDollarCostAveraging() {
    String path = properties.getProperty("resource_file") + "/" +
            properties.getProperty("default_user") + "/";

    Map<String, Double> map = new HashMap<>();
    map.put("MSFT", 50.0);
    map.put("V", 50.0);

    double totalCost = 2000;
    int pfNumber = 1;
    double commission = 10;
    String startDate = "2021-11-15";
    String endDate = "2022-11-14";
    int period = 120;

    String fileName = model.createCostAveragePortfolio(map, totalCost, pfNumber,
            commission, startDate, endDate, period,'n');

    assertEquals(model.getUser() + "1.json", fileName);

    File file = new File(path + fileName);
    file.delete();
  }


  @Test
  public void checkCostBasisAfterBuyDollarCostAveraging() {
    String path = properties.getProperty("resource_file") + "/" +
            properties.getProperty("default_user") + "/";

    Map<String, Double> map = new HashMap<>();
    map.put("MSFT", 50.0);
    map.put("V", 50.0);

    double totalCost = 2000;
    int pfNumber = 1;
    double commission = 10;
    String startDate = "2021-11-15";
    String endDate = "2022-11-14";
    int period = 120;

    String fileName = model.createCostAveragePortfolio(map, totalCost, pfNumber, commission,
            startDate, endDate, period,'n');

    assertEquals(model.getUser() + "1.json", fileName);

    assertEquals(String.format("%.2f", model.calculateCostBasis(1, "2015-02-04")),
            "5195.80");
    assertEquals(String.format("%.2f", model.calculateCostBasis(1, "2017-04-13")),
            "14063.80");
    assertEquals(String.format("%.2f", model.calculateCostBasis(1, "2021-11-14")),
            "14063.80");
    assertEquals(String.format("%.2f", model.calculateCostBasis(1, "2021-11-15")),
            "16063.80");
    assertEquals(String.format("%.2f", model.calculateCostBasis(1, "2022-03-30")),
            "18063.80");
    assertEquals(String.format("%.2f", model.calculateCostBasis(1, "2022-07-30")),
            "20063.80");
    assertEquals(String.format("%.2f", model.calculateCostBasis(1, "2022-11-15")),
            "22063.80");
    assertEquals(String.format("%.2f", model.calculateCostBasis(1, "2022-11-30")),
            "22063.80");

    File file = new File(path + fileName);
    file.delete();
  }


  @Test
  public void checkCostBasisAfterDollarCostAveraging() {
    String path = properties.getProperty("resource_file") + "/" +
            properties.getProperty("default_user") + "/";

    Map<String, Double> map = new HashMap<>();
    map.put("MSFT", 50.0);
    map.put("V", 50.0);

    double totalCost = 2000;
    int pfNumber = 0;
    double commission = 10;
    String startDate = "2021-11-15";
    String endDate = "2022-11-14";
    int period = 120;

    String fileName = model.createCostAveragePortfolio(map, totalCost, pfNumber, commission,
            startDate, endDate, period,'n');

    assertEquals(model.getUser() + "2.json", fileName);


    double cb = model.calculateCostBasis(2, "2021-11-15");
    assertEquals(String.format("%.2f", cb), "2000.00");

    assertEquals(String.format("%.2f", model.calculateCostBasis(2, "2022-03-30")),
            "4000.00");
    assertEquals(String.format("%.2f", model.calculateCostBasis(2, "2022-07-30")),
            "6000.00");
    assertEquals(String.format("%.2f", model.calculateCostBasis(2, "2022-11-15")),
            "8000.00");
    assertEquals(String.format("%.2f", model.calculateCostBasis(2, "2022-11-30")),
            "8000.00");


    File file = new File(path + fileName);
    file.delete();
  }

  @Test
  public void checkValuationAfterDollarCostAveraging() {
    String path = properties.getProperty("resource_file") + "/" +
            properties.getProperty("default_user") + "/";

    Map<String, Double> map = new HashMap<>();
    map.put("MSFT", 50.0);
    map.put("V", 50.0);

    double totalCost = 2000;
    int pfNumber = 0;
    double commission = 10;
    String startDate = "2021-11-15";
    String endDate = "2022-11-14";
    int period = 120;

    String fileName = model.createCostAveragePortfolio(map, totalCost, pfNumber, commission,
            startDate, endDate, period,'n');

    assertEquals(model.getUser() + "2.json", fileName);


    double val = model.calculateValuation(2, "2021-11-15");
    assertEquals(String.format("%.2f", val), "1818.18");
    double val1 = model.calculateValuation(2, "2022-11-15");
    assertEquals(String.format("%.2f", val1), "6885.34");


    File file = new File(path + fileName);
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