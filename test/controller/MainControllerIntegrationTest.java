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

import model.AdvancedModelInterface;
import model.BaseModelInterface;
import model.FlexibleStockModel;
import model.InflexibleStockModel;
import view.StockView;
import view.SwingViewInterface;

import static org.junit.Assert.assertEquals;

/**
 * Junit tests for integration testing between model, view and Main controller.
 */
public class MainControllerIntegrationTest {
  private Properties properties;

  private AdvancedModelInterface advancedModel;
  private BaseModelInterface baseModel;
  private SwingViewInterface sView;

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

    advancedModel = new FlexibleStockModel();
    baseModel = new InflexibleStockModel();

    advancedModel.authenticateCredentials("admin", "Asdf@1234");
    baseModel.authenticateCredentials("admin", "Asdf@1234");

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
   * display screen when application is run.
   */
  @Test
  public void mainControllerStarterScreenTest() throws IOException {
    Readable in = new StringReader("admin Asdf@1234 3");
    Appendable out = new StringBuilder();

    BaseControllerInterface mainController = new MainController(baseModel, advancedModel,
            new StockView(), in, out);
    mainController.goController();
    assertEquals("Enter username: ", out.toString().split("\n")[1]);
    assertEquals("Enter password: ", out.toString().split("\n")[2]);
  }

  /**
   * display screen when username and password is correct.
   */
  @Test
  public void validCredDisplayTest() throws IOException {
    Readable in = new StringReader("admin Asdf@1234 3");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new MainController(baseModel, advancedModel,
            new StockView(), in, out);
    sc.goController();
    assertEquals("Successfully logged in.", out.toString().split("\n")[4]);
    assertEquals("!!! Welcome !!!", out.toString().split("\n")[5]);
    assertEquals("1. Inflexible Portfolio", out.toString().split("\n")[10]);
    assertEquals("2. Flexible Portfolio", out.toString().split("\n")[11]);
    assertEquals("3. Exit", out.toString().split("\n")[12]);
    deleteFile();
  }

  /**
   * display screen when username and password is incorrect.
   */
  @Test
  public void invalidCredDisplayTest() throws IOException {
    Readable in = new StringReader("admin Asf@1234 admin Asdf@1234 3");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new MainController(baseModel, advancedModel,
            new StockView(), in, out);
    sc.goController();
    assertEquals("Enter username: ", out.toString().split("\n")[1]);
    assertEquals("Enter password: ", out.toString().split("\n")[2]);
    assertEquals("Invalid credentials. Please login again",
            out.toString().split("\n")[4]);
    assertEquals("Enter username: ",
            out.toString().split("\n")[8]);
    assertEquals("Successfully logged in.", out.toString().split("\n")[11]);
    //assertEquals("1. Create Portfolio", out.toString().split("\n")[16]);
  }

  /**
   * display screen when username and password is correct but choice is invalid.
   */
  @Test
  public void invalidChoice() throws IOException {
    Readable in = new StringReader("admin Asdf@1234 5 3");
    Appendable out = new StringBuilder();

    BaseControllerInterface sc = new MainController(baseModel, advancedModel,
            new StockView(), in, out);
    sc.goController();
    assertEquals("Enter username: ", out.toString().split("\n")[1]);
    assertEquals("Enter password: ", out.toString().split("\n")[2]);
    assertEquals("Successfully logged in.",
            out.toString().split("\n")[4]);
    assertEquals("1. Inflexible Portfolio",
            out.toString().split("\n")[10]);
    assertEquals("2. Flexible Portfolio",
            out.toString().split("\n")[11]);
    assertEquals("3. Exit",
            out.toString().split("\n")[12]);
    assertEquals("Choose the operation to be performed next:",
            out.toString().split("\n")[15]);
    assertEquals("!!! Invalid input !!!",
            out.toString().split("\n")[16]);
    assertEquals("1. Inflexible Portfolio",
            out.toString().split("\n")[20]);
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