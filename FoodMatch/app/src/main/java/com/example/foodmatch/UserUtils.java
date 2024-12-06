package com.example.foodmatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserUtils {

    private static final String PREF_NAME = "UserPrefs";
    private static final String USER_ID_KEY = "userId";

    public static void saveUserId(Context context, String userId) {
        Log.d("UserUtils", "Saving userId: " + userId); // Añade este log
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID_KEY, userId);
        editor.apply();
    }
    public static void clearUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(USER_ID_KEY);
        editor.apply();
        Log.d("UserUtils", "User ID cleared from SharedPreferences");
    }


    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString(USER_ID_KEY, null);
        Log.d("UserUtils", "Retrieved userId: " + userId); // Añade este log
        return userId;
    }

    public static String getUserIdOrThrow(Context context) {
        String userId = getUserId(context);
        Log.d("UserUtils", "getUserIdOrThrow retrieved userId from SharedPreferences: " + userId); // Añade este log
        if (userId != null) {
            return userId;
        }

        userId = getCurrentFirebaseUserId();
        Log.d("UserUtils", "getUserIdOrThrow retrieved userId from FirebaseAuth: " + userId); // Añade este log
        if (userId != null) {
            saveUserId(context, userId); // Guardar en SharedPreferences
            return userId;
        }

        Log.e("UserUtils", "User ID not available, throwing exception"); // Log de error
        throw new IllegalStateException("User ID not available. Ensure the user is logged in.");
    }
    public static String getCurrentFirebaseUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d("UserUtils", "FirebaseAuth returned userId: " + user.getUid());
            return user.getUid();
        } else {
            Log.e("UserUtils", "FirebaseAuth returned null user");
            return null;
        }
    }

}
