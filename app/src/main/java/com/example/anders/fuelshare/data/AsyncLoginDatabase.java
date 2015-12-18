package com.example.anders.fuelshare.data;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.TextView;

import com.example.anders.fuelshare.R;
import com.example.anders.fuelshare.logIn.LogInAct;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Lars on 14-12-2015.
 */
public class AsyncLoginDatabase extends AsyncTask<Void, Void, String> {
    LogInAct mActivity;
    EditText TVusername;
    String username;
    EditText TVpassword;
    String password;
    boolean flag = false;

    public AsyncLoginDatabase(Activity activity){mActivity = (LogInAct) activity;}

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        TVusername = (EditText) mActivity.findViewById(R.id.login_user_et);
        username = TVusername.getText().toString();
        TVpassword = (EditText) mActivity.findViewById(R.id.login_password_et);
        password = TVpassword.getText().toString();
    }

    @Override
    protected String doInBackground(Void... params) {

        try{
            String link="http://larspeter.mfrid.com/users.php";
            String data  = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write( data );
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                sb.append(line);
                break;
            }
            System.out.println("here: " + sb);
            if(sb.toString().equals("exists")){
                flag = true;
                publishProgress();
            } else {
                flag = false;
                System.out.println("herewhat");
            }
            return sb.toString();
        }
        catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        if(flag) {
            mActivity.login();
        } else {
            mActivity.loginFail();
        }
        super.onProgressUpdate(values);
    }
}
