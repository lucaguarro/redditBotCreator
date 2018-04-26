package redditBotCreator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.json.JSONObject;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;

public class Controller implements Initializable {
    private static reddit r = new reddit();
    private static Slack s = new Slack();

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
        for(int i = 0; i < bots.size(); i++){
            System.out.println("Name: " + bots.get(i).getName());
            System.out.println("Is on? " + bots.get(i).isOn() + "\n");
        }
    }

    public void onBotCreate(){
        String name = newBotName.getText();
        bots.add(new Bot(name));
    }

    public void testRedditRequest(){
        System.out.println("Making reddit request");
        JSONObject data;
        data = r.redditTest();
        data = data.getJSONObject("data");
        System.out.println(data.toString());
    }


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

    public void initialize(URL location, ResourceBundle resources){
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

        botTable.setItems(bots);
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
            checkbox.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                }
            });
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
