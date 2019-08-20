package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public final class BrowserFavoritesDB {
    static final String url = "url";
    static final String name = "name";
    static final String tableName = "favorites";

    public static boolean addFavorite(BrowserFavorite bf)
    {
        return DataBase.executeSql("INSERT INTO " + tableName + "(" + url + ", " + name + ") VALUES('" + bf.getUrl() + "', '" + bf.getName() + "')");
    }

    public static boolean deleteAllFavorites()
    {
        return DataBase.executeSql("DELETE FROM " + tableName);
    }

    public static boolean deleteFavorite(ArrayList<Integer> IDs)
    {
        if (IDs.size() < 1)
            return false;
        String in = IDs.get(0).toString();
        for(int i = 1; i < IDs.size(); i++)
            in += ", " + IDs.get(i);
        return DataBase.executeSql("DELETE FROM " + tableName + " WHERE ID IN (" + in + ")");
    }

    public static ArrayList<BrowserFavorite> getFavorites()
    {
        return getFavorites(DataBase.executeQuery("SELECT * FROM " + tableName + " ORDER BY ID DESC"));
    }

    public static ArrayList<BrowserFavorite> getFavoritesBar()
    {
        return getFavorites(DataBase.executeQuery("SELECT * FROM " + tableName + " ORDER BY ID DESC LIMIT 5"));
    }

    private static ArrayList<BrowserFavorite> getFavorites(ResultSet resultSet)
    {
        ArrayList<BrowserFavorite> favorites = new ArrayList<>();
        if (resultSet != null)
        {
            try {
                while (resultSet.next())
                    favorites.add(new BrowserFavorite(resultSet.getInt("ID"), resultSet.getString(url), resultSet.getString(name)));
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            DataBase.closeConnection();
        }
        return favorites;
    }
}
