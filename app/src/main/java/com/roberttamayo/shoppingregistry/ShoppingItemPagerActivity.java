package com.roberttamayo.shoppingregistry;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.List;
import java.util.UUID;

public class ShoppingItemPagerActivity extends AppCompatActivity {
    private final String TAG = "ShoppingItemPagerActivity";
    private static final String EXTRA_ITEM_ID = "com.roberttamayo.shoppingregistry.itemid";

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private List<ShoppingItem> mShoppingItems;

    public static Intent newIntent(Context context, UUID itemId) {
        Intent intent = new Intent(context, ShoppingItemPagerActivity.class);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_pager);

        UUID itemId = (UUID) getIntent().getSerializableExtra(EXTRA_ITEM_ID);

        mShoppingItems = ShoppingListManager.get(this).getShoppingItems();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        FragmentManager fragmentManager = getSupportFragmentManager();

        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int i) {
                ShoppingItem item = mShoppingItems.get(i);
                return ShoppingItemDetailFragment.newInstance(item.getId());
            }

            @Override
            public int getCount() {
                return mShoppingItems.size();
            }
        });

        for (int i = 0; i < mShoppingItems.size(); i++) {
            if (mShoppingItems.get(i).getId().equals(itemId)) {
                mViewPager.setCurrentItem(i);
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping_item_pager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
