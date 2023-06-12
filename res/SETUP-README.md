SETUP TO RUN APPLICATION

BEFORE RUNNING THE APPLICATION KINDLY RUN THE FOLLOWING COMMAND TO REMOVE .DS STORE FROM THE FOLDER

find . -name ".DS_Store" -print -delete.

--> the jar file by the name "Assignment4" is stored under res folder and is used to run the application.

--> copy the jar file and put it in the main folder ie outside res folder along with src data and portfolios folder. In this way it can access all the folders.

--> along with jar file you will require data folder that contains csv file , portfolios folder flex_portfolios to store user portfolios and src folder which will contain config.properties. Create a new folder and store the before mentioned files here.

--> to open the application in GUI :- open the main folder in terminal and enter the following command: java -jar Assignment4.jar gui
--> to open the application in text UI :- open the main folder in terminal and enter the following command: java -jar Assignment4.jar text

--> the application would then ask for username and password. Our application for the time being is for a single user.
    Username: admin
    Password: Asdf@1234

--> all the java files are present under src folder. It has config.properties file which is required to run this application.

--> the following external library needs to be installed on IDE : 
1)com.googlecode.json-simple:json-simple:1.1.1
2)org.swinglabs.swingx 1.6.1

--> the data folder holds 1 csv file Stock_Ticker.csv that holds the list of ticker symbols
    Currently there are over 500 stocks for testing.
  
--> to get the desired valuation of a stock on a specific day we call API from Alphavantage.

--> However we make use of dynamic caching where the data for a stock is called once and stored in cache. If the user wants to retrieve data about the stock again then the application does not need to call API again , it just parses the cache. Hence the dependency on API isn reduced significantly.

--> As a limitation of alpha vantage the time period is limited to 2014.
            
--> A user can enter n stocks at a time in a single portfolio. Moreover user can make n number of portfolios.

--> While uploading a custom file for inflexible portfolio user needs to make sure that the format is line 1 -> [MSFT,AAPL] line 2 -> [10,10].  Other formats would be discarded and would return an error

--> For flexible portfolio user needs to give buy stock symbol, its quantity its date and same for sell(symbol, quantity and date).

--> for inflexible portfolios of a user(admin in this case) are listed inside portfolios folder, under a folder with folder name as username.
 
--> for flexible portfolios of a user(admin in this case) are listed inside flex_portfolios folder, under a folder with folder name as username.
--> for dollar cost averaging the user may enter a blank string as end date. Here our application assumes the end date as current date and proceeds.

----------------------------------------------------------------------------------------------------------------------------------
INSTRUCTIONS TO USE GUI WHILE USING THE APPLICATION

Login page - Enter username as admin and password as Asdf@1234.
           - The alignment would be distorted sometimes as per the screen size of the device. Since the frame is SplitPane , a small notch is present                  beside the login credentials label to adjust the split panels accordingly.

 <img width="468" alt="image" src="https://media.github.khoury.northeastern.edu/user/12945/files/dc7ea00d-ea9a-490b-8a00-3f2688c38375">

Step 2: Example of a functionality.

 ![image](https://media.github.khoury.northeastern.edu/user/12945/files/3f7f8f6f-9a2d-4e92-b32b-3795988e56a5)

Step 3: Example of Performance graph

<img width="468" alt="image" src="https://media.github.khoury.northeastern.edu/user/12945/files/c88c9ced-dfbc-454b-8a15-8445f711b166">




----------------------------------------------------------------------------------------------------------------------------------


INSTRUCTION TO CREATE A PORTFOLIO WITH 3 DIFFERENT STOCKS AND GET THEIR VALUATION AS OF TODAY

(BEFORE RUNNING THE APPLICATION OPEN THE TERMINAL FOR THE CREATED FOLDER AND RUN THE FOLLOWING COMMAND:
    find . -name ".DS_Store" -print -delete.
 THIS WILL DELETE THE .DS STORE FILE IF PRESENT ON THE DEVICE)
 

1) open the folder in terminal and enter the following command: java -jar Assignment4.jar
2) enter username as "admin" and password as "Asdf@1234"
3) select option 1 to create a new portfolio.
4) enter a valid symbol and hit enter.
5) enter a valid quantity for that stock and hit enter.
6) to continue adding more stocks to a portfolio press Y/y else to finish creating enter N/n. For this scenario enter Y.
7) enter a different valid symbol from the list above and hit enter.
8) enter a valid quantity for that stock and hit enter.
9) press Y to add third stock.
10) enter a different valid symbol from the list above and hit enter.
11) enter a valid quantity for that stock and hit enter.
12) enter N to finish creating portfolio. A message of successful creation would pop up with the file name appended(admin1.txt in this case)
13) Main menu would be displayed. press 2 to examine the newly created portfolio.
14) List of portfolios would be displayed for the given user. Select the filename returned above and enter the respective portfolio number.

