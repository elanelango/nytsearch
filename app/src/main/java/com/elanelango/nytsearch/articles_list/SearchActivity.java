package com.elanelango.nytsearch.articles_list;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.elanelango.nytsearch.R;
import com.elanelango.nytsearch.models.Article;
import com.elanelango.nytsearch.net.NYTClient;
import com.elanelango.nytsearch.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity implements NYTClient.ArticlesHandler {

    public enum SortOrder {
        RELEVANT("RELEVANT", 0),
        NEWEST("NEWEST", 1),
        OLDEST("OLDEST", 2);

        private String strValue;
        private int index;

        SortOrder(String value, int i) {
            strValue = value;
            index = i;
        }

        @Override
        public String toString() {
            return strValue;
        }
    }

    public enum NewsDesk {
        ARTS("Arts"),
        FASHION("Fashion & Style"),
        SPORTS("Sports");

        private String strValue;
        NewsDesk(String value) {
            strValue = value;
        }

        @Override
        public String toString() {
            return strValue;
        }
    }

    public static Calendar OLDEST_DATE = Calendar.getInstance();

    @Bind(R.id.rvArticles)
    RecyclerView rvArticles;

    ArrayList<Article> articles;
    ArticleAdapter adapter;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tvBeginDate)
    TextView tvBeginDate;

    @Bind(R.id.tvSortOrder)
    TextView tvSortOrder;

    NYTClient client;

    String searchQuery = "";
    Calendar beginDate = Calendar.getInstance();
    SortOrder sortOrder = SortOrder.RELEVANT;
    HashMap<NewsDesk, Boolean> newsDesk = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        client = new NYTClient(this);
        articles = new ArrayList<>();
        adapter = new ArticleAdapter(articles);

        OLDEST_DATE.set(1851, Calendar.SEPTEMBER, 18, 0, 0, 0);
        setBeginDate(1851, Calendar.SEPTEMBER, 18);
        setNewsDesk(NewsDesk.ARTS, false);
        setNewsDesk(NewsDesk.FASHION, false);
        setNewsDesk(NewsDesk.SPORTS, false);

        //Setup the recycler view
        rvArticles.setAdapter(adapter);
        rvArticles.setHasFixedSize(true);
        rvArticles.addItemDecoration(new ItemSpaceDecoration(10));
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvArticles.setLayoutManager(gridLayoutManager);

        rvArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                client.getArticles(searchQuery, page, beginDate, sortOrder, newsDesk, SearchActivity.this);
            }
        });

        //refreshArticles();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_articles_list, menu);

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

                toolbar.setTitle(query);
                refreshArticles();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void refreshArticles() {
        adapter.clear();
        client.getArticles(searchQuery, 0, beginDate, sortOrder, newsDesk, SearchActivity.this);
    }

    @Override
    public void onNewArticles(ArrayList<Article> articles) {
        adapter.addAll(articles);
    }

    @OnClick(R.id.filterDate)
    public void pickDate(LinearLayout layout) {
        int beginYear = beginDate.get(Calendar.YEAR);
        int beginMonth = beginDate.get(Calendar.MONTH);
        int beginDay = beginDate.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                setBeginDate(year, monthOfYear, dayOfMonth);
                refreshArticles();
            }
        }, beginYear, beginMonth, beginDay);
        dateDialog.getDatePicker().setMinDate(OLDEST_DATE.getTime().getTime());
        dateDialog.show();
    }

    private void setBeginDate(int year, int month, int day) {
        beginDate.set(year, month, day, 0, 0, 0);
        tvBeginDate.setText(String.format("%d/%d/%d", month + 1, day, year));
    }

    @OnClick(R.id.filterSort)
    public void pickSort(LinearLayout view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.menu_sort_popup, popup.getMenu());
        popup.getMenu().getItem(sortOrder.index).setChecked(true);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mnuRelevant:
                        setSortOrder(SortOrder.RELEVANT);
                        break;
                    case R.id.mnuNewest:
                        setSortOrder(SortOrder.NEWEST);
                        break;
                    case R.id.mnuOldest:
                        setSortOrder(SortOrder.OLDEST);
                        break;

                }
                refreshArticles();
                return true;
            }
        });

        popup.show();
    }

    private void setSortOrder(SortOrder order) {
        sortOrder = order;
        tvSortOrder.setText(order.toString());
    }

    @OnClick(R.id.filterNewsDesk)
    public void pickNewsDesk(LinearLayout view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.menu_news_desk, popup.getMenu());
        popup.getMenu().getItem(0).setChecked(newsDesk.get(NewsDesk.ARTS));
        popup.getMenu().getItem(1).setChecked(newsDesk.get(NewsDesk.FASHION));
        popup.getMenu().getItem(2).setChecked(newsDesk.get(NewsDesk.SPORTS));

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mnuArts:
                        setNewsDesk(NewsDesk.ARTS, !newsDesk.get(NewsDesk.ARTS));
                        break;
                    case R.id.mnuFashion:
                        setNewsDesk(NewsDesk.FASHION, !newsDesk.get(NewsDesk.FASHION));
                        break;
                    case R.id.mnuSports:
                        setNewsDesk(NewsDesk.SPORTS, !newsDesk.get(NewsDesk.SPORTS));
                        break;
                }
                refreshArticles();
                return true;
            }
        });
        popup.show();
    }

    private void setNewsDesk(NewsDesk key, boolean value) {
        newsDesk.put(key, value);
    }
}
