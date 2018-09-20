package com.sheng.easyseekbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.sheng.lib.EasySeekBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EasySeekBar easySeekBar = findViewById(R.id.id_seekbar_4);
        easySeekBar.setItems("one","two","three","four","five");
        easySeekBar.setSeekBarDiyChangeListener(new EasySeekBar.OnSeekBarDiyChangeListener() {

            @Override
            public void onDiyChange(EasySeekBar easySeekBar, String text, int position) {
                Toast.makeText(MainActivity.this, text + " click by : " + position, Toast.LENGTH_SHORT).show();
            }
        });

        EasySeekBar easySeekBar1 = findViewById(R.id.id_seekbar_5);
        easySeekBar1.setItems("open","1/2","1/3","1/4","close");
        easySeekBar1.setSelectIndex(3);
        easySeekBar1.setSeekBarDiyChangeListener(new EasySeekBar.OnSeekBarDiyChangeListener() {
            @Override
            public void onDiyChange(EasySeekBar easySeekBar, String text, int position) {
                Toast.makeText(MainActivity.this, text + " click by : " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
