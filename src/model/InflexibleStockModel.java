package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * InflexibleStockModel class that implements BaseModelInterface and performs various operations on
 * inflexible portfolio.
 * It implements all the features of this application while performing functionalities on
 * inflexible portfolio.
 */
public class InflexibleStockModel extends AbstractStockModel implements BaseModelInterface {

  private final ReadWrite fileParser;
  private final Properties properties;
  private final String path = "portfolios";
  private Portfolio portfolio;

  /**
   * non parameterized constructor that initializes the instance of ReadWrite class,
   * Portfolio class, StockPrice and Properties class.
   * config.properties file is also loaded into FileInputStream.
   */
  public InflexibleStockModel() throws IOException {
    this.properties = new Properties();
    InputStream input = new FileInputStream("src/config.properties");
    properties.load(input);

    this.fileParser = new ReadWrite(properties);
    this.portfolio = new Portfolio();

  }

  /**
   * returns portfolio at a specified date.
   *
   * @param pfNumber portfolio number selected by user among the list of portfolios.
   * @param date     specified date in yyyy-mm-dd.
   * @return an object of type Portfolio Interface.
   * @throws IOException when there is an exception thrown while reading or writing files.
   */
  @Override
  public PortfolioInterface getPortFolioAtDate(int pfNumber, String date) throws IOException {
    return super.getPortFolioAtDate(pfNumber, date);
  }

  /**
   * function to add symbol and quantity of stocks into portfolio object.
   * internally calls a function that creates a file under portfolio folder for that username.
   *
   * @param stockMap a hash map to store symbols and quantity as string and integer respectively.
   * @return filename of the portfolio in string format.
   */
  @Override
  public final String createInflexiblePortfolio(Map<String, Double> stockMap) {
    String portfolioName;

    Portfolio portfolio = createPortfolio(stockMap);

    try {
      portfolioName = createPortfolioFile(".txt", fileParser, portfolio, getPortFolioList());
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getMessage());
    }
    return portfolioName;
  }

  /**
   * authenticates the credentials of that particular user. Here we have considered just a single
   * user.Username is "admin" and password is "Asdf@1234".
   *
   * @param user name of the user i.e. admin.
   * @param pwd  password set by admin ie Asdf@1234.
   * @return true if the credentials matches as given by user else false.
   */
  @Override
  public boolean authenticateCredentials(String user, String pwd) {
    return super.authenticateCredentials(user, pwd);
  }

  /**
   * gets the list of portfolio files created by the user.
   *
   * @return the list of portfolio files.
   */
  @Override
  public List<String> getPortFolioList() {
    return super.getFileList();
  }

  /**
   * extracts the list of ticker symbols from csv file.
   *
   * @return list of ticker symbols.
   * @throws IOException when there is an exception thrown while reading csv file.
   */
  @Override
  public List<String> getTickerList() throws IOException {
    return super.getTickerList();
  }

  /**
   * returns the username currently logged in.
   *
   * @return username in string format.
   */
  @Override
  public String getUser() {
    return super.getUser();
  }

  /**
   * returns the password of the user currently logged in.
   *
   * @return password in string format.
   */
  @Override
  public String getPwd() {
    return super.getPwd();
  }


  /**
   * it takes the file uploaded by the user then creates a portfolio and further
   * creates a file under res folder.
   *
   * @param sourceFile path of the uploaded file in string format.
   * @return returns the file name of created portfolio file in string format.
   */
  @Override
  public String savePortfolioFile(String sourceFile) throws RuntimeException {

    try {
      ReadWriteInterface<Portfolio> parser = getCorrectFileParser();
      File myFile = new File(sourceFile);
      portfolio = parser.readFromFile(myFile, portfolio);
      return createPortfolioFile(getCorrectFileType(), parser, portfolio, getPortFolioList());
    } catch (Exception e) {
      throw new RuntimeException("There was a error in converting a custom portfolio file.\n"
              + e.getMessage());
    }
  }

  /**
   * gets the portfolio among the list of portfolio options based on the pfNumber passed.
   *
   * @param pfNumber portfolio number selected by user among the list of portfolios.
   * @return an object of PortfolioInterface.
   */
  @Override
  public PortfolioInterface examinePortfolio(int pfNumber) throws IOException {
    PortfolioInterface pf = super.examinePortfolioHelper(pfNumber);
    return pf;
  }


  @Override
  String getPath() {
    return path;
  }

  @Override
  ReadWriteInterface<Portfolio> getCorrectFileParser() {
    return new ReadWrite(properties);
  }


}
