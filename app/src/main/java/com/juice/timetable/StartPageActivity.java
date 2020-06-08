package com.juice.timetable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartPageActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Thread.sleep(500);//线程休眠1s，使出现白屏时的效果更加明显
            Intent it = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(it);
            finish();//关闭当前活动
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
