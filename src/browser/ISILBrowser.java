package browser;

import browser.form.BrowserFormType;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;

public class ISILBrowser extends GridPane {
    static final String home = "https://www.google.com/",
            searchEngine = "https://www.google.com/search?q=";

    ControllerBar controllerBar;
    BrowserTabs browserTabs;
    TabPane tabPane;
    private boolean fromTabsBar;

    public ISILBrowser()
    {
        fromTabsBar = false;
        // Add css style
        getStylesheets().add(getClass().getResource("browserStyle.css").toExternalForm());
        // Let the web view row take all vertical remaining space
        // add 3 rows to reach the web view row
        getRowConstraints().addAll(new RowConstraints(), new RowConstraints(), new RowConstraints(), getFillRow());
        // Column 0 tale all space
        getColumnConstraints().add(getFillColumn());
        // Tab Pane of form/web view
        tabPane = new TabPane();
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (fromTabsBar)
                fromTabsBar = false;
            else
                if (browserTabs != null)
                    browserTabs.setSelectedTab(tabPane.getSelectionModel().getSelectedIndex());
        });
        tabPane.getTabs().addListener((ListChangeListener<? super Tab>) change -> {
            final StackPane header = (StackPane) tabPane.lookup(".tab-header-area");
            if(header != null) {
                header.setPrefHeight(-1);
            }
        });
        add(tabPane, 0, 3);
        // Tabs bar
        browserTabs = new BrowserTabs(home, arg -> onOpenTab(arg), arg -> onSelectTab(arg));
        browserTabs.setOnCloseTab(arg -> onCloseTab(arg) );
        add(browserTabs, 0, 0);
        // Controller bar
        controllerBar = new ControllerBar(home);
        controllerBar.setOnReload(arg -> browserTabs.reload());
        controllerBar.setOnGoBack(arg -> browserTabs.goBack());
        controllerBar.setOnGoForward(arg -> browserTabs.goForward());
        controllerBar.setOnGoHome(arg -> browserTabs.loadURL(home));
        controllerBar.setOnLoadURL(arg -> browserTabs.loadURL(arg));
        controllerBar.setOnShowFavorites(arg -> browserTabs.getSelectedTab().openForm(BrowserFormType.Favorite));
        controllerBar.setOnShowHistory(arg -> browserTabs.getSelectedTab().openForm(BrowserFormType.History));
        controllerBar.setOnShowDownloads(arg -> browserTabs.getSelectedTab().openForm(BrowserFormType.Download));
        add(controllerBar, 0, 1);
    }

    private void onOpenTab(BrowserTab browsertab)
    {
        browsertab.setOnUrlChanged(arg -> controllerBar.setURL(arg));
        tabPane.getTabs().add(new Tab("", browsertab.getContext()));
    }

    private void onSelectTab(int tabIndex)
    {
        if (controllerBar != null)
        {
            browserTabs.saveOldSelectedUrl(controllerBar.getURL());
            controllerBar.setURL(browserTabs.getURL());
        }
        if (tabPane.getSelectionModel().getSelectedIndex() != tabIndex)
        {
            fromTabsBar = true;
            tabPane.getSelectionModel().select(tabIndex);
        }
    }

    private void onCloseTab(int tabIndex)
    {
        tabPane.getTabs().remove(tabIndex);
    }

    public static RowConstraints getFillRow()
    {
        RowConstraints row = new RowConstraints();
        row.setVgrow(Priority.ALWAYS);
        row.setFillHeight(true);
        return row;
    }

    public static ColumnConstraints getFillColumn()
    {
        ColumnConstraints col = new ColumnConstraints();
        col.setHgrow(Priority.ALWAYS);
        col.setFillWidth(true);
        return col;
    }
}
