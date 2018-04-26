package redditBotCreator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;
import org.json.JSONObject;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;

public class Controller implements Initializable {
    private static reddit r = new reddit();
    private static Slack s = new Slack();
    ObservableList<Bot> bots = FXCollections.observableArrayList();
    //ArrayList<Bot> bots = new ArrayList<Bot>();

    @FXML
    private Label slackVerificationLabel;
    @FXML
    private FontAwesomeIconView slackVerificationIcon;
    @FXML
    private TextField newBotName;
    @FXML
    private TableView<Bot> botTable;
    @FXML
    private TableColumn<Bot, String> nameColumn;
    @FXML
    private TableColumn<Bot, CheckBox> statusColumn;

    public void initialize(URL location, ResourceBundle resources){
        nameColumn.setCellValueFactory(new PropertyValueFactory<Bot, String>("name"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<Bot, CheckBox>("on"));
        botTable.setItems(bots);
    }
    /*public ObservableList<Bot> getBots(){
        ObservableList<Bot> bots = FXCollections.observableArrayList();
        b
        return bots;
    }*/
    public void slackLoginClicked(){
        s.openWeb2();
        slackVerificationLabel.setText("Verified");
        slackVerificationLabel.setStyle("-fx-text-fill: green");
        slackVerificationIcon.setFill(Paint.valueOf("#0c8c0c"));
        //slackVerificationIcon.setGlyphStyle("CHECK_CIRCLE_ALT");
        slackVerificationIcon.setGlyphName("CHECK_CIRCLE_ALT");
    }

    public void onBotCreate(){
        String name = newBotName.getText();
        bots.add(new Bot(name));
        //Bot bot = new Bot(name);
        //bots.add(bot);
        //addBotToTable(bot);
    }

    public void testRedditRequest(){
        System.out.println("Making reddit request");
        JSONObject data;
        data = r.redditTest();
        data = data.getJSONObject("data");
        System.out.println(data.toString());
    }

}
