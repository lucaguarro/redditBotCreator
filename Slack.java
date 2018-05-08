package redditBotCreator;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Slack {
    String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36";
    private HttpClient client = HttpClientBuilder.create().build();
    String token;
    void setToken(String token){
        this.token = token;
    }
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
        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * This function joins a slack channel. If the slack channel doesn't exist, it will create it.
     * @param slackChannel the name of the slack channel to join
     */
    String joinSlackChannel(String slackChannel){
        String url = "https://slack.com/api/channels.join?token=" + token + "&name=" + slackChannel;
        HttpPost request = new HttpPost(url);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        HttpResponse response;
        JSONObject o = null;
        try {
            response = client.execute(request);
            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());
            o = new JSONObject(result.toString());
            request.releaseConnection();
            boolean isOkay = (boolean)o.get("ok");
            if(!isOkay){
                String error = (String)o.get("error");
                if(error.equals("is_archived")){
                    System.out.println("ARCHIVED");
                    return "ARCHIVED";
                }
            }
            return (String)o.getJSONObject("channel").get("id");
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "ERROR";
    }
    /**
     * This function attemps to unarchive a slack channel. The slack channel name is provided by the slackChannel parameter
     * If the slackChannel was indeed unarchived, the function returns true, otherwise it returns false.
     * This is necessary because slack does not delete channels, only archives them. This would cause errors if a user attempts
     * to create a slack channel with the same name as an archived channel.
     */
    void unarchiveChannel(String slackChannel){
        String channelID = getChannelID(slackChannel);
        String url = "https://slack.com/api/channels.unarchive?token=" + token + "&channel=" + channelID;
        HttpPost request = new HttpPost(url);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        HttpResponse response;
        JSONObject o = null;
        try {
            response = client.execute(request);
            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());
            o = new JSONObject(result.toString());
            request.releaseConnection();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
}*/
    void sendLinksToUsers(ArrayList<String> links, String slackChannel){
        String channelID = joinSlackChannel(slackChannel);
        if(channelID.equals("ARCHIVED")){
            System.out.println("Unarchiving...");
            channelID = slackChannel;
        }

        String baseURL = "https://slack.com/api/chat.postMessage?token=" + token + "&channel=" + slackChannel + "&text=";
        URI url;
        //URI url;
        /*for(int i = 0; i < links.size(); i++) {
            fullURL += links.get(i) + "\n";
        }*/
        HttpPost request = new HttpPost();
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        HttpResponse response;
        JSONObject o = null;
        for(int i = 0; i < links.size(); i++) {
            url = URI.create(baseURL + links.get(i));
            request.setURI(url);

            try {
                response = client.execute(request);
                request.releaseConnection();
                System.out.println("Response Code : "
                        + response.getStatusLine().getStatusCode());
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                System.out.println(result.toString());
                o = new JSONObject(result.toString());
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    String getChannelID(String checkChannel){
        String url = "https://slack.com/api/channels.list?token=" + token;
        HttpGet request = new HttpGet(url);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        //request.addHeader("User-Agent", USER_AGENT);
        HttpResponse response;
        JSONObject o = null;
        try {
            response = client.execute(request);
            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());
            o = new JSONObject(result.toString());
            JSONArray channels;
            channels = o.getJSONArray("channels");
            for (int i = 0; i < channels.length(); i++) {
                String channelName = (String)channels.getJSONObject(i).get("name");
                if(channelName.equals(checkChannel)){
                    System.out.println("ID fam " + (String)channels.getJSONObject(i).get("id"));
                    return (String)channels.getJSONObject(i).get("id");
                }
            }
            request.releaseConnection();
            return "NOTFOUND";
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "ERROR";
    }
    boolean authorizeToken(String token){
        String url = "https://slack.com/api/auth.test?token=" + token;
        HttpPost request = new HttpPost(url);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        HttpResponse response;
        JSONObject o = null;
        try {
            response = client.execute(request);
            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());
            o = new JSONObject(result.toString());
            request.releaseConnection();
            boolean validAuth = (boolean)(o.get("ok"));
            if(validAuth){
                this.setToken(token);
                System.out.println("Wasvalid");
            }else{
                System.out.println("not valid");
            }
            return validAuth;
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    boolean hasToken(){
        return this.token != null;
    }
}