package com.example.chatapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.chatapp.R;

public class SplashScreenActivity extends AppCompatActivity {
    TextView txt;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        txt = findViewById(R.id.txt);
        img = findViewById(R.id.img);

        YoYo.with(Techniques.FadeIn).duration(2000).playOn(txt);
        YoYo.with(Techniques.FadeIn).duration(2000).playOn(img);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, SignInActivity.class));
                finish();
            }
        }, 2000);
    }
}