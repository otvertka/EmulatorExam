package com.example.emulatorexam;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DialogScreen{

    public static final int IDD_INPUT_NAME = 1;
    private static String name = "pizda";


    public static void setName(String aName){
        name = aName;
    }

    public static String getName(){
        return name;
    }

    public static AlertDialog getDialog(final Activity activity, int ID) {

        LayoutInflater li = LayoutInflater.from(activity);//getActivity().getLayoutInflater()
        View promptsView = li.inflate(R.layout.name_dialog, null);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        switch (ID) {
            case IDD_INPUT_NAME:
                builder.setView(promptsView);
                builder.setCancelable(false);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setName(userInput.getText().toString());
                        Log.d("myLogs", "DialogScreen  " + name);

                        Toast.makeText(activity, "progato suka 1", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                return builder.create();
            default:
                return null;
        }


    }


}
