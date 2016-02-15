package com.elanelango.nytsearch.articles_list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.elanelango.nytsearch.R;
import com.elanelango.nytsearch.article_page.ArticleActivity;
import com.elanelango.nytsearch.models.Article;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by eelango on 2/12/16.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private List<Article> articles;


    //ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.ivThumbnail)
        ImageView ivThumbnail;

        @Bind(R.id.tvHeadline)
        TextView tvHeadline;

        Context context;
        Article article;

        public ViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        public void setArticle(Article article) {
            this.article = article;
            tvHeadline.setText(article.getHeadline());
            String thumbnail = article.getThumbnail();
            if (!TextUtils.isEmpty(thumbnail)) {
                Glide.with((SearchActivity)context)
                        .load(thumbnail)
                        .into(ivThumbnail);
            } else {
                ivThumbnail.setImageResource(0);
            }
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, ArticleActivity.class);
            i.putExtra("article", Parcels.wrap(article));
            context.startActivity(i);
        }
    }

    public ArticleAdapter(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View articleView = layoutInflater.inflate(R.layout.item_article, parent, false);

        ViewHolder viewHolder = new ViewHolder(context, articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.setArticle(article);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void addAll(ArrayList<Article> newArticles) {
        for (Article article : newArticles) {
            articles.add(article);
            notifyItemInserted(articles.size() - 1);
        }
    }

    public void clear() {
        int size = articles.size();
        articles.clear();
        notifyItemRangeRemoved(0, size);
    }
}
