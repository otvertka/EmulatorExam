package com.example.emulatorexam;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity{




    private static final int FILE_SELECT_CODE = 0;
    Button buttonNew;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

        buttonNew = (Button) findViewById(R.id.buttonNew);
        buttonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/*");
       // intent.addCategory(Intent.CATEGORY_OPENABLE);//толком не понял для чего это, работает и без него)

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String[] toTest;

        Log.d("myLogs", "requestCode = " + requestCode + ", resultCode = " + resultCode);

        if(requestCode == FILE_SELECT_CODE){

            if(resultCode == RESULT_OK){

                String file = data.getData().getPath();

                File sdFile = new File(file);
                String result = "";
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sdFile), "Windows-1251"));
                    String str;

                    while ((str = reader.readLine()) != null) {
                        Log.d("myLogs", "Line - " + str);

                        result += str;
                    }
                    reader.close();
                }  catch (IOException e) {e.printStackTrace();}

               toTest = result.split("~");


                textView.setText(toTest[2]);
            } else Toast.makeText(this, "Файл не выбран.", Toast.LENGTH_SHORT).show();
        }

    }
}















