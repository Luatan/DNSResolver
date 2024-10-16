package ch.luatan.DNSResolver.Gui;

import ch.luatan.DNSResolver.Data.Resolver.DefaultResolver;
import ch.luatan.DNSResolver.Model.DNS.SpecialType;
import ch.luatan.DNSResolver.Model.DNS.Type;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RecordCellFactory extends ListCell<String> {
    private Type recordType = SpecialType.RECORD;

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

            //check and handle Specialtypes like SPF records or hyperlinks
            handleSpecialTypes(item);

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

            //add Types to the output field, which cannot be selected with the mouse
            if (Arrays.stream(DefaultResolver.RECORD_TYPES).map(record -> record + ":").collect(Collectors.toList()).contains(item.trim())) {
                setGraphic(null);
                setContentDisplay(ContentDisplay.TEXT_ONLY);
                setText(item);
                setPadding(new Insets(5, 0, 10, 5));
            }

        } else {
            // empty Item
            setText("");
            setContentDisplay(ContentDisplay.TEXT_ONLY);
            setGraphic(null);
        }
    }

    private void handleSpecialTypes(String input) {
        //check for SPF Records
        Matcher spfPattern = Pattern.compile("^v=spf.*", Pattern.CASE_INSENSITIVE).matcher(input);
        if (spfPattern.find()) {
            recordType = SpecialType.SPF;
            // Build URL
            String url = "https://www.spf-record.com/spf-lookup/" + GUIController.getDomainProperty().getValue();
            setGraphic(new HyperlinkSymbol(url, input));
        }

        //check if a URL is in a text
        Matcher linkPattern = Pattern.compile(".+(http.*)", Pattern.CASE_INSENSITIVE).matcher(input);
        if (linkPattern.find()) {
            recordType = SpecialType.HYPERLINKSYMBOL;
            setText("");
            // create Symbol with link to click and display the original text from the input
            setGraphic(new HyperlinkSymbol(linkPattern.group(1), input));
        }

        //check if the item Starts with http to make it to a clickable link
        if (input.startsWith("http")) {
            recordType = SpecialType.HYPERLINK;
            setText("");
            setGraphic(getLink(input, input));
        }
    }

    private Hyperlink getLink(String text, String url) {
        Hyperlink link = new Hyperlink();
        // make the URL clickable
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
        return link;
    }

}
