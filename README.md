# Stock-Price-Simulator
Stock market simulation with real-time prices from Alpha Vantage API, facilitating learning, and experimenting with market by creating portfolios, trading, and applying strategies.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Our application provides user with 2 options to either work with Inflexible or flexible portfolios.
In Inflexible the user cannot make changes in the portfolio once it is created; whereas in inflexible the user can make changes via transactions.

We have implemented 2 types of views in our application ie textual and GUI.
In Assignment 6 we have made use of GUI.

For textual interface we have implemented both flexible and inflexible types of portfolios.
For GUI we are implementing only flexible type of portfolio with 2 additional features of weighted averages and dollar cost averaging.


FUNCTIONALITIES INCLUDED IN TEXTUAL VIEW:

-->Inflexible portfolio has the following functions:

1. Create Portfolio
2. Examine Portfolio
3. Get Portfolio Value at a specified date
4. Upload a custom portfolio

-->Flexible portfolio has the following functions:

1. Examine portfolio
2. Buy Stock
3. Sell Stock
4. Calculate Cost Basis
5. Calculate Portfolio value at a specific date
6. Performance Graph
7. Upload custom portfolio


FUNCTIONALITIES INCLUDED IN GRAPHICAL USER INTERFACE:

-->Flexible portfolio:

1. Examine portfolio
2. Create Portfolio
    -create flexible portfolio
    -create weighted portfolio
    -create dollar cost portfolio
3. Buy Portfolio
    -buy flexible portfolio
    -buy weighted portfolio
    -buy dollar cost portfolio
4. Sell stocks
5. Calculate cost basis of portfolio
6. Calculate total valuation of portfolio at a specific date
7. Upload file
8. Performance Chart

-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


INLEXIBLE PORTFOLIO
-------------------

Create Portfolio:

Here the user is given option to enter ticker symbol from a list of over 500 stock symbols listed on NYSE.
Moreover the user is asked to enter the quantity of shares. 
Upon validation the symbol and quantity is stored inside a portfolio file.
A user can enter n number of stocks and their respective quantities inside a single portfolio. Moreover he can create n number of portfolios.


Examine Portfolio:

The user is displayed the list of portfolio files created or uploaded by him/her and requests to select one out of them to examine.
Upon selection the application displays the list of company ticker symbols and their respective quantity in that particular portfolio.
The user will then be reverted back to main menu, where he/she can continue using the application.
User can examine multiple portfolios in run application run as well but one portfolio at a time.


Get Portfolio Value at a specified date:

If the user wishes to know the price of stocks and total valuation of the portfolio on particular date, he/she may select this option on the main menu.
The application asks for the portfolio file he/she wishes to analyse. It then asks for the date in yyyy-mm-dd format.
For testing purposes, the date is limited to 2014-11-01 and for dates before that, the application returns no record for the given stock.
The application would display the list of stocks, their respective quantities, price of stock on that date, total value of each stock in portfolio and total valuation of the portfolio as a whole.


Upload a custom portfolio:

A user has the advanced feature of creating a portfolio externally and uploading the same file to the application.
The application asks for the source path, then parses the file. Upon validation, it creates a portfolio and adds it to the file list of the user
This file would then be visible to the user and he can then examine and get the total valuation of the stocks in a portfolio at a specified date.


-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




FLEXIBLE PORTFOLIO
------------------

Examine Portfolio:

he user is displayed the list of portfolio files created or uploaded by him/her and requests to select one out of them to examine.
Upon selection the application displays the list of company ticker symbols and their respective quantity in that particular portfolio.
The user will then be reverted back to main menu, where he/she can continue using the application.
User can examine multiple portfolios in run application run as well but one portfolio at a time.


Buy Stock:

Here the user has the option to either create a new portfolio or buy stocks and edit in the existing portfolios.
Again the user needs to enter stock symbol and respective quantity. Along with this the user enters the date and commission amount as well.
Once the stock is bought the user can examine the same by calling the examine option above(entering option 1).
In our application a single transaction occurs on the same day.
However the user can create a portfolio with multiple transactions on multiple days.
In GUI we have added an extra feature to buy or create weighted portfolio and dollar cost portfolios.
These options are present in the combo box and the user just needs to select the desired option from the combo box and required fields would populate.


Sell Stock:

Here the user can sell particular stocks from existing portfolios.
Commission amount is added here as well.
However there are some limitations with respective to selling the stocks:
-- user cannot sell before buying anything.
-- user cannot sell on a particular day if the number of quantities to sell is greater than remaining in the portfolio.


Calculate Cost Basis:

This is to calculate the total amount of money invested in a portfolio by a specific date.
This would include all the purchases made in that portfolio till that date. This includes buying and selling stocks.
It would also include a commission fee that would be entered by the user. It should be greater than or equal to 0.


Value of Portfolio:

Determine the value of a portfolio on a specific date.
The value for a portfolio before the date of its first purchase would be 0, since each stock in the portfolio now was purchased at a specific point in time.
Value cannot be determined for future ie the application would prompt if user enters a futuristic date or a date before 2014.


Performance Graph:

It is a graphical representation of the stocks in portfolio between a given range of dates.
This range is given by the user and again the same date restrictions are applied as before.
Here the portfolio number and the range would be mentioned on the top of the graph; the diagram would be mentioned below that followed by the scale.


Uploading a custom file:

Similar to Inflexible portfolio a user has the advanced feature of creating a portfolio externally and uploading the same file to the application.
The application asks for the source path, then parses the file. Upon validation, it creates a portfolio and adds it to the file list of the user
This file would then be visible to the user and he can then examine and get the total valuation of the stocks in a portfolio at a specified date.

---------------------------------------------------------------------------------------------------------------------------------------------------------
