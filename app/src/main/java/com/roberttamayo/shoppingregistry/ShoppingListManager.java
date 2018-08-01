package com.roberttamayo.shoppingregistry;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShoppingListManager {

    private static ShoppingListManager mShoppingListManager;

    private List<ShoppingItem> mShoppingItems;

    private ShoppingListManager() {
        mShoppingItems = new ArrayList<>();
        // TODO: Fetch items from database for user's account
    }
    public static ShoppingListManager get(Context context) {
        if (mShoppingListManager == null) {
            mShoppingListManager = new ShoppingListManager();
        }
        return mShoppingListManager;
    }
    public ShoppingItem getShoppingItem(UUID id) {
        for (ShoppingItem item: mShoppingItems) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }
    public List<ShoppingItem> getShoppingItems() {
        return mShoppingItems;
    }

    public void addShoppingItem(ShoppingItem shoppingItem) {
        mShoppingItems.add(shoppingItem);
    }
}
