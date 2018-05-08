package redditBotCreator;

import javafx.scene.control.Alert;

public class utilities {
    private static utilities ourInstance = new utilities();

    public static utilities getInstance() {
        return ourInstance;
    }

    private utilities() {
    }

    public void makeAlert(String title, String headerText, String contentText){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
