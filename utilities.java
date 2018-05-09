package redditBotCreator;

import javafx.scene.control.Alert;

/**
 * Singleton class that performs functions that need to be used in all parts of the software
 */
public class utilities {
    private static utilities ourInstance = new utilities();

    public static utilities getInstance() {
        return ourInstance;
    }

    private utilities() {
    }

    /**
     * Creates an alert with the string parameters passed into it
     */
    public void makeAlert(String title, String headerText, String contentText){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
