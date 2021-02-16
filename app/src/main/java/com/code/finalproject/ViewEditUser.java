package com.code.finalproject;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.FragmentManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewEditUser extends AppCompatActivity {

    User user = null;

    ImageView userIcon;
    TextView viewEdit;
    TextView name;
    TextView password;
    TextView email;
    TextView address;

    Drawable editableDrawable;
    Drawable uneditableDrawable = null;

    Bitmap userIconBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit_user);

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
                    System.out.println("Downloading User Data");
                }

                runOnUiThread(() -> setupScreen());
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }

    public void setupScreen() {
        LoginActivity.root = getFilesDir().getAbsolutePath();

        viewEdit = findViewById(R.id.view_editUser);

        userIcon = findViewById(R.id.editUserIcon);
        name = findViewById(R.id.editUserName);
        password = findViewById(R.id.editUserPassword);
        email = findViewById(R.id.editUserEmail);
        address = findViewById(R.id.editUserAddress);

        user = UserDatabase.getUser(getIntent().getStringExtra("User"));
        setUserData();

        editableDrawable = name.getBackground();

        if (MainActivity.user == null) {
            MainActivity.user = UserDatabase.getUser(getSharedPreferences("shared", MODE_PRIVATE).getInt(LoginActivity.idKey, -1));
        }

        boolean editable = user.equals(MainActivity.user);
        viewEdit.setText((editable ? "Edit User Info" : "View User Info"));

        setEditable(name, editable);
        setEditable(password, editable);
        setEditable(email, editable);
        setEditable(address, editable);

        Button button = findViewById(R.id.userSave);
        if (editable) {
            button.setVisibility(View.VISIBLE);
            userIcon.setOnClickListener(v -> {
                takePhoto();
            });
        } else {
            button.setVisibility(View.GONE);
        }
    }

    public void setEditable(TextView textView, boolean editable) {
        textView.setFocusable(editable);
        textView.setCursorVisible(editable);

        textView.setBackground((editable ? editableDrawable : uneditableDrawable));
    }

    public void setUserData() {
        MainActivity.setUserImageForView(this, user, userIcon);

        name.setText(user.name);
        password.setText(user.password);
        email.setText(user.email);
        address.setText(user.address.street);

    }

    public void saveData(View view) {
        user.name = name.getText().toString();
        user.password = password.getText().toString();
        user.address.street = address.getText().toString();
        user.email = email.getText().toString();

        if (userIconBitmap != null)
            user.setImage(userIconBitmap);

        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStop() {
        super.onStop();

        if (MainActivity.isApplicationSentToBackground(this)) {
            sendNotification(getIntent(), this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void sendNotification(Intent intent, Context context) {
        String notificationID = "DontForget";
        String notificationName = "Dont Forget About Me";
        NotificationChannel channel = new NotificationChannel(notificationID, notificationName, NotificationManager.IMPORTANCE_DEFAULT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel.getId());
        builder.setSmallIcon(R.drawable.icon);
        builder.setContentTitle("Dont forget about me!");
        builder.setContentTitle("Reopen view/edit page");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent mainPage = new Intent(context, MainActivity.class);
        mainPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainPage.setAction(Long.toString(System.currentTimeMillis()));

        Intent editPage = intent;
        editPage.setAction(Long.toString(System.currentTimeMillis()));

        Intent[] intents = new Intent[]{mainPage, editPage};

        PendingIntent pi = PendingIntent.getActivities(context, 0, intents, PendingIntent.FLAG_ONE_SHOT);

        builder.setContentIntent(pi);
        builder.setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(0, builder.build());
    }

    public void takePhoto() {
        TakePhotoDialog td = new TakePhotoDialog();
        td.show(getSupportFragmentManager(), "Tag");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == -1) {
            Bundle extra = data.getExtras();
            Bitmap image = extra.getParcelable("data");

            if (image.getHeight() != image.getWidth()) {
                image = crop(image);
            }

            userIconBitmap = image;
            userIcon.setImageBitmap(userIconBitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap crop(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int x = width/2;
        int y = height/2;

        if (width > height) {
            return Bitmap.createBitmap(image, x - height/2, y - height/2, height, height);
        } else {
            return Bitmap.createBitmap(image, x - width/2, y - width/2, width, width);
        }
    }
}