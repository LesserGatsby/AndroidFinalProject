package com.code.finalproject;

import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Utility {

    public static final String idKey = "userID";

    public static String root;

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
}
