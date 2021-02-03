package com.dns;

import com.sun.xml.internal.bind.v2.TODO;
import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.function.ToDoubleBiFunction;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    private static boolean ThemeDark = true;
    private static boolean emptyRecordSetting = false;
    private static Parent root = null;
    private static Scene scene = null;
    private static Stage stage = null;
    private static SettingsHandler settings = new SettingsHandler();

    @Override
    public void start(Stage primaryStage) throws Exception {
        loadSettings();
        root = FXMLLoader.load(getClass().getResource("/dnsGUI.fxml"));
        stage = primaryStage;
        stage.setTitle("DNS Resolver");
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/Icon.png")));
        scene = new Scene(root, 678, 555);
        if (ThemeDark) {
            scene.getStylesheets().add("/style.css");

            // TODO: 03.02.2021 remove Windows default Title for darkmode atleast....https://stackoverflow.com/questions/9861178/javafx-primarystage-remove-windows-borders/9864496#9864496
            //stage.initStyle(StageStyle.UNDECORATED);
        }
        stage.setScene(scene);
        stage.show();
    }

    private void loadSettings() {
        ThemeDark = settings.getDarkmode();
        emptyRecordSetting = settings.getEmptyRecords();
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
        settings.changeValueJSON("darkmode", ThemeDark);
        stage.setScene(scene);
        stage.show();
    }
}
