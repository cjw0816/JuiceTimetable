package com.juice.timetable;

import android.content.Intent;
import android.os.Bundle;

import com.juice.timetable.utils.BaseActivity;

public class StartPageActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread myThread = new Thread() {//创建子线程T

            @Override
            public void run() {
                try {
                    sleep(1000);//使程序休眠一秒
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();//启动线程
        Intent it = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(it);
        finish();//关闭当前活动
    }
}
