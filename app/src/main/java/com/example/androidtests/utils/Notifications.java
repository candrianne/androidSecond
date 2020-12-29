package com.example.androidtests.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.androidtests.R;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Notifications {
    public static void sendFriendRequestNotification(Context context, String firebaseToken) throws JSONException {
        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        notificationBody.put("title", context.getResources().getString(R.string.friend_requests));
        notificationBody.put(
                "message",
                SaveSharedPreference.getLogedInUser(
                        context).getFirstName() + " " +
                        context.getResources().getString((R.string.friend_request_notification))
        );
        notification.put("to", firebaseToken);
        notification.put("data", notificationBody);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notification,
                response -> Log.i("test", "onResponse: " + response.toString()),
                error -> {
                    Toast.makeText(context, "Request error", Toast.LENGTH_LONG).show();
                    Log.i("test", "onErrorResponse: Didn't work");
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "key=" + "AAAATcI1D9Y:APA91bFNFwo1xfas-gAPf2ArbioVdnhla8QNuojksA28k-WlKJrUO415Vo_-boZMRKb4PfsWW1h3LRXhAXzCR2gyU3SQrWoT1KIXlSgoOyyQvfe7zbcZSsMzNHeIuWKotDdV3Reo57Dv");
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}
