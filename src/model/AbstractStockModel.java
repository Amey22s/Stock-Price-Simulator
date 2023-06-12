package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

abstract class AbstractStockModel {

  private final Properties properties;
  private final BaseStockPriceInterface stockPrice;
  private Portfolio portfolio;
  private String user;
  private String pwd;

  AbstractStockModel() throws IOException {
    this.properties = new Properties();
    InputStream input = new FileInputStream("src/config.properties");
    properties.load(input);
    portfolio = new Portfolio();
    this.stockPrice = new StockPrice();
  }

  abstract String getPath();

  abstract ReadWriteInterface getCorrectFileParser();


  /**
   * authenticates the credentials of that particular user. Here we have considered just a single
   * user.Username is "admin" and password is "Asdf@1234".
   *
   * @param user name of the user i.e. admin.
   * @param pwd  password set by admin ie Asdf@1234.
   * @return true if the credentials matches as given by user else false.
   */

  protected boolean authenticateCredentials(String user, String pwd) {

    if (user.equals("admin") && pwd.hashCode() == ("Asdf@1234".hashCode())) {
      setUser(user);
      setPwd(pwd);
      return true;
    }

    return false;
  }

  /**
   * function to add symbol and quantity of stocks into portfolio object.
   * internally calls a function that creates a file under portfolio folder for that username.
   *
   * @param stockMap a hash map to store symbols and quantity as string and integer respectively.
   * @return filename of the portfolio in string format.
   */

  protected Portfolio createPortfolio(Map<String, Double> stockMap) {

    List<String> symbol = new ArrayList<>();
    List<Double> quantity = new ArrayList<>();

    for (Map.Entry<String, Double> entry : stockMap.entrySet()) {

      symbol.add(entry.getKey());
      quantity.add(entry.getValue());
    }

    return new Portfolio(symbol, quantity, new ArrayList<>());
  }


  /**
   * creates a portfolio file under res folder if not present.
   *
   * @return the created filename in string format.
   */
  protected String createPortfolioFile(String fileType, ReadWriteInterface<Portfolio> parserType,
                                       Portfolio portfolio, List<String> files) {
    StringBuilder filename;
    int fileIndex = 0;
    File myObj;
    try {

      if (files.size() == 0) {
        new File(getPath() + "/" + getUser()).mkdir();
      } else {
        String file = files.get(files.size() - 1).split("\\.")[0];
        fileIndex = Integer.parseInt(file.substring(getUser().length()));
      }

      do {
        fileIndex++;
        filename = new StringBuilder(getUser());
        filename.append(fileIndex);
        myObj = new File(getPath() + "/"
                + getUser() + "/" + filename + fileType);
        if (myObj.createNewFile()) {
          parserType.writeToFile(myObj, portfolio);
        }
      }
      while (files.contains(filename + fileType));
    } catch (IOException e) {
      throw new RuntimeException("An error occurred while creating the portfolio file.");
    }

    return filename.toString();
  }

  /**
   * gets the portfolio among the list of portfolio options based on the pfNumber passed.
   *
   * @param pfNumber portfolio number selected by user among the list of portfolios.
   * @return an object of PortfolioInterface.
   */

  protected Portfolio examinePortfolioHelper(int pfNumber) throws IOException {

    ReadWriteInterface<Portfolio> parser = getCorrectFileParser();

    File file = getCorrectFile(pfNumber, getFileList());

    return parser.readFromFile(file, getPortfolio());
  }


  /**
   * returns portfolio at a specified date.
   *
   * @param pfNumber portfolio number selected by user among the list of portfolios.
   * @param date     specified date in yyyy-mm-dd.
   * @return an object of type Portfolio Interface.
   * @throws IOException when there is an exception thrown while reading or writing files.
   */
  protected PortfolioInterface getPortFolioAtDate(int pfNumber, String date)
          throws RuntimeException, IOException {

    Portfolio portfolio = examinePortfolioHelper(pfNumber);

    List<Double> price = new ArrayList<>();
    for (int i = 0; i < portfolio.getSize(); i++) {
      price.add(stockPrice.getStockPriceByApi(portfolio.getSymbol().get(i), date, properties));

    }
    portfolio.setPrice(price);
    return portfolio;
  }

  /**
   * gets the list of portfolio files created by the user.
   *
   * @return the list of portfolio files.
   */
  protected List<String> getFileList() {
    String user = getUser();
    File folder = new File(getPath() + "/" + user + "/");

    List<String> fileNames = new ArrayList<>();

    File[] files = folder.listFiles();

    if (files != null) {
      for (File f : files) {
        fileNames.add(f.getName());
      }
    }
    Collections.sort(fileNames, (f1, f2) -> {
      if (f1.length() != f2.length()) {
        return f1.length() > f2.length() ? 1 : -1;
      }
      return f1.compareTo(f2);
    });
    return fileNames;
  }

  protected BaseStockPriceInterface getStockPrice() {
    return stockPrice;
  }

  protected String getCorrectFileType() {
    if (getPath().equals("portfolios")) {
      return ".txt";
    } else if (getPath().equals("flex_portfolios")) {
      return ".json";
    } else {
      throw new RuntimeException("Invalid fileParser needed");
    }
  }

  protected File getCorrectFile(int pfNumber, List<String> fileList) {

    if (fileList.size() == 0) {
      throw new RuntimeException("No portfolio present. Please create a portfolio " +
              "before trying to examine.");
    }
    String temp_filename = fileList.get(pfNumber - 1);

    try {
      File myObj = new File(getPath() + "/"
              + getUser() + "/" + temp_filename);
      if (myObj.isFile()) {
        return myObj;
      } else {
        throw new RuntimeException("File does not exists.");
      }
    } catch (Exception e) {
      throw new RuntimeException("Error parsing file");
    }
  }

  /**
   * extracts the list of ticker symbols from csv file.
   *
   * @return list of ticker symbols.
   * @throws IOException when there is an exception thrown while reading csv file.
   */

  protected List<String> getTickerList() throws IOException {
    return getCorrectFileParser().getTickerList(properties);
  }

  /**
   * returns the username currently logged in.
   *
   * @return username in string format.
   */
  protected String getUser() {
    if (user == null) {
      return "admin";
    }
    return user;
  }

  /**
   * sets the username of that particular user.
   *
   * @param user name of the user in string format.
   */

  private void setUser(String user) {
    this.user = user;
  }

  /**
   * returns the password of the user currently logged in.
   *
   * @return password in string format.
   */
  protected String getPwd() {
    return pwd;
  }

  /**
   * sets the password of that particular user.
   *
   * @param pwd password of the user in string format.
   */
  private void setPwd(String pwd) {
    this.pwd = pwd;
  }

  /**
   * returns the portfolio object of the portfolio currently being worked on.
   *
   * @return portfolio object.
   */

  protected Portfolio getPortfolio() {
    return portfolio;
  }

  /**
   * sets the portfolio object's value as per the portfolio passed.
   *
   * @param portfolio portfolio object with values to be set.
   */
  protected void setPortfolio(Portfolio portfolio) {
    this.portfolio = portfolio;
  }

}