package com.code.finalproject;

import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Utility {

    //Key for auto login
    public static final String idKey = "userID";

    //File storage root directory
    public static String root;

    //Waits until the icon for the user is no longer null before setting the view's bitmap image to said icon
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
