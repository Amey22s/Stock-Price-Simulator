package model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Class to implement ReadWriteInterface. This class is used to work with files of type JSON.
 */
public class JsonReadWrite implements ReadWriteInterface<Portfolio> {

  private final Properties properties;

  /**
   * parameterized constructor that is package private to initialize properties object passed
   * by model.
   *
   * @param prop variable of Properties class.
   */
  JsonReadWrite(Properties prop) {
    this.properties = prop;
  }

  /**
   * creates an advanced portfolio file which contains stocks and their respective buy quantities,
   * buy price and buy dates. It also stores stocks and their respective sell quantities, sell price
   * and sell dates.
   *
   * @param file      json file to be written.
   * @param portfolio portfolio object whose data is to be written.
   */
  @Override
  public void writeToFile(File file, Portfolio portfolio) {

    JSONObject finalObj = new JSONObject();
    JSONArray buyArray = new JSONArray();
    JSONArray sellArray = new JSONArray();

    try {
      if (new BufferedReader(new FileReader(file)).readLine() != null) {
        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
          obj = parser.parse(new FileReader(file));
        } catch (IOException | ParseException e) {
          throw new RuntimeException(e);
        }
        JSONObject jsonObject = (JSONObject) obj;
        buyArray = (JSONArray) jsonObject.get("Buy");
        if (buyArray == null) {
          buyArray = new JSONArray();
        }
        sellArray = (JSONArray) jsonObject.get("Sell");
        if (sellArray == null) {
          sellArray = new JSONArray();
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    List<String> symbol = portfolio.getSymbol();
    List<Double> quantity = portfolio.getQuantity();
    List<Double> price = portfolio.getPrice();

    if (symbol == null || quantity == null) {
      return;
    }

    char status = portfolio.getOperation();

    for (int i = 0; i < portfolio.getSize(); i++) {
      JSONObject jsonObject = new JSONObject();

      jsonObject.put("Symbol", symbol.get(i));
      jsonObject.put("Quantity", quantity.get(i));
      jsonObject.put("Date", portfolio.getDate());
      if (price.size() != 0) {
        jsonObject.put("Price", price.get(i));
      }


      if (status == 'b') {
        buyArray.add(jsonObject);
      } else if (status == 's') {
        if (!isDateValid(buyArray, symbol.get(i), portfolio.getDate())) {
          throw new RuntimeException("Invalid Transaction." + " Kindly make sure that the " +
                  "selling date is not before the buying date. " +
                  "Moreover make sure the stock ticker is present in" +
                  " the current portfolio");
        }
        if (!isDatePrecedence(sellArray, symbol.get(i), portfolio.getDate())) {
          throw new RuntimeException("Invalid transaction. Entered date is before the last" +
                  " entered transaction for " + symbol.get(i));
        }
        if (!isQuantityValid(buyArray, sellArray, symbol.get(i), portfolio.getDate(),
                quantity.get(i))) {
          throw new RuntimeException("Invalid transaction. Quantity of selling shares is greater " +
                  "than quantity of buying shares at this specific date");
        }
        sellArray.add(jsonObject);
      }

    }

    finalObj.put("Buy", buyArray);
    finalObj.put("Sell", sellArray);

    try {
      FileWriter myWriter = new FileWriter(file);
      myWriter.write(finalObj.toJSONString());
      myWriter.close();
    } catch (IOException e) {
      throw new RuntimeException("An error occurred while writing to a file.");
    }

  }

  /**
   * reads a file and store its data in a portfolio object if valid.
   *
   * @param file      json file to read from.
   * @param portfolio portfolio object whose data is to be updated.
   * @return Portfolio object.
   */
  @Override
  public Portfolio readFromFile(File file, Portfolio portfolio) {

    JSONParser parser = new JSONParser();

    try {
      Object obj = parser.parse(new FileReader(file));
      JSONObject jsonObject = (JSONObject) obj;
      JSONArray buyArray = (JSONArray) jsonObject.get("Buy");
      JSONArray sellArray = (JSONArray) jsonObject.get("Sell");

      if (properties.getProperty("custom_file").equals("false")) {
        String date = portfolio.getDate();

        return generatePortfolio(buyArray, sellArray, date);
      } else if (properties.getProperty("custom_file").equals("true")) {
        List<Portfolio> portfolioList = parseJson(buyArray, sellArray);
        for (Portfolio pf : portfolioList) {
          File tempFile = new File(properties.getProperty("custom_file_path"));
          writeToFile(tempFile, pf);
        }
        return portfolioList.get(0);
      } else {
        throw new RuntimeException("Please check config properties for custom file value");
      }

    } catch (Exception e) {
      throw new RuntimeException("Error occurred while reading from a file\n" + e.getMessage());
    }
  }

  private Portfolio generatePortfolio(JSONArray buyArray, JSONArray sellArray, String date)
          throws Exception {

    List<String> symbol = new ArrayList<>();
    List<Double> quantity = new ArrayList<>();
    List<Double> price = new ArrayList<>();
    Date threshold = new SimpleDateFormat("yyyy-MM-dd").parse(date);
    Date lastTransactDate = new Date();

    for (Object sellObj : sellArray) {
      JSONObject temp = (JSONObject) sellObj;
      Date actual = new SimpleDateFormat("yyyy-MM-dd").parse((String) temp.get("Date"));

      if (actual.compareTo(threshold) <= 0) {
        symbol.add((String) temp.get("Symbol"));
        quantity.add(-1 * (double) temp.get("Quantity"));
        if (temp.containsKey("Price")) {
          price.add((Double) temp.get("Price"));
        }
      }
    }

    for (Object buyObj : buyArray) {
      JSONObject temp = (JSONObject) buyObj;
      Date actual = new SimpleDateFormat("yyyy-MM-dd").parse((String) temp.get("Date"));

      if (actual.compareTo(threshold) <= 0) {
        symbol.add((String) temp.get("Symbol"));
        quantity.add((double) temp.get("Quantity"));
        if (temp.containsKey("Price")) {
          price.add((Double) temp.get("Price"));
        }
        lastTransactDate = actual;
      }
    }
    Portfolio pf = new Portfolio(symbol, quantity, price);
    pf.setDate(new SimpleDateFormat("yyyy-MM-dd").format(lastTransactDate));

    return pf;
  }

  private List<Portfolio> parseJson(JSONArray buyArray, JSONArray sellArray) throws Exception {

    List<Date> buyDates = isValidJson(buyArray);
    List<Date> sellDates = isValidJson(sellArray);
    if (buyDates == null || sellDates == null) {
      throw new RuntimeException("Error converting custom json to a portfolio.");
    } else {
      List<Portfolio> portfolioList = new ArrayList<>();
      for (Date d : buyDates) {
        portfolioList.add(generatePortfolioAtDate(buyArray, 'b',
                new SimpleDateFormat("yyyy-MM-dd").format(d)));
      }

      for (Date d : sellDates) {
        portfolioList.add(generatePortfolioAtDate(sellArray, 's',
                new SimpleDateFormat("yyyy-MM-dd").format(d)));
      }
      return portfolioList;
    }
  }

  private Portfolio generatePortfolioAtDate(JSONArray transactArray, char op, String date)
          throws java.text.ParseException, IOException {

    List<String> symbol = new ArrayList<>();
    List<Double> quantity = new ArrayList<>();
    List<Double> price = new ArrayList<>();
    List<String> tickerList = getTickerList(properties);
    BaseStockPriceInterface stockPriceInterface = new StockPrice();
    Date threshold = new SimpleDateFormat("yyyy-MM-dd").parse(date);


    for (Object obj : transactArray) {
      JSONObject temp = (JSONObject) obj;
      Date actual = new SimpleDateFormat("yyyy-MM-dd").parse((String) temp.get("Date"));
      String tempSymbol = (String) temp.get("Symbol");
      double tempQuantity = 1.0 * (double) temp.get("Quantity");

      if (!tickerList.contains(tempSymbol)) {
        throw new RuntimeException("Error generating portfolio at date " + date +
                ". Please check input file.");
      }


      if (actual.compareTo(threshold) == 0) {
        symbol.add(tempSymbol);
        quantity.add(tempQuantity);
        price.add(stockPriceInterface.getStockPriceByApi(tempSymbol, date, properties));
      }
    }

    Portfolio pf = new Portfolio(symbol, quantity, price);
    pf.setDate(new SimpleDateFormat("yyyy-MM-dd").format(threshold));
    pf.setOperation(op);

    return pf;

  }

  private List<Date> isValidJson(JSONArray transactArray) throws java.text.ParseException {
    List<Date> dateList = new ArrayList<>();
    if (transactArray == null) {
      return dateList;
    } else {
      for (Object obj : transactArray) {
        JSONObject temp = (JSONObject) obj;
        Date actual = new SimpleDateFormat("yyyy-MM-dd").parse((String) temp.get("Date"));
        dateList.add(actual);
      }
    }
    return dateList;
  }

  @Override
  public List<String> getTickerList(Properties properties) throws IOException {
    List<String> tickerList = new ArrayList<>();
    String line = "";
    BufferedReader br = new BufferedReader(new FileReader(properties.getProperty("ticker_check")));
    while ((line = br.readLine()) != null) {
      tickerList.add(line);
    }

    return tickerList;
  }

  private boolean isDatePrecedence(JSONArray transactArray, String symbol, String date) {

    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      for (Object obj : transactArray) {
        JSONObject temp = (JSONObject) obj;
        String symbolArr = (String) temp.get("Symbol");
        String dateArr = (String) temp.get("Date");
        if (symbolArr.equals(symbol)) {
          if (sdf.parse(date).before(sdf.parse(dateArr))) {
            return false;
          }
        }
      }
      return true;
    } catch (java.text.ParseException p) {
      throw new RuntimeException("Date cannot be parsed");
    }
  }

  private boolean isDateValid(JSONArray array, String symbol, String date) {

    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      List<String> dateList = new ArrayList<>();
      List<String> symbolList = new ArrayList<>();

      for (Object obj : array) {
        JSONObject temp = (JSONObject) obj;
        String symbolArr = (String) temp.get("Symbol");
        symbolList.add(symbolArr);
        String dateArr = (String) temp.get("Date");
        dateList.add(dateArr);
      }
      if (!symbolList.contains(symbol)) {
        return false;
      } else {
        Collections.sort(dateList);
        return !sdf.parse(date).before(sdf.parse(dateList.get(0)));
      }
    } catch (java.text.ParseException p) {
      throw new RuntimeException("Date cannot be parsed");
    }
  }

  private boolean isQuantityValid(JSONArray buyArray, JSONArray sellArray, String symbol,
                                  String date, double quantity) {
    try {
      Date threshold = new SimpleDateFormat("yyyy-MM-dd").parse(date);

      int totalBuyQuantity = getQuantity(buyArray, symbol, threshold);
      int totalSellQuantity = getQuantity(sellArray, symbol, threshold);
      if (quantity <= (totalBuyQuantity - totalSellQuantity)) {
        return true;
      }

    } catch (java.text.ParseException e) {
      throw new RuntimeException(e);
    }
    return false;
  }

  private int getQuantity(JSONArray transactArray, String symbol, Date threshold)
          throws java.text.ParseException {
    int totalQuantity = 0;
    for (Object buyObj : transactArray) {
      JSONObject temp = (JSONObject) buyObj;
      Date actual = new SimpleDateFormat("yyyy-MM-dd").parse((String) temp.get("Date"));
      String symbolArr = (String) temp.get("Symbol");
      if (symbol.equals(symbolArr)) {
        if (actual.compareTo(threshold) <= 0) {
          double tempQuantity = (double) temp.get("Quantity");
          totalQuantity += tempQuantity;
        }
      }
    }

    return totalQuantity;
  }
}