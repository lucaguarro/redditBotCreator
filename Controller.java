package redditBotCreator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.paint.Paint;
import org.json.JSONObject;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;//ab2525

public class Controller implements Initializable {
    private static reddit r = new reddit();
    private static Slack s = new Slack();

    @FXML
    private Label slackVerificationLabel;
    @FXML
    private FontAwesomeIconView slackVerificationIcon;

    public void initialize(URL location, ResourceBundle resources){
        System.out.println("Loading user data...");
    }
    public void slackLoginClicked(){
        s.openWeb2();
        slackVerificationLabel.setText("Verified");
        slackVerificationLabel.setStyle("-fx-text-fill: green");
        slackVerificationIcon.setFill(Paint.valueOf("#0c8c0c"));
        //slackVerificationIcon.setGlyphStyle("CHECK_CIRCLE_ALT");
        slackVerificationIcon.setGlyphName("CHECK_CIRCLE_ALT");
    }

    public void testRedditRequest(){
        System.out.println("Making reddit request");
        JSONObject data;
        data = r.redditTest();
        data = data.getJSONObject("data");
        System.out.println(data.toString());
    }

}
