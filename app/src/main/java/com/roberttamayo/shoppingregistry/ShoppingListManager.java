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

    }
    public static ShoppingListManager get(Context context) {
        if (mShoppingListManager == null) {
            mShoppingListManager = new ShoppingListManager();
        }
        return mShoppingListManager;
    }
    public static void initialize(Context context, List<ShoppingItem> shoppingItems) {
        get(context).setShoppingItems(shoppingItems);
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

    private void setShoppingItems(List<ShoppingItem> shoppingItems) {
        mShoppingItems = shoppingItems;
    }

    public void addShoppingItem(ShoppingItem shoppingItem) {
        mShoppingItems.add(shoppingItem);
    }
}
