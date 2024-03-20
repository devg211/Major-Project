package com.example.steganomsg;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private boolean loginCheck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);




        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    UserSessionManager userSessionManager =new  UserSessionManager(getApplicationContext());
                    loginCheck = userSessionManager.isUserLoggedIn();
                    userSessionManager.getUserDetails().get("");


                    Intent intent;
                    if (loginCheck)
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                    else
                        intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);

                    finish();

                }
            }
        };

        timerThread.start();

    }
}