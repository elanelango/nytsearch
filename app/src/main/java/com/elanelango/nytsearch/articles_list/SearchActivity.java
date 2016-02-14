package com.elanelango.nytsearch.articles_list;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.EditText;

import com.elanelango.nytsearch.R;
import com.elanelango.nytsearch.models.Article;
import com.elanelango.nytsearch.net.NYTClient;
import com.elanelango.nytsearch.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity implements NYTClient.ArticlesHandler {
    @Bind(R.id.etQuery)
    EditText etQuery;

    @Bind(R.id.rvArticles)
    RecyclerView rvArticles;

    ArrayList<Article> articles;
    ArticleAdapter adapter;

    NYTClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        client = new NYTClient(this);
        articles = new ArrayList<>();
        adapter = new ArticleAdapter(articles);

        //Setup the recycler view
        rvArticles.setAdapter(adapter);
        rvArticles.setHasFixedSize(true);
        rvArticles.addItemDecoration(new ItemSpaceDecoration(10));
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvArticles.setLayoutManager(gridLayoutManager);

        rvArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                String query = etQuery.getText().toString();
                client.getArticles(query, page, SearchActivity.this);
            }
        });
    }

    @OnClick(R.id.btnSearch)
    public void onArticleSearch(View view) {
        String query = etQuery.getText().toString();
        client.getArticles(query, 0, SearchActivity.this);
    }

    @Override
    public void onNewArticles(ArrayList<Article> articles) {
        adapter.addAll(articles);
    }
}
