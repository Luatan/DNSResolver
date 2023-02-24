package ch.luatan.DNSResolver.Gui;

import ch.luatan.DNSResolver.Controller.HistoryController;
import ch.luatan.DNSResolver.DNSResolver;
import ch.luatan.DNSResolver.Data.API.IpApi;
import ch.luatan.DNSResolver.Data.Resolver.Resolvable;
import ch.luatan.DNSResolver.Model.DNS.AdditionalTypes;
import ch.luatan.DNSResolver.Model.DNS.Record;
import ch.luatan.DNSResolver.Model.DNS.SpecialType;
import ch.luatan.DNSResolver.Model.DNS.Type;
import ch.luatan.DNSResolver.Model.Tasks.CacheCleanupTask;
import ch.luatan.DNSResolver.Model.Tasks.DnsTask;
import ch.luatan.DNSResolver.Model.Tasks.GetWhoisTask;
import ch.luatan.DNSResolver.Model.Tasks.LookupTask;
import ch.luatan.DNSResolver.Model.Utils.Domain;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
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
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.List;
import java.util.*;

public class GUIController implements Initializable {
    private final static SimpleStringProperty domainProperty = new SimpleStringProperty("");
    //initialize Variables for Domain Check
    private final ObservableList<String> dnsRecordList = FXCollections.observableArrayList();
    private final ObservableList<String> whoisInfo = FXCollections.observableArrayList();
    //State Property
    private final SimpleObjectProperty<State> stateProperty = new SimpleObjectProperty<>(State.NONE);
    // history
    private final HistoryController historyController = new HistoryController();
    //List of types to choose in Combobox
    private final ObservableList<Type> types = FXCollections.observableArrayList(SpecialType.ANY);
    //list of the Nameserver TextFields
    private final List<TextField> nsTf = new LinkedList<>();
    @FXML
    private Label hostnameLbl, whoisLinkLbl, dnssec;
    @FXML
    private TextField ns1Lbl, ns2Lbl, ns3Lbl, ns4Lbl;
    @FXML
    private TextField queryTf, ipTf, hostTf, dnsServerTf;
    @FXML
    private Button copyBtn, startBtn, backBtn;
    @FXML
    private ComboBox<Type> typeComboBox;
    @FXML
    private Hyperlink whoisHyperLink;
    @FXML
    private CheckBox showRecordsTickBox, useDnssecTick;
    @FXML
    private ImageView whoisLoading, loading_duck, tools_chevron, dnssecIcon;
    @FXML
    private MenuButton historyBtn;
    @FXML
    private ListView<String> listViewRecords;
    @FXML
    private HBox tools;

    public static StringProperty getDomainProperty() {
        return domainProperty;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        types.addAll(Resolvable.RECORD_TYPES);
        types.addAll(AdditionalTypes.values());
        rotateImage(whoisLoading);

        //init listview
        listViewRecords.setCellFactory(e -> new RecordCellFactory());

        //init NS TextFields
        nsTf.add(ns1Lbl);
        nsTf.add(ns2Lbl);
        nsTf.add(ns3Lbl);
        nsTf.add(ns4Lbl);

        //init Choicebox
        typeComboBox.setItems(types);
        typeComboBox.setConverter(new StringConverter<Type>() {
            @Override
            public String toString(Type type) {
                if (type.equals(SpecialType.ANY)) {
                    return type.toString().substring(0, 1).toUpperCase() + type.toString().substring(1).toLowerCase();
                }
                return type.toString();
            }

            @Override
            public Type fromString(String s) {
                return null;
            }
        });
        //select default Value
        typeComboBox.setValue(SpecialType.ANY);

        // apply settings
        showRecordsTickBox.setSelected(DNSResolver.isShowAllRecords()); //load TickBox
        useDnssecTick.setSelected(DNSResolver.isIgnoreDNSSEC());
        updateHistoryDisplay(); //load history

        //prevent start button pressed without input
        BooleanBinding enableSearchbtn = queryTf.textProperty().isNotEmpty();
        startBtn.disableProperty().bind(enableSearchbtn.not());

        //prevent empty copy; only show if State is not NONE
        BooleanBinding enableCopybtn = stateProperty.isNotEqualTo(State.NONE);
        copyBtn.visibleProperty().bind(enableCopybtn);

        // make IP TextField only clickable, when it isn't empty and the programm is not in the IP State
        ipTf.mouseTransparentProperty().bind(stateProperty.isEqualTo(State.IP).or(ipTf.textProperty().isEmpty()));
        // make host TextField only clickable, when it is a Subdomain
        BooleanBinding hostIsSubdomain = Bindings.createBooleanBinding(() -> Domain.isSubdomain(hostTf.getText()), hostTf.textProperty());
        hostTf.mouseTransparentProperty().bind(hostTf.textProperty().isEmpty().not().and(hostIsSubdomain).not());

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
        DNSResolver.exit();
    }

