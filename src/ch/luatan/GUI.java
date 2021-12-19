package ch.luatan;

import ch.luatan.Controller.HistoryController;
import ch.luatan.Model.API.Ip_api;
import ch.luatan.Model.CustomCellFactory;
import ch.luatan.Model.DNS.Records.Record;
import ch.luatan.Tasks.CacheCleanupTask;
import ch.luatan.Tasks.DnsTask;
import ch.luatan.Tasks.GetWhoisTask;
import ch.luatan.Tasks.LookupTask;
import ch.luatan.Utils.Domain;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GUI implements Initializable {
    // used for calaculating the offset to move the Window
    private double offsetX;
    private double offsetY;
    @FXML
    HBox WindowMenu;
    @FXML
    TextField txtNS1, txtNS2, txtNS3, txtNS4;
    @FXML
    TextField txtDomain, txtFieldIP, txtFieldHost;
    @FXML
    Button cpyRecords, btnStart, scrollButton, btnWeb;
    @FXML
    ComboBox<String> typeBox;
    @FXML
    Hyperlink whoisLink;
    @FXML
    Label hyperLbl;
    @FXML
    CheckBox chckBox;
    @FXML
    ImageView moon;
    @FXML
    MenuButton historyButton;
    @FXML
    ListView<String> listViewRecords;


    //History history = new History(); // init History cache
    HistoryController historyController = new HistoryController();


    //List of DNS.Records
    ObservableList<String> types = FXCollections.observableArrayList("Any", "A", "AAAA", "CNAME", "MX", "NS", "TXT", "SRV", "SOA", "PTR");
    //initialize Variables for Domain Check

    private static ObservableList<String> listViewRecordsModel;
    private final List<String> whoisInfo = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listViewRecordsModel = FXCollections.observableArrayList();
        listViewRecords.setCellFactory(e -> new CustomCellFactory());
        listViewRecords.setItems(listViewRecordsModel);
        typeBox.setItems(types);
        typeBox.setValue("Any");
        chckBox.setSelected(Main.gui.isShowAllRecords());

        // Update history at startup
        updateHistoryDisplay();

        //prevent start button pressed without input
        BooleanBinding enableSearchbtn = txtDomain.textProperty().isNotEmpty();
        btnStart.disableProperty().bind(enableSearchbtn.not());

        //prevent empty copy
        BooleanBinding enableCopybtn = Bindings.size(listViewRecords.itemsProperty().get()).greaterThan(0);
        cpyRecords.visibleProperty().bind(enableCopybtn);

        //Start cache cleanup Task after startup
        CacheCleanupTask cachClean = new CacheCleanupTask();
        cachClean.setDaemon(true);
        cachClean.start();

    }

    @FXML
    private void onClose() {
        historyController.write();
        Main.gui.exit();
    }

    @FXML
    private void onMinimize() {
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
    private void startSearchButton() { //Handels the Start Button action
        //remove spaces before searching
        txtDomain.setText(txtDomain.getText().trim());

        //Clean up
        closeList();
        txtFieldIP.textProperty().unbind();
        txtFieldHost.textProperty().unbind();
        listViewRecordsModel.clear();
        whoisInfo.clear();

        //Do nothing if empty
        if (txtDomain.getText().equals("")) {
            return;
        }

        if (Domain.isIPAdress(txtDomain.getText())) {
            //clean up old entries
            hyperLbl.visibleProperty().unbind();
            txtFieldHost.setText("");
            nameServerDisplay(new ArrayList<>());

            //set new entries
            txtFieldIP.setText(txtDomain.getText());
            showIPInfo();
            btnWeb.setVisible(false);
            hyperLbl.setVisible(false);
            resolveHost(txtDomain.getText());

        } else {
            DNSOutput(txtDomain.getText(), typeBox.getValue());
        }

        // Add the domain to history
        historyController.history.addDomain(txtDomain.getText());
        updateHistoryDisplay(); //Update history list
    }

    private void updateHistoryDisplay() {
        historyButton.getItems().clear();
        for (int i = historyController.history.getDomains().size() - 1; i >= 0; i--) {
            MenuItem item = new MenuItem(historyController.history.getDomains().get(i));
            historyButton.getItems().add(item);

            EventHandler<ActionEvent> event1 = e -> txtDomain.setText(((MenuItem) e.getSource()).getText());
            item.setOnAction(event1);
        }
    }

    @FXML
    private void changeEmptyRecordsSetting() {
        Main.gui.setShowAllRecords();
    }

    @FXML
    private void changeTheme() {
        Main.gui.changeTheme();
    }

    @FXML
    private void copyRecords() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();

        StringBuilder sb = new StringBuilder();
        for (String element : listViewRecords.itemsProperty().get()) {
            sb.append(element).append("\n");
        }

        StringSelection strSel = new StringSelection(sb.toString());
        clipboard.setContents(strSel, null);

        System.out.println("DNS Records copied!");
        // Add animtaion to Acknowledge Copy... maybe

    }

    @FXML
    private void openWhois() {
        if (whoisInfo.isEmpty()) {
            System.err.println("No Whois found!!");
            return;
        }
        openList(whoisInfo);

    }


    @FXML
    private void showIPInfo() {
        if (txtFieldIP.getText().isEmpty()) {
            return;
        }
        Ip_api info = new Ip_api(txtFieldIP.getText());
        openList(info.getOutput());
    }

    private void openList(List<String> list) {
        closeList();
        ObservableList<String> observableList = FXCollections.observableArrayList();
        observableList.addAll(list);
        listViewRecords.setItems(observableList);
        btnWeb.setVisible(true);
    }

    @FXML
    private void closeList() {
        listViewRecords.getItems().removeAll();
        listViewRecords.setItems(listViewRecordsModel);
        btnWeb.setVisible(false);
    }

    @FXML
    private void useTextHostnameField() {
        btnWeb.setVisible(false);
        String host = txtDomain.getText();
        if (Domain.isSubdomain(host)) {
            txtDomain.setText(Domain.getMainDomain(host));
            DNSOutput(Domain.getMainDomain(host), typeBox.getValue());
        }
    }

    public void nameServerDisplay(List<Record> records) {
        txtNS1.clear();
        txtNS2.clear();
        txtNS3.clear();
        txtNS4.clear();

        if (!records.isEmpty()) {
            try {
                txtNS1.setText(records.get(0).getValue());
                txtNS2.setText(records.get(1).getValue());
                txtNS3.setText(records.get(2).getValue());
                txtNS4.setText(records.get(3).getValue());
            } catch (IndexOutOfBoundsException ignored) {

            } catch (NullPointerException e) {
                System.err.println("NullPointerException - Try catch NameServerDisplay");
            }
        }
    }

    private void DNSOutput(String host, String type) {
        // create Thread to get Whois
        getWhois(host);
        // create Thread to resolve Host
        resolveHost(host);

        //clear fileds
        txtFieldHost.clear();
        txtFieldIP.clear();

        if (!txtDomain.getText().isEmpty()) {
            listViewRecordsModel.clear();

            DnsTask dnsLookup = new DnsTask(host, type);
            if (chckBox.isSelected()) {
                dnsLookup.showEmpty(true);
            }
            dnsLookup.setOnSucceeded(e -> {
                listViewRecordsModel.addAll(dnsLookup.getValue());
                nameServerDisplay(dnsLookup.getNameservers());

            });
            new Thread(dnsLookup).start();
        }

    }

    private void resolveHost(String host) {
        if (host.equals("")) {
            return;
        }
        LookupTask lookup = new LookupTask(host);
        txtFieldHost.textProperty().bind(lookup.valueProperty());
        txtFieldIP.textProperty().bind(lookup.messageProperty());
        new Thread(lookup).start();
    }

    private void getWhois(String host) {
        if (host.equals("")) {
            hyperLbl.setVisible(false);
            return;
        }
        GetWhoisTask task = new GetWhoisTask(host);
        whoisLink.textProperty().bind(task.messageProperty());
        task.setOnSucceeded(e -> {
            if (task.getValue().size() > 0) {
                whoisInfo.addAll(task.getValue());
            }
        });
        task.setOnFailed(e -> System.err.println("Whois Task failed - " + host));

        BooleanBinding gotWhois = whoisLink.textProperty().isNotEmpty();
        hyperLbl.visibleProperty().bind(gotWhois);
        new Thread(task).start();
    }
}

