package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.time.Duration;
import java.util.EventListener;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    WebView mWebView;

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);//设置与Js交互的权限
        //设置为ChromeClinet 才能执行js代码
        WebChromeClient webChromeClient = new WebChromeClient();
        mWebView.setWebChromeClient(webChromeClient);
        // 加载JS代码
        mWebView.loadUrl("file:///android_asset/java_js.html");
        mWebView.requestFocus();
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);

        button1.setOnClickListener(v -> mWebView.evaluateJavascript("java_js:button1.onfocus()", value -> Toast.makeText(MainActivity.this, "chromium1: ",Toast.LENGTH_SHORT)
                .show()));

        button2.setOnClickListener(v -> mWebView.evaluateJavascript("java_js:button2.onfocus()", value -> Toast.makeText(MainActivity.this, "chromium2: ",Toast.LENGTH_SHORT)
                .show()));

        button3.setOnClickListener(v -> mWebView.evaluateJavascript("java_js:button3.onfocus()", value -> Toast.makeText(MainActivity.this, "chromium3: ",Toast.LENGTH_SHORT)
                .show()));
    }


}