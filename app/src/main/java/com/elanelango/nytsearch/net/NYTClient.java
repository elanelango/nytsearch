package com.elanelango.nytsearch.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.elanelango.nytsearch.R;
import com.elanelango.nytsearch.articles_list.SearchActivity;
import com.elanelango.nytsearch.models.Article;
import com.elanelango.nytsearch.models.NYTResponse;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
    private Context context;
    Gson gson;

    public NYTClient(Context context) {
        this.context = context;
        this.client = new AsyncHttpClient();
        clientKey = context.getString(R.string.client_key);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gson = gsonBuilder.create();

    }

    // Method for accessing the search API
    public void getArticles(String query, int page, Calendar begin_date,
                            SearchActivity.SortOrder order,
                            HashMap<SearchActivity.NewsDesk, Boolean> newsDeskMap,
                            final NYTClient.ArticlesHandler articleHandler) {
        RequestParams params = new RequestParams();
        params.put("api-key", clientKey);
        params.put("page", page);
        params.put("q", query);

        if (!begin_date.equals(SearchActivity.OLDEST_DATE)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String beginStr = dateFormat.format(begin_date.getTime());
            params.put("begin_date", beginStr);
        }

        if (order != SearchActivity.SortOrder.RELEVANT)
            if (order == SearchActivity.SortOrder.NEWEST)
                params.put("sort", "newest");
            else
                params.put("sort", "oldest");

        String newsDeskValues = "";
        for (Map.Entry<SearchActivity.NewsDesk, Boolean> entry : newsDeskMap.entrySet()) {
            SearchActivity.NewsDesk newsDeskKey = entry.getKey();
            boolean value = entry.getValue();
            if (value) {
                newsDeskValues += '"' + newsDeskKey.toString() + "\" ";
            }
        }
        if (!TextUtils.isEmpty(newsDeskValues)) {
            newsDeskValues = "news_desk:(" + newsDeskValues + ")";
            params.put("fq", newsDeskValues);
        }

        Log.d("DEBUG", params.toString());

        if (!isNetworkAvailable() || !isOnline()) {
            Toast.makeText(context, "Cannot retrieve articles. Please check your Internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        client.get(SEARCH_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject responseJSON = response.getJSONObject("response");
                    NYTResponse nytResponse = gson.fromJson(responseJSON.toString(), NYTResponse.class);
                    articleHandler.onNewArticles(nytResponse.articles);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(context, "Unexpected issue while retrieving articles. Please try again later.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(context, "Unexpected issue while retrieving articles. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

}
