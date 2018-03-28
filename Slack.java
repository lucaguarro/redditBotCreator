package redditBotCreator;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
public class Slack {

    void openWebxdg(){
        String url = "http://www.redditbotcreator.byethost6.com/apps/myapp/install.php";
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("xdg-open " + url);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    void openWeb2() {
        String url = "http://www.redditbotcreator.byethost6.com/apps/myapp/install.php";
        boolean websiteLaunched = false;
        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
                websiteLaunched = true;
            } catch (IOException | URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
                websiteLaunched = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if(websiteLaunched){
            waitForClipboard();
        }
    }
    void waitForClipboard(){
        
    }
    void openWeb() {
        if (Desktop.isDesktopSupported()) {
            //Desktop.getDesktop().browse(new URI("http://www.example.com"));
        }
    }
}
