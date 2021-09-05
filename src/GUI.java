import Utils.Domain;
import Model.IP_Info;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.naming.NamingException;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class GUI implements Initializable {
    // used for calaculating the offset to move the Window
    private double offsetX;
    private double offsetY;

    @FXML
    ToolBar toolbar;
    @FXML
    TextField txtNS1, txtNS2, txtNS3, txtNS4;
    @FXML
    TextField txtDomain, txtFieldIP, txtFieldHost;
    @FXML
    Button cpyRecords, btnStart, scrollButton, btnWeb;
    @FXML
    TextArea txtAreaRecords;
    @FXML
    ComboBox typeBox;
    @FXML
    Hyperlink registryLink;
    @FXML
    WebView web;
    @FXML
    Label hyperLbl;
    @FXML
    CheckBox chckBox;
    @FXML
    ImageView moon;
    @FXML
    MenuButton historyButton;
    History history = new History(); // init History cache

    //List of Records
    ObservableList<String> types = FXCollections.observableArrayList("Any", "A", "AAAA", "CNAME", "MX", "NS", "TXT", "SRV", "SOA", "PTR");
    //initialize Variables for Helper.Domain Check
    @FXML
    Label templbl;
    String domainCheckResult = "";
    String ip_data = null;
    String originalRecords = ""; //To undo

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeBox.setItems(types);
        typeBox.setValue("Any");
        chckBox.setSelected(Main.gui.isShowAllRecords());
        updateHistoryDisplay(); // Update history at startup
    }

    @FXML
    private void onClose(MouseEvent e) {
        Main.gui.exit();
    }

    @FXML
    private void onMinimize(MouseEvent e) {
        Main.gui.minimize();
    }

    @FXML
    private void getOffset(MouseEvent event) { //gets the current X and Y for the scene to be used as offset
        offsetX = event.getSceneX();
        offsetY = event.getSceneY();
    }

    @FXML
    private void moveWindow(MouseEvent event) { // If Toolbar is dragged the scene gets moved
        Main.gui.moveWindow(event.getScreenX() - offsetX, event.getScreenY() - offsetY);
    }

    @FXML
    private void startSearchButton(ActionEvent event) throws NamingException, UnknownHostException { //Handels the Start Button action
        long startSearchTime = System.currentTimeMillis();
        closeWebView(event);
        resetTempValues();
        DNSOutput(txtDomain.getText(), (String) typeBox.getValue());

        if (!txtDomain.getText().equals("")) { //add Helper.Domain to history
            history.addDomain(txtDomain.getText());
            updateHistoryDisplay(); //Update history list
        }
        System.out.println("Request took " + (System.currentTimeMillis() - startSearchTime) + "ms");
    }

    private void resetTempValues() {
        domainCheckResult = "";
        ip_data = null;
        originalRecords = "";
    }

    private void updateHistoryDisplay() {
        historyButton.getItems().clear();
        String[] historyList = history.getHistory();
        for (int i = historyList.length - 1; i >= 0; i--) {
            addHistoryDisplay(historyList[i]);
        }
    }

    private void addHistoryDisplay(String domain) {
        MenuItem item = new MenuItem(domain);
        historyButton.getItems().add(item);

        EventHandler<ActionEvent> event1 = e -> txtDomain.setText(((MenuItem) e.getSource()).getText());
        item.setOnAction(event1);
    }


    @FXML
    private void changeEmptyRecordsSetting(MouseEvent event) {
        Main.gui.setShowAllRecords();
    }

    @FXML
    private void changeTheme(MouseEvent event) {
        Main.gui.changeTheme();
    }

    @FXML
    private void scrollUPButtonVisibility(ScrollEvent event) {
        scrollButton.setVisible(txtAreaRecords.getScrollTop() > 1);
    }

    @FXML
    private void scrollUPButton(ActionEvent event) {
        txtAreaRecords.setScrollTop(0);
        scrollButton.setVisible(false);
    }

    @FXML
    private void copyRecords(ActionEvent event) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        StringSelection strSel = new StringSelection(txtAreaRecords.getText());
        clipboard.setContents(strSel, null);
        System.out.println("Records copied!");
        // Add animtaion to Acknowledge Copy... maybe
    }

    @FXML
    private void openWebView(ActionEvent event) {
        if (templbl.getText() == null) {
            domainCheckResult = "";
        } else {
            domainCheckResult = templbl.getText();
        }
        displayWebView(registryLink.getText());
    }

    @FXML
    private void closeWebView(ActionEvent event) {
        if (domainCheckResult.equals("")) {
            final WebEngine webEngine = web.getEngine();
            web.setVisible(false);
            btnWeb.setVisible(false);
            webEngine.load(null);
        } else {
            if (!originalRecords.equals("")) {
                txtAreaRecords.setText(originalRecords);
            }
            btnWeb.setVisible(false);
            hyperLbl.setVisible(true);
            btnWeb.setText("Close Web");
            txtFieldIP.setDisable(false);
        }

    }

    private void displayWebView(String host) {
        if (domainCheckResult.equals("")) {
            final WebEngine webEngine = web.getEngine();
            webEngine.load("http://" + host);
            btnWeb.setVisible(true);
            web.setVisible(true);

        } else {
            closeWebView(null);
            originalRecords = txtAreaRecords.getText();
            txtAreaRecords.setText(domainCheckResult);
            btnWeb.setVisible(true);
            hyperLbl.setVisible(false);
            btnWeb.setText("Back");
        }
    }

    @FXML
    private void openIP(MouseEvent event) {
        try {
            if (!txtFieldIP.getText().equals("")) {
                closeWebView(null);
                displayIPInfo(txtFieldIP.getText());
            }
        } catch (NullPointerException e) {
            System.err.println("null Pointer - GUI openIP Function");
        }
    }

    private void displayIPInfo(String ip) {
        domainCheckResult = "ip";
        originalRecords = txtAreaRecords.getText();
        if (!ip.equals("")) {
            if (ip_data == null) {
                IP_Info info = new IP_Info(ip);
                ip_data = info.getInfo();
            }
            txtAreaRecords.setText(ip_data);
        } else {
            txtAreaRecords.setText("No IP Address found");
        }
        btnWeb.setVisible(true);
        btnWeb.setText("Back");
    }

    @FXML
    private void useTextHostnameField(MouseEvent event) throws NamingException, UnknownHostException {
        btnWeb.setVisible(false);
        String host = txtDomain.getText();
        if (Domain.isSubdomain(host)) {
            txtDomain.setText(Domain.getMainDomain(host));
            DNSOutput(Domain.getMainDomain(host), (String) typeBox.getValue());
        }
    }

    private void nameServerDisplay(String[] records) {
        txtNS1.clear();
        txtNS2.clear();
        txtNS3.clear();
        txtNS4.clear();

        if (records != null) {
            try {
                txtNS1.setText(records[0]);
                txtNS2.setText(records[1]);
                txtNS3.setText(records[2]);
                txtNS4.setText(records[3]);
            } catch (ArrayIndexOutOfBoundsException ignored) {

            } catch (NullPointerException e) {
                System.err.println("NullPointerException - Try catch NameServerDisplay");
            }
        }
    }

    private void recordPutter(String[] list, String type) {
        if (list != null && type.equals("Messages")) {
            for (String rec : list) {
                txtAreaRecords.appendText("\t" + rec + "\n");
            }
        } else if (list != null) {
            try {
                txtAreaRecords.appendText(type + ": \n");
                for (String rec : list) {
                    txtAreaRecords.appendText("\t" + rec + "\n");
                }
                txtAreaRecords.appendText("\n");
            } catch (NullPointerException e) {
                System.err.println("No list found - recordPutter Try Catch");
            }
            txtAreaRecords.home();
        } else if (chckBox.isSelected()) {
            txtAreaRecords.appendText(type + ": \n");
            txtAreaRecords.appendText("\t" + "No Records found\n\n");
        }
        txtAreaRecords.home();
    }

    private void DNSOutput(String host, String type) throws NamingException, UnknownHostException {
        DNSRequests query;
        if (!txtDomain.getText().isEmpty()) {
            txtAreaRecords.clear();
            if (type.equals("Any")) {
                query = new DNSRequests(host, "*");
                //Set Records
                String[] requests = {"A", "AAAA", "CNAME", "MX", "TXT", "SRV", "SOA"};
                recordPutter(query.getRecords("Messages"), "Messages");
                for (String request : requests) {
                    recordPutter(query.getRecords(request), request);
                }
            } else {
                query = new DNSRequests(host, type);
                recordPutter(query.getRecords("Messages"), "Messages");
                recordPutter(query.getRecords(type), type);
                //setReachableCircle(query.getReachable());
            }
            txtFieldHost.clear();
            txtFieldIP.clear();
            nameServerDisplay(query.getRecords("NS"));
            txtFieldHost.setText(query.getHostname());
            txtFieldIP.setText(query.getIP());
        }
        getRegistrar(host);
    }

    private void getRegistrar(String host) {
        if (!host.equals("")) {
            GetRegistrarTask task = new GetRegistrarTask(host);
            registryLink.textProperty().bind(task.messageProperty());
            templbl.textProperty().bind(task.valueProperty());
            hyperLbl.disableProperty().bind(task.runningProperty());
            hyperLbl.setVisible(true);
            new Thread(task).start();
        } else {
            hyperLbl.setVisible(false);
        }

    }

}

