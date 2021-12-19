package Model;

import Model.DNS.DnsAdapter;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.function.Predicate;

public class RecordListCellFactory extends ListCell<String> {
    private final Hyperlink link = new Hyperlink();
    private TextField tf;

    @Override
    protected void updateItem(String item, boolean empty) {
        tf = new TextField();
        super.updateItem(item, empty);
        getStylesheets().add("/styles/style_common.css");
        setPadding(new Insets(1, 0, 1, 15));
        if (item != null & !empty) {
            //init Textfield
            tf.setText(item);
            tf.setEditable(false);


            //color status active to green
            if (tf.getText().matches("Status:.*")) {
                if (tf.getText().contains("delete") || tf.getText().contains("inactive")){
                    tf.getStyleClass().add("red");
                } else if (tf.getText().contains("active")){
                    tf.getStyleClass().add("green");
                } else if (tf.getText().contains("transfer")){
                    tf.getStyleClass().add("orange");
                } else {
                    tf.getStylesheets().remove("green");
                    tf.getStylesheets().remove("red");
                    tf.getStylesheets().remove("orange");
                }
            }

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
                setPadding(new Insets(0,0,0,15));
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