    @FXML
    private void toggleTools() {
        boolean isVisible = !tools.visibleProperty().get();

        if (!isVisible) {
            //clear tf if closed
            dnsServerTf.clear();
            //animation
            KeyFrame close1 = new KeyFrame(Duration.millis(0), new KeyValue(tools.prefHeightProperty(), tools.getHeight()), new KeyValue(tools.visibleProperty(), false), new KeyValue(tools_chevron.rotateProperty(), 90));
            KeyFrame close2 = new KeyFrame(Duration.millis(200), new KeyValue(tools.prefHeightProperty(), 0), new KeyValue(tools.visibleProperty(), false), new KeyValue(tools_chevron.rotateProperty(), 0));
            new Timeline(close1, close2).play();
        } else {
            //animation
            KeyFrame open1 = new KeyFrame(Duration.millis(0), new KeyValue(tools.prefHeightProperty(), 0));
            KeyFrame open2 = new KeyFrame(Duration.millis(200), new KeyValue(tools.prefHeightProperty(), 34), new KeyValue(tools.visibleProperty(), true), new KeyValue(tools_chevron.rotateProperty(), 90));
            new Timeline(open1, open2).play();
        }
    }

    @FXML
    private void onMinimize() {
        DNSResolver.minimize();
    }

    @FXML
    private void changeEmptyRecordsSetting() {
        DNSResolver.setShowAllRecords();
    }

    @FXML
    private void changeDNSSECSettings() {
        DNSResolver.setIsIgnoreDNSSEC();
    }

    @FXML
    private void changeTheme() {
        ch.luatan.DNSResolver.DNSResolver.changeTheme();
    }

    @FXML
    private void search() { //Handels the Start Button action
        //Do nothing if empty
        if (queryTf.getText().equals("")) {
            return;
        }


        //Clean up
        defaultList();
        //unbind
        ipTf.textProperty().unbind();
        hostTf.textProperty().unbind();
        whoisLinkLbl.visibleProperty().unbind();

        //clear lists
        listViewRecords.getItems().clear();
        dnsRecordList.clear();
        whoisInfo.clear();
        nsTf.forEach(TextField::clear);
        hostTf.clear();
        ipTf.clear();
        whoisLinkLbl.setVisible(false);

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
            //remove spaces and protocol before searching
            queryTf.setText(Domain.extractDomain(queryTf.getText().trim()));
            DNSOutput(queryTf.getText(), typeComboBox.getValue());
        }

        //set domain Property
        domainProperty.set(queryTf.getText());

