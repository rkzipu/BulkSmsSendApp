package com.zipu.bulksmssender;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private EditText edtUrl;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();        
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = edtUrl.getText().toString();
                if(isSmsPermissionGranted()){
                    if (url != null&&!url.isEmpty()) {
                        startSendingSms(url);
                    }else {
                        Toast.makeText(getApplicationContext(),"Enter url first",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    requestReadAndSendSmsPermission();
                }

            }
        });
    }


    /**
     * Check if we have SMS permission
     */
    public boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request runtime SMS permission
     */
    private void requestReadAndSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            // You may display a non-blocking explanation here, read more in the documentation:
            // https://developer.android.com/training/permissions/requesting.html
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1002);
    }
    private void startSendingSms(String url) {
        progressDialog.show();
        OkHttpClient client=new OkHttpClient();
        final Request request=new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressDialog.dismiss();
                Gson gson = new Gson();
                Source source=gson.fromJson(response.body().string(), Source.class);
                sendSms(source);

            }
        });
    }

    private void init() {
        edtUrl=(EditText)findViewById(R.id.edtUrl);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading ......");
        progressDialog.setCancelable(false);
    }

    final Handler handler=new Handler();
    private void sendSms(final Source source) {

        ArrayList<String> numbers = source.getNumbers();
        for (final String number:numbers) {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    sendSmsNow(source.getMessage(),number);
                }
            };
            handler.postDelayed(runnable,100);
            Log.d(TAG, "sendSms() called with: source = [" + source + "]");
        }

    }


    private void sendSmsNow(String message, String number) {
        SmsManager.getDefault().sendTextMessage(number, null, message, null, null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
