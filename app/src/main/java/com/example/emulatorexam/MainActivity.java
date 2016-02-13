package com.example.emulatorexam;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    final String SAVED_EXAM = "saved_exam";
    private static final int LAYOUT = R.layout.activity_main;
    private static final int FILE_SELECT_CODE_QUESTION = 1;
    private static final int FILE_SELECT_CODE_ANSWER = 2;
    private static final int FILE_SELECT_CODE_QUESTION_ANSWER = 3;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ListView listView;

    SharedPreferences sPref;

    String result = "";

    public ArrayList<String> examNameList = new ArrayList<>();
    ListAdapter adapter;

    public boolean choice = false;//если тру, то тогда в строке "result" заложены и ответы и вопросы
    public String questions[];
    public String answers[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        loadNamesExam();
        initToolbar();
        initNavigationView();
        initListView();

    }

    public class ListDialog extends DialogFragment{
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("My list exam ")
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadList((examNameList.get(which)));
                            Log.d("myLogs", "List adapter  " + which);
                        }
                    });
            return builder.create();
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.saveList:
                        saveList();
                        break;
                    case R.id.loadList:
                        break;
                    case R.id.search:
                        showDialog();
                        break;
                }
                return false;
            }
        });
    }

    private void initNavigationView() {

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.view_navigation_open, R.string.view_navigation_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                String s;
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.addQuestion:
                        s = "ВОПРОСАМИ";
                        showFileChooser(s, FILE_SELECT_CODE_QUESTION);
                        break;
                    case R.id.addAnswer:
                        s = "ОТВЕТАМИ";
                        showFileChooser(s, FILE_SELECT_CODE_ANSWER);
                        break;
                    case R.id.addQuestionAnswer:
                        s = "Вопросами и Ответами!";
                        showFileChooser(s, FILE_SELECT_CODE_QUESTION_ANSWER);
                        break;
                    case R.id.showList:
                        ListDialog listDialog = new ListDialog();
                        listDialog.show(getSupportFragmentManager(), "My List Dialog");
                        Log.d("myLogs", "showList прожат  ");
                        break;
                }
                return false;
            }
        });
    }

    private void initListView() {
        listView = (ListView) findViewById(R.id.listView);
        //listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, questions));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AnswerActivity.class);

                Bundle b = new Bundle();

                if (answers == null) {
                    toast();
                } else if (answers.length > position) {
                    b.putString("position", answers[position]);
                    intent.putExtras(b);
                    startActivity(intent);
                } else toast();
            }
        });
    } // разобраться с адаптером!

    private void showFileChooser(String s, int code) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/*"); //поработай над этим!!!

        // intent.addCategory(Intent.CATEGORY_OPENABLE);//толком не понял для чего это, работает и без него)

        try {
            startActivityForResult(Intent.createChooser(intent, "Выберете файл с " + s), code);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    } // работа.

    public void saveList(){
        sPref = getSharedPreferences(SAVED_EXAM, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString("fullExam", result);
        editor.apply();
    }

    public void saveList(String nameExam){
        sPref = getSharedPreferences(SAVED_EXAM, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        String resultTwo = "";
        if (!choice){
            for (int i = 0; i < questions.length; i++) {
                resultTwo += questions[i] + "/" + answers[i] + " ~";
                editor.putString(nameExam, resultTwo);
            }
        } else {
        editor.putString(nameExam, result);
        }
        editor.apply();
        saveName(nameExam);
    }

    private void saveName(String nameExam){

        examNameList.add(nameExam);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("Status_size", examNameList.size());

        for (int i = 0; i < examNameList.size(); i++) {
            editor.remove("Status_" + i);
            editor.putString("Status_" + i, examNameList.get(i));
        }

        editor.apply();

        Toast.makeText(this, nameExam + " сохранено в MainActivity" , Toast.LENGTH_SHORT).show();

    }

    private void loadList(String nameExam) {

        sPref = getSharedPreferences(SAVED_EXAM, Context.MODE_PRIVATE);
        String s = sPref.getString(nameExam, "");

        /*****************Этот код уже есть */
        String together[] = s.split(getString(R.string.spliter));
        String q[] = new String[together.length];
        String a[] = new String[together.length];
        for (int i = 0; i < together.length; i++) {
            String togetherString[] = together[i].split("/");
            q[i] = togetherString[0];
            a[i] = togetherString[1];
        }
        answers = a;
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, q));
    }

    private void loadNamesExam(){
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(this);
        examNameList.clear();
        int size = mSharedPreference1.getInt("Status_size", 0);

        for(int i=0;i<size;i++)
        {
            examNameList.add(mSharedPreference1.getString("Status_" + i, null));
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, examNameList);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_SELECT_CODE_QUESTION) {
            if (resultCode == RESULT_OK) {
                choice = false;
                String result, file;
                file = data.getData().getPath();
                result = loadingIntoString(file);

                questions = result.split(getString(R.string.spliter));
                listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, questions));
                listView.setTextFilterEnabled(true);//не понял для чего именно этот фильтер..

            } else Toast.makeText(this, "Файл с вопросами не выбран.", Toast.LENGTH_SHORT).show();
        }

        else if (requestCode == FILE_SELECT_CODE_ANSWER) {
            if (resultCode == RESULT_OK) {
                choice = false;
                String result, file;
                file = data.getData().getPath();
                result = loadingIntoString(file);

                answers = result.split(getString(R.string.spliter));
            } else
                Toast.makeText(this, "Файл с ответами не выбран.", Toast.LENGTH_SHORT).show();
        }

        else if (requestCode == FILE_SELECT_CODE_QUESTION_ANSWER) {
            if (resultCode == RESULT_OK) {
                choice = true;
                String result, file;
                file = data.getData().getPath();
                result = loadingIntoString(file);

                String together[] = result.split(getString(R.string.spliter));
                String q[] = new String[together.length];
                String a[] = new String[together.length];
                for (int i = 0; i < together.length; i++) {
                    String togetherString[] = together[i].split("/");
                    q[i] = togetherString[0];
                    a[i] = togetherString[1];
                }
                answers = a;
                listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, q));
            } else
                Toast.makeText(this, "Файл с вопросами и ответами  не выбран.", Toast.LENGTH_SHORT).show();
        }
    }

    public String loadingIntoString(String s) {

        File sdFile = new File(s);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sdFile), "Windows-1251"));
            String str;

            while ((str = reader.readLine()) != null) {
                result += str;
            }
            reader.close();
        } catch (IOException e) {
            Toast.makeText(this, "Невозможно прочитать данный файл!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return result;
    }

    public void toast() {
        Toast.makeText(this, "Ответ на этот вопрос не добавлен.", Toast.LENGTH_SHORT).show();
    }

    public void saveListOld() {
        /*Log.d("myLogs", "MainActivity  " + fileName);
            AlertDialog dialog = DialogScreen.getDialog(this, DialogScreen.IDD_INPUT_NAME);
            assert dialog != null;
            dialog.show();
            fileName = DialogScreen.getName();*/
        Log.d("myLogs", "Вход в saveList  ");
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.name_dialog, null);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(promptsView);
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String fileName = userInput.getText().toString();//дучше перенсти это говнго в трай и в дальнейше сделать через сеттер!
                try {
                    Log.d("myLogs", "Я вошел в ебаный TRY! fileName = " + fileName);

                    File file = new File(fileName);
                    String pizda = file.getCanonicalPath();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(openFileOutput(fileName, MODE_PRIVATE)));
                    //BufferedWriter writer = new BufferedWriter(new FileWriter(file));

                    Log.d("myLogs", "Абсолютный путь к фалу file: " + pizda);
                    Log.d("myLogs", "Это ГОВНО пока не сохранилось сохранилось! )");

            /*for (int i = 0; i < answers.length; i++){
                writer.write(questions[i] + "/");
                writer.write(answers[i] + "\n");
            }*/
                    writer.write("I HATE ebanuh suk");
                    writer.close();
                    Log.d("myLogs", "Это ГОВНО сохранилось! )");

                } catch (IOException e) {
                    Log.d("myLogs", "Это все изза меня!!!  СATCH");
                    e.printStackTrace();
                }
                //Log.d("myLogs", "Кнопка сохранить была нажата 1 " + s);
                sPref = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString(SAVED_EXAM, "/" + fileName);
                ed.apply();
                Log.d("myLogs", "Кнопка сохранить была нажата 2");
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showDialog(){
        FragmentManager manager = getSupportFragmentManager();
        MyDialog myDialog = new MyDialog();
        myDialog.show(manager, "My Dialog");
    }
}















