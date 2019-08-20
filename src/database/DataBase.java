package database;

import java.sql.*;

final class DataBase {
    private static Connection connection = null;
    private static Statement statement = null;

    private static boolean openConnection()
    {
        try
        {
            connection = DriverManager.getConnection("jdbc:sqlite:" + System.getProperty("user.home") + "\\isilBrowser.db");
            statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS " + BrowserHistoryDB.tableName +
                    " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + BrowserHistoryDB.url + " TEXT, " + BrowserHistoryDB.time + " TEXT)");
            statement.execute("CREATE TABLE IF NOT EXISTS " + BrowserFavoritesDB.tableName +
                    " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + BrowserFavoritesDB.url + " TEXT, " + BrowserFavoritesDB.name + " TEXT)");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    static boolean closeConnection()
    {
        try
        {
            if (statement != null)
            {
                statement.close();
                statement = null;
            }
            if (connection != null)
            {
                connection.close();
                connection = null;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    static boolean executeSql(String sql)
    {
        boolean works = false;
        openConnection();
        try
        {
            works = statement.execute(sql);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        closeConnection();
        return works;
    }

    static ResultSet executeQuery(String query)
    {
        openConnection();
        try
        {
            return statement.executeQuery(query);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            closeConnection();
        }
        return null;
    }
}
