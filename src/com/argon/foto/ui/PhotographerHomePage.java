package com.argon.foto.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.argon.foto.R;

public class PhotographerHomePage extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photographer_home);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotographerHomePage.this.finish();
            }
        });
        findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: to be implemented
            }
        });
    }
}