package ch.simon;

import ch.simon.Controller.GUIController;
import ch.simon.Utils.Config;

public class Main {
    public static GUIController gui;

    public static void main(String[] args) {
        // init config files
        Config.createHistoryConfig();
        Config.createSettingsConfig();
        Config.createWhoisConfig();

        //launch GUI
        gui = new GUIController();
        gui.work();
    }

}
