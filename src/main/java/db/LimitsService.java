package db;

import model.Limit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LimitsService {
    private JDBC driver;
    private Connection connection;
    private List<Limit> limits;


    public LimitsService(JDBC driver) {
        this.driver = driver;
        connection = driver.connect();
        limits = new ArrayList<Limit>();
    }

    private void createTable() {
        try {
            Statement statement = connection.createStatement();
            statement.executeQuery("CREATE TABLE  limits_per_hour (limit_name varchar(?), limit_value INTEGER, effective_date timestamp )");
            System.out.println("Table created");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    private void getLimitsFromSQL(){
        try{
            Statement sttmt = connection.createStatement();
            ResultSet result = sttmt.executeQuery("SELECT * FROM limits_per_hour ORDER BY effective_date");
            while(result.next()){
                limits.add( new Limit(result.getString("limit_name"),
                                        result.getLong("limit_value"),
                                        result.getTimestamp("effective_date")
                ));
            }
        }catch (SQLException ex){
            createTable();
        }
    }

    public List<Limit> getMaxDateLimits(){
        List<Limit> list = null;
        try{
            list = new ArrayList<Limit>();
            Statement stmnt = connection.createStatement();
            ResultSet result = stmnt.executeQuery("SELECT limit_name, limit_value, MAX(effective_date) from limits_per_hour WHERE effective_date <= CURRENT_TIMESTAMP GROUP BY limit_name");
            while(result.next()) {
               list.add( new Limit(result.getString("limit_name"),
                        result.getLong("limit_value"),
                        result.getTimestamp("effective_date")));
            }
        }catch (SQLException ex ){
            System.out.println(ex.getMessage());
        }
        return list;
    }
    //getter for colection limits
    public List<Limit> getLimits() {
        return limits;
    }

}
