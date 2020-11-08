package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    TextField domain;

    @FXML
    ComboBox typeBox;

    ObservableList<String> types = FXCollections.observableArrayList("Any", "A", "AAAA", "MX", "TXT", "NS", "SOA", "SRV");



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeBox.setItems(types);
    }

    public ObservableList<String> getTypes() {
        return types;
    }

    public void setTypes(ObservableList<String> types) {
        this.types = types;
    }
}
