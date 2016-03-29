package com.example.emulatorexam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class AnswerActivity extends AppCompatActivity implements View.OnTouchListener{

    TextView tv;
    float x;
    float y;
    String sDown;
    String sMove;
    String sUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //tv = new TextView(this);
        //tv.setOnTouchListener(this);
        //setContentView(tv);
        setContentView(R.layout.activity_answer);

        initTextView();

    }

    private void initTextView() {
        tv = (TextView) findViewById(R.id.textView);

        Intent intent = getIntent();
        String answer = intent.getStringExtra("position");
        tv.setText(answer);

        /*ScrollView scrollView = (ScrollView) findViewById(R.id.sv);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
                        Log.d("TAG", "Move " + mPtrCount);
                        break;
                }
                return true;
            }
        });*/

        tv.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        x = event.getX();
        y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // нажатие
                sDown = "Down: " + x + "," + y;
                sMove = ""; sUp = "";
                break;
            case MotionEvent.ACTION_MOVE: // движение
                sMove = "Move: " + x + "," + y;
                break;
            case MotionEvent.ACTION_UP: // отпускание
            case MotionEvent.ACTION_CANCEL:
                sMove = "";
                sUp = "Up: " + x + "," + y;
                break;
        }
        tv.setText(sDown + "\n" + sMove + "\n" + sUp);
        return true;
    }
}
