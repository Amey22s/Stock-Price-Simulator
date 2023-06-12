package controller;

import java.io.IOException;

/**
 * Interface BaseControllerInterface represents all the operations performed by controller.
 * It delegates the operations to model and view.
 */
public interface BaseControllerInterface {

  /**
   * entry point of the application where all the inputs from the user are taken.
   * asks the view to print starter menu and accordingly asks model to implement them.
   *
   * @throws IOException when there is an exception thrown by Appendable class.
   */
  void goController() throws IOException;

}
