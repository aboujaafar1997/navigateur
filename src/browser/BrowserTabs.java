package browser;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.util.ArrayList;

public class BrowserTabs extends GridPane {
    private ArrayList<BrowserTab> tabs;
    private double defaultTabWidth;
    private BrowserTab selectedTab, oldSelectedTab;
    private ImageView iv_add;
    private HBox hb_tabs;

    private MyEventHandler<BrowserTab> onOpenTab;
    private MyEventHandler<Integer> onSelectTab;
    private MyEventHandler<Integer> onCloseTab;

    BrowserTabs(String url, double height, double defaultTabWidth, MyEventHandler<BrowserTab> onOpenTab, MyEventHandler<Integer> onSelectTab)
    {
        super();
        setOnOpenTab(onOpenTab);
        setOnSelectTab(onSelectTab);
        getStyleClass().add("browserTabs");
        setPadding(new Insets(5, 2, 0, 2));
        this.defaultTabWidth = defaultTabWidth;
        tabs = new ArrayList<>();
        setWidth(Double.MAX_VALUE);
        setHeight(height);
        // Add button
        iv_add = new ImageView("resources/add.png");
        iv_add.setFitHeight(height);
        iv_add.setFitWidth(height);
        add(ControllerBar.createContainerButton(iv_add, event -> newTab(url)), 1, 0);
        // Tabs box
        hb_tabs = new HBox();
        add(hb_tabs, 0, 0);
        newTab(url);
    }

    BrowserTabs(String url, MyEventHandler<BrowserTab> onOpenTab, MyEventHandler<Integer> onSelectTab)
    {
        this(url, 34, 250, onOpenTab, onSelectTab);
    }

    private void newTab(String url)
    {
        BrowserTab tab = new BrowserTab(url, iv_add.getFitHeight());
        tab.setOnClosing(arg -> closeTab(arg));
        tab.setOnSelected(arg -> setSelectedTabs(arg) );
        tabs.add(tab);
        hb_tabs.getChildren().add(tab);
        setTabsWidth();
        onOpenTab.apply(tab);
        setSelectedTabs(tab);
    }

    private void setTabsWidth()
    {
        double availableWidth = getWidth() - iv_add.getFitWidth();
        double tabWidth = (defaultTabWidth * tabs.size() > availableWidth) ? availableWidth / tabs.size() : defaultTabWidth;
        for(BrowserTab tab : tabs)
            tab.setPrefWidth(tabWidth);
    }

    private void closeTab(BrowserTab tab)
    {
        if (tabs.size() == 1)
            Platform.exit();
        else
        {
            hb_tabs.getChildren().remove(tab);
            onCloseTab.apply(tabs.indexOf(tab));
            tabs.remove(tab);
            setTabsWidth();
            if (selectedTab == tab)
            {
                setSelectedTabs(tabs.get(tabs.size() - 1));
            }
        }
    }

    void setSelectedTab(int index)
    {
        if (index > tabs.size() - 1)
            return;
        BrowserTab tab = tabs.get(index);
        tab.setSelected(true);
        setSelectedTabs(tab);
    }

    private void setSelectedTabs(BrowserTab newSelectedTab)
    {
        oldSelectedTab = selectedTab;
        selectedTab = newSelectedTab;
        for(BrowserTab tab : tabs)
            tab.setSelected(tab == selectedTab);
        onSelectTab.apply(tabs.indexOf(selectedTab));
    }

    void saveOldSelectedUrl(String url)
    {
        if (oldSelectedTab != null)
            oldSelectedTab.saveURL(url);
    }

    void setOnOpenTab(MyEventHandler<BrowserTab> onOpenTab) {
        this.onOpenTab = onOpenTab;
    }

    void setOnCloseTab(MyEventHandler<Integer> onCloseTab) {
        this.onCloseTab = onCloseTab;
    }

    private void setOnSelectTab(MyEventHandler<Integer> onSelectTab) {
        this.onSelectTab = onSelectTab;
    }

    BrowserTab getSelectedTab() {
        return selectedTab;
    }

    String getURL()
    {
        return selectedTab.getUrl();
    }

    void loadURL(String url)
    {
        selectedTab.loadURL(url);
    }

    void reload()
    {
        selectedTab.reload();
    }

    void goBack()
    {
        selectedTab.goBack();
    }

    void goForward()
    {
        selectedTab.goForward();
    }
}
