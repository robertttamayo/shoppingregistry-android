package com.roberttamayo.shoppingregistry;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShoppingItemDetailFragment extends Fragment {
    private final String TAG = "ItemDetailFragment";
    private static final String ARG_ITEM_ID = "item_id";

    private ShoppingItem mShoppingItem;

    public static ShoppingItemDetailFragment newInstance(UUID itemId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM_ID, itemId);

        ShoppingItemDetailFragment fragment = new ShoppingItemDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID itemId = (UUID) getArguments().getSerializable(ARG_ITEM_ID);
        Log.d(TAG, itemId.toString());
        mShoppingItem = ShoppingListManager.get(getActivity()).getShoppingItem(itemId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_shopping_item_detail, container, false);

        EditText editText = (EditText) v.findViewById(R.id.item_title);
        editText.setText(mShoppingItem.getTitle());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mShoppingItem.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        CheckBox checkBox = (CheckBox) v.findViewById(R.id.item_is_purchased);
        checkBox.setChecked(mShoppingItem.isPurchased());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mShoppingItem.setPurchased(b);
                // TODO: Logic to remove this item from the list
            }
        });

        return v;
    }

}
