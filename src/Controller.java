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
        Requests query;

        if (!txtDomain.getText().isEmpty()) {
            txtFieldRecords.clear();
            if (typeBox.getValue().equals("Any")) {
                query = new Requests(txtDomain.getText(), "*");

                recordPutter(query.getRecords("A"), "A");
                recordPutter(query.getRecords("AAAA"), "AAAA");
                recordPutter(query.getRecords("MX"), "MX");
                recordPutter(query.getRecords("NS"), "NS");
                recordPutter(query.getRecords("TXT"), "TXT");
                recordPutter(query.getRecords("SRV"), "SRV");
                recordPutter(query.getRecords("SOA"), "SOA");

            } else {
                query = new Requests(txtDomain.getText(), (String)typeBox.getValue());

                recordPutter(query.getRecords((String)typeBox.getValue()), (String)typeBox.getValue());
            }
            txtFieldHost.setText(query.getHostname());
            txtFieldIP.setText(query.getIP());
        }
    }

    private void recordPutter(String[] list, String type) {
        txtFieldRecords.appendText(type + ": \n");
        try {
            for (String rec: list) {
                txtFieldRecords.appendText(rec + "\n");
            }
            txtFieldRecords.appendText("\n");
        } catch (NullPointerException e) {
            txtFieldRecords.appendText("No Records found\n\n");
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
        typeBox.setValue("Any");
    }

}
