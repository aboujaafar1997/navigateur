package browser.form;

import browser.ControllerBar;
import browser.ISILBrowser;
import browser.MyEventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

public abstract class BrowserForm extends GridPane {
    private MyEventHandler<BrowserForm> onClosing;
    protected GridPane gp_context;
    private boolean open;

    BrowserForm(String title)
    {
        open = false;
        setWidth(Double.MAX_VALUE);
        setHeight(Double.MAX_VALUE);
        getStyleClass().add("form");
        getColumnConstraints().add(ISILBrowser.getFillColumn());
        getRowConstraints().addAll(new RowConstraints(), ISILBrowser.getFillRow());
        //
        // Header
        //
        // Title
        Label lb_title = new Label(title);
        lb_title.setAlignment(Pos.CENTER);
        lb_title.getStyleClass().add("formTitle");
        HBox hb_title = new HBox(lb_title);
        hb_title.setMaxWidth(Double.MAX_VALUE);
        hb_title.setAlignment(Pos.CENTER_LEFT);
        hb_title.setPadding(new Insets(5, 10, 5, 20));
        hb_title.getStyleClass().add("formHeader");
        add(hb_title, 0, 0);
        // Close button
        ImageView iv_close = new ImageView("resources/close_form.png");
        iv_close.setFitWidth(30);
        iv_close.setFitHeight(30);
        HBox container = ControllerBar.createContainerButton(iv_close, event -> close());
        //container.setAlignment(Pos.CENTER);
        GridPane gp_close = new GridPane();
        gp_close.setAlignment(Pos.CENTER);
        gp_close.setPrefSize(50, 50);
        gp_close.getStyleClass().add("formHeader");
        gp_close.add(container, 0, 0);
        add(gp_close, 1, 0);
        //
        // Context
        //
        gp_context = new GridPane();
        gp_context.setMaxWidth(1000);
        gp_context.setMaxHeight(Double.MAX_VALUE);
        //gp_context.setAlignment(Pos.TOP_CENTER);
        gp_context.getStyleClass().add("formContext");
        gp_context.getColumnConstraints().add(ISILBrowser.getFillColumn());
        gp_context.getRowConstraints().addAll(new RowConstraints(), new RowConstraints(), new RowConstraints(), new RowConstraints(), ISILBrowser.getFillRow());
        add(gp_context, 0, 1, 2, 1);
        GridPane.setHalignment(gp_context, HPos.CENTER);
    }

    protected void close()
    {
        onClosing.apply(this);
    }

    public void setOnClosing(MyEventHandler<BrowserForm> eventHandler)
    {
        onClosing = eventHandler;
    }

    static Button createButton(String text, MyEventHandler eventHandler)
    {
        Button button = new Button(text);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> eventHandler.apply(button));
        button.getStyleClass().add("formButton");
        return button;
    }

    static HBox createLine()
    {
        HBox line = new HBox();
        line.setMaxHeight(Double.MAX_VALUE);
        line.setPrefHeight(1);
        line.getStyleClass().add("formLine");
        return line;
    }

    static TextField createTextField(String placeHolder)
    {
        TextField textField = new TextField();
        textField.setPromptText(placeHolder);
        textField.getStyleClass().add("browserTextField");
        textField.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        textField.setAlignment(Pos.CENTER_LEFT);
        return textField;
    }

    protected abstract void load();

    public void Open()
    {
        open = true;
        load();
    }

    public void Close()
    {
        open = false;
    }

    public boolean isOpen() {
        return open;
    }
}
