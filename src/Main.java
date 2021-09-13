import Controller.GUIController;
import Utils.FileStructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static GUIController gui;
    public static void main(String[] args) {
        //Create essential folders
        String[] dirs = new String[] {"logs", "config"};
        for (String dir:dirs) {
            if (!FileStructure.directoryExists(dir)){
                try {
                    Files.createDirectories(Paths.get(FileStructure.DIR_HOME + dir));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        gui = new GUIController();
        gui.work();
    }
}
