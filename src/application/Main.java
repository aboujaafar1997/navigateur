package application;
	
import browser.ISILBrowser;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	/*
	 * ***************************** ISIL Browser **************************************
	 * ************************  Ce projet créer par :      ***************************
	 * ******************* Ahmed Essoubai             *********************************
	 * ******************* Abdeslam Rehaimi         ***********************************
	 * ******************* Othman Aboujaafar      *************************************
	 * ******************* Abdelilah Boutizwa        **********************************
	 * *************** 2018 - 2019               **************************************
	 * *************** Mini-projet de Java        *************************************
	 * ********************************************************************************
	 */
	
	@Override
	public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(new ISILBrowser(), 1024, 768));
        primaryStage.getIcons().add(new Image("resources/logo.png"));
        primaryStage.setTitle("ISIL browser");
        primaryStage.setMinHeight(200);
        primaryStage.setMinWidth(558);
        primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
