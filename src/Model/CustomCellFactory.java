package Model;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class CustomCellFactory extends ListCell<String> {
    private final Hyperlink link = new Hyperlink();
    private Label label = new Label();
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null & !empty) {
            setText(item);
            label.setText(item);
            setGraphic(label);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            if (item.startsWith("http")) {
                setText("");
                link.setOnAction(e -> {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(new URI(item));
                        } catch (IOException | URISyntaxException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                link.setText(item);
                setGraphic(link);
            }
        } else {
            setText("");
            setContentDisplay(ContentDisplay.TEXT_ONLY);
            setGraphic(null);
        }
    }
}
