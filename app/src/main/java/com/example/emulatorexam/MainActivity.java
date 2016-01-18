package com.example.emulatorexam;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AdapterView;//Loot it also
import android.widget.ArrayAdapter;//посмотерть подробнее оь этом
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity{

    private static final int LAYOUT = R.layout.activity_main;
    private static final int FILE_SELECT_CODE_QUESTION = 1;
    private static final int FILE_SELECT_CODE_ANSWER = 2;
    private static final int FILE_SELECT_CODE_QUESTION_ANSWER = 3;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ListView listView;

    public String questions[] = {"Вы ничего не добавли", "аываываы", "dfgdfgdfgdfghdf", "g", "dfg", "Вы ничего не добавли", "аываываы", "dfgdfgdfgdfghdf", "g", "dfg", "Вы ничего не добавли", "аываываы", "dfgdfgdfgdfghdf", "g", "dfg"};
    public String answers[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initToolbar();
        initNavigationView();

        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, questions));

        //обработчик для глиста
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                //Позиция элемента, по которому щелкнули
                String number = Integer.toString(position);

                Intent intent = new Intent();

                intent.setClass(MainActivity.this, ListActivity.class);

                Bundle b = new Bundle();

                //defStrID содержит строку, которую отправим через itemname в другое Activity
                b.putString("position", number);

                intent.putExtras(b);

                //запускаем Intent
                startActivity(intent);
            }
        });*/
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        toolbar.inflateMenu(R.menu.menu);
    }

    private void initNavigationView() {

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.view_navigation_open, R.string.view_navigation_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                String s;
                drawerLayout.closeDrawers();
                switch (item.getItemId()){
                    case R.id.addQuestion:
                        s = "ВОПРОСАМИ";
                        showFileChooser(s, FILE_SELECT_CODE_QUESTION);
                        break;
                    case R.id.addAnswer:
                        s = "ОТВЕТАМИ";
                        showFileChooser(s, FILE_SELECT_CODE_ANSWER);
                        break;
                }

                return false;
            }
        });
    }

    private void showFileChooser(String s, int code) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/*");
       // intent.addCategory(Intent.CATEGORY_OPENABLE);//толком не понял для чего это, работает и без него)

        try {
            startActivityForResult(Intent.createChooser(intent, "Выберете файл с " + s),code);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("myLogs", "requestCode = " + requestCode + ", resultCode = " + resultCode);

        if (requestCode == FILE_SELECT_CODE_QUESTION) {

            if (resultCode == RESULT_OK) {

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
                } catch (IOException e) {
                    e.printStackTrace();
                }

                questions = result.split("~");

                listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, questions));
                listView.setTextFilterEnabled(true);

            } else Toast.makeText(this, "Файл не выбран.", Toast.LENGTH_SHORT).show();
        } else if (requestCode == FILE_SELECT_CODE_ANSWER) {
            if (resultCode == RESULT_OK) {

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
                } catch (IOException e) {
                    e.printStackTrace();
                }

                answers = result.split("~");
            }


        }

    }
}















