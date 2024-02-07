package ChessGUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class DBManager {
    
    private static final String URL = "jdbc:derby:chessDB";  //url of the DB host

    Connection conn;

    public DBManager() {
       
        establishConnection();
    }

    public static void main(String[] args) {
        DBManager dbManager = new DBManager();
        System.out.println(dbManager.getConnection());
    }
    
    public boolean isConnected()
    {
        return conn != null;
    }

    public Connection getConnection() {
        return this.conn;
    }

    //Establish connection
    public void establishConnection() {
        try
        {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            conn=DriverManager.getConnection(URL);
            System.out.println(URL+" connected...");
        } catch (SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    public void closeConnections() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public ResultSet queryDB(String sql) {

        Connection connection = this.conn;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return resultSet;
    }

    public void updateDB(String sql) {

        Connection connection = this.conn;
        Statement statement = null;

        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
