package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

/**
 * StockPrice class that implements BaseStockPriceInterface
 * and performs various operations on the price of stock.
 * This class is also package private that is accessible to only classes present in model package.
 */
class StockPrice implements BaseStockPriceInterface {

  private final Cache namedCache = new Cache();
  private String line;

  /**
   * retrieves the stock price for the current date by calling API for daily pricing of stock.
   *
   * @param symbol     ticker symbol.
   * @param currDate   current date.
   * @param properties config properties to retrieve the api key.
   * @return the current price in double format.
   */
  public Double getStockPriceByApi(String symbol, String currDate, Properties properties) {
    try {
      line = "";
      Calendar cal = Calendar.getInstance();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      cal.setTime(sdf.parse(currDate));

      if (!namedCache.containsKey(symbol)) {
        String apiKey = properties.getProperty("api_key");
        URL url = null;
        try {
          url = new URL("https://www.alphavantage"
                  + ".co/query?function=TIME_SERIES_DAILY"
                  + "&outputsize=full"
                  + "&symbol"
                  + "=" + symbol + "&apikey=" + apiKey + "&datatype=csv");
        } catch (MalformedURLException e) {
          throw new RuntimeException(" API has either changed or "
                  + "no longer works");
        }
        InputStream in = null;
        try {
          in = url.openStream();
          BufferedReader br = new BufferedReader(new InputStreamReader(in));
          List<String> dataList = fillStringArray(br);
          namedCache.put(symbol, dataList);
        } catch (IOException e) {
          throw new RuntimeException("No price data found for " + symbol);
        }
      }

      return getStockPrice(symbol, currDate, properties);

    } catch (ParseException e) {
      throw new RuntimeException("No price data found for " + symbol);
    }

  }

  /**
   * retrieves the stock price for a particular date by looking up the .csv file.
   *
   * @param symbol     ticker symbol.
   * @param inputDate  specified date as entered by the user.
   * @param properties config properties to retrieve Stock_Database.csv and Stock_Ticker.csv files.
   * @return the price of the stock at that particular date in double format.
   */
  public Double getStockPrice(String symbol, String inputDate, Properties properties) {

    try {

      DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      StringBuilder price = new StringBuilder();

      try {

        List<String> data = namedCache.get(symbol);

        int i = 0;
        while ((line = data.get(i)) != null) {
          if (inputDate.equals(line.split(",")[0])) {
            price.append(line.split(",")[4]);
            break;
          } else if (sdf.parse(inputDate).after(sdf.parse(line.split(",")[0]))) {
            price.append(line.split(",")[4]);
            break;
          }
          i++;
        }

      } catch (Exception e) {
        throw new RuntimeException("Error occurred while fetching stock price.");
      }

      if (price.toString().equals("")) {
        return -1.0;
      } else {
        return Double.parseDouble(price.toString());
      }
    } catch (Exception e) {
      throw new RuntimeException("No price data found for " + symbol);
    }
  }


  /**
   * retrieves the stock price for the current date by calling API for monthly price of stock.
   *
   * @param symbol     ticker symbol.
   * @param currDate   current date.
   * @param properties config properties to retrieve the api key.
   * @return the current price in double format.
   */
  public Double getStockPriceByApiMonthly(String symbol, String currDate, Properties properties) {
    line = "";

    if (!namedCache.containsKey(symbol)) {
      String apiKey = properties.getProperty("api_key");
      URL url = null;
      try {
        url = new URL("https://www.alphavantage"
                + ".co/query?function=TIME_SERIES_MONTHLY"
                + "&outputsize=full"
                + "&symbol"
                + "=" + symbol + "&apikey=" + apiKey + "&datatype=csv");
      } catch (MalformedURLException e) {
        throw new RuntimeException(" API has either changed or "
                + "no longer works");
      }
      InputStream in = null;
      try {
        in = url.openStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        List<String> dataList = fillStringArray(br);

        namedCache.put(symbol, dataList);

      } catch (IOException e) {
        throw new IllegalArgumentException("No price data found for " + symbol);
      }
    }

    return getStockPrice(symbol, currDate, properties);

  }

  private List<String> fillStringArray(BufferedReader br) throws IOException {
    br.readLine();
    List<String> dataList = new ArrayList<>();

    while ((line = br.readLine()) != null) {
      dataList.add(line);
    }

    return dataList;
  }

  /**
   * gets the last working day of the month.
   *
   * @param symbol     ticker symbol.
   * @param currDate   current date.
   * @param properties config properties to retrieve the api key.
   * @return the date in string format.
   */
  public String getMonthEndDateByApi(String symbol, String currDate, Properties properties) {
    line = "";
    String apiKey = properties.getProperty("api_key");
    URL url = null;
    try {
      url = new URL("https://www.alphavantage"
              + ".co/query?function=TIME_SERIES_MONTHLY"
              + "&outputsize=full"
              + "&symbol"
              + "=" + symbol + "&apikey=" + apiKey + "&datatype=csv");
    } catch (MalformedURLException e) {
      throw new RuntimeException(" API has either changed or "
              + "no longer works");
    }
    InputStream in = null;
    try {
      in = url.openStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      br.readLine();
      while ((line = br.readLine()) != null) {
        if (currDate.substring(0, 8).equals(line.split(",")[0].substring(0, 8))) {
          break;
        }
      }

      br.close();
    } catch (IOException e) {
      throw new IllegalArgumentException("No price data found for " + symbol);
    }

    return line.split(",")[0];
  }
}
