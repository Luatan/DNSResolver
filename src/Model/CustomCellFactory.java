package Model;

import Model.DNS.DnsAdapter;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.function.Predicate;

public class CustomCellFactory extends ListCell<String> {
    private final Hyperlink link = new Hyperlink();
    private final TextField tf = new TextField();

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setPadding(new Insets(1, 0, 1, 15));
        if (item != null & !empty) {
            //init Textfield
            tf.setText(item);
            tf.setEditable(false);

            tf.setOnMouseClicked(event -> {
                if (!isSelected()) {
                    updateSelected(true);
                }
            });

            //set Item
            setText(item);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(tf);

            if (Arrays.stream(DnsAdapter.RECORD_TYPES).anyMatch(Predicate.isEqual(item.split(":")[0]))) {
                setGraphic(null);
                setContentDisplay(ContentDisplay.TEXT_ONLY);
                setText(item);
                setPadding(new Insets(5, 0, 10, 5));
            }

            if (item.startsWith("http")) {
                setLink(item);

                // Item
                setText("");
                setGraphic(link);

            }
        } else {
            setText("");
            setContentDisplay(ContentDisplay.TEXT_ONLY);
            setGraphic(null);
        }
    }

    private void setLink(String text) {
        link.setOnAction(e -> {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(new URI(text));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });
        link.setText(text);
    }
}
