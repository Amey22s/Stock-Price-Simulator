package model;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * A class used to perform operations which enables the user to create custom high level investment
 * strategies.
 * It also extends AbstractStockModel to reuse some common code.
 */
public class CostAveraging extends AbstractStockModel implements CostAveragingInterface {

  private final BaseStockPriceInterface stockPrice;

  /**
   * default constructor to create an object of CostAveraging class while initializing
   * stockPrice object.
   */
  CostAveraging() throws IOException {
    super();
    this.stockPrice = getStockPrice();
  }

  @Override
  public Map<String, Double> getStockMap(Map<String, Double> weightMap, double totalCost,
                                         double commission, String date, Properties properties) {
    Map<String, Double> stockMap = new HashMap<>();
    try {
      Calendar cal = Calendar.getInstance();
      Date tempDate = cal.getTime();
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

      Date passedDate = df.parse(date);

      for (Map.Entry<String, Double> entry : weightMap.entrySet()) {
        if (passedDate.compareTo(tempDate) > 0) {
          stockMap.put(entry.getKey(), -1.0);
        } else {
          double stockShare = (entry.getValue() * totalCost / 100);
          double price = stockPrice.getStockPriceByApi(entry.getKey(), date, properties);
          double priceAfterCommission = price + (price * commission / 100);
          double quantity = stockShare / priceAfterCommission;

          stockMap.put(entry.getKey(), quantity);
        }

      }

    } catch (ParseException e) {
      throw new RuntimeException(e.getMessage());
    }
    return stockMap;
  }

  @Override
  public List<String> getDates(String startDate, String endDate, int period) {
    List<String> dates = new ArrayList<>();
    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Calendar c = Calendar.getInstance();
    Date currDate = new Date();

    try {
      Date d1 = sdf.parse(startDate);
      Date d2 = sdf.parse(endDate);

      if (d1.compareTo(d2) >= 0) {
        throw new RuntimeException("Start date must always be before end date.");
      }

      while (d1.compareTo(d2) <= 0 && d1.compareTo(currDate) <= 0) {
        c.setTime(d1);
        c.add(Calendar.DATE, period);
        d1 = c.getTime();
        dates.add(sdf.format(d1));
      }

      if (d1.compareTo(d2) > 0 || d1.compareTo(currDate) > 0) {
        dates.remove(dates.size() - 1);
      }

      return dates;

    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  public List<String> getStrategyList() {
    return super.getFileList();
  }

  @Override
  public String persistStrategy(Map<String, Double> weightMap, double totalCost, String pfFile,
                                double commission, String endDate, int period) {
    Strategy strategy = new Strategy(weightMap, endDate, totalCost, commission,
            period);
    if (!pfFile.equals("")) {
      strategy.getPfList().add(pfFile);
    }

    ReadWriteInterface<Strategy> strategyParser = getCorrectFileParser();

    return createStrategyFile(strategyParser, strategy,
            getStrategyList());
  }

  private String createStrategyFile(ReadWriteInterface<Strategy> parserType,
                                    Strategy strategy, List<String> files) {
    StringBuilder filename;
    int fileIndex = 0;
    File myObj;
    try {

      if (files.size() == 0) {
        new File(getPath() + "/" + super.getUser()).mkdir();
      } else {
        String file = files.get(files.size() - 1).split("\\.")[0];
        fileIndex = Integer.parseInt(file.substring(5));
      }

      do {
        fileIndex++;
        filename = new StringBuilder(super.getUser());
        filename.append(fileIndex);
        myObj = new File(getPath() + "/" + super.getUser() + "/" + filename + ".json");
        if (myObj.createNewFile()) {
          parserType.writeToFile(myObj, strategy);
        }
      }
      while (files.contains(filename + ".json"));
    } catch (IOException e) {
      throw new RuntimeException("An error occurred while creating the strategy file.");
    }

    return filename.toString();
  }

  @Override
  String getPath() {
    return "strategy";
  }

  @Override
  ReadWriteInterface<Strategy> getCorrectFileParser() {
    return new StrategyReadWrite();
  }
}
