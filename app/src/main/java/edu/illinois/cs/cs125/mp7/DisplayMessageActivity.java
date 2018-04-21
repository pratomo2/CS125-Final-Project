package edu.illinois.cs.cs125.mp7;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        try {
            //?
            Intent intent = getIntent();
            String olat = intent.getStringExtra("olat");
            String olon = intent.getStringExtra("olon");
            String dlat = intent.getStringExtra("dlat");
            String dlon = intent.getStringExtra("dlon");

            TextView View1 = findViewById(R.id.view1);
            View1.setText(olat);
            View1 = findViewById(R.id.view2);
            View1.setText(olon);
            View1 = findViewById(R.id.view3);
            View1.setText(dlat);
            View1 = findViewById(R.id.view4);
            View1.setText(dlon);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
