package view;

import java.awt.RenderingHints;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextField;

/**
 * class to include placeholders for our JTextFields so that it acts as a visual cue to the user
 * while using the application via GUI.
 */
class HintTextField extends JTextField {
  private final String hint;

  /**
   * parameterized constructor to initialize the hint that is supposed to be present in the
   * text field.
   *
   * @param hintStr hint to be highlighted in the JTextField.
   */
  public HintTextField(String hintStr) {
    hint = hintStr;
  }

  /**
   * overrides the paint() method to implement functionality.
   *
   * @param g the <code>Graphics</code> context in which to paint
   */
  @Override
  public void paint(Graphics g) {
    super.paint(g);
    if (getText().length() == 0) {
      int h = getHeight();
      ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
              RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      Insets ins = getInsets();
      FontMetrics fm = g.getFontMetrics();
      int c0 = getBackground().getRGB();
      int c1 = getForeground().getRGB();
      int m = 0xfefefefe;
      int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
      g.setColor(new Color(c2, true));
      g.setFont(g.getFont().deriveFont(Font.ITALIC));
      g.drawString(hint, ins.left, h / 2 + fm.getAscent() / 2 - 2);
    }
  }
}