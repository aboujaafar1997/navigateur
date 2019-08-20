package browser;

import browser.form.*;
import database.BrowserHistory;
import database.BrowserHistoryDB;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import java.util.Date;

public class BrowserTab extends GridPane {

    public static final PseudoClass selectedElement = PseudoClass.getPseudoClass("selected");

    private Label lb_title;
    private MyEventHandler<BrowserTab> onClosing;
    private MyEventHandler<BrowserTab> onSelected;
    private MyEventHandler<String> onUrlChanged;
    private String url;
    private boolean selected;
    private WebEngine webEngine;
    private WebHistory webHistory;
    private WebViewState webViewState;
    private BrowserForm openedForm,
            downloadsForm,
            favoritesForm,
            historyForm;
    private GridPane gp_context;

    BrowserTab(String url, double size)
    {
        setPadding(new Insets(0, 5, 0, 5));
        // Set CSS class
        getStyleClass().add("browserTab");
        pseudoClassStateChanged(selectedElement, true);
        selected = true;
        // Left border
        /*setBorder(new Border(new BorderStroke(Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY,
                BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE,
                CornerRadii.EMPTY, new BorderWidths(1), Insets.EMPTY)));*/
        // Take all available width
        getColumnConstraints().addAll(ISILBrowser.getFillColumn(), new ColumnConstraints());
        // Page title
        lb_title = new Label("Bienvenue à ISIL navigateur");
        lb_title.setMaxWidth(Double.MAX_VALUE);
        // Close button
        ImageView iv_close = new ImageView("resources/close_tab.png");
        iv_close.setFitWidth(size - 5);
        iv_close.setFitHeight(size - 5);
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {if (!selected) onSelected.apply(this);});
        add(lb_title, 0, 0);
        add(ControllerBar.createContainerButton(iv_close, event -> {selected = true; onClosing.apply(this);}), 1, 0);
        // Context
        gp_context = new GridPane();
        gp_context.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        gp_context.getColumnConstraints().add(ISILBrowser.getFillColumn());
        gp_context.getRowConstraints().add(ISILBrowser.getFillRow());
        // WebView
        WebView webView = new WebView();
        gp_context.add(webView, 0, 0);
        webEngine = webView.getEngine();
        webHistory = webEngine.getHistory();
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue)
            {
                case SCHEDULED:
                    if (this.url == null || !this.url.equals(webEngine.getLocation()))
                        BrowserHistoryDB.addHistory(new BrowserHistory(0, webEngine.getLocation(), new Date().toString()));
                    lb_title.setText(webEngine.getLocation());
                    if (!webViewState.isShowing())
                        gp_context.add(webViewState, 0, 0);
                    webViewState.setState(State.Loading);
                    this.url = webEngine.getLocation();
                    if (onUrlChanged != null)
                        onUrlChanged.apply(this.url);
                    break;
                case SUCCEEDED:
                    webViewState.close();
                    gp_context.getChildren().remove(webViewState);
                    lb_title.setText(webEngine.getTitle());
                    break;
                case FAILED: case CANCELLED:
                    if (!webViewState.isShowing())
                        gp_context.add(webViewState, 0, 0);
                    webViewState.setState(State.CanNotReach);
                    break;
            }
        });
        // Web view state
        webViewState = new WebViewState();
        loadURL(url);
    }

    GridPane getContext()
    {
        return gp_context;
    }

    void setOnClosing(MyEventHandler<BrowserTab> e)
    {
        onClosing = e;
    }

    void setOnSelected(MyEventHandler<BrowserTab> e)
    {
        onSelected = e;
    }

    void setOnUrlChanged(MyEventHandler<String> onUrlChanged) {
        this.onUrlChanged = onUrlChanged;
    }

    void openForm(BrowserFormType formType)
    {
        switch (formType)
        {
            case Favorite:
                if (favoritesForm == null)
                {
                    favoritesForm = new FavoritesForm(webEngine.getLocation(), webEngine.getTitle());
                    ((FavoritesForm) favoritesForm).setOnLinkClicked(arg -> loadURL(arg.getUrl()));
                    favoritesForm.setOnClosing(arg -> closeOpenedForm());
                }
                ((FavoritesForm) favoritesForm).setProperties(webEngine.getLocation(), webEngine.getTitle());
                openForm(favoritesForm);
                break;
            case History:
                if (historyForm == null)
                {
                    historyForm = new HistoryForm();
                    ((HistoryForm) historyForm).setOnLinkClicked(arg -> loadURL(arg.getUrl()));
                    historyForm.setOnClosing(arg -> closeOpenedForm());
                }
                openForm(historyForm);
                break;
            default:
                if (downloadsForm == null)
                {
                    downloadsForm = DownloadsForm.getDownloadsForm(webEngine.getLocation());
                    ((DownloadsForm) downloadsForm).setOnLinkClicked(arg -> loadURL(arg));
                    downloadsForm.setOnClosing(arg -> closeOpenedForm());
                }
                ((DownloadsForm)downloadsForm).setProperties(webEngine.getLocation());
                openForm(downloadsForm);
                break;
        }

    }

    private void openForm(BrowserForm form)
    {
        if (form instanceof DownloadsForm)
        {
            if (form.isOpen())
                if (openedForm == form)
                    return;
                else
                    ((GridPane)form.getParent()).getChildren().remove(form);
        }
        else
        {
            if (form.isOpen())
            {
                form.Open();
                return;
            }
        }
        closeOpenedForm();
        form.Open();
        gp_context.add(form, 0, 0);
        openedForm = form;
    }

    private void closeOpenedForm()
    {
        if (openedForm != null)
        {
            openedForm.Close();
            gp_context.getChildren().remove(openedForm);
        }
    }

    void setSelected(boolean selected)
    {
        this.selected = selected;
        // Change select state of tab for apply css style
        pseudoClassStateChanged(selectedElement, selected);
    }

    void saveURL(String url)
    {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    void loadURL(String url)
    {
        closeOpenedForm();
        webEngine.load(url);
    }

    void reload()
    {
        if (openedForm != null && openedForm.isOpen())
            openedForm.Open();
        else
            webEngine.reload();
    }

    void goBack()
    {
        if (openedForm != null && openedForm.isOpen())
            closeOpenedForm();
        else
        {
            ObservableList<WebHistory.Entry> entryList = webHistory.getEntries();
            Platform.runLater(() -> webHistory.go(entryList.size() > 1 && webHistory.getCurrentIndex() > 0 ? -1 : 0) );
        }
    }

    void goForward()
    {
        if (openedForm != null && openedForm.isOpen())
            openedForm.Open();
        else
        {
            ObservableList<WebHistory.Entry> entryList = webHistory.getEntries();
            Platform.runLater(() -> webHistory.go(entryList.size() > 1 && webHistory.getCurrentIndex() < entryList.size() - 1 ? 1 : 0) );
        }
    }
}
