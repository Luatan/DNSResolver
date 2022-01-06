import Controller.GUIController;
import Controller.HistoryController;
import Model.API.Ip_api;
import Model.DNS.Records.Record;
import Model.RecordListCellFactory;
import Model.Tasks.CacheCleanupTask;
import Model.Tasks.DnsTask;
import Model.Tasks.GetWhoisTask;
import Model.Tasks.LookupTask;
import Model.Utils.Domain;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.List;
import java.util.*;

public class GUI implements Initializable {
    @FXML
    Label hostnameLbl;
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
    @FXML
    ImageView whoisLoading;
    @FXML
    ImageView loading_duck;

    // used for calaculating the offset to move the Window
    private double offsetX;
    private double offsetY;

    //History history = new History(); // init History cache
    private final HistoryController historyController = new HistoryController();

    //List of types to choose
    private final ObservableList<String> types = FXCollections.observableArrayList("Any", "A", "AAAA", "CNAME", "MX", "NS", "TXT", "SRV", "SOA", "PTR");

    //initialize Variables for Domain Check
    private static ObservableList<String> listViewRecordsModel;
    private final ObservableList<String> whoisInfo = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rotateImage(whoisLoading);
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

    private void rotateImage(ImageView image) {
        RotateTransition rotate = new RotateTransition(Duration.millis(1500), image);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.play();

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
        ScaleTransition transition = new ScaleTransition(Duration.millis(150), copyBtn);
        transition.setAutoReverse(true);
        transition.setFromX(1);
        transition.setFromY(1);
        transition.setToX(1.3);
        transition.setToY(1.3);
        transition.setCycleCount(2);

        transition.play();
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
        } else {
            for (TextField tf:nsTf) {
                tf.clear();
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
            loading_duck.visibleProperty().bind(dnsLookup.runningProperty());
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

            //error handling, if the task fails
            dnsLookup.setOnFailed(e -> {
                Throwable error = dnsLookup.getException();
                error.printStackTrace();
            });
            new Thread(dnsLookup).start();
        }

    }

    private void resolveHost(String host) {
        if (host.equals("")) {
            return;
        }
        LookupTask lookup = new LookupTask(host);
        lookup.setOnRunning(e -> {
            ImageView hostLoading = new ImageView(new Image(Objects.requireNonNull(GUIController.class.getResourceAsStream("/icons/reload_64x64.png"))));
            hostLoading.setFitHeight(20);
            hostLoading.setPreserveRatio(true);
            rotateImage(hostLoading);
            hostnameLbl.setGraphicTextGap(10);
            hostnameLbl.setGraphic(hostLoading);
        });

        lookup.setOnSucceeded(e -> {
            hostnameLbl.setGraphic(hostTf);
            hostnameLbl.setGraphicTextGap(0);
        });

        //error handling, if the task fails
        lookup.setOnFailed(e -> {
            Throwable error = lookup.getException();
            error.printStackTrace();
        });

        hostTf.textProperty().bind(lookup.valueProperty());
        ipTf.textProperty().bind(lookup.messageProperty());
        new Thread(lookup).start();
    }



    private void getWhois(String host) {
        if (host.equals("")) {
            whoisLinkLbl.setVisible(false);
            return;
        }
        GetWhoisTask whoisTask = new GetWhoisTask(host);

        whoisHyperLink.textProperty().bind(whoisTask.messageProperty());

        BooleanProperty whoisEmpty = new SimpleBooleanProperty(false);
        whoisLoading.visibleProperty().bind(whoisTask.runningProperty());
        whoisTask.setOnSucceeded(e -> {
            if (whoisTask.getValue().size() > 1) {
                whoisInfo.addAll(whoisTask.getValue());
                whoisEmpty.setValue(true);
            } else {
                whoisEmpty.setValue(false);
            }
        });

        //error handling, if the task fails
        whoisTask.setOnFailed(e -> {
            Throwable error = whoisTask.getException();
            error.printStackTrace();
        });

        whoisHyperLink.disableProperty().bind(whoisEmpty.not());

        BooleanBinding gotWhois = whoisHyperLink.textProperty().isNotEmpty();
        whoisLinkLbl.visibleProperty().bind(gotWhois);
        new Thread(whoisTask).start();
    }
}

