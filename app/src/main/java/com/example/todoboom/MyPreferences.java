package com.example.todoboom;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

class MyPreferences {

    public static void saveStateToMyPref(Context context, ArrayList<TodoItem> todoItems) {
        SharedPreferences sharedPreferences =  context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(todoItems);
        editor.putString("todo list", json);
        editor.apply();
    }

    public static ArrayList<TodoItem> loadData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("todo list", null);
        Type type = new TypeToken<ArrayList<TodoItem>>() {}.getType();
        ArrayList<TodoItem> todoItems;

        todoItems = gson.fromJson(json, type);

        if (todoItems == null) {
            todoItems = new ArrayList<>();
        }
        return todoItems;
    }
}
