package com.roberttamayo.shoppingregistry;

import java.util.Date;
import java.util.UUID;

public class ShoppingItem {

    private String mTitle;
    private Date mDate;
    private boolean mIsPurchased;
    private UUID mId;

    private int userId;
    private int accountId;
    private int dbId;

    public ShoppingItem() {
        this(UUID.randomUUID());
    }

    public ShoppingItem(UUID id) {
        this.mId = id;
        this.mDate = new Date();
        setPurchased(false);
        setTitle("");
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public UUID getId() {
        return mId;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isPurchased() {
        return mIsPurchased;
    }

    public void setPurchased(boolean purchased) {
        mIsPurchased = purchased;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

}
