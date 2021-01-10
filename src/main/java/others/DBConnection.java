/*
 * Author : Suraj Dakua
 * Date : 3/11/2020
 * Description : JDBC Oracle 11g XE connection file.
 */

package main.java.others;

/*
 * Export class method getConnection() DBConnection
 * Return Connection object
 */


import main.java.controllers.PromptDialogController;
/* Import JDBC SQL Libraries */
import java.sql.*;

public class DBConnection{
    /* This is DBConnection Class field which is private not accessible outside class. */
    /* lsnrctl start to start service of TNS Oracle. */
    private static final String url = "jdbc:oracle:thin:@localhost:1521:XE";

    /* Create function getConnection() returns Object of Interface Connection */
    public static Connection getConnection(){
        /* Create object of class Connection */
        Connection conn = null;

        /* DriveManager Function of Interface Connection */
        /* This is called Defensive programming. */
        try{
            conn = DriverManager.getConnection(url, "eatwell", "123456");

        }catch (SQLException e){
            if(true){
                e.printStackTrace();

                /* Call constructor of class PromptDialogController. */
                new PromptDialogController("DB Failure Message", "Invalid username/password.");
            }
            return null;
        }
        return conn;
    }
}
