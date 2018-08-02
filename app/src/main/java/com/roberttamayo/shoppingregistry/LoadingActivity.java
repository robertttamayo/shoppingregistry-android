package com.roberttamayo.shoppingregistry;

import android.content.Intent;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoadingActivity extends AppCompatActivity {
    private final String TAG = "LogLoading";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        new ShoppingItemFetcher().execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private class ShoppingItemFetcher extends AsyncTask<Void, Void, List<ShoppingItem>> {

        @Override
        protected List<ShoppingItem> doInBackground(Void... voids) {
            List<ShoppingItem> shoppingItems = new ArrayList<>();
            String data = "";
            try {

                URL url = new URL("http://www.roberttamayo.com/shoplist/index.php");
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.setDoInput(true);
                client.setDoOutput(true);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(new Pair<>("account_id", "1"));
                params.add(new Pair<>("action", "get_items"));
                String postQuery = getPostQueryString((ArrayList<Pair<String, String>>) params);

                OutputStream outputStream = client.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

                bufferedWriter.write(postQuery);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStreamWriter.close();

                int responseCode = client.getResponseCode();

                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                while ((line = br.readLine()) != null) {
                    data += line;
                }
                Log.d(TAG, "data: " + data);
                JSONArray jsonItems = new JSONArray(data);
                for (int i = 0; i < jsonItems.length(); i++) {
                    Log.d(TAG, "jsonItems(i): " + jsonItems.get(i));
                    JSONObject item = new JSONObject(jsonItems.get(i).toString());
                    ShoppingItem shoppingItem = new ShoppingItem();
                    shoppingItem.setTitle(item.getString("item_name"));
                    shoppingItem.setPurchased(item.getInt("item_is_purchased") == 0 ? false : true);
                    shoppingItem.setDate(new Date());
                    shoppingItems.add(shoppingItem);
                }


            } catch (Exception e) {
                Log.d(TAG, "error: " + e.getMessage());
            } finally {

            }
            return shoppingItems;
        }

        @Override
        protected void onPostExecute(List<ShoppingItem> shoppingItems) {
            super.onPostExecute(shoppingItems);
            Log.d(TAG, "shopping items size: " + shoppingItems.size());
            ShoppingListManager.initialize(getApplicationContext(), shoppingItems);

            Intent intent = new Intent(getApplicationContext(), ListActivity.class);
            startActivity(intent);
        }

        private String getPostQueryString(ArrayList<Pair<String, String>> params) throws UnsupportedEncodingException {
            StringBuilder builder = new StringBuilder();

            boolean isFirst = true;
            for (Pair<String, String> pair : params) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    builder.append('&');
                }
                builder.append(URLEncoder.encode(pair.first, "UTF-8"))
                        .append('=')
                        .append(URLEncoder.encode(pair.second, "UTF-8"));
            }

            String queryString = builder.toString();
            return queryString;
        }
    }
}
