package com.elanelango.nytsearch.articles_list;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.elanelango.nytsearch.R;
import com.elanelango.nytsearch.models.Article;
import com.elanelango.nytsearch.net.NYTClient;
import com.elanelango.nytsearch.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity implements NYTClient.ArticlesHandler {

    @Bind(R.id.rvArticles)
    RecyclerView rvArticles;

    ArrayList<Article> articles;
    ArticleAdapter adapter;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    NYTClient client;

    String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

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
                client.getArticles(searchQuery, page, SearchActivity.this);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchQuery = query;
                searchView.clearFocus();
                searchItem.collapseActionView();
                invalidateOptionsMenu();
                toolbar.setTitle(query);
                adapter.clear();
                client.getArticles(searchQuery, 0, SearchActivity.this);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNewArticles(ArrayList<Article> articles) {
        adapter.addAll(articles);
    }

    @OnClick(R.id.filterDate)
    public void pickDate(LinearLayout layout) {

    }
}
