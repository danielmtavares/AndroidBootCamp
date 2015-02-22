package com.codepath.chatty;

import android.app.Application;

import com.codepath.chatty.model.Message;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by dtavares on 2/18/15.
 */
public class ChatApplication extends Application {
    public static final String YOUR_APPLICATION_ID = "RL9fw6QDltcua2bQdmTaj8NdXPXtn0gmQYzXSmjN";
    public static final String YOUR_CLIENT_KEY = "dcwCP8bj9OqpTvpjDle3ld1qTjuTRDdSb51Ot5QK";
    @Override
    public void onCreate() {
        super.onCreate();
        // Register your parse models here
        ParseObject.registerSubclass(Message.class);

        // Existing initialization happens after all classes are registered
        Parse.initialize(this, YOUR_APPLICATION_ID, YOUR_CLIENT_KEY);
    }
}