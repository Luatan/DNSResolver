package ch.luatan.DNSResolver;

import ch.luatan.DNSResolver.Controller.SettingsController;
import ch.luatan.DNSResolver.Model.Utils.Config;
import ch.luatan.DNSResolver.Model.Utils.FileHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class DNSResolver extends Application {

    public static final Logger LOGGER = LoggerFactory.getLogger(DNSResolver.class);
    private static final SettingsController SETTINGS = new SettingsController();
    private static boolean darkMode;
    private static boolean showAllRecords;
    private static Scene scene;
    private static Stage stage;
    private double yOffset;
    private double xOffset;

    public static void changeTheme() {
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

    public static boolean isIgnoreDNSSEC() {
        return SETTINGS.config.isIgnoreDNSSEC();
    }

    public static void setIsIgnoreDNSSEC() {
        boolean enabled = SETTINGS.config.isIgnoreDNSSEC();
        SETTINGS.config.setIgnoreDNSSEC(!enabled);
    }

    public static void setShowAllRecords() {
        SETTINGS.config.setShowEmptyRecords(!showAllRecords);
    }

    public static boolean isShowAllRecords() {
        return showAllRecords;
    }

    public static void exit() {
        LOGGER.info("Saving settings({}) and domainhistory({})", FileHelper.DIR_HOME + Config.SETTINGS_CONF_FILE, FileHelper.DIR_HOME + Config.HISTORY_LOG_FILE);
        stage.close();
        SETTINGS.write();
        System.exit(0);
    }

    public static void minimize() {
        stage.setIconified(true);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Settings init
        loadSettings();

        //FX
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/dnsGUI.fxml")));
        stage = primaryStage;
        stage.setTitle("DNS Resolver");
        stage.getIcons().add(new Image(Objects.requireNonNull(DNSResolver.class.getResourceAsStream("/icons/Icon.png"))));
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
        LOGGER.info("Loading settings from JSON");
        if (SETTINGS.config != null) {
            darkMode = SETTINGS.config.isDarkmode();
            showAllRecords = SETTINGS.config.isShowEmptyRecords();

            // Set Config Variables
            Config.CACHING = SETTINGS.config.isCache();
            Config.WHOIS_DATA_CACHE_TTL = SETTINGS.config.getWhois_cache_ttl();
            Config.WHOIS_EXT_CACHE_TTL = SETTINGS.config.getWhois_ext_cache_ttl();
        }
    }

}