        // Add the domain to history
        historyController.history.add(queryTf.getText());
        updateHistoryDisplay(); //Update history list
    }

    private void updateHistoryDisplay() {
        historyBtn.getItems().clear();
        for (int i = historyController.history.size() - 1; i >= 0; i--) {
            MenuItem item = new MenuItem(historyController.history.get(i));
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

        DNSResolver.LOGGER.debug("DNS Records copied!");
        //copy Animation
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
            DNSResolver.LOGGER.error("No Whois found!!");
            return;
        }
        openList(whoisInfo, State.WHOIS);
    }

    @FXML
    private void IpInfo() {
        if (ipTf.getText().isEmpty()) {
            return;
        }
        IpApi info = new IpApi();
        openList(info.query(ipTf.getText()), State.IP);
    }

    private void openList(List<String> list, State state) {
        //close the old list before opening a new one
//        closeList();
        listViewRecords.getItems().removeAll();
        ObservableList<String> observableList = FXCollections.observableArrayList();
        observableList.addAll(list);
        listViewRecords.setItems(observableList);
        backBtn.setVisible(state != State.DNS);
        stateProperty.setValue(state);
    }

    @FXML
    private void defaultList() {
        openList(dnsRecordList, State.DNS);
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

    private void nameServerDisplay(List<Record> records) {

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
            for (TextField tf : nsTf) {
                tf.clear();
            }
        }
    }

    private void DNSOutput(String host, Type type) {
        // create Thread to get Whois
        getWhois(host);
        // create Thread to resolve Host
        resolveHost(host);

        //clear fileds
        hostTf.clear();
        ipTf.clear();

        if (!queryTf.getText().isEmpty()) {
            dnsRecordList.clear();

            DnsTask dnsLookup = new DnsTask(host, type, dnsServerTf.getText().trim());
            loading_duck.visibleProperty().bind(dnsLookup.runningProperty().and(stateProperty.isEqualTo(State.DNS)));
            if (showRecordsTickBox.isSelected()) {
                dnsLookup.showEmpty(true);
            }
            dnsLookup.setOnSucceeded(e -> {
                dnsRecordList.addAll(dnsLookup.getValue());

                String secureZone = dnsLookup.seucreZone();
                Tooltip tooltip = new Tooltip(secureZone);
                tooltip.setFont(Font.font("Roboto Light", 12));
                Tooltip.install(dnssec, tooltip);

                if (secureZone.equals("Verified")) {
                    dnssec.setVisible(true);
                    dnssecIcon.setImage(new Image("/icons/check-solid.png"));
                } else {
                    dnssec.setVisible(true);
                    dnssecIcon.setImage(new Image("/icons/exclamation-solid.png"));
                }
                try {
                    nameServerDisplay(dnsLookup.getNameservers());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                openList(dnsRecordList, State.DNS);
            });

            //error handling, if the task fails
            dnsLookup.setOnFailed(e -> {
                Throwable error = dnsLookup.getException();
                error.printStackTrace();
                DNSResolver.LOGGER.error(error.getMessage());
            });
            Thread dnslookupThread = new Thread(dnsLookup);
            dnslookupThread.setName("DNS");
            dnslookupThread.start();
        }

    }

    private void resolveHost(String host) {
        if (host.equals("")) {
            return;
        }
        LookupTask lookup = new LookupTask(host);
        lookup.setOnRunning(e -> {
            dnssec.setVisible(false);
            ImageView hostLoading = new ImageView(new Image(Objects.requireNonNull(ch.luatan.DNSResolver.DNSResolver.class.getResourceAsStream("/icons/reload_64x64.png"))));
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
        Thread iplookup = new Thread(lookup);
        iplookup.setName("IP_Resolver");
        iplookup.start();
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
            DNSResolver.LOGGER.error(error.getMessage());
        });

        whoisHyperLink.disableProperty().bind(whoisEmpty.not());

        BooleanBinding gotWhois = whoisHyperLink.textProperty().isNotEmpty();
        whoisLinkLbl.visibleProperty().bind(gotWhois);
        Thread whoisThread = new Thread(whoisTask);
        whoisThread.setName("WHOIS");
        whoisThread.start();
    }
}

