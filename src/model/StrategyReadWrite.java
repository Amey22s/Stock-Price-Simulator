package model;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * package private class that implements ReadWriteInterface. This class performs read/ write
 * operations between strategy files and Strategy objects.
 */
class StrategyReadWrite implements ReadWriteInterface<Strategy> {
  /**
   * creates a strategy file which contains details about the strategy.
   *
   * @param file     path where the file is to be written.
   * @param strategy strategy object whose date is to be written.
   */
  @Override
  public void writeToFile(File file, Strategy strategy) {
    JSONObject jsonObject = new JSONObject();

    List<String> pfList = strategy.getPfList();
    Map<String, Double> weightMap = strategy.getWeightMap();
    double totalCost = strategy.getTotalCost();
    String endDate = strategy.getEndDate();
    int period = strategy.getPeriod();
    double commission = strategy.getCommission();

    if (pfList == null || weightMap == null) {
      throw new RuntimeException("pfList or weightMap is null");
    }

    jsonObject.put("Total Cost", totalCost);
    jsonObject.put("End Date", endDate);
    jsonObject.put("Period", period);
    jsonObject.put("Commission", commission);
    jsonObject.put("List of pf", pfList);
    jsonObject.put("Weight Map", weightMap);

    try {
      FileWriter myWriter = new FileWriter(file);
      myWriter.write(jsonObject.toJSONString());
      myWriter.close();
    } catch (IOException e) {
      throw new RuntimeException("An error occurred while writing to a file.");
    }

  }

  /**
   * reads a strategy file and store its data in a strategy object if valid.
   *
   * @param file     path where the file is to be written.
   * @param strategy Strategy object whose date is to be written.
   * @return Strategy object.
   */
  @Override
  public Strategy readFromFile(File file, Strategy strategy) {
    JSONParser parser = new JSONParser();
    try {
      Object obj = parser.parse(new FileReader(file));
      JSONObject jsonObject = (JSONObject) obj;

      double totalCost = (double) jsonObject.get("Total Cost");
      String endDate = (String) jsonObject.get("End Date");
      int period = (int) (long) jsonObject.get("Period");
      double commission = (double) jsonObject.get("Commission");
      List<String> pfList = (List<String>) jsonObject.get("List of pf");
      Map<String, Double> weightMap = (Map<String, Double>) jsonObject.get("Weight Map");

      Strategy newStrategy = new Strategy(weightMap, endDate, totalCost, commission, period);
      for (String s : pfList) {
        newStrategy.getPfList().add(s);
      }

      return newStrategy;

    } catch (Exception e) {
      throw new RuntimeException("Error occurred while reading from a file\n" + e.getMessage());
    }
  }

  /**
   * extracts the list of ticker symbols from csv file.
   *
   * @param properties config properties to retrieve the ticker list data.
   * @return list of ticker symbols.
   * @throws IOException when there is an exception thrown while reading csv file.
   */
  @Override
  public List<String> getTickerList(Properties properties) throws IOException {
    return null;
  }
}
