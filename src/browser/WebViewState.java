package browser;


import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

enum State{
    Loading, CanNotReach
}

class WebViewState extends GridPane {
    private ImageView iv_loading;
    private Label lb_message, lb_canNotReached;
    private boolean showing;
    private State state;

    WebViewState()
    {
        showing = false;

        getStyleClass().add("webState");
        setMaxHeight(Double.MAX_VALUE);
        setMaxWidth(Double.MAX_VALUE);
        getColumnConstraints().add(ISILBrowser.getFillColumn());
        getRowConstraints().addAll(ISILBrowser.getFillRow(), ISILBrowser.getFillRow(), ISILBrowser.getFillRow());

        iv_loading = new ImageView("resources/loading.png");
        iv_loading.setFitHeight(150);
        iv_loading.setFitWidth(150);
        GridPane.setHalignment(iv_loading, HPos.CENTER);
        GridPane.setValignment(iv_loading, VPos.BOTTOM);
        add(iv_loading, 0, 0);

        lb_canNotReached = new Label(";( Ce site est inaccessible");
        lb_canNotReached.setMaxHeight(200);
        lb_canNotReached.getStyleClass().add("webStateTitle");
        add(lb_canNotReached, 0, 0);
        GridPane.setHalignment(lb_canNotReached, HPos.CENTER);
        GridPane.setValignment(lb_canNotReached, VPos.BOTTOM);

        lb_message = new Label();
        lb_message.getStyleClass().add("webStateMessage");
        add(lb_message, 0, 1);
        GridPane.setHalignment(lb_message, HPos.CENTER);
        GridPane.setValignment(lb_message, VPos.TOP);
    }

    void setState(State state)
    {
        showing = true;
        this.state = state;
        if (state.equals(State.Loading))
        {
            lb_canNotReached.setVisible(false);
            lb_message.setText("Chargement...");
            iv_loading.setRotate(0);
            iv_loading.setVisible(true);
            new Thread(() ->
            {
                while (this.state.equals(State.Loading) && showing)
                {
                    Platform.runLater(() -> iv_loading.setRotate(iv_loading.getRotate() + 15));
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        else
        {
            iv_loading.setVisible(false);
            lb_message.setText("La connexion ou le site n'existe pas.\n" +
                    "Vérifier votre connexion et recharger la page");
            lb_canNotReached.setVisible(true);
        }
    }

    void close()
    {
        showing = false;
    }

    public boolean isShowing() {
        return showing;
    }
}
