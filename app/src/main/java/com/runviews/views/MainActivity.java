package com.runviews.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.runviews.viewslibrary.MicrosoftLoadView;

public class MainActivity extends AppCompatActivity {
    private MicrosoftLoadView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void two(View view) {
        loading.setItemCount(2);
        loading = findViewById(R.id.loading);
    }

    public void four(View view) {
        loading.setItemCount(4);
    }

    public void six(View view) {
        loading.setItemCount(6);
    }
}
