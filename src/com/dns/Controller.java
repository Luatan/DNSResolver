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



    //List of Records
    ObservableList<String> types = FXCollections.observableArrayList("Any", "A", "AAAA", "CNAME", "MX", "TXT", "NS", "SOA", "SRV");

    @FXML
    private void handleButtonAction(ActionEvent event) throws NamingException, UnknownHostException { //Handels the Start Button action
        DNSOutput(txtDomain.getText(), (String)typeBox.getValue());
        txtAreaRecords.setScrollTop(0);

    }

    @FXML
    private void scrolUPButtonVisibility(ScrollEvent event){
        if (txtAreaRecords.getScrollTop() > 1) {
            scollButton.setVisible(true);
        } else {
            scollButton.setVisible(false);
        }
    }

    @FXML
    private void scrolUPButton(ActionEvent event){
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
    }

    @FXML
    private void openWebView(ActionEvent event) {
        displayWebView(registryLink.getText());
    }

    @FXML
    private void closeWebView(ActionEvent event){
        final WebEngine webEngine = web.getEngine();
        web.setVisible(false);
        hyperLbl.setVisible(false);
        btnWeb.setVisible(false);
        webEngine.load(null);
    }

    private void displayWebView(String host){
        final WebEngine webEngine = web.getEngine();
        webEngine.load("http://" + host);
        btnWeb.setVisible(true);
        web.setVisible(true);
    }

    @FXML
    private void useTextIPField(MouseEvent event) {
        txtDomain.setText(txtFieldIP.getText());
    }

    @FXML
    private void useTextHostnameField(MouseEvent event) throws NamingException, UnknownHostException {
        DNSRequests subdomainQuery = new DNSRequests();
        if(subdomainQuery.isSubdomain(txtDomain.getText())) {
            String[] partDomain = txtDomain.getText().split("[.]");
            DNSOutput(partDomain[partDomain.length-2] + "." + partDomain[partDomain.length-1], (String) typeBox.getValue());
            txtDomain.setText(partDomain[partDomain.length-2] + "." + partDomain[partDomain.length-1]);
        }
    }

    private void nameServerDisplay(String[] records){
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
                System.err.println("Out of bounds - Try catch NameServerDisplay");
            } catch (NullPointerException e) {
                System.err.println("NullPointerException - Try catch NameServerDisplay");
            }
        }
    }

    private void recordPutter(String[] list, String type) {
        txtAreaRecords.appendText(type + ": \n");
        if (list != null) {
            try {
                for (String rec: list) {
                    txtAreaRecords.appendText(rec + "\n");
                }
                txtAreaRecords.appendText("\n");
            } catch (NullPointerException e) {
                System.err.println("No list found - recordPutter Try Catch");
            }
        } else {
            txtAreaRecords.appendText("No Records found\n\n");
        }
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
                txtAreaRecords.setScrollTop(0);

            } else {
                query = new DNSRequests(host, type);
                recordPutter(query.getRecords(type), type);
                txtAreaRecords.setScrollTop(0);
            }

            nameServerDisplay(query.getRecords("NS"));
            txtFieldHost.clear();
            txtFieldIP.clear();
            txtFieldHost.setText(query.getHostname());
            txtFieldIP.setText(query.getIP());
        }
        txtAreaRecords.setScrollTop(0);

        query = new DNSRequests();
        if(query.getExtension(txtFieldHost.getText()).equals("com")) {
            hyperLbl.setVisible(true);
            registryLink.setText("www.whois.com/whois/" + txtFieldHost.getText());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeBox.setItems(types);
        typeBox.setValue("Any");


    }

}
