package com.lucifer.news.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lucifer.news.Activities.NewsDetailsActivity;
import com.lucifer.news.Model.Articles;
import com.lucifer.news.R;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsRcvAdapter extends RecyclerView.Adapter<NewsRcvAdapter.ViewHolder> {

    Context context;
    List<Articles> articles;
    Thread rcvItemAnimThread;
    String time = null;

    public NewsRcvAdapter(Context context, List<Articles> articles) {
        this.context = context;
        this.articles = articles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rcv_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        //set Up Animation for Rcv Items
        rcvItemAnimThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    viewHolder.news_rcv_cardView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation_rcv_news));
                    Log.i("anim_done", "Done");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("E_rcv_anim", "Error in rcv animation\n " +
                            "file : NewsRcvAdapter\n Line : 68");
                }
            }
        });
        rcvItemAnimThread.start();

        final Articles a = articles.get(position);

        Thread setValueToItemsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                viewHolder.rcv_item_date_txtview.setText("\u2022" + dateTime(a.getPublishedAt()));
            }
        });
        setValueToItemsThread.start();

        viewHolder.rcv_item_title_txtview.setText(a.getTitle());
        viewHolder.rcv_item_source_txtview.setText(a.getSource().getName());
        //viewHolder.rcv_item_date_txtview.setText("\u2022" + dateTime(a.getPublishedAt()));

        String url = a.getUrl();
        String image_url = a.getUrlToImage();
        Picasso.get().load(image_url).into(viewHolder.rcv_item_bg_imageview);
        viewHolder.news_rcv_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsDetailsActivity.class);
                intent.putExtra("title", a.getTitle());
                intent.putExtra("source", a.getSource().getName());
                intent.putExtra("time", dateTime(a.getPublishedAt()));
                intent.putExtra("desk", dateTime(a.getDescription()));
                intent.putExtra("imageUrl", a.getUrlToImage());
                intent.putExtra("url", a.getUrl());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView rcv_item_date_txtview, rcv_item_source_txtview, rcv_item_title_txtview;
        ImageView rcv_item_bg_imageview;
        ProgressBar progressBar;
        CardView news_rcv_cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rcv_item_date_txtview = (TextView) itemView.findViewById(R.id.rcv_item_date_txtview);
            rcv_item_source_txtview = (TextView) itemView.findViewById(R.id.rcv_item_source_txtview);
            rcv_item_title_txtview = (TextView) itemView.findViewById(R.id.rcv_item_title_txtview);
            rcv_item_bg_imageview = (ImageView) itemView.findViewById(R.id.rcv_item_bg_imageview);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            news_rcv_cardView = (CardView) itemView.findViewById(R.id.news_rcv_cardView);
        }
    }

    public String dateTime(String t) {

        Thread parseDateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                PrettyTime prettyTime = new PrettyTime(new Locale(getCountry()));

                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:", Locale.ENGLISH);
                    Date date = simpleDateFormat.parse(t);
                    time = prettyTime.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.i("E_L91_F_newsAdapter", "E_L91_F_newsAdapter");
                }

            }
        });
        parseDateThread.start();

        return time;
    }

    public String getCountry() {
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        return country.toLowerCase();
    }
}