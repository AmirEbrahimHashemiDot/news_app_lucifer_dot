package com.lucifer.news.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.lucifer.news.R;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class SplashScreen extends AppCompatActivity {

    TextView logo_news_txtv;
    ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        setUpView();
        //setUpAnimation();
        setUpThread();
    }

    private void setUpAnimation() {
        logo_news_txtv.setAnimation(AnimationUtils.loadAnimation(SplashScreen.this, R.anim.fade_scale_animation_rcv_news));
    }

    private void setUpThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(2500);
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    private void setUpView() {
        logo_news_txtv = (TextView) findViewById(R.id.logo_news_txtv);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
    }
}