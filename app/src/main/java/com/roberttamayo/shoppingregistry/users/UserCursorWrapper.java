package com.roberttamayo.shoppingregistry.users;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.roberttamayo.shoppingregistry.helpers.WeNeedDbSchema;
import static com.roberttamayo.shoppingregistry.helpers.WeNeedDbSchema.UserTable;

public class UserCursorWrapper extends CursorWrapper {

    public UserCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public User getUser() {
        String username = getString(getColumnIndex(UserTable.Cols.USERNAME));
        String nickname = getString(getColumnIndex(UserTable.Cols.NICKNAME));
        int is_self = getInt(getColumnIndex(UserTable.Cols.IS_SELF));
        int user_id = getInt(getColumnIndex(UserTable.Cols.USER_ID));
        String firebasetoken = getString(getColumnIndex(UserTable.Cols.FIREBASE_TOKEN));

        boolean self = is_self == 1;

        User user = new User(user_id);
        user.setFirebaseToken(firebasetoken);
        user.setNickname(nickname);
        user.setSelf(self);
        user.setUsername(username);
        return user;
    }

    public String getPassword() {
        String password = getString(getColumnIndex(UserTable.Cols.PASSWORD));

        return password;
    }
}
