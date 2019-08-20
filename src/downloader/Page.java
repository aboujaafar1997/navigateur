package downloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

class Page {
    private String context;
    private String path;
    private String name;
    private String host;
    private String url;
    private String currectedURL;
    private int depth;
    private int numberPages;
    private Downloader downloader;

    private Page(String html, String url, String currectedURL, String path, String name, String host, int depth, int numberPages, Downloader downloader)
    {
        this.downloader = downloader;
        this.context = html;
        this.url = url;
        this.currectedURL = currectedURL;
        this.name = name;
        this.host = host;
        this.path = path;
        this.depth = depth;
        this.numberPages = numberPages;
    }

    private int downloadPage()
    {
        if (downloader.isCanceled())
            return 0;
        String newPath = makeDirectory(path, name, true);
        if (newPath == null)
            return 0;
        path = path + "/" + newPath;

        makeDirectory(path, "resources", false);
        makeDirectory(path, "css", false);
        makeDirectory(path, "scripts", false);
        makeDirectory(path, "pages", false);

        if (downloader.isCanceled())
            return 0;

        Document document;
        try {
            document = Jsoup.parse(context);
        }
        catch (Exception ignored){
            return 0;
        }

        Set<String> links = new TreeSet<>();

        Elements hrefs = document.select("a[href]");
        Elements srcs = document.select("[src]");
        Elements linkHrefs = document.select("link[href]");

        if (hrefs != null)
            for (Element element : hrefs)
                addToLinks(links, element.attr("href"), url);

        if (srcs != null)
            for (Element element : srcs)
                addToLinks(links, element.attr("src"), url);

        if (linkHrefs != null)
            for (Element element : linkHrefs)
                addToLinks(links, element.attr("href"), url);

        if (downloader.isCanceled())
            return 0;

        int numberPagesDownloaded = 0;
        for(String link : links)
        {
            if (downloader.isCanceled())
                return numberPagesDownloaded;
            PageDownloadInformation downloadInformation = download(link, currectedURL, path, host, depth, numberPages, false, downloader);
            if (downloadInformation != null && downloadInformation.getNewPath() != null)
            {
                System.out.println(link + "\t\t" + downloadInformation.getNewPath() + "\n**************************************");
                numberPagesDownloaded += downloadInformation.getNumberPagesDownloaded();
                numberPages -= downloadInformation.getNumberPagesDownloaded();
                if (link.equals("/"))
                    context = context.replace("=\"/\"", "=\"" + downloadInformation.getNewPath() + "\"");
                else
                    context = context.replace(link, downloadInformation.getNewPath());
            }
            else
                if (link.split("/").length == 1)
                    context = context.replace(link, "#");
        }
        context = context.replace("=\"/\"", "=\"https://" + host + "\"");
        context = context.replace("=\"/", "=\"https://" + host + "/");
        context = context.replace("=\".", "=\"" + url + "/.");
        return numberPagesDownloaded;
    }

    private void addToLinks(Set<String> links, String link, String url)
    {
        if (!link.isEmpty() && !link.equals("#"))
        {
            System.out.println(link + "\n------------------------------------------------");
            links.add(link);
        }
    }

    static DownloadInformation downloadIfPage(String url, int depth, int numberPages, Downloader downloader)
    {
        if (url.isEmpty())
            return null;
        if (downloader.isCanceled())
            return null;
        String path = System.getProperty("user.home").replace("\\", "/") + "/Downloads";
        path = path + "/" + makeDirectory(path, "ISIL Downloads", false);
        if (downloader.isCanceled())
            return null;
        PageDownloadInformation pageDownloadInformation = download(url, null, path, null, depth, numberPages, true, downloader);
        if (pageDownloadInformation == null)
            return null;
        else
            return new DownloadInformation(
                    numberPages,
                    depth,
                    pageDownloadInformation.getNumberPagesDownloaded(),
                    pageDownloadInformation.getAbsolutePath(),
                    url
            );
    }

