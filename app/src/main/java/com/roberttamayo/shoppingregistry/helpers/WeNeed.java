package com.roberttamayo.shoppingregistry.helpers;

import android.support.v4.util.Pair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class WeNeed {
    public static int USER_ID = 1;
    public static int ACCOUNT_ID = 1;

    public static final class API {
        public static final String URL = "http://www.roberttamayo.com/shoplist/index.php";

        public static final class Params {
            public static final String GET_ITEMS = "get_items";
            public static final String NEW_ITEM = "new_item";
            public static final String ACCOUNT_ID = "account_id";
            public static final String USER_ID = "user_id";
            public static final String ITEM_NAME = "item_name";
        }
    }

    public static final class DB {
        public static final class Cols {
            public static final String ACCOUNT_ID = "account_id";
            public static final String USER_ID = "user_id";
            public static final String ITEM_NAME = "item_name";
            public static final String ITEM_ID = "item_id";
            public static final String ITEM_ACCOUNT_ID = "item_account_id";
            public static final String ITEM_USER_ID = "item_user_id";
        }
    }

    public static String getPostQueryString(ArrayList<Pair<String, String>> params) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();

        boolean isFirst = true;
        for (Pair<String, String> pair : params) {
            if (isFirst) {
                isFirst = false;
            } else {
                builder.append('&');
            }
            builder.append(URLEncoder.encode(pair.first, "UTF-8"))
                    .append('=')
                    .append(URLEncoder.encode(pair.second, "UTF-8"));
        }

        String queryString = builder.toString();
        return queryString;
    }
}
