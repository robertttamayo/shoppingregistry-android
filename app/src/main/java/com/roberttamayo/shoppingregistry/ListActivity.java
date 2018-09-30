package com.roberttamayo.shoppingregistry;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.roberttamayo.shoppingregistry.helpers.UserFirebaseTokenUpdater;
import com.roberttamayo.shoppingregistry.helpers.WeNeedDbHelper;
import com.roberttamayo.shoppingregistry.users.UsersManager;

public class ListActivity extends SingleFragmentActivity {

//    public static UserInfo sUser;

    private final String TAG = "ListActivity";
    private UsersManager mUsersManager;

    @Override
    protected Fragment createFragment() {
        return new ListFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check device for user info, send to create username page if user doens't exist yet
        mUsersManager = new UsersManager(this.getApplicationContext());
        if (mUsersManager.getCurrentUser() == null) {
            Intent i = new Intent(ListActivity.this, LoginActivity.class);
            startActivity(i);
        }
        // Get token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
                        String msg = "Instance Token: " + token;
                        Log.d(TAG, msg);
//                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        new UserFirebaseTokenUpdater(token).execute();
                    }
                });

    }
}
