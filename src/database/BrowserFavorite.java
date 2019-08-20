package database;

public class BrowserFavorite {
    private int id;
    private String url;
    private String name;

    public BrowserFavorite(int id, String url, String name)
    {
        this.id = id;
        this.url = url;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getUrl()
    {
        return url;
    }

    public String getName() {
        return name;
    }
}
