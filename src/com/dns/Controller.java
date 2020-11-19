package com.dns;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Paint;
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
    Button scollButton;
    @FXML
    TextArea txtAreaRecords;
    @FXML
    ComboBox typeBox;
    @FXML
    Hyperlink registryLink;
    @FXML
    Button btnWeb;
    @FXML
    WebView web;
    @FXML
    Label hyperLbl;
    @FXML
    Circle reachable;
    @FXML
    CheckBox chckBox;

    //List of Records
    ObservableList<String> types = FXCollections.observableArrayList("Any", "A", "AAAA", "CNAME", "MX", "NS", "SOA", "SRV", "TXT");

    @FXML
    private void handleButtonAction(ActionEvent event) throws NamingException, UnknownHostException { //Handels the Start Button action
        closeWebView(event);
        DNSOutput(txtDomain.getText(), (String) typeBox.getValue());
    }

    @FXML
    private void scrollUPButtonVisibility(ScrollEvent event) {
        scollButton.setVisible(txtAreaRecords.getScrollTop() > 1);
    }

    @FXML
    private void scrollUPButton(ActionEvent event) {
        txtAreaRecords.setScrollTop(0);
        scollButton.setVisible(false);
    }

    @FXML
    private void copyRecords(ActionEvent event) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        StringSelection strSel = new StringSelection(txtAreaRecords.getText());
        clipboard.setContents(strSel, null);
        System.out.println("Records copied!");
        // Add animtaion to Acknowledge Copy
    }

    @FXML
    private void openWebView(ActionEvent event) {
        displayWebView(registryLink.getText());
    }

    @FXML
    private void closeWebView(ActionEvent event) {
        final WebEngine webEngine = web.getEngine();
        web.setVisible(false);
        btnWeb.setVisible(false);
        webEngine.load(null);
    }

    private void displayWebView(String host) {
        final WebEngine webEngine = web.getEngine();
        webEngine.load("http://" + host);
        btnWeb.setVisible(true);
        web.setVisible(true);
    }

    @FXML
    private void useTextIPField(MouseEvent event) throws NamingException, UnknownHostException {
        if (!txtFieldIP.getText().isEmpty()) {
            DNSOutput(txtFieldIP.getText(), (String) typeBox.getValue());
            txtDomain.setText(txtFieldIP.getText());
        }
    }

    @FXML
    private void useTextHostnameField(MouseEvent event) throws NamingException, UnknownHostException {
        DNSRequests subdomainQuery = new DNSRequests();
        if (subdomainQuery.isSubdomain(txtDomain.getText())) {
            String[] partDomain = txtDomain.getText().split("[.]");
            DNSOutput(partDomain[partDomain.length - 2] + "." + partDomain[partDomain.length - 1], (String) typeBox.getValue());
            txtDomain.setText(partDomain[partDomain.length - 2] + "." + partDomain[partDomain.length - 1]);
        }
    }

    private void setReachableCircle(boolean reachable) {
        if (reachable) {
            this.reachable.setFill(Paint.valueOf("#16dd16"));
        } else {
            this.reachable.setFill(Paint.valueOf("#ff0909"));
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
            } catch (ArrayIndexOutOfBoundsException e) {

            } catch (NullPointerException e) {
                System.err.println("NullPointerException - Try catch NameServerDisplay");
            }
        }
    }

    private void recordPutter(String[] list, String type) {
        if (list != null) {
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
        } else if (chckBox.isSelected()){
            txtAreaRecords.appendText(type + ": \n");
            txtAreaRecords.appendText("\t" +"No Records found\n\n");
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
                recordPutter(query.getRecords("A"), "A");
                recordPutter(query.getRecords("AAAA"), "AAAA");
                recordPutter(query.getRecords("CNAME"), "CNAME");
                recordPutter(query.getRecords("MX"), "MX");
                recordPutter(query.getRecords("TXT"), "TXT");
                recordPutter(query.getRecords("SRV"), "SRV");
                recordPutter(query.getRecords("SOA"), "SOA");
                setReachableCircle(query.getReachable());
            } else {
                query = new DNSRequests(host, type);
                recordPutter(query.getRecords(type), type);
                setReachableCircle(query.getReachable());
            }
            txtFieldHost.clear();
            txtFieldIP.clear();
            nameServerDisplay(query.getRecords("NS"));
            txtFieldHost.setText(query.getHostname());
            txtFieldIP.setText(query.getIP());
        }
        domainCheckerLink(host);
    }

    private void domainCheckerLink(String host) {
        DNSRequests query = new DNSRequests();
        switch (query.getExtension(host)) {
            case "com":
            case "net":
            case "fr":
            case "es":
            case "ru":
            case "eu":
            case "org":
            case "ca":
                hyperLbl.setVisible(true);
                registryLink.setText("whois.com/whois/" + host);
                break;
            default:
                hyperLbl.setVisible(false);
                break;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeBox.setItems(types);
        typeBox.setValue("Any");
    }


}
