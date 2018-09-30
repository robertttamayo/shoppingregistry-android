package com.roberttamayo.shoppingregistry.helpers;

import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.util.Log;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class UserFirebaseTokenUpdater extends AsyncTask<Void, Void, String>{

    private final String TAG = "FirebaseUpdater";

    private String mFirebaseToken;

    public UserFirebaseTokenUpdater(String firebaseToken) {
        mFirebaseToken = firebaseToken;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String data = "";

        try {
            URL url = new URL("http://www.roberttamayo.com/shoplist/index.php");
            HttpURLConnection client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setDoInput(true);
            client.setDoOutput(true);

            List<Pair<String, String>> params = new ArrayList<>();
            params.add(new Pair<>("user_id", "2"));
            params.add(new Pair<>("user_account_id", "1"));
            params.add(new Pair<>("user_firebase_token", mFirebaseToken));
            params.add(new Pair<>("action", "update_user_firebase_token"));
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

            }

            client.disconnect();

        } catch (Exception e) {
            Log.d(TAG, "error: " + e.getMessage());
        } finally {

        }
        return "";
    }

    @Override
    protected void onPostExecute(String message) {
        super.onPostExecute(message);
    }
}