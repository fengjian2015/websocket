package com.example.websockettest;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.in.livechat.ui.chat.activity.ChatActivity;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void chat(View view) {
        ChatActivity.startChat(this,"user123");
    }

}
