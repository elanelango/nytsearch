package com.elanelango.nytsearch.articles_list;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
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

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by eelango on 2/12/16.
 */
public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Article> articles;

    public enum ArticleType {
        IMAGE(0),
        TEXT(1);

        private int val;

        ArticleType(int v) {
            val = v;
        }
    }

    //ImageViewHolder
    public static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.ivThumbnail)
        ImageView ivThumbnail;

        @Bind(R.id.tvHeadline)
        TextView tvHeadline;

        Context context;
        Article article;

        public ImageViewHolder(Context context, View itemView) {
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
                Glide.with((SearchActivity) context)
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

    public static class TextViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.tvHeadline)
        TextView tvHeadline;

        @Bind(R.id.tvSnippet)
        TextView tvSnippet;

        Context context;
        Article article;

        public TextViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        public void setArticle(Article article) {
            this.article = article;
            tvHeadline.setText(article.getHeadline());
            tvSnippet.setText(article.getSnippet());
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
    public int getItemViewType(int position) {
        Article article = articles.get(position);
        if (article.hasThumbnail())
            return ArticleType.IMAGE.val;
        else
            return ArticleType.TEXT.val;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        int resource = 0;
        if (viewType == ArticleType.IMAGE.val)
            resource = R.layout.item_image_article;
        else
            resource = R.layout.item_text_article;

        RecyclerView.ViewHolder viewHolder;
        View articleView = layoutInflater.inflate(resource, parent, false);
        if (viewType == ArticleType.IMAGE.val) {
            viewHolder = new ImageViewHolder(context, articleView);
        } else {
            viewHolder = new TextViewHolder(context, articleView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Article article = articles.get(position);
        int viewType = holder.getItemViewType();
        if (viewType == ArticleType.IMAGE.val) {
            ImageViewHolder vholder = (ImageViewHolder) holder;
            vholder.setArticle(article);
        } else {
            TextViewHolder vholder = (TextViewHolder)holder;
            vholder.setArticle(article);
        }
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
