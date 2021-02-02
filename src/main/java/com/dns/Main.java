package com.dns;

import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    public static boolean ThemeDark = true;
    private static Parent root = null;
    private static Scene scene = null;
    private static Stage stage = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        root = FXMLLoader.load(getClass().getResource("/dnsGUI.fxml"));
        stage = primaryStage;
        stage.setTitle("DNS Resolver");
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/Icon.png")));
        scene = new Scene(root, 678, 555);
        if (ThemeDark) {
            scene.getStylesheets().add("/style.css");
        }
        stage.setScene(scene);
        stage.show();
    }

    public static void changeTheme(){
        if (ThemeDark){
            ThemeDark = false;
        } else {
            ThemeDark = true;
        }
        
        if (ThemeDark) {
            scene.getStylesheets().add("/style.css");
        } else {
            scene.getStylesheets().remove("/style.css");
        }
        stage.setScene(scene);
        stage.show();
    }
}
