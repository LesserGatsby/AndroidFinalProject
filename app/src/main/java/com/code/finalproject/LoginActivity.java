package com.code.finalproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    TextView emailBox;
    TextView passwordBox;
    Button loginButton;

    //Enables login button if email and password boxes are not empty
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean emailEmpty = emailBox.getText().toString().trim().equals("");
            boolean passEmpty = passwordBox.getText().toString().trim().equals("");

            loginButton.setEnabled (!(emailEmpty || passEmpty));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserDatabase.initDatabase();

        setContentView(R.layout.activity_login);

        Utility.root = getFilesDir().getAbsolutePath();

        emailBox = findViewById(R.id.email);
        passwordBox = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        loginButton.setEnabled(false);

        emailBox.addTextChangedListener(textWatcher);
        passwordBox.addTextChangedListener(textWatcher);

        SharedPreferences shared = getSharedPreferences("shared", MODE_PRIVATE);

        if (shared.contains(Utility.idKey)) {
            int id = shared.getInt(Utility.idKey, -1);
            User user = UserDatabase.getUser(id);

            openMain();
        }
    }

    public void logIn(View view) {

        SharedPreferences shared = getSharedPreferences("shared", MODE_PRIVATE);

        String email = emailBox.getText().toString().trim();
        String password = passwordBox.getText().toString().trim();

        if (!email.equals("") && !password.equals("")) {

            User user = UserDatabase.getUser(email);
            if (user != null && user.password.equals(password)) {

                SharedPreferences.Editor editor = shared.edit();
                Log.d("USER ID ", user.id + "");
                editor.putInt(Utility.idKey, user.id);
                editor.apply();

                openMain();
            } else {
                Toast.makeText(getApplicationContext(), "Username or Password incorrect", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Opens Main Activity and removes login activity from the back stack
    public void openMain() {
        LoginActivity activity = this;

        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }
}