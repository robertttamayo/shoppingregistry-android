package com.roberttamayo.shoppingregistry.users;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.roberttamayo.shoppingregistry.helpers.WeNeedDbHelper;
import com.roberttamayo.shoppingregistry.helpers.WeNeedDbSchema;

import java.util.ArrayList;
import java.util.List;

import static com.roberttamayo.shoppingregistry.helpers.WeNeedDbSchema.UserTable;

public class UsersManager {
    private static final String TAG = "UsersManager";

    private Context mContext;
    private List<User> mUsers;
    private SQLiteDatabase mDatabase;

    public UsersManager(Context context){
        mContext = context;
        mUsers = new ArrayList<>();
        mDatabase = new WeNeedDbHelper(mContext).getWritableDatabase();

        // get all users
        initialize();
    }

    private void initialize() {
        List<User> users = new ArrayList<>();

        UserCursorWrapper cursor = queryUsers(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                users.add(cursor.getUser());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        mUsers = users;
    }

    public void addUser(User user) {
        mUsers.add(user);
    }
    public List<User> getUsers() {
        return mUsers;
    }
    private static ContentValues getContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(UserTable.Cols.USERNAME, user.getUsername());
        values.put(UserTable.Cols.FIREBASE_TOKEN, user.getFirebaseToken());
        values.put(UserTable.Cols.IS_SELF, user.isSelf());

        return values;
    }

    public User getCurrentUser(){
        Log.d(TAG, "getting current user");
        User user = null;
        for(User u : mUsers) {
            if (u.isSelf()) {
                user = u;
                break;
            }
        }
        return user;
    }

    public void addUserDB(User user) {
        ContentValues values = getContentValues(user);
        mDatabase.insert(UserTable.NAME, null, values);
    }

    public void updateUserDB(User user) {
        String userid = Integer.toString(user.getUserid());
        ContentValues values = getContentValues(user);

        mDatabase.update(UserTable.NAME, values, UserTable.Cols.USER_ID + " = ?", new String[]{userid});
    }

    public UserCursorWrapper queryUsers(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                UserTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new UserCursorWrapper(cursor);
    }

}
