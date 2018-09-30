package com.roberttamayo.shoppingregistry.helpers;

public class WeNeedDbSchema {
    public static final class UserTable {
        public static final String NAME = "users";
        public static final class Cols {
            public static final String USERNAME = "user_name";
            public static final String NICKNAME = "nick_name";
            public static final String PASSWORD = "magicword";
            public static final String FIREBASE_TOKEN = "user_firebase_token";
            public static final String ACCOUNT_ID = "user_account_id"; // the active shopping list
            public static final String IS_SELF = "is_self";
            public static final String USER_ID = "user_id";
        }
    }
}
