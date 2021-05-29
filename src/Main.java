import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

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
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/Icon.png")));

        // TODO: 03.02.2021 remove Windows default Title....https://stackoverflow.com/questions/9861178/javafx-primarystage-remove-windows-borders/9864496#9864496
        // TODO: 29.05.2021 Cleanup Light mode 
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        scene = new Scene(root, 678, 555);


        if (ThemeDark) {
            scene.getStylesheets().add("/style.css");
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
            scene.getStylesheets().add("/style.css");
        } else {
            scene.getStylesheets().remove("/style.css");
        }
        settings.editSettingsJSON("darkmode", ThemeDark);
        stage.setScene(scene);
        stage.show();
    }

    public static void setEmptyRecordSetting() {
        settings.editSettingsJSON("ShowEmptyRecords", !emptyRecordSetting);

    }
}
