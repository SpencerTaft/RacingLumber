package com.example.racinglumber;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class fileManageActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener , View.OnClickListener {
    private BottomNavigationView bottomNavigationView;
    private static String returnString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manage);

        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        Button loadButton = (Button) findViewById(R.id.loadButton);
        loadButton.setOnClickListener(this);

        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_id);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_save_button);
    }

    //////////////////////////File Access Functions//////////////////////////

    private static final int fileSaveRequestCode = 1;
    private static final int fileLoadRequestCode = 2;
    private static final int fileDeleteRequestCode = 3;

    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "saveData.txt");

        startActivityForResult(intent, fileSaveRequestCode);
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        startActivityForResult(intent, fileLoadRequestCode);
    }

    private void deleteFile() { //todo remove all delete functionality
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        startActivityForResult(intent, fileDeleteRequestCode);
    }

    //todo clean this code up, these branches are terrible
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        Uri uri;

        if ((requestCode == fileLoadRequestCode) && (resultCode == Activity.RESULT_OK))
        {
            // The result data contains a URI for the document or directory that the user selected
            if (resultData != null) {
                uri = resultData.getData();
                returnString = uri.toString();
            }
        }
        else if ((requestCode == fileSaveRequestCode) && (resultCode == Activity.RESULT_OK))
        {
            // The result data contains a URI for the document or directory that the user created
            if (resultData != null) {
                writeInFile(resultData.getData(), getEncodedDataString());
            }
        }
        else if ((requestCode == fileDeleteRequestCode) && (resultCode == Activity.RESULT_OK))
        {
            // The result data contains a URI for the document or directory that the user created
            if (resultData != null) {

                uri = resultData.getData();
////////////////////////////////////////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                String fileName = getFileName(uri);

                File dir =new File(getApplicationContext().getExternalFilesDir(null)+"/"+fileName);
                boolean success = dir.delete();

                String path = uri.getPath();
                File filesDir = getFilesDir();
                //File[] files = filesDir.listFiles();

                File file2 = new File(filesDir, path);
////////////////////////////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                File file = new File(uri.getPath());
                //File file2 = new File(getFilesDir(), uri.getPath());
                if (file.exists())
                {
                    deleteFile(uri.getPath());
                }
                else if(file2.exists())
                {
                    file2.delete();
                }
                //////////////////////////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            }
        }
        else
        {
            //do nothing
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void writeInFile(@NonNull Uri uri, @NonNull String text) {
        OutputStream outputStream;
        try {
            outputStream = getContentResolver().openOutputStream(uri);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            bw.write(text);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //////////////////////////File Encoding and Decoding Functions//////////////////////////
    ///////////////////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    ///////////////////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    ///////////////////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    ///////////////////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    ///////////////////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    private String getEncodedDataString()
    {
        //TODO replace with proper encoding
        dataStorage recordedVars;
        String returnString = "";
        int dataArrayLen;
        float accelVal;

        recordedVars = new dataStorage();
        dataArrayLen = recordedVars.getDataArrayLen();

        returnString += Integer.toString(dataArrayLen); //first encode number of data points
        returnString += " ";

        for (int index = 0; index < dataArrayLen; index++)
        {
            accelVal = recordedVars.getValue(dataStorage.Axis.X, dataStorage.RecordType.acceleration, index);
            returnString += Float.toString(accelVal);
            returnString += " ";
        }
        return returnString;
    }
    ///////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    ///////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    ///////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    ///////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    ///////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    //////////////////////////User Interface Functions//////////////////////////

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.saveButton) ///TODO this should be a switch statement
        {
            //Save button clicked
            this.createFile();
        }
        else if (v.getId() == R.id.loadButton)
        {
            //Load button clicked
            this.openFile();
        }
        else //delete button
        {
            this.deleteFile();
           // this.deleteFilePartTwo();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        boolean returnVal = true;

        switch (itemID)
        {
            case R.id.bottom_nav_record_button:
                startActivity(new Intent(fileManageActivity.this, MainActivity.class));
                break;
            case R.id.bottom_nav_graph_button:
                startActivity(new Intent(fileManageActivity.this, graphActivity.class));
                break;
            case R.id.bottom_nav_save_button:
                break;
            default:
                returnVal = false;
                break;
        }

        return returnVal;
    }
}