package com.roberttamayo.shoppingregistry.helpers;

import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.Toast;

import com.roberttamayo.shoppingregistry.ShoppingItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShoppingItemDeleter extends AsyncTask<Void, Void, ShoppingItem> {
    private final String TAG = "ItemDeleterAsync";
    private ShoppingItem mItem;
    private AsyncTaskExecutable<ShoppingItem> mCaller;

    public ShoppingItemDeleter(AsyncTaskExecutable<ShoppingItem> caller, ShoppingItem item) {
        mItem = item;
        mCaller = caller;
        Log.d(TAG, "item id: " + item.getDbId());
    }

    @Override
    protected ShoppingItem doInBackground(Void... voids) {
        ShoppingItem shoppingItem = mItem;
        String data = "";
        try {
            URL url = new URL(WeNeed.API.URL);
            HttpURLConnection client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setDoInput(true);
            client.setDoOutput(true);

            String accountId = Integer.toString(WeNeed.ACCOUNT_ID);
            String userId = Integer.toString(WeNeed.USER_ID);

            Log.d(TAG, "Shopping item item_id: " + shoppingItem.getDbId());
            List<Pair<String, String>> params = new ArrayList<>();
            params.add(new Pair<>("action", "modify_item"));
            params.add(new Pair<>("item_id", Integer.toString(shoppingItem.getDbId())));
            params.add(new Pair<>("item_is_purchased", shoppingItem.isPurchased() ? "1" : "0"));
            String postQuery = WeNeed.getPostQueryString((ArrayList<Pair<String, String>>) params);

            OutputStream outputStream = client.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            bufferedWriter.write(postQuery);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStreamWriter.close();

            int responseCode = client.getResponseCode();

            if (responseCode == 200) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                while ((line = br.readLine()) != null) {
                    data += line;
                }
                Log.d(TAG, "data: " + data);

                JSONArray jsonArray = new JSONArray(data);

                JSONObject jsonObject = new JSONObject(jsonArray.getString(0));

                shoppingItem.setDate(new Date());
                shoppingItem.setTitle(jsonObject.getString(WeNeed.DB.Cols.ITEM_NAME));
                shoppingItem.setDbId(jsonObject.getInt(WeNeed.DB.Cols.ITEM_ID));
                shoppingItem.setUserId(jsonObject.getInt(WeNeed.DB.Cols.ITEM_USER_ID));
                shoppingItem.setAccountId(jsonObject.getInt(WeNeed.DB.Cols.ITEM_ACCOUNT_ID));
                shoppingItem.setPurchased(jsonObject.getInt("item_is_purchased") == 1);

            }

        } catch (Exception e) {
            Log.d(TAG, "error: " + e.getMessage());
        } finally {

        }
        return shoppingItem;
    }
    @Override
    protected void onPostExecute(ShoppingItem item) {
        super.onPostExecute(item);
        mCaller.onFinish(item);
    }
}
