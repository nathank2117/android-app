package com.appisoft.perkz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;

import com.google.android.gms.net.CronetProviderInstaller;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        /*
        CronetEngine.Builder myBuilder = new CronetEngine.Builder(context);
        CronetEngine cronetEngine = myBuilder.build();
        */
        setContentView(R.layout.activity_main);
    }


        public void sendMessage(View view) {
        /*
        CronetProviderInstaller.installProvider(this);
        CronetEngine.Builder myBuilder = new CronetEngine.Builder(this);
        CronetEngine cronetEngine = myBuilder.build();

        UrlRequest.Callback callback = new MyUrlRequestCallback(this);
        Executor executor = Executors.newSingleThreadExecutor();
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                "http://ec2-18-222-172-237.us-east-2.compute.amazonaws.com:8080/api/dailyspecial/1", callback, executor);

        UrlRequest request = requestBuilder.build();
        request.start();
        */

        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }
}
