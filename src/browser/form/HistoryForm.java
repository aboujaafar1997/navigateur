package browser.form;

import browser.ISILBrowser;
import browser.MyEventHandler;
import database.BrowserHistory;
import database.BrowserHistoryDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

public class HistoryForm extends BrowserForm{
    private ObservableList<GridPane> listHistory;
    private ArrayList<Integer> listIDs;
    private MyEventHandler<BrowserHistory> onLinkClicked;
    private ListView lv_data;

    public HistoryForm()
    {
        super("Histoire");
        gp_context.setHgap(10);
        gp_context.setVgap(10);
        // Remove buttons
        Button btn_deleteSelected = createButton("Supprimer sélectionnée", e -> deleteSelected());
        gp_context.add(btn_deleteSelected, 0, 0);
        GridPane.setHalignment(btn_deleteSelected, HPos.RIGHT);
        HBox hb_btn_deleteAll = new HBox(createButton("Nettoyer histoire", e -> {
            listHistory.clear();
            listIDs.clear();
            BrowserHistoryDB.deleteAllHistory();
        }));
        hb_btn_deleteAll.setPadding(new Insets(0, 10, 0, 0));
        gp_context.add(hb_btn_deleteAll, 1, 0);
        gp_context.add(createLine(), 0, 1, 2, 1);
        // Create history list
        listHistory = FXCollections.observableArrayList();
        lv_data = new ListView<>(listHistory);
        lv_data.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        gp_context.add(lv_data, 0, 4, 2, 1);
    }

    protected void load()
    {
        listIDs = new ArrayList<>();
        listHistory.clear();
        ArrayList<BrowserHistory> histories = BrowserHistoryDB.getHistories();
        for(BrowserHistory bh : histories)
            addHistory(bh);
    }

    private void addHistory(BrowserHistory bh)
    {
        GridPane gp_history = new GridPane();
        gp_history.setPadding(new Insets(5, 10, 5, 10));
        gp_history.setHgap(25);
        gp_history.getColumnConstraints().addAll(new ColumnConstraints(), ISILBrowser.getFillColumn());
        // Time
        Label lb_time = new Label(bh.getTime());
        lb_time.setMinWidth(100);
        gp_history.add(lb_time, 0, 0);
        // URL
        Label lb_url = new Label(bh.getUrl());
        lb_url.getStyleClass().add("link");
        lb_url.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
        {
            close();
            onLinkClicked.apply(bh);
        });
        gp_history.add(lb_url, 1, 0);
        listHistory.add(gp_history);
        listIDs.add(bh.getId());
    }

    private void deleteSelected()
    {
        ArrayList<Integer> IDs = new ArrayList<>();
        ObservableList<Integer> selectedIndices = lv_data.getSelectionModel().getSelectedIndices();
        for (Integer selectedIndex : selectedIndices)
            IDs.add(listIDs.get(selectedIndex));
        BrowserHistoryDB.deleteHistory(IDs);
        load();
    }

    public void setOnLinkClicked(MyEventHandler<BrowserHistory> eventHandler)
    {
        onLinkClicked = eventHandler;
    }
}
