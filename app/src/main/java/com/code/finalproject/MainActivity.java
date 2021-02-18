package com.code.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static User user = null;

    public static void setUserImageForView(AppCompatActivity activity, User user, ImageView view) {
        user.loadImage();

        Thread wait = new Thread(new Runnable() {
            @Override
            public void run() {
                while (user.icon == null) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setImageBitmap(user.icon);
                    }
                });
            }
        });

        wait.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utility.root = getFilesDir().getAbsolutePath();

        loadData();

    }

    public void loadData() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                UserDatabase.initDatabase();

                while (UserDatabase.isDownloading()) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("Downloading", "Downloading User Data");
                }

                runOnUiThread(() -> setupScreen());
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }

    public void setupScreen() {
        SharedPreferences sp = getSharedPreferences("shared", MODE_PRIVATE);

        Log.d("Contains Id Key", sp.contains(Utility.idKey) + "");
        Log.d("Id Key", sp.getInt(Utility.idKey, -1) + "");
        user = UserDatabase.getUser(getSharedPreferences("shared", MODE_PRIVATE).getInt(Utility.idKey, -1));
        if (user == null) {
            logOut(null);
            finish();
        } else {

            ImageView userImage = findViewById(R.id.userIcon);
            TextView userName = findViewById(R.id.userName);

            if (user.name.equals("")) {
                userName.setText(user.email);
            } else {
                userName.setText(user.name);
            }

            setUserImageForView(this, user, userImage);

            UserDatabase.initDatabase();
            setData(UserDatabase.getUsers());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadData();
    }

    public void logOut(View view) {
        SharedPreferences.Editor editor = getSharedPreferences("shared", MODE_PRIVATE).edit();
        editor.remove(Utility.idKey);
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(intent);
    }

    public void setData(ArrayList<User> userList) {

        User[] users = new User[userList.size()];
        userList.toArray(users);

        if (users == null) {
            users = new User[0];
        }

        RecyclerView rv = findViewById(R.id.userList);

        UserAdapter userAdapter = new UserAdapter(users, this);

        rv.setAdapter(userAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStop() {
        super.onStop();

        if (isApplicationSentToBackground(this)) {
            sendNotification(getIntent(), this);
        }
    }

    public static boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void sendNotification(Intent intent, Context context) {
        String notificationID = "DontForget";
        String notificationName = "Dont Forget About Me";
        NotificationChannel channel = new NotificationChannel(notificationID, notificationName, NotificationManager.IMPORTANCE_DEFAULT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel.getId());
        builder.setSmallIcon(R.drawable.icon);
        builder.setContentTitle("Dont forget about me!");
        builder.setContentTitle("Reopen list page");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent reopenPage = intent;
        PendingIntent pi = PendingIntent.getActivity(context, 0, reopenPage, 0);

        builder.setContentIntent(pi);
        builder.setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(0, builder.build());
    }
}