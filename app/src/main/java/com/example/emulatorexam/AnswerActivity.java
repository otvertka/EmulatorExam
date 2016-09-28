package com.example.emulatorexam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class AnswerActivity extends AppCompatActivity implements View.OnTouchListener{

    public final static int LAYOUT = R.layout.activity_answer;
    public static final String TAG = "myLogs";

    TextView tv;
    float x;
    float y;
    String sDown;
    String sMove;
    String sUp;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);

        //tv = new TextView(this);
        //tv.setOnTouchListener(this);
        //setContentView(tv);
        setContentView(LAYOUT);
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        initTextView();
    }

    private void initTextView() {
        tv = (TextView) findViewById(R.id.textView);

        Intent intent = getIntent();
        String answer = intent.getStringExtra("position");
        tv.setText(answer);

        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(sp.getString("list" , "24")));

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

        //tv.setOnTouchListener(this);
    }

    @Override
    protected void onResume() {
        //float textSize = sp.getInt("list" , 24);
        //tv.setTextSize(sp.getInt("list" , 24));
        super.onResume();
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
