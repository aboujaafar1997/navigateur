package browser.form;

import browser.ISILBrowser;
import browser.MyEventHandler;
import downloader.Downloader;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.File;

class DownloadSite extends GridPane{
    private Label lb_numberPagesDownloaded;
    private Button btn_cancel_delete, btn_openFile;
    private Downloader downloader;
    private MyEventHandler<DownloadSite> onDeleting;
    private MyEventHandler<String> onLinkClicked;
    private GridPane gp_context;

    DownloadSite(Downloader downloader)
    {
        setMaxWidth(Double.MAX_VALUE);
        getColumnConstraints().add(ISILBrowser.getFillColumn());
        gp_context = new GridPane();
        gp_context.setVgap(10);
        gp_context.setHgap(10);
        gp_context.setMaxWidth(400);
        gp_context.getStyleClass().add("downloadSiteBox");
        add(gp_context, 0, 0);
        GridPane.setHalignment(gp_context, HPos.CENTER);
        this.downloader = downloader;
        this.downloader.setOnUpdated(arg -> onUpdate());
        this.downloader.setOnFinished(arg -> onFinished());
        this.downloader.setOnCanceled(arg -> onCanceled());

        Label lb_url = new Label(downloader.getDownloadInformation().getUrl());
        lb_url.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
        {
            if (onLinkClicked != null)
                onLinkClicked.apply(this.downloader.getDownloadInformation().getUrl());
        });
        lb_url.getStyleClass().addAll("link", "downloadURL");
        gp_context.add(lb_url, 0, 0, 2, 1);
        lb_numberPagesDownloaded = new Label(downloader.getDownloadInformation().getNumberPagesDownloaded()
                + " pages de " +
                downloader.getDownloadInformation().getNumberPagesAsked()
                + ", Profondeur de " +
                downloader.getDownloadInformation().getDepthAsked());
        lb_numberPagesDownloaded.getStyleClass().add("downloadInfo");
        gp_context.add(lb_numberPagesDownloaded, 0, 1, 2, 1);

        btn_cancel_delete = new Button(downloader.isCanceled() ? "Supprimer" : "Annuler");
        btn_cancel_delete.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> btn_cancel_deleteClicked());
        btn_cancel_delete.getStyleClass().add("formButton");
        gp_context.add(btn_cancel_delete, 0, 2);

        btn_openFile = new Button("Montrer dans le fichier");
        btn_openFile.addEventHandler(MouseEvent.MOUSE_CLICKED,
                event ->
                {
                    System.out.println(this.downloader.getDownloadInformation().getPath());
                    try {
                        Desktop.getDesktop().open(new File(this.downloader.getDownloadInformation().getPath()));
                        //new ProcessBuilder("explorer.exe", "/select," + ).start();
                        //Runtime.getRuntime().exec("explorer.exe /select," + this.downloader.getDownloadInformation().getPath());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                );
        btn_openFile.getStyleClass().add("formButton");
    }

    void setOnDeleting(MyEventHandler<DownloadSite> onDeleting) {
        this.onDeleting = onDeleting;
    }

    void setOnLinkClicked(MyEventHandler<String> eventHandler)
    {
        onLinkClicked = eventHandler;
    }

    private void onUpdate()
    {
        Platform.runLater( ()->
                lb_numberPagesDownloaded.setText(downloader.getDownloadInformation().getNumberPagesDownloaded()
                        + " pages de " +
                        downloader.getDownloadInformation().getNumberPagesAsked()
                        + ", Profondeur de " +
                        downloader.getDownloadInformation().getDepthAsked())
        );
    }

    private void onFinished()
    {
        Platform.runLater( ()->
                {
                    btn_cancel_delete.setText("Supprimer");
                    gp_context.add(btn_openFile, 1 , 2);
                }
        );
    }

    private void onCanceled()
    {
        Platform.runLater( ()->
                {
                    btn_cancel_delete.setText("Supprimer");
                    lb_numberPagesDownloaded.setText("Annulé ou échoué");
                }
        );
    }

    private void btn_cancel_deleteClicked()
    {
        if (downloader.isFinished())
            onDeleting.apply(this);
        else
            downloader.cancel();
    }
}

public class DownloadsForm extends BrowserForm{
    private static DownloadsForm downloadsForm;
    private VBox vb_downloads;
    private MyEventHandler<String> onLinkClicked;
    private TextField tf_url, tf_numberPages, tf_depth;

    public static DownloadsForm getDownloadsForm(String url)
    {
        if (downloadsForm == null)
            downloadsForm = new DownloadsForm(url);
        else
            downloadsForm.setProperties(url);
        return downloadsForm;
    }

    DownloadsForm(String url)
    {
        super("Téléchargements");
        gp_context.setHgap(10);
        gp_context.setVgap(10);
        gp_context.getColumnConstraints().add(ISILBrowser.getFillColumn());
        // Add download
        GridPane gp_tf_url = new GridPane();
        gp_tf_url.getRowConstraints().add(ISILBrowser.getFillRow());
        gp_tf_url.getColumnConstraints().add(ISILBrowser.getFillColumn());
        gp_tf_url.setPadding(new Insets(0, 0, 0, 10));
        tf_url = createTextField("URL");
        gp_tf_url.add(tf_url, 0, 0);
        gp_context.add(gp_tf_url, 0, 2);

        tf_numberPages = createTextField("Nombre de pages");
        gp_context.add(tf_numberPages, 1, 2);

        tf_depth = createTextField("Profondeur");
        gp_context.add(tf_depth, 2, 2);

        HBox hb_btn_download = new HBox(createButton("Télécharger", e -> startDownload()));
        hb_btn_download.setPadding(new Insets(0, 10, 0, 0));
        gp_context.add(hb_btn_download, 3, 2);
        gp_context.add(createLine(), 0, 3, 4, 1);
        setProperties(url);

        vb_downloads = new VBox(20);
        vb_downloads.setPadding(new Insets(20));
        vb_downloads.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        ScrollPane sp_downloads = new ScrollPane(vb_downloads);
        sp_downloads.getStyleClass().add("downloadList");
        sp_downloads.setFitToWidth(true);
        sp_downloads.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        gp_context.add(sp_downloads, 0, 4, 4, 1);
    }

    protected void load(){}

    private void startDownload()
    {
        if (!tf_depth.getText().matches("-?(0|[1-9]\\d*)") || !tf_numberPages.getText().matches("-?(0|[1-9]\\d*)") || tf_url.getText().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("URL ou nombre invalide");
            (alert).show();
        }
        else
        {
            Downloader downloader = new Downloader(tf_url.getText(), Integer.parseInt(tf_depth.getText()), Integer.parseInt(tf_numberPages.getText()));
            DownloadSite downloadSite = new DownloadSite(downloader);
            downloadSite.setOnLinkClicked(arg ->
            {
                if (onLinkClicked != null)
                    onLinkClicked.apply(arg);
            });
            downloadSite.setOnDeleting(arg ->
                Platform.runLater(() -> vb_downloads.getChildren().remove(arg) )
            );
            vb_downloads.getChildren().add(0, downloadSite);
            downloader.start();
            tf_url.setText("");
            tf_depth.setText("");
            tf_numberPages.setText("");
            load();
        }
    }

    public void setOnLinkClicked(MyEventHandler<String> eventHandler)
    {
        onLinkClicked = eventHandler;
    }

    public void setProperties(String url)
    {
        tf_url.setText(url);
    }
}
