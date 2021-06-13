package com.lucifer.news.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.lucifer.news.API.ApiClient;
import com.lucifer.news.Adapter.NewsRcvAdapter;
import com.lucifer.news.Model.Articles;
import com.lucifer.news.Model.Headlines;
import com.lucifer.news.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    TextView news_header_logo;
    RecyclerView recyclerview_home;
    ProgressBar news_progressbar;
    SwipeRefreshLayout news_swipe_fresh_rcv;
    EditText news_search_edt_text;
    Button btnSearch;
    GridLayout search_parent;
    NewsRcvAdapter news_adapter;
    List<Articles> articlesList = new ArrayList<>();
    final String NEWS_API_KEY = "27a99facfdf64d2795f5b593249bcb08";
    public static String country;
    public static String error_net_is_off = "Unable to resolve host \"newsapi.org\": No address associated with hostname";

    Thread search_bar_anim_thread, request_to_news_list_api_thread;
    Handler search_bar_anim_handler, request_to_news_list_api_handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpViews();
        setUpLogoAnimation();
        onSearchButtonClicked();
        country = getCountry();
        setUpSwipeRefresh();
        retrieveJson("", country, NEWS_API_KEY);
    }

    private void setUpLogoAnimation() {
        search_bar_anim_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                search_bar_anim_handler.post(new Runnable() {
                    @Override
                    public void run() {
                        YoYo.with(Techniques.Shake)
                                .duration(1500)
                                .repeat(1)
                                .playOn(findViewById(R.id.search_parent));
                    }
                });
            }
        });
        search_bar_anim_thread.start();
    }

    private void onSearchButtonClicked() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Search
                if (!news_search_edt_text.getText().toString().equals("")) {
                    setUpSwipeRefresh();
                    retrieveJson(news_search_edt_text.getText().toString(), country, NEWS_API_KEY);
                } else {
                    setUpSwipeRefresh();
                    retrieveJson("", country, NEWS_API_KEY);
                }
            }
        });
    }

    private void setUpSwipeRefresh() {
        news_swipe_fresh_rcv.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (news_search_edt_text.getText().toString().equals("")) {
                    retrieveJson("", country, NEWS_API_KEY);
                    news_search_edt_text.setText("");
                } else if (!news_search_edt_text.getText().toString().equals("")) {
                    retrieveJson(news_search_edt_text.getText().toString(), country, NEWS_API_KEY);
                }
            }
        });
    }

    private void setUpViews() {
        request_to_news_list_api_handler = new Handler();
        search_bar_anim_handler = new Handler();
        search_parent = (GridLayout) findViewById(R.id.search_parent);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        recyclerview_home = (RecyclerView) findViewById(R.id.recyclerview_home);
        news_swipe_fresh_rcv = (SwipeRefreshLayout) findViewById(R.id.news_swipe_fresh_rcv);
        recyclerview_home.setLayoutManager(new LinearLayoutManager(this));
        news_progressbar = (ProgressBar) findViewById(R.id.news_progressbar);
        news_progressbar.setVisibility(View.VISIBLE);
        news_search_edt_text = (EditText) findViewById(R.id.news_search_edt_text);
        //Hide Automatically Open up keyboard in s7 edge
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public String getCountry() {
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        return country.toLowerCase();
    }

    public void retrieveJson(String query, String country, String apiKey) {

        request_to_news_list_api_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                request_to_news_list_api_handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Do

                        //news_swipe_fresh_rcv.setRefreshing(true);
                        Call<Headlines> call;
                        if (!news_search_edt_text.getText().toString().equals("")) {
                            call = ApiClient.getInstance().getApi().getSpecificData(query, apiKey);
                        } else {
                            call = ApiClient.getInstance().getApi().getHeadlines(country, apiKey);
                        }
                        call.enqueue(new Callback<Headlines>() {
                            @Override
                            public void onResponse(Call<Headlines> call, Response<Headlines> response) {
                                if (response.isSuccessful() && response.body().getArticles() != null) {
                                    news_progressbar.setVisibility(View.INVISIBLE);
                                    news_swipe_fresh_rcv.setRefreshing(false);
                                    articlesList.clear();
                                    articlesList = response.body().getArticles();
                                    news_adapter = new NewsRcvAdapter(MainActivity.this, articlesList);
                                    recyclerview_home.setAdapter(news_adapter);
                                } else if (!response.isSuccessful() || response.body().getArticles() == null) {
                                    Thread delayThread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                sleep(15000);
                                                //retrieveJson(country, NEWS_API_KEY);
                                                //retrieveJson("", country, NEWS_API_KEY);
                                                retrieveJson(news_search_edt_text.getText().toString(), country, NEWS_API_KEY);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    delayThread.start();
                                    Toast.makeText(MainActivity.this, "Reloading...", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onFailure(Call<Headlines> call, Throwable t) {
                                //Toast.makeText(MainActivity.this, "E > MainActivity L66 \n " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(MainActivity.this, "Failed to load :(", Toast.LENGTH_SHORT).show();
                                Log.i("MainActL66", "Error : " + t.getLocalizedMessage());
                                Log.i("MainActL66", "Error : " + t.getMessage());

                                Thread delayThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            sleep(15000);
                                            //retrieveJson(country, NEWS_API_KEY);
                                            //retrieveJson("", country, NEWS_API_KEY);
                                            retrieveJson(news_search_edt_text.getText().toString(), country, NEWS_API_KEY);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                delayThread.start();
                                Toast.makeText(MainActivity.this, "Reloading...", Toast.LENGTH_SHORT).show();
                                //news_swipe_fresh_rcv.setRefreshing(false);
                            }
                        });
                    }
                });
            }
        });
        request_to_news_list_api_thread.start();
    }
}