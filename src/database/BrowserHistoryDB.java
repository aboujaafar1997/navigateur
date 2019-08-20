package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public final class BrowserHistoryDB {
    static final String url = "url";
    static final String time = "time";
    static final String tableName = "history";

    public static boolean addHistory(BrowserHistory bh)
    {
        return DataBase.executeSql("INSERT INTO " + tableName + "(" + url + ", " + time + ") VALUES('" + bh.getUrl() + "', '" + bh.getTime() + "')");
    }

    public static boolean deleteAllHistory()
    {
        return DataBase.executeSql("DELETE FROM " + tableName);
    }

    public static boolean deleteHistory(ArrayList<Integer> IDs)
    {
        if (IDs.size() < 1)
            return false;
        String in = IDs.get(0).toString();
        for(int i = 1; i < IDs.size(); i++)
            in += ", " + IDs.get(i);
        return DataBase.executeSql("DELETE FROM " + tableName + " WHERE ID IN (" + in + ")");
    }

    public static ArrayList<BrowserHistory> getHistories()
    {
        ResultSet resultSet = DataBase.executeQuery("SELECT * FROM " + tableName + " ORDER BY ID DESC");

        ArrayList<BrowserHistory> histories = new ArrayList<>();
        if (resultSet != null)
        {
            try {
                while (resultSet.next())
                    histories.add(new BrowserHistory(resultSet.getInt("ID"), resultSet.getString(url), resultSet.getString(time)));
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            DataBase.closeConnection();
        }
        return histories;
    }
}
