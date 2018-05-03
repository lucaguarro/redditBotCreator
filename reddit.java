package redditBotCreator;
//key = QQk0C890TbkUVA

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;


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
	public boolean doesSubredditExist(String subreddit) {
		String url = "https://api.reddit.com/api/search_reddit_names.json?query=" + subreddit + "&exact=true";
		HttpGet request = new HttpGet(url);
		request.addHeader("User-Agent", USER_AGENT);
		HttpResponse response;
		JSONObject o = null;
		try {
			response = client.execute(request);
			request.releaseConnection();
			if(199 <= response.getStatusLine().getStatusCode() && response.getStatusLine().getStatusCode() <= 300)
				return true;
			else
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
	public JSONObject redditTest() {
		String url = "https://www.reddit.com/r/dogs/search.json?q=title:dog&sort=new&restrict_sr=o";
		HttpGet request = new HttpGet(url);
		request.addHeader("User-Agent", USER_AGENT);
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

			o = new JSONObject(result.toString());

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;
	}
	/*
	public void testingHttpClient() {
		String url = "https://api.github.com/users/lucaguarro";
		HttpGet request = new HttpGet(url);
		request.addHeader("User-Agent", USER_AGENT);
		HttpResponse response;
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

			JSONObject o = new JSONObject(result.toString());
			System.out.println(o.toString());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
}
