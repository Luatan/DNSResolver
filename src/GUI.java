import Controller.HistoryController;
import Model.API.Ip_api;
import Model.RecordListCellFactory;
import Model.DNS.Records.Record;
import Tasks.CacheCleanupTask;
import Tasks.DnsTask;
import Tasks.GetWhoisTask;
import Tasks.LookupTask;
import Utils.Domain;
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
import java.util.*;
import java.util.List;

public class GUI implements Initializable {
    @FXML
    TextField ns1Lbl, ns2Lbl, ns3Lbl, ns4Lbl;
    @FXML
    TextField queryTf, ipTf, hostTf;
    @FXML
    Button copyBtn, startBtn, scrollTopBtn, backBtn;
    @FXML
    ComboBox<String> typeComboBox;
    @FXML
    Hyperlink whoisHyperLink;
    @FXML
    Label whoisLinkLbl;
    @FXML
    CheckBox showRecordsTickBox;
    @FXML
    ImageView moonImg;
    @FXML
    MenuButton historyBtn;
    @FXML
    ListView<String> listViewRecords;
    @FXML
    HBox WindowMenu;

    // used for calaculating the offset to move the Window
    private double offsetX;
    private double offsetY;

    //History history = new History(); // init History cache
    private final HistoryController historyController = new HistoryController();

    //List of types to choose
    private final ObservableList<String> types = FXCollections.observableArrayList("Any", "A", "AAAA", "CNAME", "MX", "NS", "TXT", "SRV", "SOA", "PTR");

    //initialize Variables for Domain Check
    private static ObservableList<String> listViewRecordsModel;
    private final List<String> whoisInfo = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO fix NS clear

        //init listview
        listViewRecordsModel = FXCollections.observableArrayList();
        listViewRecords.setCellFactory(e -> new RecordListCellFactory());
        listViewRecords.setItems(listViewRecordsModel);

        //init Choicebox
        typeComboBox.setItems(types);
        typeComboBox.setValue("Any");
        showRecordsTickBox.setSelected(Main.gui.isShowAllRecords());

        // Update history at startup
        updateHistoryDisplay();

        //prevent start button pressed without input
        BooleanBinding enableSearchbtn = queryTf.textProperty().isNotEmpty();
        startBtn.disableProperty().bind(enableSearchbtn.not());

        //prevent empty copy
        BooleanBinding enableCopybtn = Bindings.size(listViewRecords.itemsProperty().get()).greaterThan(0);
        copyBtn.visibleProperty().bind(enableCopybtn);

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
    private void changeEmptyRecordsSetting() {
        Main.gui.setShowAllRecords();
    }

    @FXML
    private void changeTheme() {
        Main.gui.changeTheme();
    }

    @FXML
    private void search() { //Handels the Start Button action
        //remove spaces before searching
        queryTf.setText(queryTf.getText().trim());

        //Clean up
        closeList();
        ipTf.textProperty().unbind();
        hostTf.textProperty().unbind();
        listViewRecordsModel.clear();
        whoisInfo.clear();

        //Do nothing if empty
        if (queryTf.getText().equals("")) {
            return;
        }

        if (Domain.isIPAdress(queryTf.getText())) {
            //clean up old entries
            whoisLinkLbl.visibleProperty().unbind();
            hostTf.setText("");
            nameServerDisplay(new ArrayList<>());

            //set new entries
            ipTf.setText(queryTf.getText());
            IpInfo();
            backBtn.setVisible(false);
            whoisLinkLbl.setVisible(false);
            resolveHost(queryTf.getText());

        } else {
            DNSOutput(queryTf.getText(), typeComboBox.getValue());
        }

        // Add the domain to history
        historyController.history.addDomain(queryTf.getText());
        updateHistoryDisplay(); //Update history list
    }

