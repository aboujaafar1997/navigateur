package browser.form;

import browser.ISILBrowser;
import browser.MyEventHandler;
import database.BrowserFavorite;
import database.BrowserFavoritesDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

public class FavoritesForm extends BrowserForm{
    private ObservableList<GridPane> listFavorites;
    private ArrayList<Integer> listIDs;
    private MyEventHandler<BrowserFavorite> onLinkClicked;
    private TextField tf_name, tf_url;
    private ListView lv_data;

    public FavoritesForm(String url, String name)
    {
        super("Favoris");
        gp_context.setHgap(10);
        gp_context.setVgap(10);
        gp_context.getColumnConstraints().add(ISILBrowser.getFillColumn());
        // Remove buttons
        Button btn_deleteSelected = createButton("Supprimer sélectionnée", e -> deleteSelected());
        gp_context.add(btn_deleteSelected, 0, 0, 2, 1);
        GridPane.setHalignment(btn_deleteSelected, HPos.RIGHT);
        HBox hb_btn_deleteAll = new HBox(createButton("Nettoyer les favoris", e -> {
            listFavorites.clear();
            listIDs.clear();
            BrowserFavoritesDB.deleteAllFavorites();
        }));
        hb_btn_deleteAll.setPadding(new Insets(0, 10, 0, 0));
        gp_context.add(hb_btn_deleteAll, 2, 0);
        gp_context.add(createLine(), 0, 1, 3, 1);
        // Add favorite
        GridPane gp_tf_name = new GridPane();
        gp_tf_name.getRowConstraints().add(ISILBrowser.getFillRow());
        gp_tf_name.getColumnConstraints().add(ISILBrowser.getFillColumn());
        gp_tf_name.setPadding(new Insets(0, 0, 0, 10));
        tf_name = createTextField("Nom");
        gp_tf_name.add(tf_name, 0, 0);
        gp_context.add(gp_tf_name, 0, 2);
        tf_url = createTextField("URL");
        gp_context.add(tf_url, 1, 2);
        HBox hb_btn_add = new HBox(createButton("Ajouter", e -> addFavorite()));
        hb_btn_add.setPadding(new Insets(0, 10, 0, 0));
        gp_context.add(hb_btn_add, 2, 2);
        gp_context.add(createLine(), 0, 3, 3, 1);
        setProperties(url, name);
        // Create favorites list
        listFavorites = FXCollections.observableArrayList();
        lv_data = new ListView<>(listFavorites);
        lv_data.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        gp_context.add(lv_data, 0, 4, 3, 1);
    }

    protected void load()
    {
        listIDs = new ArrayList<>();
        listFavorites.clear();
        ArrayList<BrowserFavorite> favorites = BrowserFavoritesDB.getFavorites();
        for(BrowserFavorite bf : favorites)
            createFavorite(bf);
    }

    private void createFavorite(BrowserFavorite bf)
    {
        GridPane gp_favorite = new GridPane();
        gp_favorite.setPadding(new Insets(5, 10, 5, 10));
        gp_favorite.getColumnConstraints().addAll(ISILBrowser.getFillColumn());
        Label lb_url = new Label(bf.getName());
        lb_url.getStyleClass().add("link");
        lb_url.addEventHandler(MouseEvent.MOUSE_CLICKED,  event ->
        {
            close();
            onLinkClicked.apply(bf);
        });
        gp_favorite.add(lb_url, 0, 0);
        listFavorites.add(gp_favorite);
        listIDs.add(bf.getId());
    }

    private void deleteSelected()
    {
        ArrayList<Integer> IDs = new ArrayList<>();
        ObservableList<Integer> selectedIndices = lv_data.getSelectionModel().getSelectedIndices();
        for (Integer selectedIndex : selectedIndices)
            IDs.add(listIDs.get(selectedIndex));
        BrowserFavoritesDB.deleteFavorite(IDs);
        load();
    }
    
    private void addFavorite()
    {
        if (tf_name.getText().isEmpty() || tf_url.getText().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Nom ou URL invalide");
            (alert).show();
        }
        else
        {
            BrowserFavoritesDB.addFavorite(new BrowserFavorite(0, tf_url.getText(), tf_name.getText()));
            tf_url.setText("");
            tf_name.setText("");
            load();
        }
    }

    public void setOnLinkClicked(MyEventHandler<BrowserFavorite> eventHandler)
    {
        onLinkClicked = eventHandler;
    }

    public void setProperties(String url, String name)
    {
        tf_url.setText(url);
        tf_name.setText(name);
    }
}
