package model;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Interface that is used to perform read and write operations on portfolio objects and parse files.
 * It is also package private that is accessible to only classes present in model package.
 */
interface ReadWriteInterface<T> {

  /**
   * creates a portfolio file which contains stocks and their respective quantities.
   *
   * @param file      path where the file is to be written.
   * @param portfolio portfolio object whose date is to be written.
   */
  void writeToFile(File file, T portfolio);

  /**
   * reads a file and store its data in a portfolio object if valid.
   *
   * @param file      path where the file is to be written.
   * @param portfolio portfolio object whose date is to be written.
   * @return Portfolio object.
   */
  T readFromFile(File file, T portfolio);

  /**
   * extracts the list of ticker symbols from csv file.
   *
   * @param properties config properties to retrieve the ticker list data.
   * @return list of ticker symbols.
   * @throws IOException when there is an exception thrown while reading csv file.
   */
  List<String> getTickerList(Properties properties) throws IOException;
}
