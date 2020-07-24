package com.prm391.project.bingeeproject.Common;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.prm391.project.bingeeproject.R;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;

public class SplashActivity extends AppCompatActivity {

    private Timer timer;
    ImageView ivLogo;
    TextView tvBrandName;
    Animation bottomAnim;
    SharedPreferences onBoardingScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        ivLogo=findViewById(R.id.ivLogo);
        tvBrandName=findViewById(R.id.tvBrandName);

//        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom_anim);
//        tvBrandName.setAnimation(bottomAnim);

        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(ivLogo, "scaleX", 0.6f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(ivLogo, "scaleY", 0.6f);
        scaleDownX.setDuration(1000);
        scaleDownY.setDuration(1000);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);

        scaleDown.start();

        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(tvBrandName, "scaleX", 1.5f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(tvBrandName, "scaleY", 1.5f);
        scaleUpX.setDuration(1000);
        scaleUpY.setDuration(1000);

        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.play(scaleUpX).with(scaleUpY);

        scaleUp.start();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                onBoardingScreen =getSharedPreferences("onBoardingScreen",MODE_PRIVATE);
                boolean isFirstTime =onBoardingScreen.getBoolean("firstTime",true);
                if(isFirstTime){
                    SharedPreferences.Editor editor = onBoardingScreen.edit();
                    editor.putBoolean("firstTime",false);
                    editor.commit();
                    Intent i = new Intent(SplashActivity.this, OnBoardingActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };

        timer = new Timer();
        timer.schedule(task, 3000);
    }
    @Override
    protected void onPause() {
        timer.cancel();
        super.onPause();
    }
}