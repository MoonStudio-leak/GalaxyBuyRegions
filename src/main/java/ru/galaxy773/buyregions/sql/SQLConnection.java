package ru.galaxy773.buyregions.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import ru.galaxy773.buyregions.Main;

public class SQLConnection {

    public Connection connection;

    public void openConnection() {
        try {
            Class.forName("org.sqlite.JDBC").newInstance();
            this.connection = DriverManager.getConnection("jdbc:sqlite://" + Main.getInstance().getDataFolder().getAbsolutePath() + "/data.db");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
  
    public void execute(final String query, Object... values) {
        (new Thread(new Runnable() {
            public void run() {
                try {
                    if (SQLConnection.this.connection == null || SQLConnection.this.connection.isClosed()) {
                        SQLConnection.this.openConnection();
                    }
                    PreparedStatement ex = SQLConnection.this.connection.prepareStatement(query);
                    for (int i = 0; i < values.length; i++) {
                        ex.setObject(i + 1, values[i]);
                    }
                    ex.executeUpdate();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
          }
        })).start();
    }
  
    public ResultSet executeQuery(String query, Object... values) {
        try {
            if (this.connection == null || this.connection.isClosed()) openConnection();
        } catch (SQLException ex) {
            ex.printStackTrace();
            }
        ResultSetThread rst = new ResultSetThread(query, values);
        rst.run();
        return rst.res;
    }
  
    public class ResultSetThread extends Thread {
        ResultSet res;
        String query;
        Object[] values;

        public ResultSetThread(String query, Object... values) {
            this.query = query;
            this.values = values;
        }

        public void run() {
            try {
                if (SQLConnection.this.connection == null || SQLConnection.this.connection.isClosed()) {
                    SQLConnection.this.openConnection();
                }
                PreparedStatement ex = SQLConnection.this.connection.prepareStatement(this.query);
                for (int i = 0; i < this.values.length; i++) {
                    ex.setObject(i + 1, this.values[i]);
                }
                this.res = ex.executeQuery();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
