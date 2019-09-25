package com.piyu.urlbookmark;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.JsonReader;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
EditText url;
EditText folder_name;
private RecyclerView recyclerView;
private GridLayoutManager gridLayoutManager;
private jsonadapter adapter;
private List<bookmarkedUrl> listurl;

Dialog mydialog;
ProgressBar progressBar;
TextView notavailable;
android.support.v7.widget.Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        mydialog =new Dialog(this);
       recyclerView=(RecyclerView)findViewById(R.id.urldata);
       recyclerView.setHasFixedSize(true);
        new parseurl().execute();
        listurl=new ArrayList<>();
        gridLayoutManager=new GridLayoutManager(this,GridLayoutManager.VERTICAL);
       recyclerView.setLayoutManager(gridLayoutManager);
       adapter=new jsonadapter(this,listurl);
      recyclerView.setAdapter(adapter);





    }
//URL VALIDATION
    private boolean isValidUrl(String url) {

        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }
 //BOOKMARK ADD FORM
    public void showpopup(View view){
        TextView txtclose;

        Button sendata;
        mydialog.setContentView(R.layout.addbookmarkpopup);
        txtclose =(TextView) mydialog.findViewById(R.id.textViewclose);
        txtclose.setText("X");
        url=(EditText) mydialog.findViewById(R.id.url);
        folder_name=(EditText)mydialog.findViewById(R.id.folder_name);
        folder_name.setFilters(new InputFilter[]{
                new InputFilter.AllCaps()
        });
        sendata=(Button) mydialog.findViewById(R.id.sendData);

        sendata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(folder_name.getText().toString().length()==0)
                {
                    folder_name.setText("DEFAULT");
                }
                if(url.getText().toString().length()>0) {
                    if (isValidUrl(url.getText().toString())) {
                        new HTTPAsyncTask().execute("https://frozen-oasis-52735.herokuapp.com/post");
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                        mydialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "Enter Valid Url!", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please Reconfirm Your Data", Toast.LENGTH_LONG).show();
                }

            }
        });
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydialog.dismiss();
            }
        });

        mydialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mydialog.show();
    }
//ASYNC TASK FOR PARSING JSON
   private  class parseurl extends AsyncTask<String,Void,String>
   {


       @Override
       protected String doInBackground(String... strings) {
           OkHttpClient client =new OkHttpClient();
           Request request =new Request.Builder().url("https://frozen-oasis-52735.herokuapp.com/post").build();
           try {
               Response response=client.newCall(request).execute();
               JSONArray array= null;
               try {
                   array = new JSONArray(response.body().string());
               } catch (JSONException e) {

               }
               if(array.length()>0) {
                   for (int i = 0; i < array.length(); i++) {

                       JSONObject array1 = null;
                       JSONArray array2 = null;
                       JSONObject urlfinal = null;
                       try {


                           array1 = array.getJSONObject(i);
                           array2 = array1.getJSONArray("urls");
                           urlfinal = array2.getJSONObject(0);


                           bookmarkedUrl response1 = null;

                           response1 = new bookmarkedUrl(array1.getString("folder_name"), urlfinal.getString("url"));
                           listurl.add(response1);


                       } catch (JSONException e) {
                           e.printStackTrace();
                       }


                   }
               }
               else {
                   Toast.makeText(getApplicationContext(),"NO DATA AVAILABLE",Toast.LENGTH_SHORT).show();
                   notavailable=(TextView)findViewById(R.id.notavailable);
                   notavailable.setVisibility(View.VISIBLE);
               }

           } catch (IOException e) {
               e.printStackTrace();
           }
           return null;
       }



       @Override
       protected void onPostExecute(String s) {
           adapter.notifyDataSetChanged();
           progressBar.setVisibility(View.GONE);

   }}

//ASYNC TASK FOR POST DATA TO SERVER

    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                try {
                    return HttpPost(urls[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "Error!";
                }
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
//            tvResult.setText(result);
        }
    }
    private String HttpPost(String myUrl) throws IOException, JSONException {
        String result = "";

        URL url = new URL(myUrl);

        // 1. create HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        // 2. build JSON object
        JSONObject jsonObject = buidJsonObject();

        // 3. add JSON content to POST request body
        setPostRequestContent(conn, jsonObject);

        // 4. make POST request to the given URL
        conn.connect();

        // 5. return response message
        return conn.getResponseMessage()+"";

    }
    private JSONObject buidJsonObject() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        JSONObject urls=new JSONObject();
        urls.accumulate("url", url.getText().toString());
        jsonObject.accumulate("urls",urls);
        jsonObject.accumulate("folder_name", folder_name.getText().toString());


        return jsonObject;
    }
    private void setPostRequestContent(HttpURLConnection conn,
                                       JSONObject jsonObject) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        Log.i(MainActivity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }
}
