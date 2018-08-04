package com.roberttamayo.shoppingregistry;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.GregorianCalendar;
import java.util.List;

public class ListFragment extends Fragment {

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
                // start a new activity to add details
//                Intent intent = ShoppingItemPagerActivity.newIntent(getActivity(), shoppingItem.getId());
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
        }

        public void bindCrime(ShoppingItem shoppingItem) {
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
            mShoppingItems = shoppingItems;
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
            holder.bindCrime(shoppingItem);
        }

        @Override
        public int getItemCount() {
            return mShoppingItems.size();
        }
    }

}
