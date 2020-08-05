package com.amandhiraj.weekzero;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Activity_Login extends AppCompatActivity {


    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    RelativeLayout rellay1;
    SharedPreferences sp;

    private  EditText passVal;
    private Button login_btn;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //check login status
            sp = getSharedPreferences("login",MODE_PRIVATE);
            if(isNetworkAvailable() == true){
                if(sp.getBoolean("logged",false)){
                    goToMainActivity();
                } else {
                    rellay1.setVisibility(View.VISIBLE);
                }
            } else {

                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "NO NETWORK! PLEASE CONNECT TO WIFI OR DATA", Snackbar.LENGTH_LONG);
                View view = snackbar.getView();
                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                view.setLayoutParams(params);
                view.setBackgroundColor(Color.parseColor("#ff6b6b"));
                TextView mainTextView = (TextView) (view).findViewById(android.support.design.R.id.snackbar_text);
                mainTextView.setTextColor(Color.WHITE);

                snackbar.show();
                rellay1.setVisibility(View.VISIBLE);
            }

        }
    };
    Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_login);
        myDialog = new Dialog(this);
        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);
        handler.postDelayed(runnable, 2000); //2000 is the timeout for the splash

        login_btn = (Button) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openScanner();
            }
        });
    }
    public void goToMainActivity() {
        Intent i = new Intent(this, Scanner.class);
        startActivity(i);
    }
    public void openScanner() {
        new AsyncRetrieve().execute();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private class AsyncRetrieve extends AsyncTask<String, String, String> {
        // create a ProgressDialog instance, with a specified theme:
        ProgressDialog pdLoading = new ProgressDialog(Activity_Login.this, ProgressDialog.THEME_HOLO_DARK);
        HttpURLConnection conn;
        URL url = null;

        //this method will interact with UI, here display loading message
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        // This method does not interact with UI, You need to pass result to onPostExecute to display
        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL("https://localhost/appserver/auth/");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {

                // Setup HttpURLConnection class to send and receive data from php
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");

                // setDoOutput to true as we recieve data from json file
                conn.setDoOutput(true);

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }


        }

        // this method will interact with UI, display result sent from doInBackground method
        @Override
        protected void onPostExecute(String result) {

            pdLoading.dismiss();

            passVal = (EditText) findViewById(R.id.passwordVal);
            if(isNetworkAvailable() == true){
                if(result.equals(passVal.getText().toString())){
                    sp.edit().putBoolean("logged",true).apply();
                    Intent scannerAcv = new Intent(Activity_Login.this, Scanner.class);
                    scannerAcv.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    scannerAcv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(scannerAcv);

                } else {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(android.R.id.content), "WRONG PASSWORD", Snackbar.LENGTH_LONG);
                    View view = snackbar.getView();
                    FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                    view.setLayoutParams(params);
                    view.setBackgroundColor(Color.parseColor("#ff6b6b"));
                    TextView mainTextView = (TextView) (view).findViewById(android.support.design.R.id.snackbar_text);
                    mainTextView.setTextColor(Color.WHITE);

                    snackbar.show();
                }
            } else {
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "NO NETWORK! PLEASE CONNECT TO WIFI OR DATA", Snackbar.LENGTH_LONG);
                View view = snackbar.getView();
                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                view.setLayoutParams(params);
                view.setBackgroundColor(Color.parseColor("#ff6b6b"));
                TextView mainTextView = (TextView) (view).findViewById(android.support.design.R.id.snackbar_text);
                mainTextView.setTextColor(Color.WHITE);

                snackbar.show();
            }
        }

    }
}
