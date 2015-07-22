package com.felicity.mummyding.felicitychatrobot;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends Activity implements View.OnClickListener{


    ListView listView;
    EditText input_box;
    Button send_btn;
    List<MsgItem> msg_list;
    MsgAdapter msgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
        callRobot(VALUES.HELLO);
    }
    void init(){
        listView = (ListView) findViewById(R.id.msg_list_view);
        input_box = (EditText) findViewById(R.id.input_box);
        send_btn = (Button) findViewById(R.id.send_btn);
        send_btn.setOnClickListener(this);

        msg_list = new ArrayList<MsgItem>();
        msgAdapter = new MsgAdapter(getBaseContext(),R.id.msg_list_view,msg_list);
        listView.setAdapter(msgAdapter);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()){
            case R.id.getLoc:
                String loc_data = "经度:"+VALUES.Longitude+"<br>纬度:"+VALUES.Latitude+
                        "<br>地址:"+VALUES.LOCATION;
                sendData(loc_data,MsgItem.TYPE_ROBOT);
                break;
            case R.id.getWeather:
                String wea_data = VALUES.CITY+"的天气";
                callRobot(wea_data);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu,menu);
        return true;
    }

    /**
     * ListView 界面更新
     * @param msg
     * @param type
     */
    void sendData(String msg,int type){
        MsgItem sendMsg= new MsgItem(msg,type);
        msg_list.add(sendMsg);
        msgAdapter.notifyDataSetChanged();
        //定位到listview尾部
        listView.setSelection(msg_list.size());
    }


    /**
     * 通过API接口获取数据
     * @param key
     * @param msg
     * @return 返回值为空则表示网络有问题
     */
    StringBuffer sb;
    String getData(String key,String msg) {
        final String getURL = "http://www.tuling123.com/openapi/api?key=" + key + "&info=" + msg;
        sb = new StringBuffer();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
              try {
                    URL getUrl = new URL(getURL);
                    HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
                   // connection.setRequestMethod("GET");
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(5000);
                    //访问失败
                    if (connection.getResponseCode() != 200) {
                        Toast.makeText(ChatActivity.this,"访问失败",Toast.LENGTH_SHORT).show();
                         sb.append("");
                        return ;
                    }
                    // 取得输入流，并使用Reader读取
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();
                    // 断开连接
                    connection.disconnect();

                }catch (Exception e){

                }
            }
        });
        thread.start();
        try{
            thread.join();
        }catch (Exception e){

        }

        return sb.toString();

    }

    /**
     * 解析获得的数据
     * @param data
     * @return 为空则表示KEY无效
     */
    String dealData(String data){
        try {
            JSONObject jsonObject = new JSONObject(data);
            int code = jsonObject.getInt("code");
            switch (code){
                case VALUES.CODE_TEXT:
                    data = jsonObject.getString("text");
                    break;
                case VALUES.CODE_LINK:
                    data = jsonObject.getString("text")+"<br>"+
                            "<a href="+"\""+jsonObject.getString("url")+"\""+">点我查看</a>";
                    break;
                case VALUES.CODE_NEWS:
                    JSONArray jsonNewsArray = jsonObject.getJSONArray("list");
                    StringBuffer detail_news = new StringBuffer();
                    for(int i = 0 ; i < jsonNewsArray.length() ; i++){
                        JSONObject object = jsonNewsArray.getJSONObject(i);
                        detail_news.append("标题:"+object.getString("article")+"<br>"+
                        "来源:"+object.getString("source")+"<br>"+
                        "详情:"+"<a href="+"\""+object.getString("detailurl")+"\""+">点我查看</a>"+"<br>"
                        );
                    }
                    data = jsonObject.getString("text")+"<br>"+detail_news.toString();
                    break;
                case VALUES.CODE_TRAIN:
                    JSONArray jsonTrainArray = jsonObject.getJSONArray("list");
                    StringBuffer detail_train = new StringBuffer();
                    for(int i = 0 ; i < jsonTrainArray.length() ; i++){
                        JSONObject object = jsonTrainArray.getJSONObject(i);
                        detail_train.append("车次:"+object.getString("trainnum")+"<br>"+
                                        "起始站:"+object.getString("start")+"<br>"+
                                        "终点站:"+object.getString("terminal")+"<br>"+
                                        "出发时间:"+object.getString("starttime")+"<br>"+
                                        "到达时间:"+object.getString("endtime")+"<br>"+
                                        "详情:"+"<a href="+"\""+object.getString("detailurl")+"\""+">点我查看</a>"+"<br>"
                        );
                    }
                    data = jsonObject.getString("text")+"<br>"+detail_train.toString();
                    break;
                case VALUES.CODE_FLIGHT:
                    JSONArray jsonFlightArray = jsonObject.getJSONArray("list");
                    StringBuffer detail_flight = new StringBuffer();
                    for(int i = 0 ; i < jsonFlightArray.length() ; i++){
                        JSONObject object = jsonFlightArray.getJSONObject(i);
                        detail_flight.append("航班:"+object.getString("flight")+"<br>"+
                                        "出发时间:"+object.getString("starttime")+"<br>"+
                                        "到达时间:"+object.getString("endtime")+"<br>"+
                                        "<img src="+"\""+object.getString("icon")+"\""+"/><br>"
                        );
                    }
                    data = jsonObject.getString("text")+"<br>"+detail_flight.toString();
                    break;
                case VALUES.CODE_MENU:
                    JSONArray jsonMenuArray = jsonObject.getJSONArray("list");
                    StringBuffer detail_menu = new StringBuffer();
                    for(int i = 0 ; i < jsonMenuArray.length() ; i++){
                        JSONObject object = jsonMenuArray.getJSONObject(i);
                        detail_menu.append("菜名:"+object.getString("name")+"<br>"+
                                        "原料:"+object.getString("info")+"<br>"+
                                        "详情:"+"<a href="+"\""+object.getString("detailurl")+"\""+">点我查看</a>"+"<br>"+
                                        "<img src="+"\""+object.getString("icon")+"\""+"/><br>"
                        );
                    }
                    data = jsonObject.getString("text")+"<br>"+detail_menu.toString();
                    break;
                case VALUES.CODE_KEYOVER:
                    data = "";
                    break;
                default:
                    data = VALUES.ERROR;
                    //服务器错误
                    break;

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 用户发送消息给机器人
     * @param msg
     * @return
     */
    boolean callRobot(String msg){
        String resultInfo;
        for(String key: VALUES.KEYS){
            String data = getData(key,msg);
            /**
             * 访问失败
             */
            if(data.equals("") == true){
                sendData(VALUES.INTERNET_ERROR,MsgItem.TYPE_ROBOT);
                return false;
            }
            resultInfo = dealData(data);
            /**
             * 有数据返回则表示一切正常
             */
            if(resultInfo.equals("") == false){
                //发送消息
                sendData(resultInfo,MsgItem.TYPE_ROBOT);
                return true;
            }

        }
        /**
         * 到这里还没返回，说明KEY无效
         */
        return false;
    }

    /**
     * 响应发送消息按钮
     * @param v
     */
    @Override
    public void onClick(View v) {
        String msg = input_box.getText().toString();
        //Send when msg is not null

        if (msg.equals("")==false){
            sendData(msg,MsgItem.TYPE_USER);

            //清空输入框
           input_box.setText("");
            //隐藏键盘
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            if(callRobot(msg) == false){
                //发送消息
                sendData(VALUES.ERROR,MsgItem.TYPE_ROBOT);
            }

        }
    }
}
