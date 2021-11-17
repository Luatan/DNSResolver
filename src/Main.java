import Controller.GUIController;
import Utils.Config;

public class Main {
    public static GUIController gui;

    public static void main(String[] args) {
        //Init work
        Config.createDirs();
        Config.createHistoryConfig();
        Config.createSettingsConfig();
        Config.createWhoisConfig();

        //launch GUI
        gui = new GUIController();
        gui.work();
    }

}
