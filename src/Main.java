import Controller.GUIController;
import Model.Utils.Config;

public class Main {
    public static GUIController gui;

    public static void main(String[] args) {
        // init config files
        Config.createHistoryConfig();
        Config.createSettingsConfig();

        //launch GUI
        gui = new GUIController();
        gui.work();
    }
}
