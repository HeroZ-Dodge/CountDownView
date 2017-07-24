package com.dodo.hero.z.countdownview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.z.hero.dodge.countdown.CountDownView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final CountDownView countDownView = (CountDownView) findViewById(R.id.count_down);
        countDownView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownView.startAnimation();
            }
        });

        final CountDownView countDownView0 = (CountDownView) findViewById(R.id.count_down_0);
        countDownView0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownView0.startAnimation();
            }
        });

        final CountDownView countDownView1 = (CountDownView) findViewById(R.id.count_down_1);
        countDownView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownView1.startAnimation();
            }
        });

        final CountDownView countDownView2 = (CountDownView) findViewById(R.id.count_down_2);
        countDownView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownView2.startAnimation();
            }
        });

    }
}
