import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static boolean ThemeDark = true;
    public static boolean emptyRecordSetting = false;
    private static Scene scene = null;
    public static Stage stage = null;
    private static final Settings settings = new Settings();

    @Override
    public void start(Stage primaryStage) throws Exception {
        loadSettings();
        Parent root = FXMLLoader.load(getClass().getResource("/dnsGUI.fxml"));
        stage = primaryStage;
        stage.setTitle("DNS Resolver");
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/icons/Icon.png")));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        scene = new Scene(root, 678, 585);


        if (ThemeDark) {
            scene.getStylesheets().add("/styles/style_dark.css");
        } else {
            scene.getStylesheets().add("/styles/style_light.css");
        }
        stage.setScene(scene);
        stage.show();
    }

    private void loadSettings() {
        JSONHandler JSON = new JSONHandler("settings.json");
        ThemeDark = JSON.getBoolValue("darkmode");
        emptyRecordSetting = JSON.getBoolValue("ShowEmptyRecords");
    }

    public static void changeTheme() {
        ThemeDark = !ThemeDark;

        if (ThemeDark) {
            scene.getStylesheets().remove("/styles/style_light.css");
            scene.getStylesheets().add("/styles/style_dark.css");
        } else {
            scene.getStylesheets().remove("/styles/style_dark.css");
            scene.getStylesheets().add("/styles/style_light.css");
        }
        settings.editSettingsJSON("darkmode", ThemeDark);
        stage.setScene(scene);
        stage.show();
    }

    public static void setEmptyRecordSetting() {
        settings.editSettingsJSON("ShowEmptyRecords", !emptyRecordSetting);

    }
}
