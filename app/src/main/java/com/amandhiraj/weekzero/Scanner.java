package com.amandhiraj.weekzero;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Scanner extends AppCompatActivity {
    RelativeLayout rellay2;

    private Button login_btn;
    private String qrscanVal;
    Dialog myDialog;
    Button scanbtn;
    TextView result, stdNameValue, stdNumberValue, stdQrValue, version;
    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scanner);
        myDialog = new Dialog(this);
        rellay2 = (RelativeLayout) findViewById(R.id.rellay2);
        scanbtn = (Button) findViewById(R.id.scanbtn);
       /* result = (TextView) findViewById(R.id.result);*/
        version  = (TextView) findViewById(R.id.verVal);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version.setText("Version: " + pInfo.versionName);
            int versionCode = pInfo.versionCode;
        }catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        rellay2.setVisibility(View.VISIBLE);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        }
        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Scanner.this, Scanner_Camera.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            if(data != null){
                final Barcode barcode = data.getParcelableExtra("barcode");
                version.post(new Runnable() {
                    @Override
                    public void run() {
                        qrscanVal = barcode.displayValue;
                        ShowPopup();
                        new AsyncRetrieve().execute();
                    }
                });
            }
        }
    }
    public void ShowPopup() {
        TextView txtclose;
        Button btnFollow;
        myDialog.setContentView(R.layout.result_popup);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        //txtclose.setText("M");
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
    private class AsyncRetrieve extends AsyncTask<String, String, String> {
        // create a ProgressDialog instance, with a specified theme:
        ProgressDialog pdLoading = new ProgressDialog(Scanner.this, ProgressDialog.THEME_HOLO_DARK);
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
                url = new URL("" + qrscanVal);

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
            stdNameValue =(TextView) myDialog.findViewById(R.id.student_name);
            stdNumberValue =(TextView) myDialog.findViewById(R.id.student_number);
            stdQrValue =(TextView) myDialog.findViewById(R.id.student_qr);

            Switch toggle1 = (Switch) myDialog.findViewById(R.id.event1);
            Switch toggle2 = (Switch) myDialog.findViewById(R.id.event2);
            Switch toggle3 = (Switch) myDialog.findViewById(R.id.event3);
            Switch toggle4 = (Switch) myDialog.findViewById(R.id.event4);
            Switch toggle5 = (Switch) myDialog.findViewById(R.id.event5);
            Switch toggle6 = (Switch) myDialog.findViewById(R.id.event6);
            Switch toggle7 = (Switch) myDialog.findViewById(R.id.event7);

            TextView toggle1text = (TextView) myDialog.findViewById(R.id.event1text);
            TextView toggle2text = (TextView) myDialog.findViewById(R.id.event2text);
            TextView toggle3text = (TextView) myDialog.findViewById(R.id.event3text);
            TextView toggle4text = (TextView) myDialog.findViewById(R.id.event4text);
            TextView toggle5text = (TextView) myDialog.findViewById(R.id.event5text);
            TextView toggle6text = (TextView) myDialog.findViewById(R.id.event6text);
            TextView toggle7text = (TextView) myDialog.findViewById(R.id.event7text);
            //Toast.makeText(Scanner.this, "" + result, Toast.LENGTH_SHORT).show();
            try {
                JSONArray jsonarray = new JSONArray(result);
                if (jsonarray.length() != 0) {
                JSONObject jsonobject = jsonarray.getJSONObject(0);
                    final String stdName =jsonobject.getString("studentName");
                    String stdNumber = jsonobject.getString("studentNumber");
                    String stdQR = jsonobject.getString("studentQR");
                    String slPart = jsonobject.getString("slParty");
                    String btneCarnival = jsonobject.getString("btneCarnival");
                    String foundParty = jsonobject.getString("foundParty");
                    String lassLounge = jsonobject.getString("lassLounge");
                    String macParty = jsonobject.getString("MacQP");
                    String stongPP = jsonobject.getString("stongPP");
                    String ncFest = jsonobject.getString("ncFest");

                    stdNameValue.setText("Name: " + stdName);
                    stdNumberValue.setText("#: " + stdNumber);
                    stdQrValue.setText("QR code: " + stdQR);

                    //load data from database
                    if(slPart.equals("x")) {

                        toggle1text.setText("✘");
                        toggle1text.setTextColor(Color.parseColor("#ff6b6b"));
                        toggle1.setChecked(false);
                    } else {
                        toggle1text.setText("✓");
                        toggle1text.setTextColor(Color.parseColor("#2ecc71"));
                        toggle1.setChecked(true);
                    }

                    if( btneCarnival.equals("x") ){
                        toggle2text.setText("✘");
                        toggle2text.setTextColor(Color.parseColor("#ff6b6b"));
                        toggle2.setChecked(false);
                    } else {
                        toggle2text.setText("✓");
                        toggle2text.setTextColor(Color.parseColor("#2ecc71"));
                        toggle2.setChecked(true);

                    }
                    if(foundParty.equals("x")){
                        toggle3text.setText("✘");
                        toggle3text.setTextColor(Color.parseColor("#ff6b6b"));
                        toggle3.setChecked(false);
                    } else {
                        toggle3text.setText("✓");
                        toggle3text.setTextColor(Color.parseColor("#2ecc71"));
                        toggle3.setChecked(true);
                    }
                    if(lassLounge.equals("x")){
                        toggle4text.setText("✘");
                        toggle4text.setTextColor(Color.parseColor("#ff6b6b"));
                        toggle4.setChecked(false);
                    } else {
                        toggle4text.setText("✓");
                        toggle4text.setTextColor(Color.parseColor("#2ecc71"));
                        toggle4.setChecked(true);
                    }
                    if(macParty.equals("x")){
                        toggle6text.setText("✘");
                        toggle6text.setTextColor(Color.parseColor("#ff6b6b"));
                        toggle6.setChecked(false);
                    } else {
                        toggle6text.setText("✓");
                        toggle6text.setTextColor(Color.parseColor("#2ecc71"));
                        toggle6.setChecked(true);
                    }
                    if(stongPP.equals("x")){
                        toggle5text.setText("✘");
                        toggle5text.setTextColor(Color.parseColor("#ff6b6b"));
                        toggle5.setChecked(false);
                    } else {
                        toggle5text.setText("✓");
                        toggle5text.setTextColor(Color.parseColor("#2ecc71"));
                        toggle5.setChecked(true);
                    }
                    if(ncFest.equals("x")){
                        toggle7text.setText("✘");
                        toggle7text.setTextColor(Color.parseColor("#ff6b6b"));
                        toggle7.setChecked(false);
                    } else {
                        toggle7text.setText("✓");
                        toggle7text.setTextColor(Color.parseColor("#2ecc71"));
                        toggle7.setChecked(true);
                    }


                    //update database
                    toggle1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        TextView toggle1text = (TextView) myDialog.findViewById(R.id.event1text);
                        //Toast.makeText(Scanner.this, "" + result, Toast.LENGTH_SHORT).show();
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                new AsyncPost("slParty", "yes").execute();
                                toggle1text.setText("✓");
                                toggle1text.setTextColor(Color.parseColor("#2ecc71"));
                            } else {
                                new AsyncPost("slParty", "no").execute();
                                toggle1text.setText("✘");
                                toggle1text.setTextColor(Color.parseColor("#ff6b6b"));
                            }
                        }
                    });

                    toggle2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        TextView toggle2text = (TextView) myDialog.findViewById(R.id.event2text);
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                new AsyncPost("btneCarnival", "yes").execute();
                                toggle2text.setText("✓");
                                toggle2text.setTextColor(Color.parseColor("#2ecc71"));
                            } else {
                                new AsyncPost("btneCarnival", "no").execute();
                                toggle2text.setText("✘");
                                toggle2text.setTextColor(Color.parseColor("#ff6b6b"));
                            }
                        }
                    });
                    toggle3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            TextView toggle3text = (TextView) myDialog.findViewById(R.id.event3text);
                            if (isChecked) {
                                new AsyncPost("foundParty", "yes").execute();
                                toggle3text.setText("✓");
                                toggle3text.setTextColor(Color.parseColor("#2ecc71"));
                            } else {
                                new AsyncPost("foundParty", "no").execute();
                                toggle3text.setText("✘");
                                toggle3text.setTextColor(Color.parseColor("#ff6b6b"));
                            }
                        }
                    });
                    toggle4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            TextView toggle4text = (TextView) myDialog.findViewById(R.id.event4text);
                            if (isChecked) {
                                new AsyncPost("lassLounge", "yes").execute();
                                toggle4text.setText("✓");
                                toggle4text.setTextColor(Color.parseColor("#2ecc71"));
                            } else {
                                new AsyncPost("lassLounge", "no").execute();
                                toggle4text.setText("✘");
                                toggle4text.setTextColor(Color.parseColor("#ff6b6b"));
                            }
                        }
                    });
                    toggle5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            TextView toggle5text = (TextView) myDialog.findViewById(R.id.event5text);
                            if (isChecked) {
                                new AsyncPost("stongPP", "yes").execute();
                                toggle5text.setText("✓");
                                toggle5text.setTextColor(Color.parseColor("#2ecc71"));
                            } else {
                                new AsyncPost("stongPP", "no").execute();
                                toggle5text.setText("✘");
                                toggle5text.setTextColor(Color.parseColor("#ff6b6b"));
                            }
                        }
                    });
                    toggle6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            TextView toggle6text = (TextView) myDialog.findViewById(R.id.event6text);
                            if (isChecked) {
                                new AsyncPost("MacQP", "yes").execute();
                                toggle6text.setText("✓");
                                toggle6text.setTextColor(Color.parseColor("#2ecc71"));
                            } else {
                                new AsyncPost("MacQP", "no").execute();
                                toggle6text.setText("✘");
                                toggle6text.setTextColor(Color.parseColor("#ff6b6b"));
                            }
                        }
                    });
                    toggle7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            TextView toggle7text = (TextView) myDialog.findViewById(R.id.event7text);
                            if (isChecked) {
                                new AsyncPost("ncFest", "yes").execute();
                                toggle7text.setText("✓");
                                toggle7text.setTextColor(Color.parseColor("#2ecc71"));
                            } else {
                                new AsyncPost("ncFest", "no").execute();
                                toggle7text.setText("✘");
                                toggle7text.setTextColor(Color.parseColor("#ff6b6b"));
                            }
                        }
                    });


                } else {
                    stdNameValue.setText("ERROR");
                    stdNumberValue.setText("ERROR");
                    stdQrValue.setText("ERROR");
                    myDialog.dismiss();
                    Snackbar snackbar = Snackbar
                            .make(findViewById(android.R.id.content), "The QR Code does not exist in the database", Snackbar.LENGTH_LONG);
                    View view = snackbar.getView();
                    FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                    view.setLayoutParams(params);
                    view.setBackgroundColor(Color.parseColor("#ff6b6b"));
                    TextView mainTextView = (TextView) (view).findViewById(android.support.design.R.id.snackbar_text);
                    mainTextView.setTextColor(Color.WHITE);

                    snackbar.show();
                }
            } catch (JSONException e) {
                Log.e("MYAPP", "unexpected JSON exception", e);
                // Do something to recover ... or kill the app.
            }
        }

    }
    /*public void setButtuon() {
        Switch toggle1 = (Switch) myDialog.findViewById(R.id.event1);
        Switch toggle2 = (Switch) findViewById(R.id.event2);
        Switch toggle3 = (Switch) findViewById(R.id.event3);
        Switch toggle4 = (Switch) findViewById(R.id.event4);
        Switch toggle5 = (Switch) findViewById(R.id.event5);
        Switch toggle6 = (Switch) findViewById(R.id.event6);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                } else {
                    // The toggle is disabled
                }
            }
        });
    }*/
    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    //post data
    private class AsyncPost extends AsyncTask<String, String, String> {
        String partyNameVal;
        String updateType;
        public AsyncPost(String partyNam, String updateT) {
            super();
           partyNameVal = partyNam;
           updateType = updateT;
        }


        // create a ProgressDialog instance, with a specified theme:
        ProgressDialog pdLoading = new ProgressDialog(Scanner.this, ProgressDialog.THEME_HOLO_DARK);
        HttpURLConnection conn;
        URL url = null;

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
                url = new URL("" + qrscanVal + "&partyName=" + partyNameVal + "&type=" + updateType);

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

            if(result.equals("ok")){
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "Success", Snackbar.LENGTH_LONG);
                View view = snackbar.getView();
                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                view.setLayoutParams(params);
                view.setBackgroundColor(Color.parseColor("#2ecc71"));
                TextView mainTextView = (TextView) (view).findViewById(android.support.design.R.id.snackbar_text);
                mainTextView.setTextColor(Color.WHITE);

                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "ERROR: Updating data", Snackbar.LENGTH_LONG);
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