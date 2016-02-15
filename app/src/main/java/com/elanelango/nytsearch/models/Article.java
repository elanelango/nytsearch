package com.elanelango.nytsearch.models;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eelango on 2/12/16.
 */
@Parcel
public class Article {

    public String webUrl;
    public Headline headline;

    public ArrayList<Media> multimedia;

    public Article() {
        multimedia = new ArrayList<>();
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline.main;
    }

    public String getThumbnail() {
        if (multimedia.size() > 0) {
            return "http://www.nytimes.com/" + multimedia.get(0).url;
        } else {
            return "";
        }
    }
}
