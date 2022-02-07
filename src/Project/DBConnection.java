package Project;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * This class holds all the functionality for connection to the database.
 */
public class DBConnection{
    /*
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    static final String DB_URL = "jdbc:mariadb://localhost:3307/cab302-project"; //edit this to match database name
    //  Database credentials -- change these to match your server creds
    static final String USER = "root";
    static final String PASS = "root";
    */

    // Variables //

    private static Connection conn = null;


    // Methods //

    /**
     * DBConnection Constructor
     */
    private DBConnection(){

        Properties props = new Properties();
        FileInputStream input = null;


        try {
            input = new FileInputStream("src/project/resources/db.properties"); // read database config details
            props.load(input);
            input.close();

            // specify driver, db url, username and password
            String driver = props.getProperty("jdbc.driver");
            String url = props.getProperty("jdbc.url");
            String port = props.getProperty("jdbc.port");
            String username = props.getProperty("jdbc.username");
            String password = props.getProperty("jdbc.password");
            String schema = props.getProperty("jdbc.schema");


            //Register JDBC driver
            Class.forName(driver);

            //Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(
                    url+":"+port+"/"+schema, username,password);
            System.out.println("Connected database successfully...");

        } catch (Exception se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }//Handle errors for Class.forName


    }

    /**
     * Connects to the database.
     *
     * @return The database connection.
     */
    public static Connection getConn(){
        if (conn == null){
            new DBConnection();
        }
        return conn;
    }

}