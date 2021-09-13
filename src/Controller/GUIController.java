package Controller;

import Utils.FileStructure;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class GUIController extends Application {

    private static boolean darkMode;
    private static boolean showAllRecords;
    private static Scene scene;
    private static Stage stage;
    private final SettingsController settingsController = new SettingsController();

    public void work() {
        checkWhoisSettings();
        String[] args = new String[0];
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        loadSettings();
        Parent root = FXMLLoader.load(getClass().getResource("/dnsGUI.fxml"));
        stage = primaryStage;
        stage.setTitle("DNS Resolver");
        stage.getIcons().add(new Image(GUIController.class.getResourceAsStream("/icons/Icon.png")));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        scene = new Scene(root, 678, 585);

        if (darkMode) {
            scene.getStylesheets().add("/styles/style_dark.css");
        } else {
            scene.getStylesheets().add("/styles/style_light.css");
        }
        stage.setScene(scene);
        stage.show();
    }

    private void checkWhoisSettings() {
        if (FileStructure.fileExists("config/whois-servers.json")){
            return;
        }

        // try to get Stream
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config_default/default_whois.json")) {

            // convert input stream to file
            FileUtils.copyInputStreamToFile(is, new File(FileStructure.DIR_HOME + "config/whois_servers.json"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadSettings() {
        darkMode = settingsController.getDisplayMode();
        showAllRecords = settingsController.getDisplayRecords();
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
        settingsController.edit("darkmode", darkMode);
        stage.setScene(scene);
        stage.show();
    }

    public void setShowAllRecords() {
        settingsController.edit("ShowEmptyRecords", !showAllRecords);
    }

    public boolean isShowAllRecords() {
        return showAllRecords;
    }

    public void exit() {
        stage.close();
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
