package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBC {
    private Connection connection;
    private String user;
    private String password;
    private String url;

    public JDBC( String user, String password, String url) {
        this.user = user;
        this.password = password;
        this.url = url;
    }

    public Connection connect(){
        try{
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);
        }catch (SQLException ex){
            System.out.println(ex.getMessage());
        }catch (ClassNotFoundException ex){
            System.out.println(ex.getMessage());
        }
        return connection;
    }
}
