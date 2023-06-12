package view;

import org.jdesktop.swingx.JXDatePicker;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.Color;
import java.awt.FlowLayout;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.ImageIcon;
import javax.swing.ButtonGroup;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import javax.swing.table.DefaultTableModel;

import controller.SwingControllerInterface;

/**
 * Concrete class to implement the functionalities of SwingViewInterface. It extends JFrame class
 * to use all the features of swing inside a single frame. All the functionalities of flexible
 * portfolios are implemented in this class.
 */
public class SwingViewImpl extends JFrame implements SwingViewInterface {
  private final JComboBox<String> listOfPf;
  private final JComboBox<String> typeOfPf;
  private final JComboBox<String> typeOfBuyPf;
  private final JLabel password;
  private final JLabel user;
  private final JLabel display;
  private final JLabel filePathLabel;
  private final JLabel commission;
  private final JLabel date;
  private final JLabel symbol;
  private final JLabel quantity;
  private final JLabel endDate;
  private final JLabel period;
  private final JLabel totalCost;

  private final JButton loginButton;
  private final JButton buyButton;
  private final JButton sellButton;
  private final JButton examineSubmitButton;
  private final JButton fileButton;
  private final JButton uploadFileButton;
  private final JButton cbDisplayButton;
  private final JButton valueDisplayButton;
  private final JButton addButton;
  private final JButton createButton;
  private final JButton createWeightedPfButton;
  private final JButton createDollarCostButton;
  private final JButton buyWeightedPfButton;
  private final JButton buyDollarCostButton;
  private final JButton performanceButton;

  private final JTextField userText;
  private final JTextField commValue;
  private final JTextField symbolValue;
  private final JTextField quantityValue;
  private final JTextField totalCostValue;
  private final JTextField periodValue;
  private final JPasswordField pwdText;
  private final JFrame frame;

  private final JRadioButton radioButton1;
  private final JRadioButton radioButton2;
  private final JRadioButton radioButton3;
  private final JRadioButton radioButton4;
  private final JRadioButton radioButton5;
  private final JRadioButton radioButton6;
  private final JRadioButton radioButton7;
  private final JRadioButton radioButton8;
  private final JRadioButton radioButton9;
  private final JTable table;
  private final List<JTextField> tfList;
  private final List<Integer> points;
  private final List<String> timePeriod;
  private final DefaultTableModel model;
  private final JXDatePicker pickDate;
  private final JXDatePicker endPickDate;
  private int pfNumber;
  private String filePath;
  private String startDateStr;
  private String endDateStr;
  private Map<String, Double> stockMap;
  private Map<String,Double> resStockMap;


