package database;

public class BrowserHistory {
    private int id;
    private String url;
    private String time;

    public BrowserHistory(int id, String url, String time)
    {
        this.id = id;
        this.url = url;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getUrl()
    {
        return url;
    }

    public String getTime() {
        return time;
    }
}
