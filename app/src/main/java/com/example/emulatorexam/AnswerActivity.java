package com.example.emulatorexam;

import android.content.Intent;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

public class AnswerActivity extends AppCompatActivity {

    TextView textView;
    int mPtrCount;

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = (event.getAction() & MotionEvent.ACTION_MASK);
        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN:
                mPtrCount++;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mPtrCount--;
                break;
            case MotionEvent.ACTION_DOWN:
                mPtrCount++;
                break;
            case MotionEvent.ACTION_UP:
                mPtrCount--;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("myLogs", "Move " + mPtrCount);
                break;

        }

        return true;
    }
}
