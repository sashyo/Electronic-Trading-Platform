# CAB302 Major Project GroupOfFour_030 Electronic Trading Platform

###### Andrew Wilks | Libby Robinson | Ash Phillips | Sasha Le

# Getting Started

### Preparation - Getting started with IntelliJ
You can download IntelliJ IDEA Community Edition from https://www.jetbrains.com/idea/download/ and Amazon correcto 15 from https://docs.aws.amazon.com/corretto/latest/corretto-15-ug/downloads-list.html.
Follow the prompts and install the default suggestions.

We have provided our source code in the .zip. If this is the first time you're running the IDEA, a popup screen with import/open or "Get from version control" will appear. Click on import/open and navigate to the included source to open. You can also open this by going to File -> New -> Open -> Navigate to unzipped folder and open the project.

You will need to install the MariaDB JDBC driver in  **IntelliJ by opening File -> Project Structure -> Libraries -> New Project Library (+ button) -> From Maven -> Search for org.mariadb.jdbc:mariadb-java-client and install the latest version**. Alternatively, the MySQL JDBC driver (MySQL Connector/J) is available from http://dev.mysql.com/downloads/connector/, along with connectors for other programming languages.

Our other libraries we have used for testing are **org.junit.jupiter:junit-jupiter:5.4.2**


### Preparation - Getting started with MariaDB
Our group has developed our software to work with MariaDB to store our system data.
We have included two SQL dump that will need to be imported to your local server. 
* Initial_DATA.sql
* SAMPLE_DATA.sql

### Setting up MariaDB on local machine
First, if you have not done so already, you will need to install MariaDB Server. MariaDB Server can be downloaded from https://mariadb.org/download/.

The exact process of installation will depend on your particular platform and requirements. There are two specific steps during installation you should make note of:
The port number. By default, MariaDB runs on a port of 3306. This is usually fine, but if you already have another MariaDB or MySQL instance installed and running, you should choose another port so they do not clash.

The root password. You will need this to make any changes to the database.

**Note: We have configured a properties file located under src/Project/Resources/ called ***db.properties***
 This file contains the software database connection configuration.This is where you will need to change the configuration to match your local server setup. Please check this carefully and change where needed**
 ***Common configuration to change is the port number, username and password***
 
 ### Running the Electronic Trading Platform
 Once you have IntelliJ IDEA and MariaDB setup. Please make sure that the db.properties files are correctly configured to match you local MariaDb settings and the project is open in IntelliJ IDEA.
 
 You can now build and run our software. **You can do this by opening the project in IntelliJ IDEA, expanding Project and navigate to CAB302_MajorProject_GroupOfFour_030/src/Project -> right clik GUI.java and select 'Run GUI.main();'**
