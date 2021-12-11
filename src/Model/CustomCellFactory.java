package Model;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class CustomCellFactory extends ListCell<String> {
    Hyperlink link = new Hyperlink();
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null & !empty) {
            setText(item);
            if (item.startsWith("http")) {
                setText("");
                link.setOnAction(e -> {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(new URI(item));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (URISyntaxException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                link.setText(item);
                setGraphic(link);
                System.out.println("set hyperlink");
            }
        }
    }
}
