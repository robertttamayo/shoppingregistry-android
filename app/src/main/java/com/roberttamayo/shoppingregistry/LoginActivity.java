package com.roberttamayo.shoppingregistry;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.roberttamayo.shoppingregistry.helpers.AsyncTaskExecutable;
import com.roberttamayo.shoppingregistry.users.AsyncUserLogin;
import com.roberttamayo.shoppingregistry.users.User;

public class LoginActivity extends AppCompatActivity implements AsyncTaskExecutable<User>{

    private final static String TAG = "LoginActivity";

    public enum Mode {
        LOGIN, CREATE
    }

    private Mode mMode;

    private EditText mUsernameField;
    private EditText mPasswordField;
    private EditText mPasswordFieldConfirm;
    private Button mCreateButton;
    private Button mSubmitButton;

    private AsyncUserLogin mAsyncUserLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mMode = Mode.LOGIN;

        mUsernameField = findViewById(R.id.username_field);
        mPasswordField = findViewById(R.id.password_field);
        mPasswordFieldConfirm = findViewById(R.id.password_confirm_field);
        mSubmitButton = findViewById(R.id.button_login);
        mCreateButton = findViewById(R.id.button_create_username);

        initLogin();
        initCreate();

        setMode(mMode);
    }
    public void setMode(Mode mode) {
        mMode = mode;
        switch (mMode) {
            case LOGIN:
                mPasswordFieldConfirm.setVisibility(View.GONE);
                mSubmitButton.setVisibility(View.VISIBLE);
                mCreateButton.setVisibility(View.GONE);
                break;
            case CREATE:
                mPasswordFieldConfirm.setVisibility(View.VISIBLE);
                mSubmitButton.setVisibility(View.GONE);
                mCreateButton.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void initLogin(){
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }
    private void login() {
        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();
        mAsyncUserLogin = new AsyncUserLogin(this, username, password);
        mAsyncUserLogin.execute();
    }
    private void initCreate(){

    }
    @Override
    public void onFinish(User user) {
        Log.d(TAG, "Finished");
        if (user != null) {
            Log.d(TAG, "User login was a success");
        } else {
            Log.d(TAG, "User login was a failure");
        }
    }
}
