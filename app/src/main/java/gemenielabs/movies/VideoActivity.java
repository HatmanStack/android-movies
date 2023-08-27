package gemenielabs.movies;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import java.net.URL;

public class VideoActivity extends MainActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);
        WebView video = (WebView) findViewById(R.id.webview);
        Intent intent = getIntent();
        String key = intent.getStringExtra("KEY");
        String url = "https://www.youtube.com/embed/" + key + "?API_key=" + getString(R.string.google_youtube_api_key);
        video.loadUrl(url);
    }
}
