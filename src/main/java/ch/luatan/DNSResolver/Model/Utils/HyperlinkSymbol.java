package ch.luatan.DNSResolver.Model.Utils;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HyperlinkSymbol extends GridPane {
    private final ImageView defaultLinkImg = new ImageView("/icons/link_external.png");
    private final Hyperlink link = new Hyperlink();
    private final TextField tf = new TextField();

    public HyperlinkSymbol(String url) {
        this.add(tf, 0, 0);
        this.add(link, 1, 0);
        tf.setEditable(false);
        GridPane.setValignment(link, VPos.CENTER);
        GridPane.setHalignment(link, HPos.CENTER);

        link.setOnAction(e -> {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });
        link.setGraphic(defaultLinkImg);
    }

    public HyperlinkSymbol(String url, String text) {
        this(url);
        tf.setText(text);
        tf.setPrefWidth(new Text(tf.textProperty().getValue()).getBoundsInLocal().getWidth() + tf.getText().length());
    }

    public HyperlinkSymbol(String url, String text, ImageView img) {
        this(url, text);
        this.link.setGraphic(img);
    }
}
