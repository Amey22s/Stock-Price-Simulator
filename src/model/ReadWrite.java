package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * ReadWrite class and performs various operations on portfolio file and object.
 * This class is also package private that is accessible to only classes present in model package.
 */

class ReadWrite implements ReadWriteInterface<Portfolio> {

  private final Properties properties;

  /**
   * parameterized constructor that is package private to initialize properties object passed
   * by model.
   *
   * @param prop variable of Properties class.
   */
  ReadWrite(Properties prop) {
    this.properties = prop;
  }

  /**
   * creates a portfolio file which contains stocks and their respective quantities.
   *
   * @param file      path where the file is to be written.
   * @param portfolio portfolio object whose date is to be written.
   */
  public void writeToFile(File file, Portfolio portfolio) {
    try {
      FileWriter myWriter = new FileWriter(file);
      myWriter.write(portfolio.getSymbol().toString());
      myWriter.write(System.lineSeparator());
      myWriter.write(portfolio.getQuantity().toString());

      myWriter.close();
    } catch (IOException e) {
      throw new RuntimeException("An error occurred while writing to a file.");
    }
  }

  /**
   * reads a file and store its data in a portfolio object if valid.
   *
   * @param file      path where the file is to be written.
   * @param portfolio portfolio object whose date is to be written.
   * @return Portfolio object.
   */
  public Portfolio readFromFile(File file, Portfolio portfolio) {
    try {
      FileReader myReader = new FileReader(file);
      BufferedReader bufferedReader = new BufferedReader(myReader);

      String symbolString = bufferedReader.readLine();
      symbolString = symbolString.strip();
      String quantityString = bufferedReader.readLine();
      quantityString = quantityString.strip();

      if (fileCheck(symbolString) && fileCheck(quantityString)) {
        symbolString = symbolString.substring(1, symbolString.length() - 1);
        quantityString = quantityString.substring(1, quantityString.length() - 1);
      }

      myReader.close();

      String[] symbols = symbolString.split(",");
      String[] quantities = quantityString.split(",");

      return generatePortfolio(symbols, quantities);

    } catch (Exception e) {
      throw new RuntimeException("An error occurred while reading from a file.\n" + e.getMessage());
    }
  }

  private boolean fileCheck(String inputString) {
    int index_of_open = inputString.indexOf('[');
    int index_of_close = inputString.indexOf(']');

    if (index_of_open != 0 || index_of_close != inputString.length() - 1) {
      throw new RuntimeException("Format of portfolio file passed was incorrect please follow " +
              "given format to create a portfolio file." +
              "\n line 1 -> [comma separated list of ticker symbols]" +
              "\n line 2 -> [comma separated list of quantity of stocks for " +
              "respective ticker symbol]\n");
    }
    return true;
  }

  private Portfolio generatePortfolio(String[] symbols, String[] quantities)
          throws Exception {


    List<String> symbol = new ArrayList<>();
    List<Double> quantity = new ArrayList<>();
    List<String> tickerList = getTickerList(properties);

    for (int i = 0; i < symbols.length; i++) {
      String temp_symbol = symbols[i].strip();
      String temp_quantity = quantities[i].strip();

      if (!tickerList.contains(temp_symbol)) {
        throw new RuntimeException("Invalid ticker symbol in symbol list.");
      }

      try {
        if (Integer.parseInt(temp_quantity) < 0) {
          throw new RuntimeException();
        }
      } catch (Exception e) {
        throw new RuntimeException("Invalid quantity in quantity list");
      }

      if (symbol.contains(temp_symbol)) {
        int prev_index = symbol.indexOf(temp_symbol);
        double prev_quantity = quantity.get(prev_index);
        int current_quantity = Integer.parseInt(temp_quantity);
        quantity.set(prev_index, prev_quantity + current_quantity);
        continue;
      }
      symbol.add(temp_symbol);
      quantity.add(Double.parseDouble(temp_quantity));
    }

    return new Portfolio(symbol, quantity, new ArrayList<>());

  }

  /**
   * extracts the list of ticker symbols from csv file.
   *
   * @return list of ticker symbols.
   * @throws IOException when there is an exception thrown while reading csv file.
   */

  public List<String> getTickerList(Properties properties) throws IOException {
    List<String> tickerList = new ArrayList<>();
    String line = "";
    BufferedReader br = new BufferedReader(new FileReader(properties.getProperty("ticker_check")));
    while ((line = br.readLine()) != null) {
      tickerList.add(line);
    }

    return tickerList;
  }

}
