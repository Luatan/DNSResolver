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
    TextField txtNS1;
    @FXML
    TextField txtNS2;
    @FXML
    TextField txtNS3;
    @FXML
    TextField txtNS4;
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
    TextArea txtAreaRecords;
    @FXML
    ComboBox typeBox;

    String currentType;

    ObservableList<String> types = FXCollections.observableArrayList("Any", "A", "AAAA", "MX", "TXT", "NS", "SOA", "SRV");

    @FXML
    private void handleButtonAction(ActionEvent event) throws NamingException, UnknownHostException {
        Requests query;
        if (!txtDomain.getText().isEmpty()) {
            txtAreaRecords.clear();
            if (typeBox.getValue().equals("Any")) {
                query = new Requests(txtDomain.getText(), "*");

                recordPutter(query.getRecords("A"), "A");
                recordPutter(query.getRecords("AAAA"), "AAAA");
                recordPutter(query.getRecords("MX"), "MX");
                recordPutter(query.getRecords("TXT"), "TXT");
                recordPutter(query.getRecords("SRV"), "SRV");
                recordPutter(query.getRecords("SOA"), "SOA");

            } else {
                query = new Requests(txtDomain.getText(), (String)typeBox.getValue());
                recordPutter(query.getRecords((String)typeBox.getValue()), (String)typeBox.getValue());
            }

            nameServerDisplay(query.getRecords("NS"));
            txtFieldHost.clear();
            txtFieldIP.clear();
            txtFieldHost.setText(query.getHostname());
            txtFieldIP.setText(query.getIP());
        }
    }

    private void nameServerDisplay(String[] records){
        txtNS1.clear();
        txtNS2.clear();
        txtNS3.clear();
        txtNS4.clear();

        try {
            txtNS1.setText(records[0]);
            txtNS2.setText(records[1]);
            txtNS3.setText(records[2]);
            txtNS4.setText(records[3]);
        } catch (ArrayIndexOutOfBoundsException e) {
            //Skip
        } catch (NullPointerException e) {
            //ignore
        }
    }

    private void recordPutter(String[] list, String type) {
        txtAreaRecords.appendText(type + ": \n");
        try {
            for (String rec: list) {
                txtAreaRecords.appendText(rec + "\n");
            }
            txtAreaRecords.appendText("\n");
        } catch (NullPointerException e) {
            txtAreaRecords.appendText("No Records found\n\n");
        }
    }

    @FXML
    private void copyRecords(ActionEvent event) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        StringSelection strSel = new StringSelection(txtAreaRecords.getText());
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
