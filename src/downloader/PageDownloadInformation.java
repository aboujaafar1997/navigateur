package downloader;

class PageDownloadInformation
{
    private int numberPagesDownloaded;
    private String newPath,
            absolutePath;

    PageDownloadInformation(int numberPagesDownloaded, String newPath, String absolutePath)
    {
        this.numberPagesDownloaded = numberPagesDownloaded;
        this.newPath = newPath;
        this.absolutePath = absolutePath;
    }

    int getNumberPagesDownloaded() {
        return numberPagesDownloaded;
    }

    String getNewPath() {
        return newPath;
    }

    String getAbsolutePath() {
        return absolutePath;
    }
}
