package com.sheng.easyseekbar;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.sheng.lib.EasySeekBar;
import com.sheng.lib.TextShowHelper;

import java.lang.ref.WeakReference;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private MyHandler myHandler;

    public EasySeekBar easySeekBar1;
    public EasySeekBar easySeekBar4;
    public EasySeekBar easySeekBar5;
    public EasySeekBar easySeekBar6;
    public TextView textView6;
    public TextView textView5;
    public EasySeekBar easySeekBar7;
    public TextView textView7;
    public EasySeekBar easySeekBar8;
    public EasySeekBar easySeekBar9;
    public TextView textView8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myHandler = new MyHandler(this);

        easySeekBar1 = findViewById(R.id.id_seekbar_1);
        easySeekBar1.setProgress(60);

        easySeekBar4 = findViewById(R.id.id_seekbar_4);

        // 设置分段
        easySeekBar5 = findViewById(R.id.id_seekbar_5);
        textView5 = findViewById(R.id.id_tv_5);
        easySeekBar5.setItems("one", "two", "three", "four", "five");
        easySeekBar5.setSeekBarDiyChangeListener(new EasySeekBar.OnSeekBarDiyChangeListener() {

            @Override
            public void onDiyChange(EasySeekBar easySeekBar, String text, int position) {
                String format = String.format(Locale.getDefault(), "%s click by : %d", text, position);
                textView5.setText(format);
            }
        });

        easySeekBar6 = findViewById(R.id.id_seekbar_6);
        textView6 = findViewById(R.id.id_tv_6);
        easySeekBar6.setItems("open", "1/2", "1/3");
        easySeekBar6.setSeekBarDiyChangeListener(new EasySeekBar.OnSeekBarDiyChangeListener() {
            @Override
            public void onDiyChange(EasySeekBar easySeekBar, String text, int position) {
                String format = String.format(Locale.getDefault(), "%s click by : %d", text, position);
                textView6.setText(format);
            }
        });

        // 普通进度条
        easySeekBar7 = findViewById(R.id.id_seekbar_7);
        textView7 = findViewById(R.id.id_tv_7);
        myHandler.sendEmptyMessage(101);

        final String str8 = "low : %s, height : %s";
        easySeekBar8 = findViewById(R.id.id_seekbar_8);
        textView8 = findViewById(R.id.id_tv_8);
        easySeekBar8.setSeekBarLowOrHeightListener(new EasySeekBar.OnLowOrHeightProgressChangeListener() {
            @Override
            public void onLowStart(EasySeekBar easySeekBar, int progress) {
                textView8.setText(String.format(str8, progress, easySeekBar8.getHeightProgress()));
            }

            @Override
            public void onLowChange(EasySeekBar easySeekBar, int progress) {
                textView8.setText(String.format(str8, progress, easySeekBar8.getHeightProgress()));
            }

            @Override
            public void onLowStop(EasySeekBar easySeekBar, int progress) {
                textView8.setText(String.format(str8, progress, easySeekBar8.getHeightProgress()));
            }

            @Override
            public void onHeightStart(EasySeekBar easySeekBar, int progress) {
                textView8.setText(String.format(str8, easySeekBar8.getLowProgress(), progress));
            }

            @Override
            public void onHeightChange(EasySeekBar easySeekBar, int progress) {
                textView8.setText(String.format(str8, easySeekBar8.getLowProgress(), progress));
            }

            @Override
            public void onHeightStop(EasySeekBar easySeekBar, int progress) {
                textView8.setText(String.format(str8, easySeekBar8.getLowProgress(), progress));
            }
        });

        easySeekBar9 = findViewById(R.id.id_seekbar_9);
        easySeekBar9.setTextShowHelper(new TextShowHelper() {
            @Override
            public String getTextByProgress(int progress) {
                return progress + "s";
            }
        });
    }

    public static class MyHandler extends Handler {

        WeakReference<MainActivity> activityWeakReference;

        public MyHandler(Context context) {
            activityWeakReference = new WeakReference<>((MainActivity) context);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = activityWeakReference.get();
            if (mainActivity == null) {
                return;
            }
            switch (msg.what) {
                case 101:
                    int progress4 = (int) mainActivity.easySeekBar4.getProgress();
                    int progress7 = (int) mainActivity.easySeekBar7.getProgress();
                    progress4 += 10;
                    if (progress4 > mainActivity.easySeekBar4.getMax()) {
                        progress4 = 0;
                    }
                    progress7 += 10;
                    if (progress7 > mainActivity.easySeekBar7.getMax()) {
                        progress7 = 0;
                    }
                    mainActivity.easySeekBar4.setProgress(progress4);
                    mainActivity.easySeekBar7.setProgress(progress7);
                    mainActivity.textView7.setText(String.valueOf(progress7));
                    sendMessageDelayed(Message.obtain(this, 101), 1000);
                    break;
            }
        }
    }
}
