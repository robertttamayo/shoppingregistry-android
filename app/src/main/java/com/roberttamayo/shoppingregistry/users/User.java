package com.roberttamayo.shoppingregistry.users;

import org.json.JSONArray;

public class User {

    private String mFirebaseToken;
    private String mUsername;
    private String mNickname;
    private int mUserid;
    private JSONArray mAccountIds;

    private int mActiveAccount;

    private boolean mIsSelf;

    public User(int userid) {
        mUserid = userid;
    }

    public String getFirebaseToken() {
        return mFirebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        mFirebaseToken = firebaseToken;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getNickname() {
        return mNickname;
    }

    public void setNickname(String nickname) {
        mNickname = nickname;
    }

    public int getUserid() {
        return mUserid;
    }

    public boolean isSelf() {
        return mIsSelf;
    }

    public void setSelf(boolean self) {
        mIsSelf = self;
    }

    public void setAccountIds(JSONArray accountIds) {
        mAccountIds = accountIds;
    }

    public int getActiveAccount() {
        return mActiveAccount;
    }

    public void setActiveAccount(int activeAccount) {
        mActiveAccount = activeAccount;
    }
}
