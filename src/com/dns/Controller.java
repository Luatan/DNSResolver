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

    //List of Records
    ObservableList<String> types = FXCollections.observableArrayList("Any", "A", "AAAA", "CNAME", "MX", "NS", "TXT", "SRV", "SOA", "PTR");
    //initialize Variables for Domain Check
    String domainCheckResult = "";
    //To undo
    String originalRecords = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeBox.setItems(types);
        typeBox.setValue("Any");
    }

    @FXML
    private void startSearchButton(ActionEvent event) throws NamingException, UnknownHostException { //Handels the Start Button action
        closeWebView(event);
        DNSOutput(txtDomain.getText(), (String) typeBox.getValue());
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
            DNSOutput(subdomainQuery.getMainDomain(host), (String)typeBox.getValue());
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
        }else if (list != null) {
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
        DNSRequests query = new DNSRequests();

        if (query.isSubdomain(host)) {
            host = query.getMainDomain(host);
        }
        switch (query.getExtension(host)) {
            case "com":
            case "net":
            case "ru":
            case "org":
            case "ca":
                hyperLbl.setVisible(true);
                registryLink.setText("whois.com/whois/" + host);
                break;
            case "eu":
                domainCheckResult = setDomainCheckResult(host, "whois.eu");
                break;
            case "fr":
                domainCheckResult = setDomainCheckResult(host, "whois.afnic.fr");
                break;
            case "ch":
            case "li":
                //domainCheckResult = setDomainCheckResult(host, "whois.nic.ch");
                hyperLbl.setVisible(false);
                break;
            case "swiss":
                domainCheckResult = setDomainCheckResult(host, "whois.nic.swiss");
                break;
            case "de":
                domainCheckResult = setDomainCheckResult("-T dn " + host, "whois.denic.de");
                break;
            default:
                domainCheckResult = "";
                hyperLbl.setVisible(false);
                btnWeb.setVisible(false);
                break;
        }
    }

    private String setDomainCheckResult(String host, String whoisServer) {
        registryLink.setText("Click here for a Domain Check");
        hyperLbl.setVisible(true);
        return new Whois().getWhois(host, whoisServer);
    }
}
