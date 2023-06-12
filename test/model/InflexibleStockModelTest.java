package model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * JUnit class for testing the standalone functionality of the model.
 */
public class InflexibleStockModelTest {
  private BaseModelInterface model;
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
  public void checkGetPortfolio() throws IOException {


    List<String> expected_symbol = new ArrayList<>(Arrays.asList("AAPL", "TSLA"));
    List<Double> expected_quantity = new ArrayList<>(Arrays.asList(10.0, 15.0));


    PortfolioInterface pf = model.examinePortfolio(1);
    List<String> actual_symbol = pf.getSymbol();
    List<Double> actual_quantity = pf.getQuantity();
    for (int i = 0; i < pf.getSymbol().size(); i++) {
      assertEquals(actual_symbol.get(i), expected_symbol.get(i));
      assertEquals(expected_quantity.get(i), actual_quantity.get(i));
    }

  }

  @Test

  public void checkCreatePortfolio() {
    Map<String, Double> map = new HashMap<>();
    map.put("AAPL", 10.0);
    map.put("TSLA", 15.0);

    String path = properties.getProperty("resource_file") + "/" +
            properties.getProperty("default_user") + "/";
    String pfName = model.createInflexiblePortfolio(map) + ".txt";

    assertEquals(model.getUser() + "2.txt", pfName);

    File file = new File(path + pfName);
    file.delete();

  }

  @Test
  public void checkGetPortfolioAtDateByAPI() throws IOException, ParseException {

    List<String> expected_symbol = new ArrayList<>(Arrays.asList("AAPL", "TSLA"));
    List<Double> expected_quantity = new ArrayList<>(Arrays.asList(10.0, 15.0));
    List<Double> expected_price = new ArrayList<>(Arrays.asList(153.34, 227.54));


    String curr_date = "2022-10-31";

    PortfolioInterface pf1 = model.getPortFolioAtDate(1, curr_date);

    List<String> actual_symbol = pf1.getSymbol();
    List<Double> actual_quantity = pf1.getQuantity();
    List<Double> actual_price = pf1.getPrice();
    for (int i = 0; i < pf1.getSymbol().size(); i++) {
      assertEquals(actual_symbol.get(i), expected_symbol.get(i));
      assertEquals(expected_quantity.get(i), actual_quantity.get(i));
      assertEquals(expected_price.get(i), actual_price.get(i));
    }

  }


  @Test
  public void checkPortfolioList() {

    List<String> expected_fileNames = new ArrayList<>(List.of("admin1.txt"));

    List<String> actual_fileNames = model.getPortFolioList();

    assertEquals(expected_fileNames.size(), actual_fileNames.size());
    for (int i = 0; i < expected_fileNames.size(); i++) {
      assertEquals(expected_fileNames.get(i), actual_fileNames.get(i));
    }
  }

  @Test
  public void checkSavePortfolioFile() {

    String path = properties.getProperty("resource_file") + "/" +
            properties.getProperty("default_user") + "/";
    String sourcePath = properties.getProperty("path_for_test_savePortfolio")
            + "/multi_stocks_of_same_company.txt";
    String actual_file_name = model.savePortfolioFile(sourcePath);
    assertEquals("admin2", actual_file_name);


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