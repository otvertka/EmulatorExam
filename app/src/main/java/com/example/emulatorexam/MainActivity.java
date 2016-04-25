package com.example.emulatorexam;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myLogs";
    final String SAVED_EXAM = "saved_exam";
    final String SAVED_NAME = "saved_name";
    private static final int LAYOUT = R.layout.activity_main;
    private static final int FILE_SELECT_CODE_QUESTION = 1;
    private static final int FILE_SELECT_CODE_ANSWER = 2;
    private static final int FILE_SELECT_CODE_QUESTION_ANSWER = 3;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ListView listView;

    public SharedPreferences sPref;
    public SharedPreferences sPrefName;

    String result = "";

    public ArrayList<String> examNameList = new ArrayList<>();
    ListAdapter adapter;

    public boolean choiceQA = false;//если тру, то тогда в строке "result" заложены и ответы и вопросы
    public boolean choiceA = false;
    public boolean choiceQ = false;
    public String[] questions;
    public String[] answers;
    public ArrayList<String> answerArray = new ArrayList<>();


    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;


    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        loadNamesExam();
        initToolbar();
        initNavigationView();
        initListView();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        //toolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(toolbar);//для поддержки старых версий, вроде как) с этим говном не показываются менюшки выше

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.saveList:
                        showDialog();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);

        if(searchItem != null){
            searchView = (SearchView) searchItem.getActionView();//MenuItemCompat.getActionView(searchItem);
        }
        if(searchView != null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //Log.d("myLogs", query + " Отправлено");

                    pleaseSearchIt(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    pleaseSearchIt(newText);

                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void pleaseSearchIt(String searchText){
        ArrayList<String> outputArrayQuestions = new ArrayList<>();
        int k = 0;

        for (int i = 0; i < questions.length; i++) {
            if(questions[i].contains(searchText))
            {
                outputArrayQuestions.add(questions[i]);
                answerArray.add(k, answers[i]);
                k++;
            }
        }
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, outputArrayQuestions));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                // Not implemented here
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    public class ListDialog extends DialogFragment{
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Список экзаменов: ")
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadList((examNameList.get(which)));
                           // Log.d(TAG, "List adapter  " + which);
                        }
                    });

            final AlertDialog ad = builder.create();
            ad.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    ListView lv = ad.getListView();
                    lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
                            final AlertDialog.Builder builderTwo = new AlertDialog.Builder(MainActivity.this);
                            builderTwo.setTitle("Do you want to delete?");
                            builderTwo.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    examNameList.remove(position);

                                    sPrefName = getSharedPreferences(SAVED_NAME, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sPrefName.edit();
                                    editor.putInt("Status_size", examNameList.size());
                                    for (int i = 0; i < examNameList.size(); i++) {
                                        editor.remove("Status_" + i);
                                        editor.putString("Status_" + i, examNameList.get(i));
                                    }
                                    editor.apply();

                                    ad.cancel();
                                }
                            });
                            builderTwo.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(MainActivity.this, "Item is not Deleted.", Toast.LENGTH_SHORT).show();
                                }
                            });

                            AlertDialog dialog = builderTwo.create();
                            dialog.show();
                            return true;
                        }
                    });
                }
            });
            return ad;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, v, menuInfo);
        }
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
                        break;
                }
                return false;
            }
        });
    }

    private void initListView() {
        listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AnswerActivity.class);

                Bundle b = new Bundle();

                /*if (answers == null) {
                    toast();
                } else if (answers.length > position) {
                    b.putString("position", answers[position]);
                    intent.putExtras(b);
                    startActivity(intent);
                } else toast();*/ //было раньше со статичным массивом

                if (answerArray == null) {
                    toast();
                } else if (answerArray.size() > position) {
                    b.putString("position", answerArray.get(position));
                    intent.putExtras(b);
                    startActivity(intent);
                }
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

    public void saveList(String nameExam){

        sPref = getSharedPreferences(SAVED_EXAM, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        String resultTwo = "";
        if(choiceQA){
            editor.putString(nameExam, result);
            editor.apply();
            saveName(nameExam);
        }
        else if (choiceA && choiceQ) {
            for (int i = 0; i < questions.length; i++) {
                resultTwo += questions[i] + "/" + answers[i] + " ~";
            }
            editor.putString(nameExam, resultTwo);
            editor.apply();
            saveName(nameExam);
        }
        else if (!choiceA && !choiceQ){
            Toast.makeText(this, "Для сохранения добавьте вопросы и ответы", Toast.LENGTH_SHORT).show();
        }
        else if (choiceQ){
            Toast.makeText(this, "Для сохранения добавьте ответы", Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(this, "Для сохранения добавьте вопросы", Toast.LENGTH_SHORT).show();
    }

    private void saveName(String nameExam) {

        examNameList.add(nameExam);

        sPrefName = getSharedPreferences(SAVED_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPrefName.edit();
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
        String together[] = s.toLowerCase().split(getString(R.string.spliter));
        if (answerArray != null){
            answerArray.clear();
        }
        answers = null;
        questions = null;
        answers = new String[together.length];
        questions = new String[together.length];
        for (int i = 0; i < together.length; i++) {
            String togetherString[] = together[i].split("/");
            questions[i] = togetherString[0];
            answers[i] = togetherString[1];
            answerArray.add(togetherString[1]);
        }
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, questions));
    }

    private void loadNamesExam(){

        sPrefName = getSharedPreferences(SAVED_NAME, Context.MODE_PRIVATE);

        examNameList.clear();

        int size = sPrefName.getInt("Status_size", 0);

        for(int i=0;i<size;i++)
        {
            examNameList.add(sPrefName.getString("Status_" + i, null));
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, examNameList);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_SELECT_CODE_QUESTION) {
            if (resultCode == RESULT_OK) {
                choiceQ = true;
                choiceQA = false;
                String result, file;
                file = data.getData().getPath();
                result = loadingIntoString(file);

                questions = null;
                listView.setAdapter(null);
                questions = result.split(getString(R.string.spliter));
                listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, questions));
                listView.setTextFilterEnabled(true);//не понял для чего именно этот фильтер..

            } else Toast.makeText(this, "Файл с вопросами не выбран.", Toast.LENGTH_SHORT).show();
        }

        else if (requestCode == FILE_SELECT_CODE_ANSWER) {
            if (resultCode == RESULT_OK) {
                choiceA = true;
                choiceQA = false;
                String result, file;
                file = data.getData().getPath();
                result = loadingIntoString(file);

                answers = null;
                answers = result.split(getString(R.string.spliter));

                if (answerArray != null){
                    answerArray.clear();
                }
/*
                было
                for (int i = 0; i < answers.length; i++) {
                    answerArray.add(answers[i]);
                }
*/
                Collections.addAll(answerArray, answers);// стало
            } else
                Toast.makeText(this, "Файл с ответами не выбран.", Toast.LENGTH_SHORT).show();
        }

        else if (requestCode == FILE_SELECT_CODE_QUESTION_ANSWER) {
            if (resultCode == RESULT_OK) {
                choiceQA = true;
                choiceA = false;
                choiceQ = false;
                String result, file;
                file = data.getData().getPath();
                result = loadingIntoString(file);

                String together[] = result.split(getString(R.string.spliter));
                answers = null;
                questions = null;
                if (answerArray != null){
                    answerArray.clear();
                }
                answers = new String[together.length];
                questions = new String[together.length];
                for (int i = 0; i < together.length; i++) {
                    String togetherString[] = together[i].split("/");
                    questions[i] = togetherString[0];
                    answers[i] = togetherString[1];
                    answerArray.add(togetherString[1]);
                }
                listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, questions));
            } else
                Toast.makeText(this, "Файл с вопросами и ответами  не выбран.", Toast.LENGTH_SHORT).show();
        }
    }

    public String loadingIntoString(String s) {

        result = "";
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
        return result.toLowerCase();
    }

    public void toast() {
        Toast.makeText(this, "Ответ на этот вопрос не добавлен.", Toast.LENGTH_SHORT).show();
    }

    public void showDialog(){
        FragmentManager manager = getSupportFragmentManager();
        MyDialog myDialog = new MyDialog();
        myDialog.show(manager, "My Dialog");
    }
}