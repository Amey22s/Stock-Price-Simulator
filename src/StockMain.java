import java.io.IOException;
import java.io.InputStreamReader;

import controller.BaseControllerInterface;
import controller.MainController;
import controller.SwingController;
import controller.SwingControllerInterface;
import model.AdvancedModelInterface;
import model.BaseModelInterface;
import model.FlexibleStockModel;
import model.InflexibleStockModel;
import view.BaseViewInterface;
import view.StockView;
import view.SwingViewImpl;
import view.SwingViewInterface;

/**
 * entry point of the application that has the main method used to call either textual view or
 * GUI.
 */
public class StockMain {
  /**
   * entry point of the application.
   * from here the control is given to controller.
   */
  public static void main(String[] args) throws IOException {

    if (args[0].equalsIgnoreCase("text")) {
      AdvancedModelInterface advancedModel = new FlexibleStockModel();
      BaseModelInterface baseModel = new InflexibleStockModel();
      BaseViewInterface view = new StockView();
      BaseControllerInterface controller = new MainController(baseModel, advancedModel, view,
              new InputStreamReader(System.in), System.out);
      controller.goController();
    } else if (args[0].equalsIgnoreCase("gui")) {
      AdvancedModelInterface advancedModel = new FlexibleStockModel();
      SwingViewInterface sView = new SwingViewImpl();
      SwingControllerInterface swingController = new SwingController(advancedModel, sView);
    } else {
      System.out.println("Please enter either 'text' or 'gui' after jar name");
    }

  }

}
