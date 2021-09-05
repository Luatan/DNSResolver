import Controller.GUIController;

public class Main {
    public static GUIController gui;
    public static void main(String[] args) {
        gui = new GUIController();
        gui.work(args);
    }
}
