package com.shalinijain.news_gateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Admin on 23-04-2018.
 */

public class NewsArticleDownloader extends AsyncTask<String, Void , String> {

    private static final String TAG = "NewsArticleDownloader";
    private static final String articleURL = "https://newsapi.org/v1/articles"; //?";
    private static final String apiKey = "1215ac7ddfd64072a344ade0d6ea4139";
    NewsService newsService;
    String source_name;
    private ArrayList<DataofArticle> datalistofarticle;

    public NewsArticleDownloader(NewsService newsService, String source) {
        this.newsService = newsService;
        this.source_name = source;
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: ");

        String jsonString = "";

        Uri.Builder buildUri = Uri.parse(articleURL).buildUpon();
        buildUri.appendQueryParameter("source", source_name);
        buildUri.appendQueryParameter("key", apiKey);

        String urlToUse = buildUri.build().toString();
        Log.d(TAG, "doInBackground: Generated url is: "+urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            InputStream inpStrm = conn.getInputStream();
            BufferedReader buf_reader = new BufferedReader(new InputStreamReader(inpStrm));

            String inputline;
            while((inputline = buf_reader.readLine()) != null)
                sb.append(inputline).append('\n');
        }
        catch (Exception e) {
            Log.d(TAG, "doInBackground: Exception while fetching the article data");
            e.printStackTrace();
            return null;
        }

        jsonString = sb.toString();
        Log.d(TAG, "doInBackground: The json string:"+ jsonString);
        return jsonString;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d(TAG, "onPostExecute: ");

        parseJSONString(s);

        newsService.setArticles(datalistofarticle);
    }


    private ArrayList<DataofArticle> parseJSONString(String s) {
        if(s.equals("") || s == null) {
        }
        Log.d(TAG, "parseJSONString: ");
        datalistofarticle = new ArrayList<>();

        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray jarticlesArray = jObjMain.getJSONArray("articles");

            for(int i=0; i<jarticlesArray.length(); i++)
            {
                JSONObject jArtObj = (JSONObject) jarticlesArray.get(i);

                String author = jArtObj.getString("author");
                String title = jArtObj.getString("title");
                String urlToImage = jArtObj.getString("urlToImage");
                String publishedAt = jArtObj.getString("publishedAt");
                String web_url = jArtObj.getString("url");

                DataofArticle article_obj = new DataofArticle();
                article_obj.setAuthor(author);
                article_obj.setTitle(title);
                article_obj.setUrlToimage(urlToImage);
                article_obj.setPublisheddate(publishedAt);
                article_obj.setWeburl(web_url);

                datalistofarticle.add(article_obj);
            }
            return datalistofarticle;

        }
        catch (Exception e)
        {
            Log.d(TAG, "parseJSONString: Error while parsing the JSON data");
            e.printStackTrace();
        }
        return null;
    }
}
