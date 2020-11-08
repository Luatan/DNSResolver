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
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    TextField txtDomain;

    @FXML
    Button cpyRecords;

    @FXML
    TextField txtFieldIP;

    @FXML
    TextField txtFieldHost;

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
        Requests querry;

        if (!txtDomain.getText().isEmpty()) {
            txtFieldRecords.clear();
            if (typeBox.getValue().equals("Any")) {
                querry = new Requests(txtDomain.getText(), "*");

            } else {
                querry = new Requests(txtDomain.getText(), (String)typeBox.getValue());
            }
            txtFieldHost.setText(querry.getHostname());
            txtFieldIP.setText(querry.getIP());

            try {
                for (int i = 0; i<querry.getRecords().length; i++) {
                    txtFieldRecords.appendText(querry.getRecords()[i] + "\n");
                }
            } catch (NullPointerException e) {
                System.out.println("For loop not possible - Controller:65");
                txtFieldRecords.appendText("No Records found, sry... \n try to search for a different domain or type");
            }

        }
    }

    @FXML
    private void copyRecords(ActionEvent event) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        StringSelection strSel = new StringSelection(txtFieldRecords.getText());
        clipboard.setContents(strSel, null);
        System.out.println("Records copied!");
    }

    private void handleTypeBox(ActionEvent event) {
        currentType = (String)typeBox.getValue();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeBox.setItems(types);
    }

}
