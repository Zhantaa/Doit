package com.example.upc.Doit.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.upc.Doit.R;
import com.example.upc.Doit.Receiver.NetworkReceiver;
import com.example.upc.Doit.Service.AlarmService;
import com.example.upc.Doit.Utils.FileUtils;
import com.example.upc.Doit.Utils.NetWorkUtils;
import com.example.upc.Doit.Utils.SPUtils;
import com.example.upc.Doit.Bean.User;

import cn.bmob.v3.Bmob;
import site.gemus.openingstartanimation.NormalDrawStrategy;
import site.gemus.openingstartanimation.OpeningStartAnimation;

public class SplashActivity extends BasicActivity {


    private final int SPLASH_DISPLAY_LENGTH = 1500;
    private static final String APP_ID = "defd0728f1564647ef4d8c9207fa45c6";
    private NetworkReceiver networkReceiver;
    private FileUtils fileUtils;
    private static final String KEY_VIBRATE = "vibrator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        //复制assets下的资源文件到sd卡
        fileUtils = new FileUtils();
        fileUtils.copyData(getApplicationContext());
        SPUtils.put(this,"isFocus",false);

        if (NetWorkUtils.isNetworkConnected(getApplication())){
            Bmob.initialize(getApplication(), APP_ID);
        }


        Resources res = this.getResources();
        startService(new Intent(this, AlarmService.class));
        OpeningStartAnimation openingStartAnimation = new OpeningStartAnimation.Builder(this)
                .setDrawStategy(new NormalDrawStrategy()) //设置动画效果
                .setAppIcon(res.getDrawable(R.drawable.ic_launcher)) //设置图

                .setColorOfAppName(R.color.icon_color) //设置app名称颜色
                .setAppStatement("生命不息，奋斗不止")

                .setColorOfAppStatement(R.color.icon_color) // 设置一句话描述的颜色
                .create();
        openingStartAnimation.show(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                if (User.getCurrentUser(User.class)==null){
                    Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else {
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }

            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