    private static PageDownloadInformation download(String url, String parent, String path, String host, int depth, int numberPages, boolean start, Downloader downloader)
    {
        if (downloader.isCanceled())
            return null;
        String u = url;
        if (!url.isEmpty())
        {
            try
            {
                String[] currentURL = {url};
                URLConnection urlConnection = getURLConnection(currentURL, parent, host);
                String type = urlConnection.getContentType().split(";")[0].split("/")[1];
                if (!type.equals("html"))
                {
                    if (downloader.isCanceled())
                        return null;
                    if (!start)
                    {
                        String newURL = null;
                        if (isSupportedImage(type))
                        {
                            newURL = "resources/" + Paths.get(urlConnection.getURL().getPath()).getFileName();
                            ImageIO.write(ImageIO.read(urlConnection.getInputStream()),
                                    type,
                                    new BufferedOutputStream( new FileOutputStream(new File(path + "/resources/" +
                                            Paths.get(urlConnection.getURL().getPath()).getFileName()))));
                        }
                        else if (type.equals("javascript") || type.equals("x-javascript") || type.equals("json") || type.equals("css"))
                        {
                            newURL = (type.equals("json") ? "resources" : type.equals("javascript") || type.equals("x-javascript") ? "scripts" : type) + "/"
                                    + Paths.get(urlConnection.getURL().getPath()).getFileName();
                            saveFile(getContextFromStream(urlConnection.getInputStream()),
                                    path + "/" + newURL);
                        }
                        return new PageDownloadInformation(0, newURL, path);
                    }
                }
                else if (depth > 0 && numberPages > 0)
                {
                    if (downloader.isCanceled())
                        return null;
                    if (start || urlConnection.getURL().getHost().equals(host))
                    {
                        if (!start)
                            path += "/pages";
                        Page page = new Page(getContextFromStream(urlConnection.getInputStream()),
                                url,
                                currentURL[0],
                                path,
                                getName(url),
                                urlConnection.getURL().getHost(),
                                depth - 1, numberPages - 1,
                                downloader);
                        if (downloader.isCanceled())
                            return null;
                        int nbPages = page.downloadPage() + 1;
                        String fileName = page.name + ".html";
                        saveFile(page.context, page.path + "/" + fileName);
                        downloader.update(nbPages);
                        return new PageDownloadInformation(nbPages, fileName, page.path);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("url : " + url + "\n" + "parent : " + parent/* + "\ntype : " + type + "\ncontext : " + c*/);
                return null;
            }
        }
        return new PageDownloadInformation(0, null, null);
    }

    private static String getName(String url)
    {
        //Paths.get(urlConnection.getURL().getPath()).getFileName().toString()
        String name = url.substring( url.lastIndexOf('/')+1);
        if (name.isEmpty())
            name = "downloadedPage";
        return name;
    }

    private static void saveFile(String context, String path) throws IOException
    {
        ArrayList<String> contextArray = new ArrayList();
        contextArray.add(context);
        Files.write(Paths.get(path), contextArray, Charset.forName("UTF-8"));
    }

    private static String getContextFromStream(InputStream stream)
    {
        return new BufferedReader(new InputStreamReader(stream)).lines().parallel().collect(Collectors.joining("\n"));
    }

    private static boolean isSupportedImage(String type)
    {
        return (type.equals("jpg") || type.equals("png") || type.equals("gif"));
    }

    private static String makeDirectory(String path, String directoryName, boolean force)
    {
        File folder;
        int n = 0;
        do{
            folder = new File(path, directoryName + ((n > 0) ? n : ""));
            if (!folder.exists())
            {
                if (!folder.mkdir())
                {
                    System.out.println(path + "       " + folder.getName());
                    return null;
                }
                n = 0;
            }
            else if (force)
                n++;
        }while (n != 0);
        return folder.getName();
    }

    private static URLConnection getURLConnection(String[] url, String parent, String host)
    {
        url[0] = url[0].replace("\\", "/");
        URLConnection urlConnection = null;
        if (!url[0].startsWith("https://"))
        {
            if (url[0].startsWith("http://"))
            {
                url[0] = url[0].replaceFirst("http:", "https:");
            }
            else
            {
                if (url[0].equals("/"))
                {
                    if (host != null)
                        url[0] = host;
                }
                else
                    if (parent != null && (url[0].startsWith(".") || url[0].startsWith("/")))
                        url[0] = parent + (parent.endsWith("/") ? "" : "/") + url[0];
            }
        }
        int x = parent == null ? 1 : 0;
        while (x < 2)
        {
            try {
                urlConnection = new URL(url[0]).openConnection();
                x = 2;
            }
            catch (IOException e)
            {
                System.out.println("NULL");
                url[0] = parent + url[0];
                x++;
            }
        }
        System.out.println("getURLConnection : " + url[0] + "    " + ((urlConnection == null) ? "*  " + (parent == null) : ""));
        return urlConnection;
    }
}
