package view;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * JUnit class for testing the standalone functionality of the view.
 */
public class StockViewTest {

  BaseViewInterface view;
  StringBuffer buf = new StringBuffer();
  String str = "Invalid Input";

  @Before
  public void setUp() {
    view = new StockView();
  }

  @Test
  public void printStarterMenuTest() throws IOException {
    String expected = "\n" + "*".repeat(100) + "\n"
            + "1. Create Account\n"
            + "2. Login \n"
            + "\n" + "*".repeat(100) + "\n";
    view.printStarter(buf);
    assertEquals(expected, buf.toString());
  }

  @Test
  public void printMenuTest() throws IOException {
    String expected = "\n" + "*".repeat(100) + "\n"
            + "1. Create Portfolio\n"
            + "2. Examine Portfolio\n"
            + "3. Get Portfolio at a specific date\n"
            + "4. Upload custom portfolio\n"
            + "5. Exit\n"
            + "\n" + "*".repeat(100) + "\n"
            + "Choose the operation to be performed next:\n";
    view.printMenu(buf);
    assertEquals(expected, buf.toString());
  }

  @Test
  public void printAuthErrorTest() throws IOException {
    String expected = "\n" + "*".repeat(100) + "\n"
            + "Invalid credentials. Please login again\n"
            + "\n" + "*".repeat(100) + "\n";
    view.printAuthError(buf);
    assertEquals(expected, buf.toString());
  }

  @Test
  public void printStatusTest() throws IOException {

    String expected = "\n" + "*".repeat(100) + "\n"
            + str
            + "\n" + "*".repeat(100) + "\n";

    view.printStatus(str, buf);
    assertEquals(expected, buf.toString());
  }

  @Test
  public void printInflexibleControllerStarterMenu() throws IOException {
    String expected = "\n" + "*".repeat(100) + "\n"
            + "1. Examine portfolio\n"
            + "2. Buy Stock\n"
            + "3. Sell Stock\n"
            + "4. Calculate Cost Basis\n"
            + "5. Calculate Portfolio value at a specific date\n"
            + "6. Performance Graph\n"
            + "7. Upload custom portfolio\n"
            + "8. Exit\n"
            + "\n" + "*".repeat(100) + "\n"
            + "Choose the operation to be performed next:\n";

    view.printFlexibleMenu(buf);
    assertEquals(expected, buf.toString());
  }

  @Test
  public void printMainControllerStarterMenu() throws IOException {
    String expected = "\n" + "*".repeat(100) + "\n"
            + "\n1. Inflexible Portfolio\n"
            + "2. Flexible Portfolio\n"
            + "3. Exit\n"
            + "\n" + "*".repeat(100) + "\n"
            + "Choose the operation to be performed next:\n";

    view.printMainControllerMenu(buf);
    assertEquals(expected, buf.toString());
  }


}