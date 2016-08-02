package tw.com.pcschool.t080101;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<NewsItem> news;
    ListView lv;
    ArrayAdapter adapter;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        news = new ArrayList<>();
        lv = (ListView) findViewById(R.id.listView);

        new Thread(){
            public void run(){
                String strurl = "http://udn.com/rssfeed/news/1";
                String result = "";
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
                    result = sb.toString();



                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }



                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();

                    xpp.setInput( new StringReader(result) );

                    int eventType = xpp.getEventType();
                    boolean isTitle = false;
                    boolean isItem = false;
                    boolean isLink = false;
                    NewsItem currentItem = null;
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if(eventType == XmlPullParser.START_DOCUMENT) {
                            Log.d("READ", "Start document");
                        } else if(eventType == XmlPullParser.START_TAG) {
                            // Log.d("READ", "Start tag "+xpp.getName());
                            if (xpp.getName().equals("title"))
                            {
                                isTitle = true;
                            }
                            if (xpp.getName().equals("item"))
                            {
                                isItem = true;
                                currentItem = new NewsItem();
                            }
                            if (xpp.getName().equals("link"))
                            {
                                isLink = true;
                            }
                        } else if(eventType == XmlPullParser.END_TAG) {
                            // Log.d("READ", "End tag "+xpp.getName());
                            if (xpp.getName().equals("title"))
                            {
                                isTitle = false;
                            }
                            if (xpp.getName().equals("item"))
                            {
                                isItem = false;
                                news.add(currentItem);
                            }
                            if (xpp.getName().equals("link"))
                            {
                                isLink = false;
                            }
                        } else if(eventType == XmlPullParser.TEXT) {
                            if (isTitle == true && isItem == true)
                            {
                                Log.d("READ", "Text "+xpp.getText());
                                currentItem.mTitle = xpp.getText();
                            }
                            if (isLink == true && isItem == true)
                            {
                                Log.d("READ", "Link:"+xpp.getText());
                                currentItem.mLink = xpp.getText();
                            }

                        }
                        eventType = xpp.next();
                    }
                    System.out.println("End document");

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] s = new String[news.size()];
                            for (int i=0;i<news.size();i++)
                            {
                                s[i] = news.get(i).mTitle;
                            }
                            adapter = new ArrayAdapter(MainActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    s);
                            lv.setAdapter(adapter);

                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    Uri uri=Uri.parse(news.get(position).mLink);
                                    Intent i=new Intent(Intent.ACTION_VIEW,uri);
                                    startActivity(i);
                                }
                            });
                        }
                    });


                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }



            }
        }.start();
    }
}
