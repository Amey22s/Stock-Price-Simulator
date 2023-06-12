package view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.Point;
import java.awt.FontMetrics;

import javax.swing.JPanel;
import javax.swing.JLabel;

/**
 * class used to display the performance of a portfolio over a range of time given by the user.
 */
class PerformanceChart extends JPanel {
  private final int padding;
  private final int labelPadding;
  private final Color lineColor;
  private final Color pointColor;
  private final Color gridColor;
  private final Stroke GRAPH_STROKE;
  private final int pointWidth;
  private final int numberYDivisions;
  private final List<Integer> yAxis;
  private final List<String> xAxis;

  /**
   * parameterized constructor to initialize variables used to draw teh graph like the axes, scale,
   * label, color and fonts for these variables.
   *
   * @param yAxis valuations of the stock.
   * @param xAxis time period.
   */
  PerformanceChart(List<Integer> yAxis, List<String> xAxis) {
    this.yAxis = yAxis;
    this.xAxis = xAxis;

    String scale = xAxis.get(xAxis.size() - 1);
    JLabel scaleLabel = new JLabel();
    scaleLabel.setText("Scale: 1 unit on Y axis = " + scale);
    this.add(scaleLabel);

    this.yAxis.remove(yAxis.size() - 1);
    this.xAxis.remove(xAxis.size() - 1);

    this.lineColor = new Color(44, 102, 230, 180);
    this.pointColor = new Color(100, 100, 100, 180);
    this.gridColor = new Color(200, 200, 200, 200);
    this.GRAPH_STROKE = new BasicStroke(2f);

    this.padding = 25;
    this.labelPadding = 25;

    this.pointWidth = 5;
    numberYDivisions = 10;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


    double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (yAxis.size() - 1);
    double yScale = ((double) getHeight() - 2 * padding - labelPadding)
            / (getMaxScore() - getMinScore());

    List<Point> graphPoints = new ArrayList<>();
    for (int i = 0; i < yAxis.size(); i++) {
      int x1 = (int) (i * xScale + padding + labelPadding);
      int y1 = (int) ((getMaxScore() - yAxis.get(i)) * yScale + padding);
      graphPoints.add(new Point(x1, y1));
    }

    g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding,
            padding + labelPadding, padding);
    g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding,
            getWidth() - padding, getHeight() - padding - labelPadding);

    drawBackground(g2);

    createHatchMarksOnYAxis(g2);
    createHatchMarksOnXAxis(g2);
    drawGraph(g2,graphPoints);
  }

  private void drawBackground(Graphics2D g2) {

    g2.setColor(Color.WHITE);
    g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding,
            getHeight() - 2 * padding - labelPadding);
    g2.setColor(Color.BLACK);
  }

  private void createHatchMarksOnYAxis(Graphics2D g2) {
    for (int i = 0; i < numberYDivisions + 1; i++) {
      int x0 = padding + labelPadding;
      int x1 = pointWidth + padding + labelPadding;
      int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) /
              numberYDivisions + padding + labelPadding);
      int y1 = y0;
      if (yAxis.size() > 0) {
        g2.setColor(gridColor);
        g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
        g2.setColor(Color.BLACK);
        String yLabel = ((int) ((getMinScore() + (getMaxScore() - getMinScore()) * ((i * 1.0) /
                numberYDivisions)) * 100)) / 100.0 + "";
        FontMetrics metrics = g2.getFontMetrics();
        int labelWidth = metrics.stringWidth(yLabel);
        g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
      }
      g2.drawLine(x0, y0, x1, y1);
    }
  }

  private void createHatchMarksOnXAxis(Graphics2D g2) {
    for (int i = 0; i < xAxis.size(); i++) {
      if (xAxis.size() > 1) {
        int x0 = i * (getWidth() - padding * 2 - labelPadding) / (xAxis.size() - 1) +
                padding + labelPadding;
        int x1 = x0;
        int y0 = getHeight() - padding - labelPadding;
        int y1 = y0 - pointWidth;
        if ((i % ((int) ((xAxis.size() / 20.0)) + 1)) == 0) {
          g2.setColor(gridColor);
          g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
          g2.setColor(Color.BLACK);
          String xLabel = xAxis.get(i);
          FontMetrics metrics = g2.getFontMetrics();
          int labelWidth = metrics.stringWidth(xLabel);
          g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
        }
        g2.drawLine(x0, y0, x1, y1);
      }
    }
  }

  private void drawGraph(Graphics2D g2, List<Point> points) {
    Stroke oldStroke = g2.getStroke();
    g2.setColor(lineColor);
    g2.setStroke(GRAPH_STROKE);
    for (int i = 0; i < points.size() - 1; i++) {
      int x1 = points.get(i).x;
      int y1 = points.get(i).y;
      int x2 = points.get(i + 1).x;
      int y2 = points.get(i + 1).y;
      g2.drawLine(x1, y1, x2, y2);
    }

    g2.setStroke(oldStroke);
    g2.setColor(pointColor);
    for (int i = 0; i < points.size(); i++) {
      int x = points.get(i).x - pointWidth / 2;
      int y = points.get(i).y - pointWidth / 2;
      int ovalW = pointWidth;
      int ovalH = pointWidth;
      g2.fillOval(x, y, ovalW, ovalH);
    }
  }

  private double getMinScore() {
    double minScore = Double.MAX_VALUE;
    for (Integer score : yAxis) {
      minScore = Math.min(minScore, score);
    }
    return minScore;
  }

  private double getMaxScore() {
    double maxScore = Double.MIN_VALUE;
    for (Integer score : yAxis) {
      maxScore = Math.max(maxScore, score);
    }
    return maxScore;
  }

}
