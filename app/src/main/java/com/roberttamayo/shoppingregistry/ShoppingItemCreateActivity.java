package com.roberttamayo.shoppingregistry;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ShoppingItemCreateActivity extends AppCompatActivity {

    // handles creating a new ShoppingItem and adding it to the remote database.
    // user can either create a new ShoppingItem or return to the ListActivity when done

    private final String TAG = "WeNeedCreateActivity";
    private ProgressBar mProgressBar;
    private String mItemName;
    private EditText mEditTitle;
    private TextView mTitleAdded;
    private TextView mRecentlyAddedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_item_create);

        mTitleAdded = findViewById(R.id.title_added_so_far);
        mTitleAdded.setVisibility(View.GONE);

        mRecentlyAddedList = (TextView) findViewById(R.id.recently_added_list);

        mProgressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        mProgressBar.setVisibility(View.GONE);

        String itemName;
        mEditTitle = (EditText) findViewById(R.id.item_title);
        mEditTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mItemName = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mEditTitle.setOnEditorActionListener(new EditText.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    int userId = WeNeed.USER_ID;
                    int accountId = WeNeed.ACCOUNT_ID;

                    InputMethodManager imm = (InputMethodManager) textView.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

                    mProgressBar.setVisibility(View.VISIBLE);

                    new ShoppingItemCreateActivity.ShoppingItemPusher(mItemName).execute();

                    return true;
                }
                return false;
            }
        });
    }

    private class ShoppingItemPusher extends AsyncTask<Void, Void, ShoppingItem>{

        private String mItemName;

        public ShoppingItemPusher(String itemName) {
            mItemName = itemName;
        }
        @Override
        protected ShoppingItem doInBackground(Void... voids) {
            ShoppingItem shoppingItem = new ShoppingItem();
            String data = "";
            try {
                URL url = new URL(WeNeed.API.URL);
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.setDoInput(true);
                client.setDoOutput(true);

                String accountId = Integer.toString(WeNeed.ACCOUNT_ID);
                String userId = Integer.toString(WeNeed.USER_ID);

                List<Pair<String, String>> params = new ArrayList<>();
                params.add(new Pair<>("account_id", accountId));
                params.add(new Pair<>("user_id", userId));
                params.add(new Pair<>("action", WeNeed.API.Params.NEW_ITEM));
                params.add(new Pair<>("item_name", mItemName));
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

                    JSONObject jsonObject = new JSONObject(data);

                    shoppingItem.setDate(new Date());
                    shoppingItem.setTitle(jsonObject.getString(WeNeed.DB.Cols.ITEM_NAME));
                    shoppingItem.setDbId(jsonObject.getInt(WeNeed.DB.Cols.ITEM_ID));
                    shoppingItem.setUserId(jsonObject.getInt(WeNeed.DB.Cols.ITEM_USER_ID));
                    shoppingItem.setAccountId(jsonObject.getInt(WeNeed.DB.Cols.ITEM_ACCOUNT_ID));
                    shoppingItem.setPurchased(false);

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
            ShoppingListManager.get(getApplicationContext()).addShoppingItem(item);

            mProgressBar.setVisibility(View.GONE);
            mEditTitle.clearFocus();
            mEditTitle.setText("");

            mTitleAdded.setVisibility(View.VISIBLE);

            String currentText = (String) mRecentlyAddedList.getText();
            if (currentText.length() != 0) {
                currentText += "\n";
            }
            mRecentlyAddedList.setText(currentText + item.getTitle());

            Toast.makeText(getApplicationContext()
                    , "Success! Added " + item.getTitle() + " to your list."
                    , Toast.LENGTH_SHORT)
                    .show();
        }
    }

}
