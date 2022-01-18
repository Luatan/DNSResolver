package Controller;

import Model.Utils.Config;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class GUIController extends Application {

    private static boolean darkMode;
    private static boolean showAllRecords;
    private static Scene scene;
    private static Stage stage;
    private final SettingsController SETTINGS = new SettingsController();
    private double yOffset;
    private double xOffset;

    public void work() {
        try {
            launch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Settings init
        loadSettings();

        //FX
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/dnsGUI.fxml")));
        stage = primaryStage;
        stage.setTitle("DNS Resolver");
        stage.getIcons().add(new Image(Objects.requireNonNull(GUIController.class.getResourceAsStream("/icons/Icon.png"))));
        stage.initStyle(StageStyle.UNDECORATED);
        scene = new Scene(root);

        // set Darkmode on Start if in Settings
        scene.getStylesheets().add("/styles/style_common.css");
        if (darkMode) {
            scene.getStylesheets().add("/styles/style_dark.css");
        } else {
            scene.getStylesheets().add("/styles/style_light.css");
        }
        stage.setScene(scene);


        root.getChildrenUnmodifiable().get(0).setOnMousePressed(event -> {
            xOffset = event.getX();
            yOffset = event.getY();
        });


        root.getChildrenUnmodifiable().get(0).setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        stage.show();
    }

    private void loadSettings() {
        if (SETTINGS.config != null) {
            darkMode = SETTINGS.config.isDarkmode();
            showAllRecords = SETTINGS.config.isShowEmptyRecords();

            // Set Config Variables
            Config.CACHING = SETTINGS.config.isCache();
            Config.WHOIS_DATA_CACHE_TTL = SETTINGS.config.getWhois_cache_seconds();
            Config.WHOIS_EXT_CACHE_TTL = SETTINGS.config.getWhois_ext_cache_days();
        }
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

}
