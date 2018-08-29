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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.roberttamayo.shoppingregistry.helpers.AsyncTaskExecutable;
import com.roberttamayo.shoppingregistry.helpers.ShoppingItemDeleter;
import com.roberttamayo.shoppingregistry.helpers.ShoppingItemFetcher;
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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ListFragment extends Fragment implements AsyncTaskExecutable<List<ShoppingItem>> {

    private final String TAG = "LogListFragment";
    private RecyclerView mItemRecyclerView;
    private ShoppingItemAdapter mAdapter;
    private Button mRefreshButton;
    private ShoppingItemFetcher mFetcher;
    private ProgressBar mLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        mLoader = (ProgressBar) view.findViewById(R.id.loading_spinner);
        mLoader.setVisibility(View.GONE);

        mItemRecyclerView = (RecyclerView) view.findViewById(R.id.item_recycler_view);
        mItemRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRefreshButton = (Button) view.findViewById(R.id.refresh_button);
        final AsyncTaskExecutable<List<ShoppingItem>> mCaller = this;
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoader.setVisibility(View.VISIBLE);
                mFetcher = new ShoppingItemFetcher(mCaller);
                mFetcher.execute();
            }
        });

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

    @Override
    public void onFinish(List<ShoppingItem> shoppingItems) {
        mLoader.setVisibility(View.GONE);
        ShoppingListManager.initialize(getActivity(), shoppingItems);
        mAdapter.updateShoppingItems(shoppingItems);
        refreshUI();
    }

    private class ShoppingItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, AsyncTaskExecutable<ShoppingItem> {

        private ShoppingItem mShoppingItem;
        private ShoppingItemDeleter mAsyncDeleter;

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mPurchasedCheckBox;
        private ProgressBar mUpdatePending;
        private TextView mUpdatedTextView;

        public ShoppingItemHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_date_text_view);
            mPurchasedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_is_purchased_check_box);
            mUpdatePending = (ProgressBar) itemView.findViewById(R.id.list_item_update_pending);
            mUpdatedTextView = (TextView) itemView.findViewById(R.id.list_item_updated_text);

            mUpdatePending.setVisibility(View.GONE);
            mUpdatedTextView.setVisibility(View.GONE);

            mPurchasedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    mShoppingItem.setPurchased(b);
                    mUpdatedTextView.setVisibility(View.GONE);
                    if (b) {
                        mUpdatePending.setVisibility(View.VISIBLE);
                        mAsyncDeleter.execute();
                    }
                }
            });
        }

        public void bindItem(ShoppingItem shoppingItem) {
            mShoppingItem = shoppingItem;

            mAsyncDeleter = new ShoppingItemDeleter(this, mShoppingItem);

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

        @Override
        public void onFinish(ShoppingItem item) {
            mShoppingItem = item;
            mAsyncDeleter = new ShoppingItemDeleter(this, mShoppingItem);
            mUpdatePending.setVisibility(View.GONE);
            mUpdatedTextView.setVisibility(View.VISIBLE);
            Toast.makeText(getContext()
                    , "Success!" + item.isPurchased()
                    , Toast.LENGTH_SHORT)
                    .show();
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
        public void updateShoppingItems(List<ShoppingItem> shoppingItems) {
            mShoppingItems.clear();
            for(ShoppingItem item : shoppingItems) {
                if (!item.isPurchased()) {
                    mShoppingItems.add(item);
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



}
