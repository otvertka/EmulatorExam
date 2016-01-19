package com.example.emulatorexam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AnswerActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        initTextView();

    }

    private void initTextView() {
        textView = (TextView) findViewById(R.id.textView);

        Intent intent = getIntent();
        String answer = intent.getStringExtra("position");
        textView.setText(answer);
    }
}
