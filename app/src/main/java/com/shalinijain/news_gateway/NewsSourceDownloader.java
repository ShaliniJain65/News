package com.shalinijain.news_gateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

public class NewsSourceDownloader extends AsyncTask<String, Void, String> {
    private static final String TAG = "NewsSourceDownloader";
    private MainActivity mainActivity;
   // private String new_cat;
    private ArrayList<DataofSource> sourcesData_list;
    String categoryofnews;
    private ArrayList<String> category_list;
    private static final String apiKey = "1215ac7ddfd64072a344ade0d6ea4139";
    private static final String categoryurl = "https://newsapi.org/v1/sources?language=en&country=us";

    public NewsSourceDownloader(MainActivity mainActivity, String new_category) {
        this.mainActivity = mainActivity;
        this.categoryofnews=new_category;

        if (categoryofnews.equalsIgnoreCase("ALL") || categoryofnews.equals(""))
            this.categoryofnews = "";
        else
            this.categoryofnews = categoryofnews;
    }


    @Override
    protected String doInBackground(String... strings) {
        String jsonString = "";

        Uri.Builder buildUri = Uri.parse(categoryurl).buildUpon();
        buildUri.appendQueryParameter("category", categoryofnews);
        buildUri.appendQueryParameter("key", apiKey);

        String urlToUse = buildUri.build().toString();
        Log.d(TAG, "doInBackground: Url to use: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream inpStrm = conn.getInputStream();
            BufferedReader buf_reader = new BufferedReader(new InputStreamReader(inpStrm));

            String inputline;
            while ((inputline = buf_reader.readLine()) != null)
                sb.append(inputline).append('\n');
        } catch (Exception e) {
            Log.d(TAG, "doInBackground: Exception in source");
            e.printStackTrace();
            return null;
        }

        jsonString = sb.toString();
        Log.d(TAG, "doInBackground: Json String : " + jsonString);
        return jsonString;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d(TAG, "onPostExecute: ");

        boolean isParseSuccessful = fetchJson(s);
        if (isParseSuccessful) {
            category_list = new ArrayList<>();
            for (int i = 0; i < sourcesData_list.size(); i++) {
                String cat = sourcesData_list.get(i).getSource_category();

                //--check: if this contains works?
                boolean in = category_list.contains(cat);
                //If already there then don't add
                if (!in)
                    category_list.add(cat);
                Log.d(TAG, "onPostExecute: " + category_list);
            }
            mainActivity.setSources(sourcesData_list, category_list);
        } else
            mainActivity.setSources(null, null);
    }

    private boolean fetchJson(String s) {

        if (s == null) {
            Toast.makeText(mainActivity, "No News Service", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(s.equals("")) {
            Toast.makeText(mainActivity, "No data for this category", Toast.LENGTH_SHORT).show();
            return false;
        }
        Log.d(TAG, "fetchJson: ");
        sourcesData_list = new ArrayList<>();

        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray jSourcesArray = jObjMain.getJSONArray("sources");

            for (int i = 0; i < jSourcesArray.length(); i++) {
                JSONObject jSrcObj = (JSONObject) jSourcesArray.get(i);

                String id = jSrcObj.getString("id");
                String name = jSrcObj.getString("name");
                String url = jSrcObj.getString("url");
                String category = jSrcObj.getString("category");

                DataofSource source_obj = new DataofSource();
                source_obj.setSource_id(id);
                source_obj.setSource_name(name);
                source_obj.setSource_url(url);
                source_obj.setSource_category(category);
                sourcesData_list.add(source_obj);

            }
        } catch (Exception e) {
            Log.d(TAG, "parseJSONString: Error while parsing the JSON data");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
