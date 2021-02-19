package com.code.finalproject;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

import androidx.annotation.RequiresApi;

public class User implements Serializable {
    public int id;
    public String email;
    public String password;
    public String name;
    public Address address;
    public transient Bitmap icon;

    private String iconPath() {
        int hash = email.hashCode();
        if (hash < 0)
            hash *= -1;
        String prePath = Utility.root;
        String imagePath = prePath + "/" + hash + "icon.png";

        return imagePath;
    }

    public User(int id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
        name = "";
        address = new Address();
    }

    public User(int id, String email, String password, String name, String address) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.address = new Address();
        this.address.street = address;
    }

    public void setImage(Bitmap bitmap) {

        icon = Bitmap.createBitmap(bitmap);
        String imagePath = iconPath();

        Log.d("User Icon Path Detail", "User " + email + " icon at " + imagePath);

        Log.d("Saving ", email);

        try (FileOutputStream out = new FileOutputStream(imagePath)){
            icon.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadImage() {
        if (icon != null)
            return;

        String imagePath = iconPath();

        File file = new File(imagePath);

        Log.d("User Icon Location", "User " + email + " icon at " + imagePath);
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            Log.d("Loading ", email + " icon from card");

            Picasso.get().load(uri).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    icon = bitmap;
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        } else {
            Log.d("Loading ", email + " icon from web");
            Picasso.get().load("https://robohash.org/" + email).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    icon = bitmap;

                    try (FileOutputStream out = new FileOutputStream(imagePath)) {
                        icon.compress(Bitmap.CompressFormat.PNG, 100, out);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null)
            return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (address != null ? !address.equals(user.address) : user.address != null) return false;
        return icon != null ? icon.equals(user.icon) : user.icon == null;
    }

    public class Address{
        String street;
    }
}


