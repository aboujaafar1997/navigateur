package downloader;

import browser.MyEventHandler;

public class Downloader extends Thread{
    private String url;
    private int depth;
    private int numberPages;
    private boolean canceled,
            finished;
    private DownloadInformation downloadInformation;
    private MyEventHandler onCanceled,
            onUpdated,
            onFinished;

    public Downloader(String url, int depth, int numberPages)
    {
        finished = false;
        canceled = false;
        downloadInformation = new DownloadInformation(numberPages, depth, 0, null, url);
        this.url = url;
        this.depth = depth;
        this.numberPages = numberPages;
    }

    @Override
    public void run() {
        DownloadInformation downloadInformation = Page.downloadIfPage(url, depth, numberPages, this);
        finished = true;
        if (downloadInformation == null)
        {
            if (onCanceled != null)
                onCanceled.apply(null);
            return;
        }
        if (!isCanceled())
        {
            this.downloadInformation = downloadInformation;
            System.out.println("------------>" + this.downloadInformation.getPath());
            if (onFinished != null)
                onFinished.apply(downloadInformation);
        }
    }

    void update(int numberPagesDownloaded)
    {
        downloadInformation.setNumberPagesDownloaded(downloadInformation.getNumberPagesDownloaded() + numberPagesDownloaded);
        if (onUpdated != null)
            onUpdated.apply(numberPagesDownloaded);
    }


    public boolean isCanceled()
    {
        return canceled;
    }

    public boolean isFinished()
    {
        return finished;
    }

    public void cancel()
    {
        canceled = true;
        if (onCanceled != null)
            onCanceled.apply(null);
    }

    public void setOnFinished(MyEventHandler onFinished) {
        this.onFinished = onFinished;
    }

    public void setOnUpdated(MyEventHandler onUpdated) {
        this.onUpdated = onUpdated;
    }

    public void setOnCanceled(MyEventHandler onCanceled) {
        this.onCanceled = onCanceled;
    }

    public DownloadInformation getDownloadInformation() {
        return downloadInformation;
    }
}