DISCLAIMER - KINDLY CHECK WHETHER .DS STORE IS DELETED SUCCESSFULLY. IF IT IS PRESENT IN THE FOLDER THEN IT WOULD BE HIDDEN AND WOULD HINDER THE CREATION AND EXAMINE PROCESS. IF PRESENT THEN PORTFOLIO WONT BE CREATED AND SUCCESSFUL MESSAGE WONT BE DISPLAYED RATHER AN ERROR REGARDING INDEX WOULD BE DISPLAYED

15) enter the date in yyyy-mm-dd format. for example enter the date as follows : 2022-11-01. Hit enter.
16) the list of stocks and their respective price would be visible now. At the bottom the total valuation would also be displayed.
17) enter 5 to exit or 1 to continue.
-------------------------------------------------------------------------------------------------
INSTRUCTION TO CREATE A SECOND PORTFOLIO WITH 2 DIFFERENT STOCKS AND GET THEIR VALUATION AS OF TODAY

1) select option 1 to create a new portfolio.
2) enter a valid symbol from the list above and hit enter.
3) enter a valid quantity for that stock and hit enter.
4) to continue adding more stocks to a portfolio press Y/y else to finish creating enter N/n. For this scenario enter Y.
5) enter a valid symbol from the list above and hit enter.
6) enter a valid quantity for that stock and hit enter.
7) enter N to finish creating portfolio. A message of successful creation would pop up with the file name appended(admin2.txt)
8) Main menu would be displayed. press 2 to examine the newly created portfolio.
9) List of portfolios would be displayed for the given user. Select the filename returned above and enter the respective portfolio number.
10) enter the date in yyyy-mm-dd format. for example enter the date as follows : 2010-11-01. Hit enter.
11) the list of stocks and their respective price would be visible now. At the bottom the total valuation would also be displayed.
12) enter 5 to exit.

-------------------------------------------------------------------------------------------------------
INSTRUCTIONS TO CREATE A FLEXIBLE PORTFOLIO TO BUY 3 DIFFERENT STOCKS.

1) Credentials screen:

Enter username: admin

Enter password: Asdf@1234

****************************************************************************************************
Successfully logged in.
!!! Welcome !!!
****************************************************************************************************

****************************************************************************************************

1. Inflexible Portfolio
2. Flexible Portfolio
3. Exit

****************************************************************************************************
Choose the operation to be performed next:


2) Enter 2 for flexible portfolio:

Choose the operation to be performed next:
2

****************************************************************************************************
1. Examine portfolio
2. Buy Stock
3. Sell Stock
4. Calculate Cost Basis
5. Calculate Portfolio value at a specific date
6. Performance Graph
7. Upload custom portfolio
8. Exit

****************************************************************************************************
Choose the operation to be performed next:


3) Since no portfolio is present create a new one:

Choose the operation to be performed next:
2

Enter 0 to create a new portfolio

****************************************************************************************************

No portfolios present, Please create one
****************************************************************************************************
Please enter the date at which you want to buy/sell stocks in a given portfolio(Date format: yyyy-mm-dd):


4) Enter  date as 2015-02-03.Select symbol as MSFT and quantity as 10. Enter N.
5) Enter 2 to buy again
6) Select symbol as GOOG and quantity as 20. Enter date as 2015-02-03. Enter N
7) Enter 2 to buy again.
8) Select symbol as TSLA and quantity as 30. Enter date as 2022-11-15. Enter N.
9) Press 1 to examine. A table would populate showing company tickers and their respective quantities.
10) press 4 to calculate cost basis or press 5 to calculate the value of a portfolio at specific date.
11) for both we need to enter the date.
12) For performance graph we need to enter 6
13) enter start date as 2015-02-03 and end date as 2015-11-15. You will see the graph.


