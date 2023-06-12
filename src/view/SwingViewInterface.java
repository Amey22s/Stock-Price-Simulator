package view;

import java.util.List;
import java.util.Map;

import javax.swing.JSplitPane;

import controller.SwingControllerInterface;

/**
 * Custom view interface that has methods to offer functionalities of GUI.
 */
public interface SwingViewInterface {
  /**
   * it resets all the fields in the panel to default position.
   */
  void resetFields();

  /**
   * function to implement ActionListeners of all buttons and comboBoxes in the panel.
   *
   * @param swingControllerInterface object of interface SwingControllerInterface.
   */
  void addFeatures(SwingControllerInterface swingControllerInterface);

  /**
   * accepts a JSplitPane and sets the focus on the right component.
   *
   * @param panel JSplitPane with 2 components that is left and right.
   */
  void switchScreen(JSplitPane panel);

  /**
   * returns a JSplitPane with 2 components, the components being determined by the operation
   * passed as parameter.
   *
   * @param operation each JSplitPane is assigned an operation name.
   * @return corresponding JSplitPane.
   */
  JSplitPane getPanel(String operation);

  /**
   * populates the filenames of all the portfolios in the combo box of the panel.
   *
   * @param portfolioList list of portfolios that stores symbols and quantities.
   */
  void populatePortfolioList(List<String> portfolioList);

  /**
   * populates the data required to plot the performance chart that is on x-axis and y-axis.
   *
   * @param performanceMap LinkedHashMap where keys are the time period and values are the relative
   *                       sampled value.
   */
  void populateGraphPoints(Map<String, String> performanceMap);

  /**
   * displays the results for examine functionality where a table is displayed for symbols and
   * their respective quantities in a portfolio.
   *
   * @param map map that contains the contents of a portfolio ie symbols as keys and quantities
   *            as values.
   */
  void examineDisplay(Map<String, Double> map);

  /**
   * displays pop up message boxes for different status in the program.
   *
   * @param panel JSplit pane where the message needs to be displayed.
   * @param msg   the actual message that needs to be displayed in the message box.
   */
  void printStatus(JSplitPane panel, String msg);

}
