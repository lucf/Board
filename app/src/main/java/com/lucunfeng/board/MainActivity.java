package com.lucunfeng.board;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //* host and port
    String host = "127.0.0.1";
    int port = 8700;
    final List<String> in = new ArrayList<String>();
    final List<String> out = new ArrayList<String>();
    TextView tv_in,tv_out;
    RecyclerAdapter_Buttons mAdapter,mAdapter_out;
    String text_size="34";

    String[] indata,outdata ;

    int selected_in=0,selected_out=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("tcp", "onCreate: ");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        tv_in= findViewById(R.id.tv_in);
        tv_out=findViewById(R.id.tv_out);
        //设置输入列表
        RecyclerView recyclerView_InButtons  = findViewById(R.id.rv_in);
        recyclerView_InButtons.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 4);
        recyclerView_InButtons.setLayoutManager(mLayoutManager);

        mAdapter = new RecyclerAdapter_Buttons(MainActivity.this,in,R.layout.item_of_buttons_view);
        recyclerView_InButtons.setAdapter(mAdapter);
        //recyclerView_InButtons.addItemDecoration(new NormalDecoration(MainActivity.this, OrientationHelper.HORIZONTAL));
        //recyclerView_InButtons.addItemDecoration(new NormalDecoration(MainActivity.this, OrientationHelper.VERTICAL));
        mAdapter.setText_size(text_size);
        mAdapter.setOnRecyclerItemClickListener(new RecyclerAdapter_Buttons.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
        mAdapter.setOnRecyclerItemFocusCkhangeListener(new RecyclerAdapter_Buttons.OnRecyclerItemFocusChangeListener() {
            @Override
            public void OnItemFocusChange(View view, int position, Boolean hasFocus) {
                if (hasFocus){
                    selected_in=position+1;
                    selected_out=0;
                    TextView tv_item = (TextView)view.findViewById(R.id.item_tv);
                    tv_in.setText("输入 : "+tv_item.getText());
                    Toast.makeText(MainActivity.this,selected_in + ":" +selected_out,Toast.LENGTH_SHORT).show();
                }
            }
        });

        //设置输出列表
        RecyclerView recyclerView_OutButtons  = findViewById(R.id.rv_out);
        recyclerView_OutButtons.setHasFixedSize(true);
        GridLayoutManager mLayoutManager_out = new GridLayoutManager(this, 4);
        recyclerView_OutButtons.setLayoutManager(mLayoutManager_out);

        mAdapter_out = new RecyclerAdapter_Buttons(MainActivity.this,out,R.layout.item_of_buttons_view);
        recyclerView_OutButtons.setAdapter(mAdapter_out);

        //recyclerView_OutButtons.addItemDecoration(new NormalDecoration(MainActivity.this, OrientationHelper.HORIZONTAL));
        //recyclerView_OutButtons.addItemDecoration(new NormalDecoration(MainActivity.this, OrientationHelper.VERTICAL));
        mAdapter_out.setText_size(text_size);

        mAdapter_out.setOnRecyclerItemClickListener(new RecyclerAdapter_Buttons.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
        mAdapter_out.setOnRecyclerItemFocusCkhangeListener(new RecyclerAdapter_Buttons.OnRecyclerItemFocusChangeListener() {
            @Override
            public void OnItemFocusChange(View view, int position, Boolean hasFocus) {
                if (hasFocus){
                    selected_out=position+1;

                    if (selected_in>0 && selected_out>0){
                        Toast.makeText(MainActivity.this,selected_in+" => "+selected_out,Toast.LENGTH_SHORT).show();
                        String cmd = "*"+selected_in+"D"+selected_out+"#";
                        crete_Socket_Send_Close(cmd);

                        tv_in.setText("输入");
                        selected_in=0;
                        selected_out=0;
                    }else{
                        Toast.makeText(MainActivity.this,"请先按输入按钮，再按输出按钮",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //设置
        ImageButton ib_setting = findViewById(R.id.bt_setting);
        ib_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SettingsActivity2.class);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        });
        //加载配置
        loadConfig();
    }

    private void loadConfig() {
        //获取配置信息
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        host= settings.getString("host","29.47.1.59");
        port = Integer.parseInt(settings.getString("port","8989"));
        String strIn= settings.getString("in","in1,in2,in3,in4,in5,in6,in7,in8");
        String strOut =settings.getString("out","out1,out2,out3,out4,out5,out6,out7,out8");

        indata = strIn.split(",");
        outdata = strOut.split(",");
        in.clear();
        out.clear();
        in.addAll(Arrays.asList(indata));
        out.addAll(Arrays.asList(outdata));

        text_size= settings.getString("button_text_size",text_size);

        if (mAdapter!=null){
            mAdapter.setText_size(text_size);
            mAdapter.notifyDataSetChanged();
        }
        if(mAdapter_out!=null){
            mAdapter_out.setText_size(text_size);
            mAdapter_out.notifyDataSetChanged();
        }

    }

    @Override
    protected void onResume() {

        super.onResume();
        Log.d("tcp", "onResume: ");
        loadConfig();
    }


    //创建socket发送命令然后关闭socket.
    private void crete_Socket_Send_Close(String cmd) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //Log.d("tcp",  "是否主线程："+ (Looper.getMainLooper().getThread() == Thread.currentThread()) );
                Socket socket = null;
                try {
                    socket = new Socket(host, port);//第一种方式创建连接
                    //InetSocketAddress address = new InetSocketAddress(host,port);//第二种方式创建连接
                    //socket.connect(address,5000);
                    if (socket.isConnected()) {
                        Log.d("tcp", "run: connected localport:"+socket.getLocalPort());
                    }
                    //设置读流的超时时间
                    socket.setSoTimeout(8000);
                    socket.setKeepAlive(true);
                    //获取输入输出流
                    InputStream in = socket.getInputStream();
                    OutputStream os = socket.getOutputStream();

                    //发送数据
                    byte[] sendData = cmd.getBytes(Charset.forName("ASCII"));
                    try {
                        os.write(sendData);
                    }catch ( IOException e){
                        e.printStackTrace();
                    }
                    os.flush();
                    Log.d("tcp", "run: send "+cmd);

/*                    byte[] buf = new byte[1024];
                    int len = in.read(buf);
                    String receiveData = new String(buf, 0, len, Charset.forName("UTF-8"));
                    Log.i("tcp", "Client receive Server data:" + receiveData);*/
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"连接时发生异常："+e.getMessage(),Toast.LENGTH_SHORT).show();
                } finally {
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
}

