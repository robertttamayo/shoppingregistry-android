package com.roberttamayo.shoppingregistry;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.roberttamayo.shoppingregistry.helpers.AsyncTaskExecutable;
import com.roberttamayo.shoppingregistry.helpers.ShoppingItemFetcher;
import com.roberttamayo.shoppingregistry.helpers.WeNeed;
import com.roberttamayo.shoppingregistry.helpers.WeNeedDbHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoadingActivity extends AppCompatActivity implements AsyncTaskExecutable<List<ShoppingItem>>{
    private final String TAG = "LogLoading";
    private ProgressBar mProgressBar;
    private ShoppingItemFetcher mShoppingItemFetcher;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public LoadingActivity(){
        mContext = getApplicationContext();
        mDatabase = new WeNeedDbHelper(mContext).getWritableDatabase();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        mProgressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        mProgressBar.setVisibility(View.VISIBLE);

        mShoppingItemFetcher = new ShoppingItemFetcher(this);
        mShoppingItemFetcher.execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public void onFinish(List<ShoppingItem> shoppingItems) {
        ShoppingListManager.initialize(this, shoppingItems);
        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
        startActivity(intent);
    }

}
