package com.lucifer.news.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.lucifer.news.R;
import com.squareup.picasso.Picasso;

public class NewsDetailsActivity extends AppCompatActivity {

    ProgressBar news_detail_progressbar_bar, news_detail_web_view_progress_bar;
    TextView news_detail_txt_view_desc, news_detail_txt_view_title, news_detail_txt_view_date,
            news_detail_txt_view_source;
    ImageView news_detail_news_image_view;
    CardView news_detail_cardView;
    ScrollView news_detail_scroll_view;
    WebView news_detail_web_view;

    Thread animThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        setUpView();
        getDataFromNewsAdapter();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void getDataFromNewsAdapter() {
        Intent intent = getIntent();

        String title = intent.getStringExtra("title");
        String source = intent.getStringExtra("source");
        String time = intent.getStringExtra("time");
        String desc = intent.getStringExtra("desk");
        String imageUrl = intent.getStringExtra("imageUrl");
        String url = intent.getStringExtra("url");

        news_detail_txt_view_title.setText(title);
        news_detail_txt_view_source.setText(source);
        news_detail_txt_view_date.setText(time);
        news_detail_txt_view_desc.setText(desc);

        Picasso.get().load(imageUrl).into(news_detail_news_image_view);

        news_detail_web_view.getSettings().setDomStorageEnabled(true);
        news_detail_web_view.getSettings().setJavaScriptEnabled(true);
        news_detail_web_view.getSettings().setLoadsImagesAutomatically(true);
        news_detail_web_view.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        news_detail_web_view.setWebViewClient(new WebViewClient());
        news_detail_web_view.loadUrl(url);
        if (news_detail_web_view.isShown()) {
            news_detail_web_view_progress_bar.setVisibility(View.INVISIBLE);
        }
    }

    private void setUpView() {
        news_detail_progressbar_bar = (ProgressBar) findViewById(R.id.news_detail_progressbar_bar);
        news_detail_web_view_progress_bar = (ProgressBar) findViewById(R.id.news_detail_web_view_progress_bar);
        news_detail_txt_view_desc = (TextView) findViewById(R.id.news_detail_txt_view_desc);
        news_detail_txt_view_title = (TextView) findViewById(R.id.news_detail_txt_view_title);
        news_detail_txt_view_date = (TextView) findViewById(R.id.news_detail_txt_view_date);
        news_detail_txt_view_source = (TextView) findViewById(R.id.news_detail_txt_view_source);
        news_detail_news_image_view = (ImageView) findViewById(R.id.news_detail_news_image_view);
        news_detail_cardView = (CardView) findViewById(R.id.news_detail_cardView);
        news_detail_scroll_view = (ScrollView) findViewById(R.id.news_detail_scroll_view);
        news_detail_web_view = (WebView) findViewById(R.id.news_detail_web_view);
        news_detail_web_view_progress_bar.setVisibility(View.VISIBLE);


        animThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //set Up Animation
                    news_detail_news_image_view.setAnimation(AnimationUtils.loadAnimation(NewsDetailsActivity.this, R.anim.fade_scale_animation_rcv_news));
                    news_detail_txt_view_source.setAnimation(AnimationUtils.loadAnimation(NewsDetailsActivity.this, R.anim.fade_scale_animation_rcv_news));
                    news_detail_txt_view_date.setAnimation(AnimationUtils.loadAnimation(NewsDetailsActivity.this, R.anim.fade_scale_animation_rcv_news));
                    news_detail_txt_view_title.setAnimation(AnimationUtils.loadAnimation(NewsDetailsActivity.this, R.anim.fade_scale_animation_rcv_news));
                    news_detail_txt_view_desc.setAnimation(AnimationUtils.loadAnimation(NewsDetailsActivity.this, R.anim.fade_scale_animation_rcv_news));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("E_details_anim", "Error in details animation\n " +
                            "file : NewsDetailsActivity\n Line : 92");
                }
            }
        });
        animThread.start();
    }
}