package com.code.finalproject;

import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.RequiresApi;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserDatabase {

    private static ArrayList<User> userList = new ArrayList<>();
    private static OkHttpClient client = new OkHttpClient();

    public static void initDatabase() {

        if (userList.size() == 0) {
            /*
            userList.add(new User(0,"mail@mail.co", "pass", "Bill Bob", "1000 Street street"));
            userList.add(new User(1,"pail@mail.co", "pass", "Jack N Jill", "256 A Ln"));
            userList.add(new User(2,"longemailaddress@mail.co", "pass", "Walla Walla Wonion Wings", "645 Nice Road"));
            userList.add(new User(3,"whatisthis@mail.co", "pass", "Lesser Gatsby", "4678 Long Drive"));
            userList.add(new User(4,"email@mail.co", "pass", "King Cong", "446 King Court"));
            userList.add(new User(5,"gmail@mail.co", "pass", "Mighty Bee", "674 Nowhere Rd"));
            userList.add(new User(6,"wemail@mail.co", "pass", "Doland Goofe", "000 Null Lane"));
            userList.add(new User(7,"themail@mail.co", "pass", "Ansem Wise", "457 Lost Ship Doc"));
            userList.add(new User(8,"impact@mail.co", "pass", "Name Man", "476 My Name"));
             */

            run();
        }
    }

    public static void initDatabase(User[] users) {
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

    private static void run() {
        String url = "https://jsonplaceholder.typicode.com/users";

        Request request = new Request.Builder().url(url).build();

        final boolean[] donwloading = {true};

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
                    System.out.println("Response Data");
                    System.out.println(data);

                    Gson gson = new Gson();
                    UserDatabase.initDatabase(gson.fromJson(data, User[].class));
                    donwloading[0] = false;
                }
            }
        });

        while (donwloading[0]) {

        }
    }

}
