package model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Calendar.DATE;
import static java.util.Calendar.YEAR;

/**
 * PerformanceGraph class and performs various operations needed to fetch data to plot
 * performance graph for a portfolio.
 * This class is also package private that is accessible to only classes present in model package.
 */
public class PerformanceGraph implements PerformanceGraphInterface {


  /**
   * This function is used to convert the duration between start and end date
   * into equally distributed time intervals.
   *
   * @param startDate date passed by user from where performance graph begins.
   * @param endDate   date passed by user till where performance graph is to be displayed.
   * @return a map of values where keys are the values to be displayed on X-axis in the graph.
   */
  @Override
  public Map<String, String> getTimeIntervalMap(String startDate, String endDate) {

    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat monthYear = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);

    Map<String, String> perfMap = new LinkedHashMap<>();

    try {
      Date d1 = sdf.parse(startDate);
      Date d2 = sdf.parse(endDate);

      if (d1.compareTo(d2) >= 0) {
        throw new RuntimeException("Start date must always be before end date.");
      }

      Calendar startCalendar = getCalendar(d1);
      Calendar endCalendar = getCalendar(d2);

      int years = elapsed(startCalendar, endCalendar, YEAR);
      int months = elapsed(startCalendar, endCalendar, Calendar.MONTH);
      long days = elapsed(startCalendar, endCalendar, DATE);

      String rowCount = getRowCount(days, months, years);

      char op = rowCount.charAt(0);
      int duration = Integer.parseInt(rowCount.substring(1));

      long intervalSize = (d2.getTime() - d1.getTime()) / duration;

      for (int i = 0; i <= duration; i++) {
        Date date = new Date(d1.getTime() + intervalSize * i);
        Calendar temp = getCalendar(date);

        if (op == 'm') {
          perfMap.put(monthYear.format(date), op + sdf.format(date));
        } else if (op == 'y') {
          perfMap.put(String.valueOf(temp.get(YEAR)), op + sdf.format(date));
        } else {
          perfMap.put(sdf.format(date), op + sdf.format(date));
        }
      }
      return perfMap;
    } catch (ParseException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * This function is used to convert values of valuation of a portfolio from double format
   * to string of asterisks. Number of asterisks depends on the valuation at that point in the graph
   * and is scaled accordingly in this function. The decision of using relative or absolute scale
   * is also taken in this function.
   *
   * @param map map containing time intervals for X-axis and their respective valuation values in
   *            double format.
   * @return a map of time intervals for X-axis and variable number of asterisks depending on
   */
  @Override
  public Map<String, String> graphScaling(Map<String, Double> map) {

    Collection<Double> values = map.values();
    List<Double> listOfValues = new ArrayList<>(values);
    double maxPrice = Double.MIN_VALUE;
    double minPrice = Double.MAX_VALUE;
    int flag = 0;
    int interval = 0;
    List<String> starArr = new ArrayList<>();
    Map<String, String> graphMap = new LinkedHashMap<>();
    String star = "*";

    for (Double price : listOfValues) {
      if (price > maxPrice) {
        maxPrice = price;
      }
      if (price < minPrice && price > 0) {
        minPrice = price;
      }
    }
    if (Math.ceil(maxPrice / minPrice) < 50) {
      flag = 1;
      for (Double price : listOfValues) {
        starArr.add(star.repeat((int) Math.ceil(price / minPrice)));
      }
    } else {
      interval = (int) Math.ceil((maxPrice - minPrice) / 49);
      Double[] intervals = new Double[51];
      intervals[0] = minPrice;
      for (int i = 1; i <= 50; i++) {
        intervals[i] = (double) (interval * i);
      }
      for (Double price : listOfValues) {
        if (price == 0.0) {
          starArr.add(" ");
          continue;
        }
        for (int i = 0; i < intervals.length - 1; i++) {
          double start = intervals[i];
          double end = intervals[i + 1];
          if (price >= start && price <= end) {
            starArr.add(star.repeat(i + 1));
          }
        }
      }
    }
    for (Map.Entry<String, Double> entry : map.entrySet()) {
      graphMap.put(entry.getKey(), starArr.get(listOfValues.indexOf(entry.getValue())));
    }
    if (flag == 1) {
      graphMap.put("Scale =  *  ", "$" + String.format("%.2f", minPrice));
    } else {
      graphMap.put("Base = *  : $" + String.format("%.2f", minPrice) + "  |  Scale =  *  ",
              "$" + interval);

    }
    return graphMap;
  }

  private Calendar getCalendar(Date date) {
    Calendar cal = Calendar.getInstance(Locale.US);
    cal.setTime(date);
    return cal;
  }

  private int elapsed(Calendar before, Calendar after, int field) {

    Calendar clone = (Calendar) before.clone();
    int elapsed = -1;
    while (!clone.after(after)) {
      clone.add(field, 1);
      elapsed++;
    }
    return elapsed;
  }

  private String getRowCount(long daysCount, int monthCount, int yearsCount) {
    if (yearsCount >= 5 && yearsCount <= 30) {
      return "y" + yearsCount;
    } else if (yearsCount > 30) {
      return "y" + yearsCount / 6;
    } else {
      if (monthCount >= 5 && monthCount <= 30) {
        return "m" + monthCount;
      } else if (monthCount > 30) {
        return "m" + monthCount / 6;
      } else {
        if (daysCount <= 30) {
          return "d" + daysCount;
        } else {
          return "d" + daysCount / 6;
        }
      }
    }
  }
}

