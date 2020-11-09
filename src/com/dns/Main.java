package com.dns;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("dnsGUI.fxml"));
        primaryStage.setTitle("DNS Resolver");
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/Icon.png")));
        primaryStage.setScene(new Scene(root, 670, 560));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
