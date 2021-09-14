import Controller.GUIController;
import Utils.FileStructure;

public class Main {
    public static GUIController gui;
    public static void main(String[] args) {
        //Create essential folders in RunDir (on runtime)
        String[] dirs = new String[] {"logs", "config"};
        for (String dir:dirs) {
            FileStructure.createDir(dir);
        }

        gui = new GUIController();
        gui.work();
    }

}
