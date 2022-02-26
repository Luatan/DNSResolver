package ch.luatan.DNSResolver.Model.Utils;

import ch.luatan.DNSResolver.Controller.DNSController;
import ch.luatan.DNSResolver.Controller.GUIController;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RecordCellFactory extends ListCell<String> {
    private final Hyperlink link = new Hyperlink();
    private SpecialType recordType = SpecialType.RECORD;

    @Override
    protected void updateItem(String item, boolean empty) {
        TextField tf = new TextField();
        super.updateItem(item, empty);
        getStylesheets().add("/styles/style_common.css");
        setPadding(new Insets(1, 0, 1, 15));
        if (item != null & !empty) {
            //init Textfield
            tf.setText(item);
            tf.setEditable(false);

            //set Item
            setText(item);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(tf);

            //check for special types
            recordType = detectType(item);
            setType();

            //color status active to green
            if (tf.getText().matches("Status:.*")) {
                if (tf.getText().contains("delete") || tf.getText().contains("inactive")) {
                    tf.getStyleClass().add("red");
                } else if (tf.getText().contains("active")) {
                    tf.getStyleClass().add("green");
                } else if (tf.getText().contains("transfer")) {
                    tf.getStyleClass().add("orange");
                } else {
                    tf.getStylesheets().remove("green");
                    tf.getStylesheets().remove("red");
                    tf.getStylesheets().remove("orange");
                }
            }

            if (Arrays.stream(DNSController.RECORD_TYPES).map(record -> record + ":").collect(Collectors.toList()).contains(item.trim())) {
                setGraphic(null);
                setContentDisplay(ContentDisplay.TEXT_ONLY);
                setText(item);
                setPadding(new Insets(5, 0, 10, 5));
            }

        } else {
            setText("");
            setContentDisplay(ContentDisplay.TEXT_ONLY);
            setGraphic(null);
        }
    }

    private void setLink(String text, String url) {
        link.setOnAction(e -> {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });
        link.setText(text);
    }

    private void setType() {
        switch (recordType) {
            case HYPERLINK:
                setLink(getText(), getText());

                // Item
                setText("");
                setGraphic(link);
                break;
            case SPF:
                setLink(getText(), "https://www.spf-record.com/spf-lookup/" + GUIController.getDomainProperty().getValue());
                // Item
                setText("");
                setGraphic(link);
                break;
        }
    }

    private SpecialType detectType(String input) {
        //check for SPF Records
        Pattern spfPattern = Pattern.compile("^v=spf.*", Pattern.CASE_INSENSITIVE);
        if (spfPattern.matcher(input).find()) {
            return SpecialType.SPF;
        }

        //check if it is a Link with the http(s) protocoll
        if (input.startsWith("http")) {
            return SpecialType.HYPERLINK;
        }
        return SpecialType.RECORD;
    }

}
