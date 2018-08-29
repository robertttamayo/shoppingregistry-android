package com.roberttamayo.shoppingregistry.helpers;

import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.util.Log;

import com.roberttamayo.shoppingregistry.ShoppingItem;
import com.roberttamayo.shoppingregistry.ShoppingListManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ShoppingItemFetcher extends AsyncTask<Void, Void, List<ShoppingItem>> {

    private final String TAG = "LogItemFetcher";

    private AsyncTaskExecutable<List<ShoppingItem>> mCaller;

    public ShoppingItemFetcher (AsyncTaskExecutable<List<ShoppingItem>> caller) {
        mCaller = caller;
    }

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
            String postQuery = WeNeed.getPostQueryString((ArrayList<Pair<String, String>>) params);

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
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                shoppingItem.setDate(sdf.parse(item.getString("item_date_added")));
                shoppingItem.setDbId(item.getInt("item_id"));
                shoppingItems.add(shoppingItem);
            }

            client.disconnect();

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
        mCaller.onFinish(shoppingItems);
    }
}