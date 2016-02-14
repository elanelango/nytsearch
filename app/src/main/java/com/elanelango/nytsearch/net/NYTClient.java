package com.elanelango.nytsearch.net;

import android.content.Context;
import android.util.Log;

import com.elanelango.nytsearch.R;
import com.elanelango.nytsearch.models.Article;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by eelango on 2/13/16.
 */

public class NYTClient {

    public interface ArticlesHandler {
        void onNewArticles(ArrayList<Article> articles);
    }

    private static final String SEARCH_URL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    private AsyncHttpClient client;
    private static String clientKey;
    private ArrayList<Article> resArticles = new ArrayList<>();

    public NYTClient(Context context) {
        this.client = new AsyncHttpClient();
        clientKey = context.getString(R.string.client_key);
    }

    // Method for accessing the search API
    public void getArticles(String query, int page, final NYTClient.ArticlesHandler articleHandler) {
        RequestParams params = new RequestParams();
        params.put("api-key", clientKey);
        params.put("page", page);
        params.put("q", query);

        resArticles.clear();
        client.get(SEARCH_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJSONResults = null;
                try {
                    articleJSONResults = response.getJSONObject("response").getJSONArray("docs");
                    articleHandler.onNewArticles(Article.fromJSONArray(articleJSONResults));
                    Log.d("DEBUG", resArticles.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
