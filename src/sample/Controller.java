package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.naming.NamingException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public Button cpyRecords;
    @FXML
    TextField txtDomain;
    //TextField txtFieldRecords;

    @FXML
    Button btnStart;

    @FXML
    TextArea txtFieldRecords;

    @FXML
    ComboBox typeBox;
    String currentType;

    ObservableList<String> types = FXCollections.observableArrayList("Any", "A", "AAAA", "MX", "TXT", "NS", "SOA", "SRV");

    @FXML
    private void handleButtonAction(ActionEvent event) throws NamingException, UnknownHostException {
        if (!txtDomain.getText().isEmpty()) {
            txtFieldRecords.clear();
            Requests mySearch = new Requests(txtDomain.getText(), (String)typeBox.getValue());
            System.out.println(mySearch.getHostname());
            System.out.println(mySearch.getIP());
            System.out.println();

            for (int i = 0; i<mySearch.getRecords().length; i++) {
                txtFieldRecords.insertText(i, mySearch.getRecords()[i] + "\n");
            }
        }
    }

    private void handleTypeBox(ActionEvent event) {
        currentType = (String)typeBox.getValue();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeBox.setItems(types);
    }

    public ObservableList<String> getTypes() {
        return types;
    }

    private void setTypes(ObservableList<String> types) {
        this.types = types;
    }

}
