package redditBotCreator;
	
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.json.JSONObject;
//import redditBotCreator.slack;
//import redditBotCreator.reddit;


public class Main extends Application {
	private static reddit r = new reddit();
	private static Slack s = new Slack();
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        Button btn = new Button();
        btn.setText("Test Reddit Request");
        btn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                    System.out.println("Making reddit request");
                    JSONObject data;
                    data = r.redditTest();
                    data = data.getJSONObject("data");
                    System.out.println(data.toString());
            }
        });

        Button slackBtn = new Button();
        slackBtn.setText("Login to Slack");
        slackBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                s.openWebxdg();
            }
        });

        BorderPane root = new BorderPane();
        root.setLeft(btn);
        root.setRight(slackBtn);
        /*StackPane root = new StackPane();
        root.getChildren().add(slackBtn);
        root.getChildren().add(btn);*/
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }
}
