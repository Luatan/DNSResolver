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
import javafx.scene.shape.Circle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.naming.NamingException;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class Controller implements Initializable {
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
    Circle reachable;
    @FXML
    CheckBox chckBox;
    boolean checkBoxSetting = false;
    @FXML
    ImageView moon;
    @FXML
    MenuButton historyButton;
    SettingsHandler history = new SettingsHandler();
    //List of Records
    ObservableList<String> types = FXCollections.observableArrayList("Any", "A", "AAAA", "CNAME", "MX", "NS", "TXT", "SRV", "SOA", "PTR");
    //initialize Variables for Domain Check
    @FXML
    Label templbl;
    String domainCheckResult = "";
    //To undo
    String originalRecords = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeBox.setItems(types);
        typeBox.setValue("Any");
        chckBox.setSelected(Main.emptyRecordSetting);
        addHistory();
    }

    @FXML
    private void startSearchButton(ActionEvent event) throws NamingException, UnknownHostException { //Handels the Start Button action
        long startSearchTime = System.currentTimeMillis();
        closeWebView(event);
        DNSOutput(txtDomain.getText(), (String) typeBox.getValue());
        //add Domain to history
        if (!txtDomain.getText().equals("")) {
            history.addDomainToHistory(txtDomain.getText());
            //add item to JSON
            addItemsToHistoryMenu(txtDomain.getText());
        }
        System.out.println("Request took " + (System.currentTimeMillis() - startSearchTime) + "ms");
    }

    private void addItemsToHistoryMenu(String domain) {
        MenuItem item = new MenuItem(domain);
        historyButton.getItems().add(item);

        EventHandler<ActionEvent> event1 = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                txtDomain.setText(((MenuItem) e.getSource()).getText());
            }
        };
        item.setOnAction(event1);

        if (historyButton.getItems().size() >= 10) {
            historyButton.getItems().clear();
            history.removeHistoryIndex(0);
            addHistory();
        }
    }

    private void addHistory() {
        String[] historyList = history.readHistory();
        for (int i = historyList.length - 1; i >= 0; i--) {
            addItemsToHistoryMenu(historyList[i]);
        }
    }

    @FXML
    private void changeEmptyRecordsSetting(MouseEvent event) {
        Main.setEmptyRecordSetting();
    }

    @FXML
    private void changeTheme(MouseEvent event) {
        Main.changeTheme();
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
            txtAreaRecords.setText(originalRecords);
            btnWeb.setVisible(false);
            hyperLbl.setVisible(true);
            btnWeb.setText("Close Web");
        }
    }

    private void displayWebView(String host) {
        if (domainCheckResult.equals("")) {
            final WebEngine webEngine = web.getEngine();
            webEngine.load("http://" + host);
            btnWeb.setVisible(true);
            web.setVisible(true);
        } else {
            originalRecords = txtAreaRecords.getText();
            txtAreaRecords.setText(domainCheckResult);
            btnWeb.setVisible(true);
            hyperLbl.setVisible(false);
            btnWeb.setText("Back");
        }
    }

    @FXML
    private void useTextHostnameField(MouseEvent event) throws NamingException, UnknownHostException {
        btnWeb.setVisible(false);
        DNSRequests subdomainQuery = new DNSRequests();
        String host = txtDomain.getText();
        if (subdomainQuery.isSubdomain(host)) {
            txtDomain.setText(subdomainQuery.getMainDomain(host));
            DNSOutput(subdomainQuery.getMainDomain(host), (String) typeBox.getValue());
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
        GetRegistrarTask task = new GetRegistrarTask(host);
        registryLink.textProperty().bind(task.messageProperty());
        templbl.textProperty().bind(task.valueProperty());
        hyperLbl.disableProperty().bind(task.runningProperty());
        hyperLbl.setVisible(true);
        new Thread(task).start();
    }

}

