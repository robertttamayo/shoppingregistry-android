package com.roberttamayo.shoppingregistry;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class ListFragment extends Fragment {

    private final String TAG = "LogListFragment";
    private RecyclerView mItemRecyclerView;
    private ShoppingItemAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        mItemRecyclerView = (RecyclerView) view.findViewById(R.id.item_recycler_view);
        mItemRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//        if (savedInstanceState != null) {
//            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE, false);
//        }
//
        refreshUI();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_item_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_new_shopping_item:
                // start a new activity to add new item
                Intent intent = new Intent(getActivity(), ShoppingItemCreateActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshUI() {
        ShoppingListManager manager = ShoppingListManager.get(getActivity());
        List<ShoppingItem> shoppingItems = manager.getShoppingItems();

        if (mAdapter == null) {
            mAdapter = new ShoppingItemAdapter(shoppingItems);
            mItemRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ShoppingItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ShoppingItem mShoppingItem;

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mPurchasedCheckBox;

        public ShoppingItemHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_date_text_view);
            mPurchasedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_is_purchased_check_box);

            mPurchasedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    mShoppingItem.setPurchased(b);
                    if (b) {
                        new ListFragment.ShoppingItemDeleter(mShoppingItem).execute();
                    }
                }
            });
        }

        public void bindItem(ShoppingItem shoppingItem) {
            mShoppingItem = shoppingItem;

            DateFormat df = new DateFormat();
            CharSequence formattedDate = df.format("MMM d", shoppingItem.getDate());
            mTitleTextView.setText(mShoppingItem.getTitle());
            mDateTextView.setText("Added " + formattedDate);
            mPurchasedCheckBox.setChecked(mShoppingItem.isPurchased());
        }

        @Override
        public void onClick(View view) {
            Intent intent = ShoppingItemPagerActivity.newIntent(getActivity(), mShoppingItem.getId());
            startActivity(intent);
        }
    }

    private class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemHolder> {

        private List<ShoppingItem> mShoppingItems;

        public ShoppingItemAdapter(List<ShoppingItem> shoppingItems) {
            mShoppingItems = new ArrayList<>();
            for (ShoppingItem shoppingItem : shoppingItems) {
                if (!shoppingItem.isPurchased()) {
                    mShoppingItems.add(shoppingItem);
                }
            }
        }

        @Override
        public ShoppingItemHolder onCreateViewHolder(ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_item, parent, false);
            return new ShoppingItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ShoppingItemHolder holder, int i) {
            ShoppingItem shoppingItem = mShoppingItems.get(i);
            holder.bindItem(shoppingItem);
        }

        @Override
        public int getItemCount() {
            return mShoppingItems.size();
        }
    }

    private class ShoppingItemDeleter extends AsyncTask<Void, Void, ShoppingItem> {
        ShoppingItem mItem;

        public ShoppingItemDeleter(ShoppingItem item) {
            mItem = item;
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
            Toast.makeText(getContext()
                    , "Success!" + item.isPurchased()
                    , Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
