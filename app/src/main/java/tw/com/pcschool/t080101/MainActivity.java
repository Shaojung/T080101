package tw.com.pcschool.t080101;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(){
            public void run(){
                String strurl = "http://udn.com/rssfeed/news/1";
                try {
                    URL url = new URL(strurl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    BufferedReader r = new BufferedReader(new InputStreamReader(is));

                    StringBuilder sb = new StringBuilder();
                    String x = r.readLine();
                    while (x != null)
                    {
                        sb.append(x);
                        x = r.readLine();
                    }

                    Log.d("READ", sb.toString());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
}
