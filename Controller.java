package redditBotCreator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.json.JSONObject;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;

public class Controller implements Initializable {
    private static Slack s = new Slack();
    private reddit r;
    private Bot currentBot;


    @FXML
    private Label slackVerificationLabel;
    @FXML
    private FontAwesomeIconView slackVerificationIcon;
    @FXML
    private TextField newBotName;

    ObservableList<Bot> bots = FXCollections.observableArrayList();
    @FXML
    private TableView<Bot> botTable;
    @FXML
    private TableColumn<Bot, String> nameColumn;
    @FXML
    private TableColumn<Bot, Boolean> statusColumn;
    @FXML
    private TextField subredditTextField;
    @FXML
    private TextField wordTextField;
    @FXML
    private ListView subredditList;
    @FXML
    private ListView wordList;
    @FXML
    private ComboBox freqBox;

    protected ListProperty<String> subredditsProperty = new SimpleListProperty<>();
    protected ListProperty<String> wordsProperty = new SimpleListProperty<>();
    //ArrayList<Bot> bots = new ArrayList<Bot>();
    public void slackLoginClicked(){
        s.openWeb2();
        slackVerificationLabel.setText("Verified");
        slackVerificationLabel.setStyle("-fx-text-fill: green");
        slackVerificationIcon.setFill(Paint.valueOf("#0c8c0c"));
        //slackVerificationIcon.setGlyphStyle("CHECK_CIRCLE_ALT");
        slackVerificationIcon.setGlyphName("CHECK_CIRCLE_ALT");
    }

    public void testMe(){
        boolean ay = r.doesSubredditExist("dogs");
        System.out.println(ay);
        ay = r.doesSubredditExist("dogsasdfasdfasdfsd");
        System.out.println(ay);
    }

    public void onBotCreate(){
        String name = newBotName.getText();
        bots.add(new Bot(name));
    }

    public void addSubreddit(){
        String subreddit = subredditTextField.getText();
        boolean exists = r.doesSubredditExist(subreddit);
        if(exists)
            currentBot.getSubreddits().add(subreddit);
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Bad Subreddit");
            alert.setContentText(subreddit + " is not a valid subreddit!");
            alert.showAndWait();
        }
    }

    public void addWord(){
        currentBot.getWords().add(wordTextField.getText());
    }

    public void testRedditRequest(){
        System.out.println("Making reddit request");
        JSONObject data;
        data = r.redditTest();
        data = data.getJSONObject("data");
        System.out.println(data.toString());
    }

    public void initialize(URL location, ResourceBundle resources){
        r = reddit.getInstance();
        nameColumn.setCellValueFactory(new PropertyValueFactory<Bot, String>("name"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<Bot, Boolean>("on"));
        statusColumn.setSortable(false);

        statusColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bot, Boolean>, ObservableValue<Boolean>>(){
            @Override public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Bot, Boolean> features) {
                return new SimpleBooleanProperty(features.getValue().isOn());
            }
        });

        // create a cell value factory with an add button for each row in the table.
        statusColumn.setCellFactory(new Callback<TableColumn<Bot, Boolean>, TableCell<Bot, Boolean>>() {
            @Override public TableCell<Bot, Boolean> call(TableColumn<Bot, Boolean> personBooleanTableColumn) {
                return new AddBotCell(/*stage, botTable*/);
            }
        });

        /**
         * Adds a click listener to each row as it is added to the table
         */
        botTable.setRowFactory(tv -> {
            TableRow<Bot> row = new TableRow<>(); //create row
            row.setOnMouseClicked(event -> { //give click listener
                if (! row.isEmpty() && event.getButton()== MouseButton.PRIMARY) {
                    currentBot = row.getItem();
                    subredditList.setItems(currentBot.getSubreddits());
                    wordList.setItems(currentBot.getWords());
                }
                else if(! row.isEmpty() && event.getButton() == MouseButton.SECONDARY){
                    System.out.println("GOtemmmm");
                }
            });
            return row ;
        });
        botTable.setItems(bots);
        //int[] frequencies = {60000, 900000, 1800000, 3600000, 14400000, 43200000, 86400000, 604800000};
        freqBox.getItems().addAll(
                new Frequency("Every Minute", 60000),
                new Frequency("Every 15 Minutes", 900000),
                new Frequency("Every 30 Minutes", 1800000),
                new Frequency("Every Hour", 3600000),
                new Frequency("Every 4 Hours", 14400000),
                new Frequency("Every 12 Hours", 43200000),
                new Frequency("Every Day", 86400000),
                new Frequency("Every Week", 604800000)
        );
        freqBox.setConverter(new StringConverter<Frequency>(){
            @Override
            public String toString(Frequency object) {
                return object.getEnglish();
            }
            @Override
            public Frequency fromString(String string) {
                return null;
            }
        });
    }

    /** A table cell containing a button for adding a new person. */
    private class AddBotCell extends TableCell<Bot, Boolean> {
        // a button for adding a new bot.
        final CheckBox checkbox = new CheckBox();
        // pads and centers the add button in the cell.
        final StackPane paddedCheckBox = new StackPane();
        // records the y pos of the last button press so that the add person dialog can be shown next to the cell.
        private boolean updating = false;

        AddBotCell(/*final Stage stage, final TableView table*/) {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            paddedCheckBox.setPadding(new Insets(3));
            paddedCheckBox.getChildren().add(checkbox);

            checkbox.selectedProperty().addListener((o, oldValue, newValue) -> {
                if (!updating) {
                    updating = true;
                    ((Bot)getTableRow().getItem()).setIsOn(newValue);
                    updating = false;
                }
            });
        }
        /** places an add checkbox in the row only if the row is not empty. */
        @Override protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                setGraphic(paddedCheckBox);
                updating = true;
                checkbox.setSelected(item);
                updating = false;
            }
        }
    }
}
