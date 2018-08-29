package com.roberttamayo.shoppingregistry.helpers;

public interface AsyncTaskExecutable<T> {
    void onFinish(T t);
}
