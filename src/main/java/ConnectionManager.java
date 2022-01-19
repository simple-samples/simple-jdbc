import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {


    //private static ConnectionManager connectionManager;
    private static Connection connection;

    private ConnectionManager() {
    }

//    public static ConnectionManager getConnectionManager() {
//        if(connectionManager == null) {
//            connectionManager = new ConnectionManager();
//        }
//        return connectionManager;
//    }

    public static Connection getConnection() {
        if(connection == null) {
            connection = connect();
        }
        return connection;
    }

    private static Connection connect() {
        //connection logic here
        /*
        jdbc:mariadb://<hostname>:<port>/<databaseName>?user=<username>&password=<password>
         */
        try {
            Properties props = new Properties();
            FileReader fr = new FileReader("src/main/resources/jdbc.properties");
            props.load(fr);

            String connectionString = "jdbc:mariadb://" +
            props.getProperty("hostname") + ":" +
            props.getProperty("port") + "/" +
            props.getProperty("dbname") + "?user=" +
            props.getProperty("username") + "&password=" +
            props.getProperty("password");

            //Class.forName("org.mariadb.jdbc.Driver");

            connection = DriverManager.getConnection(connectionString);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }


        return connection;
    }


}
