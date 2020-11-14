package com.dns;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.lang.annotation.Native;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("dnsGUI.fxml"));
        primaryStage.setTitle("DNS Resolver");
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/Icon.png")));
        primaryStage.setScene(new Scene(root, 670, 555));
        primaryStage.show();
    }
}
