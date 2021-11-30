package Controller;

import Utils.Config;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GUIController extends Application {

    private static boolean darkMode;
    private static boolean showAllRecords;
    private static Scene scene;
    private static Stage stage;
    private final SettingsController SETTINGS = new SettingsController();

    public void work() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Settings init
        loadSettings();

        // Set Config Variables
        Config.CACHING = SETTINGS.config.isCache();
        Config.CACHE_TIME_TO_LIVE = SETTINGS.config.getCacheTime();

        //FX
        Parent root = FXMLLoader.load(getClass().getResource("/dnsGUI.fxml"));
        stage = primaryStage;
        stage.setTitle("DNS Resolver");
        stage.getIcons().add(new Image(GUIController.class.getResourceAsStream("/icons/Icon.png")));
        stage.initStyle(StageStyle.UNDECORATED);
        scene = new Scene(root);

        // set Darkmode on Start if in Settings
        if (darkMode) {
            scene.getStylesheets().add("/styles/style_dark.css");
        } else {
            scene.getStylesheets().add("/styles/style_light.css");
        }
        stage.setScene(scene);
        stage.show();
    }

    private void loadSettings() {
        darkMode = SETTINGS.config.isDarkmode();
        showAllRecords = SETTINGS.config.isShowEmptyRecords();
    }

    public void changeTheme() {
        darkMode = !darkMode;
        if (darkMode) {
            scene.getStylesheets().add("/styles/style_dark.css");
            scene.getStylesheets().remove("/styles/style_light.css");
        } else {
            scene.getStylesheets().add("/styles/style_light.css");
            scene.getStylesheets().remove("/styles/style_dark.css");

        }

        //Change Config
        SETTINGS.config.setDarkmode(darkMode);

        // make changes visible
        stage.setScene(scene);
        stage.show();
    }

    public void setShowAllRecords() {
//        SETTINGS.edit("ShowEmptyRecords", !showAllRecords);
        SETTINGS.config.setShowEmptyRecords(!showAllRecords);
    }

    public boolean isShowAllRecords() {
        return showAllRecords;
    }

    public void exit() {
        stage.close();
        SETTINGS.write();
        System.exit(0);
    }

    public void minimize() {
        stage.setIconified(true);
    }

    public void moveWindow(double x, double y) {
        stage.setX(x);
        stage.setY(y);
    }


}
