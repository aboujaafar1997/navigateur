package downloader;

public class DownloadInformation
{
    private int numberPagesAsked;
    private int depthAsked;
    private int numberPagesDownloaded;
    private String path;
    private String url;

    public DownloadInformation(int numberPagesAsked, int depthAsked, int numberPagesDownloaded, String path, String url)
    {
        this.numberPagesAsked = numberPagesAsked;
        this.depthAsked = depthAsked;
        this.numberPagesDownloaded = numberPagesDownloaded;
        this.path = path;
        this.url = url;
    }

    public int getNumberPagesAsked() {
        return numberPagesAsked;
    }

    public int getDepthAsked() {
        return depthAsked;
    }

    public int getNumberPagesDownloaded() {
        return numberPagesDownloaded;
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }

    public void setNumberPagesDownloaded(int numberPagesDownloaded) {
        this.numberPagesDownloaded = numberPagesDownloaded;
    }

    @Override
    public String toString() {
        return "Url : " + url + "\nPath : " + path + "\nDepth asked : " + depthAsked + "\nNumber pages asked : " + numberPagesAsked + "\nNumber pages downloaded : " + numberPagesDownloaded;
    }
}