    private void updateHistoryDisplay() {
        historyBtn.getItems().clear();
        for (int i = historyController.history.getDomains().size() - 1; i >= 0; i--) {
            MenuItem item = new MenuItem(historyController.history.getDomains().get(i));
            historyBtn.getItems().add(item);

            EventHandler<ActionEvent> event1 = e -> queryTf.setText(((MenuItem) e.getSource()).getText());
            item.setOnAction(event1);
        }
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
    private void whois() {
        if (whoisInfo.isEmpty()) {
            System.err.println("No Whois found!!");
            return;
        }
        openList(whoisInfo);
    }

    @FXML
    private void IpInfo() {
        if (ipTf.getText().isEmpty()) {
            return;
        }
        Ip_api info = new Ip_api(ipTf.getText());
        openList(info.getOutput());
    }

    private void openList(List<String> list) {
        //close the old list before opening a new one
        closeList();
        ObservableList<String> observableList = FXCollections.observableArrayList();
        observableList.addAll(list);
        listViewRecords.setItems(observableList);
        backBtn.setVisible(true);
    }

    @FXML
    private void closeList() {
        listViewRecords.getItems().removeAll();
        listViewRecords.setItems(listViewRecordsModel);
        backBtn.setVisible(false);
    }

    @FXML
    private void useTextHostnameField() {
        backBtn.setVisible(false);
        String host = queryTf.getText();
        if (Domain.isSubdomain(host)) {
            queryTf.setText(Domain.getMainDomain(host));
            DNSOutput(Domain.getMainDomain(host), typeComboBox.getValue());
        }
    }

    public void nameServerDisplay(List<Record> records) {
        List<TextField> nsTf = new LinkedList<>();
        nsTf.add(ns1Lbl);
        nsTf.add(ns2Lbl);
        nsTf.add(ns3Lbl);
        nsTf.add(ns4Lbl);

        //sort list
        records.sort(Comparator.comparing(Record::getValue));

        //set Textfield text
        if (!records.isEmpty()) {
            for (int i = 0; i < nsTf.size(); i++) {
                nsTf.get(i).clear();
                try {
                    nsTf.get(i).setText(records.get(i).getValue());
                } catch (IndexOutOfBoundsException ignored) {
                    //do nothing
                }
            }
        }
    }

    private void DNSOutput(String host, String type) {
        // create Thread to get Whois
        getWhois(host);
        // create Thread to resolve Host
        resolveHost(host);

        //clear fileds
        hostTf.clear();
        ipTf.clear();

        if (!queryTf.getText().isEmpty()) {
            listViewRecordsModel.clear();

            DnsTask dnsLookup = new DnsTask(host, type);
            if (showRecordsTickBox.isSelected()) {
                dnsLookup.showEmpty(true);
            }
            dnsLookup.setOnSucceeded(e -> {
                listViewRecordsModel.addAll(dnsLookup.getValue());
                try {
                    nameServerDisplay(dnsLookup.getNameservers());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            new Thread(dnsLookup).start();
        }

    }

    private void resolveHost(String host) {
        if (host.equals("")) {
            return;
        }
        LookupTask lookup = new LookupTask(host);
        hostTf.textProperty().bind(lookup.valueProperty());
        ipTf.textProperty().bind(lookup.messageProperty());
        new Thread(lookup).start();
    }

    private void getWhois(String host) {
        if (host.equals("")) {
            whoisLinkLbl.setVisible(false);
            return;
        }
        GetWhoisTask task = new GetWhoisTask(host);
        whoisHyperLink.textProperty().bind(task.messageProperty());
        task.setOnSucceeded(e -> {
            if (task.getValue().size() > 0) {
                whoisInfo.addAll(task.getValue());
            }
        });
        task.setOnFailed(e -> System.err.println("Whois Task failed - " + host));

        BooleanBinding gotWhois = whoisHyperLink.textProperty().isNotEmpty();
        whoisLinkLbl.visibleProperty().bind(gotWhois);
        new Thread(task).start();
    }
}

