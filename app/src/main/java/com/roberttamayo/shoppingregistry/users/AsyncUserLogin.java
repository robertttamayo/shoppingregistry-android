package com.roberttamayo.shoppingregistry.users;

import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.util.Log;

import com.roberttamayo.shoppingregistry.ShoppingItem;
import com.roberttamayo.shoppingregistry.helpers.AsyncTaskExecutable;
import com.roberttamayo.shoppingregistry.helpers.WeNeed;

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

public class AsyncUserLogin extends AsyncTask<Void, Void, User> {

    private static final String TAG = "AsyncUserLogin";

    private AsyncTaskExecutable<User> mCaller;
    private String mUsername;
    private String mPassword;

    public AsyncUserLogin(AsyncTaskExecutable<User> caller, String username, String password) {
        mCaller = caller;
        mUsername = username;
        mPassword = password;
    }

    @Override
    protected User doInBackground(Void... voids) {

        String data = "";
        User user = null;

        try {
            URL url = new URL("http://www.roberttamayo.com/shoplist/login.php");
            HttpURLConnection client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setDoInput(true);
            client.setDoOutput(true);

            List<Pair<String, String>> params = new ArrayList<>();
            params.add(new Pair<>("username", mUsername));
            params.add(new Pair<>("password", mPassword));
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
                int user_id = item.getInt("user_id");
                user = new User(user_id);

                user.setUsername(item.getString("user_name"));
                user.setFirebaseToken(item.getString("user_firebase_token"));
                user.setSelf(true);
                user.setNickname(item.getString("user_nickname"));
                user.setActiveAccount(item.getInt("user_account_id"));

                JSONArray account_ids = item.getJSONArray("account_ids");
                user.setAccountIds(account_ids);
            }

            client.disconnect();

        } catch (Exception e) {
            Log.d(TAG, "error: " + e.getMessage());
        } finally {

        }
        return user;
    }
    @Override
    protected void onPostExecute(User user) {
        super.onPostExecute(user);
        mCaller.onFinish(user);
    }
}
