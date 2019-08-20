package browser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.apache.commons.validator.routines.UrlValidator;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class ControllerBar extends GridPane {


    private MyEventHandler onShowHistory,
            onShowFavorites,
            onShowDownloads,
            onReload,
            onGoBack,
            onGoForward,
            onGoHome;

    private MyEventHandler<String> onLoadURL;
    private TextField tf_url;
    private boolean searchMode;

    ControllerBar(String url, double height)
    {
        searchMode = true;
        // Bottom border
        setBorder(new Border(new BorderStroke(Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY,
                BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
                CornerRadii.EMPTY, new BorderWidths(1), Insets.EMPTY)));
        getStyleClass().add("controllerBar");
        setPadding(new Insets(5));
        setHeight(height);
        setHgap(5);
        // Set bar columns
        ColumnConstraints col = new ColumnConstraints();
        col.setFillWidth(true);
        col.setHgrow(Priority.ALWAYS);
        getColumnConstraints().addAll(new ColumnConstraints(), new ColumnConstraints(), new ColumnConstraints(), new ColumnConstraints(), col);
        // Back button
        ImageView iv_back = new ImageView("resources/forward.png");
        iv_back.setRotate(180);
        iv_back.getStyleClass().add("navButton");
        iv_back.setFitWidth(getHeight());
        iv_back.setFitHeight(getHeight());
        add(createContainerButton(iv_back, e -> onGoBack.apply("")), 0, 0);
        // Forward button
        ImageView iv_forward = new ImageView("resources/forward.png");
        iv_forward.getStyleClass().add("navButton");
        iv_forward.setFitWidth(getHeight());
        iv_forward.setFitHeight(getHeight());
        add(createContainerButton(iv_forward, e -> onGoForward.apply("")), 1, 0);
        // Reload button
        ImageView iv_reload = new ImageView("resources/reload.png");
        iv_reload.setFitWidth(getHeight());
        iv_reload.setFitHeight(getHeight());
        add(createContainerButton(iv_reload, e -> onReload.apply("")), 2, 0);
        // Home button
        ImageView iv_home = new ImageView("resources/home.png");
        iv_home.setFitWidth(getHeight());
        iv_home.setFitHeight(getHeight());
        add(createContainerButton(iv_home, e -> onGoHome.apply("")), 3, 0);
        // URL box
        GridPane gp_url = new GridPane();
        gp_url.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        gp_url.getColumnConstraints().add(ISILBrowser.getFillColumn());
        gp_url.getRowConstraints().add(ISILBrowser.getFillRow());
        tf_url = new TextField();
        tf_url.setText(url);
        tf_url.setPromptText("Rechercher sur Google ou taper une URL");
        tf_url.getStyleClass().add("browserTextField");
        tf_url.getStyleClass().add("urlTextField");
        tf_url.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        tf_url.setAlignment(Pos.CENTER_LEFT);
        tf_url.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !tf_url.getText().isEmpty())
            {
                String _url = tf_url.getText();
                if (searchMode)
                {
                    boolean search = false;
                    if (!new UrlValidator().isValid(_url))
                        if (!_url.startsWith("https://") || !_url.startsWith("http://"))
                        {
                            _url = "https://" + _url;
                            if (!new UrlValidator().isValid(_url))
                                search = true;
                        }
                    if (search)
                    {
                        try {
                            _url = ISILBrowser.searchEngine + URLEncoder.encode(tf_url.getText(), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
                onLoadURL.apply(_url);
            }
        });
        gp_url.add(tf_url, 0, 0);
        ImageView iv_search = new ImageView();
        iv_search.getStyleClass().add("searchIcon");
        iv_search.pseudoClassStateChanged(BrowserTab.selectedElement, searchMode);
        iv_search.setFitWidth(getHeight());
        iv_search.setFitHeight(getHeight());
        HBox c = createContainerButton(iv_search, arg ->
                {
                    searchMode = !searchMode;
                    iv_search.pseudoClassStateChanged(BrowserTab.selectedElement, searchMode);
                });
        c.setMaxWidth(getHeight());
        gp_url.add(c, 0, 0);
        add(gp_url, 4, 0);
        // Favorites button
        ImageView iv_favorite = new ImageView("resources/favorite.png");
        iv_favorite.setFitWidth(getHeight());
        iv_favorite.setFitHeight(getHeight());
        add(createContainerButton(iv_favorite, e -> onShowFavorites.apply("")), 5, 0);
        // Download button
        ImageView iv_download = new ImageView("resources/download.png");
        iv_download.setFitWidth(getHeight());
        iv_download.setFitHeight(getHeight());
        add(createContainerButton(iv_download, e -> onShowDownloads.apply("")), 6, 0);
        // History button
        ImageView iv_history = new ImageView("resources/history.png");
        iv_history.setFitWidth(getHeight());
        iv_history.setFitHeight(getHeight());
        add(createContainerButton(iv_history, e -> onShowHistory.apply("")), 7, 0);
    }

    ControllerBar(String url)
    {
        this(url, 30);
    }

    public static HBox createContainerButton(ImageView childNode, MyEventHandler eventHandler)
    {
        HBox hb_container = new HBox(childNode);
        hb_container.setPrefSize(childNode.getFitWidth(), childNode.getFitHeight());
        hb_container.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (eventHandler != null)
                eventHandler.apply(event);
            else
                System.out.println("NULL");
        });
        hb_container.getStyleClass().add("browseButton");
        return hb_container;
    }

    void setURL(String url)
    {
        tf_url.setText(url);
    }

    String getURL()
    {
        return tf_url.getText();
    }

    void setOnLoadURL(MyEventHandler<String> onLoadURL) {
        this.onLoadURL = onLoadURL;
    }

    void setOnShowDownloads(MyEventHandler onShowDownloads) {
        this.onShowDownloads = onShowDownloads;
    }

    void setOnShowFavorites(MyEventHandler onShowFavorites) {
        this.onShowFavorites = onShowFavorites;
    }

    void setOnShowHistory(MyEventHandler onShowHistory) {
        this.onShowHistory = onShowHistory;
    }

    void setOnReload(MyEventHandler onReload) {
        this.onReload = onReload;
    }

    void setOnGoForward(MyEventHandler onGoForward) {
        this.onGoForward = onGoForward;
    }

    void setOnGoBack(MyEventHandler onGoBack) {
        this.onGoBack = onGoBack;
    }

    void setOnGoHome(MyEventHandler onGoHome) {
        this.onGoHome = onGoHome;
    }
}
