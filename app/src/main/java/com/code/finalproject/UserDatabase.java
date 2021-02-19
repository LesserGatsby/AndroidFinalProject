package com.code.finalproject;

import android.util.Log;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserDatabase {

    private static ArrayList<User> userList = new ArrayList<>();
    private static OkHttpClient client = new OkHttpClient();
    private static boolean downloading = false;

    public static boolean isDownloading() {
        return downloading;
    }

    public static void initDatabase() {

        if (userList.size() == 0) {
            downloadUserData();
        }
    }

    private static void initDatabase(User[] users) {
        userList = new ArrayList<User>(Arrays.asList(users));
        for (int i = 0; i < userList.size(); i++) {
            userList.get(i).id = i;
            userList.get(i).password = "pass";
        }

        userList.add(new User(userList.size(), "mail@mail.com", "pass"));
    }

    public static User getUser(int id) {
        if (userList.size() == 0) {
            initDatabase();
        }

        List<User> users = getUsers();

        for (User user : users)
            if (user.id == id)
                return user;

        return null;
    }

    public static User getUser(String email) {

        if (userList.size() == 0) {
            initDatabase();
        }

        List<User> users = getUsers();

        for (User user : users)
            if (user.email.equals(email))
                return user;

        return null;
    }

    public static ArrayList<User> getUsers() {
        if (userList.size() == 0) {
            initDatabase();
        }

        ArrayList<User> sortedList = new ArrayList<>(userList);

        Collections.sort(sortedList, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.name.compareTo(o2.name);
            }
        });

        return sortedList;
    }

    private static void downloadUserData() {
        String url = "https://jsonplaceholder.typicode.com/users";

        Request request = new Request.Builder().url(url).build();

        downloading = true;

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    String data = response.body().string();
                    Log.d("Response Data", "data");

                    Gson gson = new Gson();
                    UserDatabase.initDatabase(gson.fromJson(data, User[].class));
                    UserDatabase.downloading = false;
                }
            }
        });
    }

}
