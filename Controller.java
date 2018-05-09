package redditBotCreator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javafx.fxml.FXML;

/**
 * This class is responsible for handling all UI controls
 */
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
    @FXML
    private ToggleButton executeBtn;
    @FXML
    private TextField accessToken;
    @FXML
    private TextArea console;

    protected ListProperty<String> subredditsProperty = new SimpleListProperty<>();
    protected ListProperty<String> wordsProperty = new SimpleListProperty<>();

    Thread botThread;

    /**
     * Function used by the console-side of the UI to print the current date and time in a formatted fashion
     */
    public void consoleDateAndTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        console.appendText(dtf.format(now)+"\n");
    }

    /**
     * This function is called when the "run the bots" button is clicked
     */
    public void onExecuteToggle(){
        //If the button is selected
        if(executeBtn.isSelected()){
            //Make sure that a valid token was supplied
            //If it wasn't notify the user with an alert and unselect the button
            if(!s.hasToken()){
                utilities.getInstance().makeAlert("Error", "No Token", "You did not provide a token. Please click the add to slack button to get the access token.");
                executeBtn.setSelected(false);
            }
            //If there is a valid token
            else {
                //create a new thread to run our bots on
                botThread = new Thread(new Runnable() {
                    public void run() {
                        consoleDateAndTime();
                        console.appendText("Bots starting up...\n\n");
                        runTheBots(); // code goes here.
                    }
                });
                botThread.start();
            }
        }
        //If the button is now deselected, it means that the program was running before and we want to terminate the bot thread
        else{
            consoleDateAndTime();
            console.appendText("Bots terminating...\n");
            botThread.interrupt();
        }
    }

    /**
     * Funcion that is called when the bots are told to execute
     */
    public void runTheBots(){
        /*
            Create our priority queue of bots. The priority queue determines which bot is up next to make
            the HTTP requests to reddit.
         */
        PriorityQueue<Bot> onBots = new PriorityQueue<>();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));;
        for(int i = 0; i < bots.size(); i++){
            //We only want to add the bots that are turned on to the priority queue
            if(bots.get(i).isOn()) {
                //Start time remaining will make all bots perform requests to reddit at the start
                bots.get(i).startTimeRemaining();
                //Start off by getting all posts in the past hour
                System.out.println("millis: " + (cal.getTimeInMillis()/1000-3600));
                System.out.println("Last time stamp: " + bots.get(i).getLastTimeStamp());

                bots.get(i).setLastTimeStamp(cal.getTimeInMillis()/1000-3600);
                onBots.add(bots.get(i));
            }
        }
        if(onBots.size() == 0){
            executeBtn.setSelected(false);
            console.appendText("No bots are on\n");
            Thread.currentThread().interrupt();
        }
        long timeRemaining;
        /*
            Now we are ready to get the bots running
         */
        while(executeBtn.isSelected()){ //We go until the user turns off the execute button
            timeRemaining = onBots.peek().getTimeRemaining(); //shortest time remaining will be the peek (since its a PQ)
            while(timeRemaining == 0){ //If it is 0, then it is time for this bot to make its reddit requests
                System.out.println("Time remaining: " + timeRemaining);
                Bot bot = onBots.remove(); //Bot with timeRemaining = 0 is the top
                bot.restartTimeRemaining(); //refresh its timer
                onBots.add(bot); //read it to the PQ
                timeRemaining = onBots.peek().getTimeRemaining(); //get the time remaining of the next bot in the PQ
                /*
                    REDDIT REQUEST GOES HERE
                 */
                ArrayList<String> links = r.getLinksToPosts(bot);

                int numPostsFound;
                consoleDateAndTime();
                console.appendText("Bot: " + bot.getName() + " queried reddit\n");
                numPostsFound = s.sendLinksToUsers(links, bot.getName().toString());
                console.appendText("Bot: " + bot.getName() + " found " + numPostsFound + " new posts \n\n");
                //update the last time stamp so we do not get duplicate posts
                bot.setLastTimeStamp(cal.getTimeInMillis()/1000);
            }
            try {
                System.out.println("sleeping for " + timeRemaining + " milliseconds");
                //If we get here it means that we have gotten a non-zero timeRemaining value
                Thread.sleep(timeRemaining); //sleep the bot thread
                Iterator<Bot> iter = onBots.iterator();
                Bot current;

                /*
                    Need to decrement all of the bots time remaining by how long the thread slept for
                 */
                while (iter.hasNext()) {
                    current = iter.next();
                    current.decrementTimeRemaining(timeRemaining);
                }
            } catch (InterruptedException e) {
                System.out.println("Interrupting thread");
            }
        }

        System.out.println("Stopping run...");
    }

    /**
     * function called when the slack button is clicked. Opens up the web browser.
     */
    public void slackLoginClicked(){
        try {
            s.crossPlatformOpenWebApp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function called when the add new bot button is clicked. A new bot is created only when there is text in the corresponding
     * text field. When this bot is added, it is displayed in the table
     */
    public void onBotCreate(){
        if(!newBotName.getText().isEmpty()) {
            String name = newBotName.getText().toLowerCase();
            bots.add(new Bot(name));
        }
    }

    /**
     * This function is called when the user selects a new frequency. User validation to make sure there is actually a bot
     * selected
     */
    public void setFrequency(){
        if (currentBot != null) {
            Frequency freq = (Frequency) freqBox.getValue();
            currentBot.setFrequency(freq);
        } else {
            utilities.getInstance().makeAlert("No Bot", "You did not select a bot", "Please select a bot before" +
                    " attempting to give it a frequency");
        }
    }

    /**
     * This function is called when the user clicks "add new subreddit" button
     * Validation is performed with the Reddit API to make sure that subreddit actually exists
     * Also validation to make sure a bot is selected
     */
    public void addSubreddit(){
        if(currentBot != null) {
            String subreddit = subredditTextField.getText();
            boolean exists = r.doesSubredditExist(subreddit);
            if (exists) {
                currentBot.getSubreddits().add(subreddit);
                subredditTextField.clear();
            } else {
                utilities.getInstance().makeAlert("Error", "Bad Subreddit", subreddit + " is not a valid subreddit!");
            }
            subredditTextField.requestFocus();
        }
        else{
            utilities.getInstance().makeAlert("No Bot", "You did not select a bot", "Please select a bot before" +
                    " attempting to add a subreddit");
        }
    }

    /**
     * This function is called when the user clicks "add new word" button
     * User validation to make sure that a bot is selected
     */
    public void addWord(){
        if(currentBot != null) {
            currentBot.getWords().add(wordTextField.getText());
            wordTextField.clear();
            wordTextField.requestFocus();
        }
        else{
            utilities.getInstance().makeAlert("No Bot", "You did not select a bot", "Please select a bot before" +
                    " attempting to add a word");
        }
    }

    /**
     * This function is called when the user exits the copy-past slack token textbox
     */
    public void validateUserEntry(){
        System.out.println("called");
        String token = this.accessToken.getText();
        boolean value = s.authorizeToken(token);
        if(value){
            slackVerificationLabel.setText("Verified");
            slackVerificationLabel.setStyle("-fx-text-fill: green");
            slackVerificationIcon.setFill(Paint.valueOf("#0c8c0c"));
            slackVerificationIcon.setGlyphName("CHECK_CIRCLE_ALT");
        }
    }

    /**
     * This function is used with JavaFX to initialize things with the UI describing how the controls should behave
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources){
        r = reddit.getInstance();
        nameColumn.setCellValueFactory(new PropertyValueFactory<Bot, String>("name"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<Bot, Boolean>("on"));
        statusColumn.setSortable(false);
        wordTextField.setFocusTraversable(false);
        subredditTextField.setFocusTraversable(false);
        console.setEditable(false);

        /**
         * Creates a boolean property in the status column for each bot as it is added to the table. Represented as a checkbox.
         */
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
                    freqBox.setValue(currentBot.getFrequency());
                }
                else if(! row.isEmpty() && event.getButton() == MouseButton.SECONDARY){
                    System.out.println("GOtemmmm");
                }
            });
            return row;
        });
        botTable.setItems(bots);

        //Initialize all the frequencies. This is static exhaustive list for the frequency box.
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

        /**
         * Tells the frequency box how to display a Frequency object
         */
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