  /**
   * constructor to initialise all the components declared above. It initially creates a frame and
   * add login panel to it.
   */
  public SwingViewImpl() {
    super();

    frame = new JFrame();
    frame.setTitle("Portfolio Management System");

    frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));


    frame.setLocation(new Point(500, 300));
    frame.setSize(new Dimension(400, 200));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    // Labels
    display = new JLabel("!! Welcome !!");
    user = new JLabel("Username");
    password = new JLabel("Password");
    commission = new JLabel("Commission");
    date = new JLabel("Date");
    symbol = new JLabel("Symbol");
    quantity = new JLabel();
    filePathLabel = new JLabel("File path will appear here");
    endDate = new JLabel("End Date");
    period = new JLabel("Duration(in days)");
    totalCost = new JLabel("Total cost");


    // Text fields

    userText = new HintTextField("admin");
    userText.setSize(10, 2);
    commValue = new HintTextField("example: 10");
    commValue.setSize(10, 2);
    symbolValue = new HintTextField("example: MSFT");
    symbolValue.setSize(10, 2);
    quantityValue = new HintTextField("example: 20");
    quantityValue.setSize(10, 2);
    totalCostValue = new HintTextField("example: 1000");
    totalCostValue.setSize(10, 2);
    periodValue = new HintTextField("duration between 2 investments");
    periodValue.setSize(10, 2);


    // Password fields
    pwdText = new JPasswordField(10);


    //Buttons
    buyButton = new JButton("Buy Button");
    sellButton = new JButton("Sell Button");
    examineSubmitButton = new JButton("Submit");
    loginButton = new JButton("Login");
    cbDisplayButton = new JButton("Submit");
    valueDisplayButton = new JButton("Submit");
    fileButton = new JButton("Open a file");
    uploadFileButton = new JButton("Upload a file");
    addButton = new JButton("Add");
    createButton = new JButton("Create a new portfolio");
    createWeightedPfButton = new JButton("Create a new passive portfolio");
    createDollarCostButton = new JButton("Create a new Dollar Cost Portfolio");
    buyWeightedPfButton = new JButton("Buy");
    buyDollarCostButton = new JButton("Buy");
    performanceButton = new JButton("Submit");


    //radio buttons
    radioButton1 = new JRadioButton("1.  Examine Portfolio");
    radioButton2 = new JRadioButton("2.  Create new Portfolio");
    radioButton3 = new JRadioButton("3.  Buy Stocks");
    radioButton4 = new JRadioButton("4.  Sell Stocks");
    radioButton5 = new JRadioButton("5.  Calculate Cost Basis of Portfolio");
    radioButton6 = new JRadioButton("6.  Portfolio Value at a specific date");
    radioButton7 = new JRadioButton("7.  Upload a custom file");
    radioButton8 = new JRadioButton("8.  Performance Graph");
    radioButton9 = new JRadioButton("9. Exit");


    //Table
    model = new DefaultTableModel();
    table = new JTable(model);
    table.setBounds(30, 40, 200, 300);
    model.addColumn("Symbol");
    model.addColumn("Quantity");


    // arrayList
    tfList = new ArrayList<>();
    points = new ArrayList<>();
    timePeriod = new ArrayList<>();
    stockMap = new LinkedHashMap<>();

    //date
    pickDate = new JXDatePicker();
    pickDate.setFormats(new SimpleDateFormat("yyyy-MM-dd"));
    pickDate.setToolTipText("Select date");

    endPickDate = new JXDatePicker();
    endPickDate.setFormats(new SimpleDateFormat("yyyy-MM-dd"));
    endPickDate.setToolTipText("Select date");


    // Combo box
    listOfPf = new JComboBox<>();
    typeOfPf = new JComboBox<>();
    typeOfBuyPf = new JComboBox<>();
    typeOfPf.addItem("Create Flexible Portfolio");
    typeOfPf.addItem("Create Weighted Portfolio");
    typeOfPf.addItem("Create Dollar Cost Averaging");

    typeOfBuyPf.addItem("Buy Flexible Portfolio");
    typeOfBuyPf.addItem("Buy Weighted Portfolio");
    typeOfBuyPf.addItem("Buy Dollar Cost Averaging");


    //frame.add(loginPanel());
    JSplitPane loginPage = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, loginPanel(), imagePanel());
    loginPage.setDividerLocation(350);

    frame.add(loginPage);

    pack();
    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    frame.setVisible(true);

  }

  @Override
  public void addFeatures(SwingControllerInterface swingControllerInterface) {
    loginButton.addActionListener(evt -> swingControllerInterface.authenticateCredentialsView(
            userText.getText(),
            new String(pwdText.getPassword())));
    radioButton1.addActionListener(evt -> swingControllerInterface.displayPortfolios(
            "examine"));
    radioButton2.addActionListener(evt -> swingControllerInterface.displayPortfolios(
            "Create Flexible Portfolio"));
    radioButton3.addActionListener(evt -> swingControllerInterface.displayPortfolios(
            "Buy Flexible Portfolio"));
    radioButton4.addActionListener(evt -> swingControllerInterface.displayPortfolios(
            "sell"));
    radioButton5.addActionListener(evt -> swingControllerInterface.displayPortfolios(
            "costBasis"));
    radioButton6.addActionListener(evt -> swingControllerInterface.displayPortfolios(
            "getValue"));
    radioButton7.addActionListener(evt -> swingControllerInterface.displayPortfolios(
            "uploadFile"));
    radioButton8.addActionListener(evt -> swingControllerInterface.displayPortfolios(
            "performanceChart"));
    radioButton9.addActionListener(evt -> switchScreen(getPanel("login page")));
    listOfPf.addActionListener(evt -> pfNumber = listOfPf.getSelectedIndex() + 1);
    typeOfPf.addActionListener(evt -> switchScreen(getPanel(
            typeOfPf.getItemAt(typeOfPf.getSelectedIndex()))));
    typeOfBuyPf.addActionListener(evt -> switchScreen(
            getPanel(typeOfBuyPf.getItemAt(typeOfBuyPf.getSelectedIndex()))));
    examineSubmitButton.addActionListener(evt -> swingControllerInterface.examinePortfolio(pfNumber,
            startDateStr));
    cbDisplayButton.addActionListener(evt -> swingControllerInterface.getCostBasis(pfNumber,
            startDateStr));
    valueDisplayButton.addActionListener(evt -> swingControllerInterface.getPfValue(pfNumber,
            startDateStr));
    fileButton.addActionListener(evt -> filePath = getFilePath());
    uploadFileButton.addActionListener(evt -> swingControllerInterface.savePortfolio(filePath));

    buyButton.addActionListener(evt -> swingControllerInterface.buyStocks(resStockMap, pfNumber,
            commValue.getText(), startDateStr));
    addButton.addActionListener(evt -> resStockMap =
            addStocks(((JButton) evt.getSource()).getParent().getParent(), symbolValue.getText(),
                    quantityValue.getText(), commValue.getText(), startDateStr, endDateStr,
                    totalCostValue.getText(), periodValue.getText()));
    createButton.addActionListener(evt -> swingControllerInterface.buyStocks(resStockMap,
            0,commValue.getText(), startDateStr));
    createDollarCostButton.addActionListener(evt -> swingControllerInterface.dollarCostAveraging(
            resStockMap, totalCostValue.getText(), 0, commValue.getText(), startDateStr,
            endDateStr, Integer.parseInt(periodValue.getText())));
    createWeightedPfButton.addActionListener(evt ->
            swingControllerInterface.createWeightedPortfolio(resStockMap, totalCostValue.getText(),
                    0, commValue.getText(), startDateStr));
    buyWeightedPfButton.addActionListener(evt -> swingControllerInterface.createWeightedPortfolio(
            resStockMap, totalCostValue.getText(), pfNumber, commValue.getText(), startDateStr));
    buyDollarCostButton.addActionListener(evt -> swingControllerInterface.dollarCostAveraging(
            resStockMap, totalCostValue.getText(), pfNumber, commValue.getText(), startDateStr,
            endDateStr, Integer.parseInt(periodValue.getText())));
    sellButton.addActionListener(evt -> swingControllerInterface.sellStocks(resStockMap, pfNumber,
            commValue.getText(),
            startDateStr));
    performanceButton.addActionListener(evt -> swingControllerInterface.performanceChart(pfNumber,
            startDateStr, endDateStr));

  }

  @Override
  public void switchScreen(JSplitPane switchPane) {
    frame.getContentPane().removeAll();
    frame.add(switchPane);
    frame.getContentPane().revalidate();
    frame.getContentPane().repaint();
    this.setFocus(switchPane.getRightComponent());
    resetFields();
  }


  @Override
  public JSplitPane getPanel(String operation) {
    switch (operation) {
      case "login page":
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, loginPanel(), imagePanel());
      case "flexMenu":
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, flexMenu(), flexMenuDisplay());
      case "examine":
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, flexMenu(), examinePanel());
      case "Create Flexible Portfolio":
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, flexMenu(), createPortfolio());
      case "Create Weighted Portfolio":
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, flexMenu(), createWeightedPortfolio());
      case "Create Dollar Cost Averaging":
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, flexMenu(), createDollarCostPortfolio());
      case "Buy Flexible Portfolio":
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, flexMenu(), buyPanel());
      case "Buy Weighted Portfolio":
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, flexMenu(), buyWeightedPortfolio());
      case "Buy Dollar Cost Averaging":
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, flexMenu(), buyDollarCostPortfolio());
      case "sell":
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, flexMenu(), sellPanel());
      case "costBasis":
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, flexMenu(), calculateCostBasis());
      case "getValue":
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, flexMenu(), valueOfPortfolio());
      case "uploadFile":
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, flexMenu(), customPortfolio());
      case "performanceChart":
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, flexMenu(), performanceChart());
      default:
        throw new RuntimeException("Invalid operation.");
    }
  }

  private JPanel loginPanel() {
    JPanel loginPanel = new JPanel();
    loginPanel.setName("login panel");

    //loginPanel.setLayout(null);
    loginPanel.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.gridheight = 10;
    gbc.fill = GridBagConstraints.BOTH;


    loginPanel.add(user);

    loginPanel.add(userText, gbc);
    tfList.add(userText);


    loginPanel.add(password);

    loginPanel.add(pwdText, gbc);
    tfList.add(pwdText);

    loginButton.setActionCommand("Login Button");
    loginPanel.add(loginButton, gbc);

    loginPanel.repaint();
    loginPanel.revalidate();

    loginPanel.setMaximumSize(new Dimension(1000, 1000));
    loginPanel.setPreferredSize(new Dimension(500, 500));


    return loginPanel;
  }

  private JPanel imagePanel() {
    JPanel imagePanel = new JPanel();
    Image loginImage = null;
    try {
      BufferedImage myPicture = ImageIO.read(new File("src/view/st.jpg"));
      loginImage = myPicture.getScaledInstance(800, 800, 0);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    JLabel textLabel = new JLabel("!! Welcome to StockApp !!");
    textLabel.setForeground(Color.white);
    JLabel picLabel = new JLabel("", new ImageIcon(loginImage), JLabel.RIGHT);
    imagePanel.add(picLabel);
    imagePanel.add(textLabel);
    imagePanel.setBackground(Color.black);
    imagePanel.setSize(500, 800);

    return imagePanel;
  }

  private JPanel flexMenu() {

    JPanel flexPanel = new JPanel();
    flexPanel.setName("flexible menu panel");
    flexPanel.setBorder(BorderFactory.createTitledBorder("Please select the option from below"));
    flexPanel.setLayout(new BoxLayout(flexPanel, BoxLayout.PAGE_AXIS));

    ButtonGroup group = new ButtonGroup();

    radioButton1.setActionCommand("RB1");
    group.add(radioButton1);
    flexPanel.add(radioButton1);
    radioButton2.setActionCommand("RB2");
    group.add(radioButton2);
    flexPanel.add(radioButton2);
    radioButton3.setActionCommand("RB3");
    group.add(radioButton3);
    flexPanel.add(radioButton3);
    radioButton4.setActionCommand("RB4");
    group.add(radioButton4);
    flexPanel.add(radioButton4);
    radioButton5.setActionCommand("RB5");
    group.add(radioButton5);
    flexPanel.add(radioButton5);
    radioButton6.setActionCommand("RB6");
    group.add(radioButton6);
    flexPanel.add(radioButton6);
    radioButton7.setActionCommand("RB7");
    group.add(radioButton7);
    flexPanel.add(radioButton7);
    radioButton8.setActionCommand("RB8");
    group.add(radioButton8);
    flexPanel.add(radioButton8);
    radioButton8.setActionCommand("RB9");
    group.add(radioButton9);
    flexPanel.add(radioButton9);

    return flexPanel;

  }

  private JPanel flexMenuDisplay() {
    JPanel displayPanel = new JPanel();
    displayPanel.setName("display panel");
    display.setText("Welcome");

    displayPanel.add(display);

    return displayPanel;
  }

  private JPanel genericTransactPanel(String s) {

    JPanel finalPanel = new JPanel();
    finalPanel.setLayout(new BoxLayout(finalPanel, BoxLayout.PAGE_AXIS));
    finalPanel.setBorder(BorderFactory.createTitledBorder("Please fill all the fields below"));

    JPanel genericTransactPanel = new JPanel(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.gridheight = 10;
    gbc.fill = GridBagConstraints.BOTH;

    quantity.setText("Quantity");
    date.setText("Date");

    addCommonFields(genericTransactPanel, gbc, s);

    if (s.contains("examine") || s.contains("cost basis") || s.contains("valuation")
            || s.contains("performance chart")) {
      finalPanel.add(genericTransactPanel);

      selectCorrectButton(finalPanel, genericTransactPanel, gbc, s);

      return finalPanel;
    }

    addCreateBuySellFields(genericTransactPanel, gbc, s);

    selectCorrectButton(finalPanel, genericTransactPanel, gbc, s);

    finalPanel.add(genericTransactPanel);

    return finalPanel;
  }

  private void addCommonFields(JPanel genericTransactPanel, GridBagConstraints gbc, String s) {
    if (s.contains("create")) {
      genericTransactPanel.add(typeOfPf, gbc);
    } else if (s.contains("buy")) {
      genericTransactPanel.add(typeOfBuyPf, gbc);
    }

    if (!s.contains("create")) {
      genericTransactPanel.add(listOfPf, gbc);
    }

    genericTransactPanel.add(date);
    genericTransactPanel.add(pickDate, gbc);

    pickDate.addActionListener(evt -> {
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      Date d = pickDate.getDate();
      startDateStr = df.format(d);
    });


    if (s.contains("performance chart") || s.contains("cost avg")) {
      date.setText("Start Date");

      genericTransactPanel.add(endDate);
      genericTransactPanel.add(endPickDate, gbc);

      endPickDate.addActionListener(evt -> {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date d = endPickDate.getDate();
        endDateStr = df.format(d);
      });

      if (s.contains("cost avg")) {
        genericTransactPanel.add(period);

        genericTransactPanel.add(periodValue, gbc);
        tfList.add(periodValue);
      }
    }
  }

  private void addCreateBuySellFields(JPanel genericTransactPanel, GridBagConstraints gbc,
                                      String s) {
    genericTransactPanel.add(commission);

    genericTransactPanel.add(commValue, gbc);
    tfList.add(commValue);

    if (s.contains("weighted")) {

      quantity.setText("Weight in Portfolio(in %)");

      totalCost.setText("Amount to be invested");
      genericTransactPanel.add(totalCost);

      genericTransactPanel.add(totalCostValue, gbc);
      tfList.add(totalCostValue);
    }

    genericTransactPanel.add(symbol);

    genericTransactPanel.add(symbolValue, gbc);
    tfList.add(symbolValue);

    genericTransactPanel.add(quantity);

    genericTransactPanel.add(quantityValue, gbc);
    tfList.add(quantityValue);

    addButton.setActionCommand("Add Button");
    genericTransactPanel.add(addButton);
  }


  private void selectCorrectButton(JPanel finalPanel, JPanel genericTransactPanel,
                                   GridBagConstraints gbc, String s) {
    switch (s) {
      case "create":
        finalPanel.setName("create flexible panel");
        createButton.setActionCommand("Create Button");
        genericTransactPanel.add(createButton, gbc);
        break;
      case "create weighted":
        finalPanel.setName("create weighted panel");
        createWeightedPfButton.setActionCommand("Create Button");
        genericTransactPanel.add(createWeightedPfButton, gbc);
        break;
      case "create weighted cost avg":
        finalPanel.setName("create dc panel");
        createDollarCostButton.setActionCommand("Create Button");
        genericTransactPanel.add(createDollarCostButton);
        break;
      case "buy":
        finalPanel.setName("buy flexible panel");
        buyButton.setActionCommand("Buy Button");
        genericTransactPanel.add(buyButton);
        break;
      case "buy weighted":
        finalPanel.setName("buy weighted panel");
        buyWeightedPfButton.setActionCommand("Create Button");
        genericTransactPanel.add(buyWeightedPfButton);
        break;
      case "buy weighted cost avg":
        finalPanel.setName("buy dc panel");
        buyDollarCostButton.setActionCommand("Create Button");
        genericTransactPanel.add(buyDollarCostButton);
        break;
      case "sell":
        finalPanel.setName("sell panel");
        sellButton.setActionCommand("Sell Button");
        genericTransactPanel.add(sellButton);
        break;
      case "examine":
        finalPanel.setName("examine panel");
        examineSubmitButton.setActionCommand("Submit Button");
        genericTransactPanel.add(examineSubmitButton, gbc);
        break;
      case "cost basis":
        finalPanel.setName("cost panel");
        cbDisplayButton.setActionCommand("Show cost basis");
        genericTransactPanel.add(cbDisplayButton, gbc);
        break;
      case "valuation":
        finalPanel.setName("value panel");
        valueDisplayButton.setActionCommand("Show value at a specific date");
        genericTransactPanel.add(valueDisplayButton, gbc);
        break;
      case "performance chart":
        finalPanel.setName("performance panel");
        performanceButton.setActionCommand("Add new Pf Button");
        genericTransactPanel.add(performanceButton, gbc);
        break;
      default:
        printStatus(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, flexMenu(), flexMenuDisplay()),
                "Invalid panel");

    }
  }


  private JPanel createPortfolio() {

    return genericTransactPanel("create");
  }

  private JPanel createWeightedPortfolio() {

    return genericTransactPanel("create weighted");

  }

  private JPanel createDollarCostPortfolio() {
    return genericTransactPanel("create weighted cost avg");
  }

  private JPanel buyPanel() {
    return genericTransactPanel("buy");
  }

  private JPanel buyWeightedPortfolio() {
    return genericTransactPanel("buy weighted");
  }

  private JPanel buyDollarCostPortfolio() {
    return genericTransactPanel("buy weighted cost avg");
  }


  private JPanel sellPanel() {
    return genericTransactPanel("sell");
  }


  private Map<String, Double> addStocks(Container panel, String symbol, String stockQuantity,
                                        String commission, String startDate, String endDate,
                                        String totalCost, String period) {

    Map<String, Double> resMap = new HashMap<>();

    if (model.getColumnCount() != 0) {
      model.setColumnCount(0);
    }

    model.addColumn("Symbol");
    symbolValue.setText("");
    quantityValue.setText("");
    pickDate.setEnabled(false);
    commValue.setEnabled(false);

    if ((panel.getName().equals("buy panel") || panel.getName().equals("sell panel") ||
            panel.getName().equals("create flexible panel") ||
            panel.getName().equals("buy flexible panel"))
            && (fieldsCheckerForBuySell(panel, symbol, stockQuantity, commission, startDate))) {
      model.addColumn("Quantity");
      resMap = addStocksHelper(panel, symbol, stockQuantity);
    } else if ((panel.getName().equals("create weighted panel") ||
            panel.getName().equals("buy weighted panel"))
            && (fieldsCheckerForBuySell(panel, symbol, stockQuantity, commission, startDate)) &&
            !totalCost.equals("")) {
      model.addColumn("Weight(in %)");
      resMap = addStocksHelper(panel, symbol, stockQuantity);
      totalCostValue.setEnabled(false);
    } else if ((panel.getName().equals("create dc panel") || panel.getName().equals("buy dc panel"))
            && (fieldsCheckerForBuySell(panel, symbol, stockQuantity, commission, startDate)) &&
            !totalCost.equals("") && !period.equals("")) {
      model.addColumn("Weight(in %)");
      resMap = addStocksHelper(panel, symbol, stockQuantity);
      endPickDate.setEnabled(false);
      periodValue.setEnabled(false);
      totalCostValue.setEnabled(false);
    }
    return resMap;
  }

  private Map<String, Double> addStocksHelper(Container panel, String symbol,
                                              String stockQuantity) {
    try {
      double quantity = Double.parseDouble(stockQuantity);

      if (model.getRowCount() != 0) {
        model.setRowCount(0);
      }
      stockMap.put(symbol, quantity);

      for (Map.Entry<String, Double> entry : stockMap.entrySet()) {

        model.addRow(new Object[]{entry.getKey(), String.valueOf(entry.getValue())});
      }

      panel.add(new JScrollPane(table));

      panel.revalidate();
      panel.repaint();

    } catch (Exception e) {
      printStatus(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, flexMenu(), panel),
              "Please check your add button functionality");
    }
    return stockMap;
  }

  private boolean fieldsCheckerForBuySell(Container panel, String symbol, String quantity,
                                          String commission, String date) {

    int flag = 0;
    if (symbol.equals("")) {
      flag = 1;
      printStatus(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, flexMenu(), panel),
              "Please enter a value for symbol.");
    } else if (quantity.equals("")) {
      flag = 1;
      printStatus(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, flexMenu(), panel),
              "Please enter a value for quantity.");
    } else if (commission.equals("")) {
      flag = 1;
      printStatus(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, flexMenu(), panel),
              "Please enter a value for commission.");
    } else if (date.equals("")) {
      flag = 1;
      printStatus(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, flexMenu(), panel),
              "Please enter a value for date.");
    }
    return flag != 1;
  }

  @Override
  public void examineDisplay(Map<String, Double> map) {

    if (model.getRowCount() != 0) {
      model.setRowCount(0);
    }
    for (Map.Entry<String, Double> entry : map.entrySet()) {

      model.addRow(new Object[]{entry.getKey(), String.valueOf(entry.getValue())});
    }

    JPanel displayExaminePanel = new JPanel();
    displayExaminePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    displayExaminePanel.setPreferredSize(new Dimension(500, 500));
    displayExaminePanel.setMaximumSize(new Dimension(500, 500));
    displayExaminePanel.setBorder(BorderFactory.createTitledBorder("Examine Results"));
    displayExaminePanel.add(new JScrollPane(table));
    JDialog dialog = new JDialog();
    dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    dialog.setContentPane(displayExaminePanel);

    resetFields();

    dialog.pack();
    dialog.setVisible(true);
  }

  private JPanel customPortfolio() {

    JPanel filePanel = new JPanel();
    filePanel.setName("file panel");
    filePanel.setLayout(new FlowLayout());

    fileButton.setActionCommand("Open file");
    filePanel.add(fileButton);

    uploadFileButton.setActionCommand("Upload file");
    filePanel.add(uploadFileButton);

    filePanel.add(filePathLabel);

    return filePanel;

  }

  private JPanel examinePanel() {
    return genericTransactPanel("examine");
  }

  private JPanel calculateCostBasis() {
    return genericTransactPanel("cost basis");
  }

  private JPanel performanceChart() {
    return genericTransactPanel("performance chart");
  }

  private JPanel valueOfPortfolio() {
    return genericTransactPanel("valuation");
  }

  private String getFilePath() {
    final JFileChooser file = new JFileChooser(".");
    int selectedFile = file.showOpenDialog(SwingViewImpl.this);
    if (selectedFile == JFileChooser.APPROVE_OPTION) {
      File f = file.getSelectedFile();
      filePathLabel.setText(f.getAbsolutePath());
      return f.getAbsolutePath();
    }
    return "";
  }

  @Override
  public void populatePortfolioList(List<String> portfolioList) {
    listOfPf.removeAllItems();
    //typeOfPf.removeAllItems();

    for (String s : portfolioList) {
      listOfPf.addItem(s);
    }
  }

  @Override
  public void populateGraphPoints(Map<String, String> performanceMap) {

    timePeriod.removeAll(timePeriod);
    points.removeAll(points);

    for (Map.Entry<String, String> entry : performanceMap.entrySet()) {
      if (entry.getKey().contains("Scale") || entry.getKey().contains("Base")) {
        timePeriod.add(entry.getValue());
        System.out.println(entry.getKey() + entry.getValue());
      } else {
        timePeriod.add(entry.getKey());
      }

      points.add(entry.getValue().length());
    }
  }

  @Override
  public void printStatus(JSplitPane pane, String msg) {

    if (msg.contains("DPC:")) {
      displayPerformanceChart(msg.substring(4));
    } else {
      JOptionPane.showMessageDialog(pane,
              msg, "Result", JOptionPane.PLAIN_MESSAGE);
    }
    switchScreen(pane);
    resetFields();
  }

  private void displayPerformanceChart(String msg) {

    PerformanceChart mainPanel = new PerformanceChart(points, timePeriod);
    mainPanel.setPreferredSize(new Dimension(800, 600));
    JFrame frame = new JFrame(msg);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.getContentPane().add(mainPanel);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  @Override
  public void resetFields() {
    for (JTextField tf : tfList) {
      tf.setText("");
      tf.setEnabled(true);
    }
    pickDate.setEnabled(true);
    pickDate.setDate(null);
    endPickDate.setEnabled(true);
    endPickDate.setDate(null);
    startDateStr = "";
    endDateStr = "";
    stockMap.clear();
  }

  private void setFocus(Component panel) {
    panel.setVisible(true);
    panel.setFocusable(true);
    panel.requestFocus();
  }

}
