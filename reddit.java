package redditBotCreator;
//key = QQk0C890TbkUVA

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class is responsible for all requests to the Reddit API
 */
public class reddit {
	String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36";
	private HttpClient client = HttpClientBuilder.create().build();
	private static reddit reddit;
	private reddit(){}
	public static reddit getInstance(){
		if(reddit == null){
			reddit = new reddit();
		}
		return reddit;
	}

	/**
	 * This function checks if the subreddit exists. It is used for user validation
	 * @param subreddit
	 * @return
	 */
	public boolean doesSubredditExist(String subreddit) {
		String url = "https://api.reddit.com/api/search_reddit_names.json?query=" + subreddit + "&exact=true";
		HttpGet request = new HttpGet(url);
		request.addHeader("User-Agent", USER_AGENT);
		HttpResponse response;
		JSONObject o = null;
		try {
			response = client.execute(request);
			request.releaseConnection();
			//If we get a 200 code, we sent a valid request
			if(199 <= response.getStatusLine().getStatusCode() && response.getStatusLine().getStatusCode() <= 300)
				return true;
			else //otherwise we did not
				return false;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	/**
     * This function gets all the posts from getLinksToPosts and filters out the ones that were created before
     * the long parameter since
	 */
	public void addLinksToArrayList(ArrayList<String> linksToPosts, String url, long since){
		JSONArray allData;
		allData = makeRedditRequest(url);
		if(allData != null) {
            System.out.println("number of posts = " + allData.length());
            for (int i = 0; i < allData.length(); i++) {
                JSONObject objects = allData.getJSONObject(i).getJSONObject("data");
                //double created_utc = (double)objects.get("created_utc");
                long created_utc = (long) ((double) objects.get("created_utc"));
                if (created_utc > since) {
                    String link = (String) objects.get("url");
                    linksToPosts.add(link);
                }
            }
        }
	}

	/**
	 * This function gets all of the links to a post for a specific bot
	 * @param bot the bot getting the posts
	 * @return the links the bot fetched
	 */
    public ArrayList<String> getLinksToPosts(Bot bot){
		String baseURL = "https://www.reddit.com/r/";
		for (String subreddit: bot.getSubreddits()) {
			baseURL += subreddit + "+";
		}
		baseURL = baseURL.substring(0, baseURL.length() - 1); //get rid of last plus sign
		baseURL += "/search.json?q=title:";
		String url;
		ArrayList<String> linksToPosts = new ArrayList<>();
		for (String word: bot.getWords()){
			url = baseURL + word + "&sort=new&restrict_sr=o&include_over_18=o";
			addLinksToArrayList(linksToPosts, url, bot.getLastTimeStamp());
		}
		return linksToPosts;
	}

	public JSONArray makeRedditRequest(String url){
		HttpGet request = new HttpGet(url);
		request.addHeader("User-Agent", USER_AGENT);
		HttpResponse response;
		JSONObject o = null;
		try {
		    System.out.println(url);
			response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
            o = new JSONObject(result.toString());
            JSONArray arr;
            arr = o.getJSONObject("data").getJSONArray("children");
            request.releaseConnection();
            return arr;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
