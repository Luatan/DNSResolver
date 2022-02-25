package ch.luatan.DNSResolver;

import ch.luatan.DNSResolver.Controller.GUIController;
import ch.luatan.DNSResolver.Model.Utils.Config;

public class Main {
    public static GUIController gui;

    public static void main(String[] args) {
        // init config files
        Config.createHistoryConfig();
        Config.createSettingsConfig();

        //launch ch.luatan.DNSResolver.GUI
        gui = new GUIController();
        gui.work();
    }
}
