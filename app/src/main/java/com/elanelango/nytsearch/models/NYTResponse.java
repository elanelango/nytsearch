package com.elanelango.nytsearch.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eelango on 2/14/16.
 */
public class NYTResponse {

    @SerializedName("docs")
    public ArrayList<Article> articles;

    public NYTResponse() {
        articles = new ArrayList<>();
    }
}
