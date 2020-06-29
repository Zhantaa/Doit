package com.example.upc.Doit.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.upc.Doit.R;
import com.example.upc.Doit.Service.FocusService;
import com.example.upc.Doit.Utils.SPUtils;
import com.example.upc.Doit.Utils.TimeFormatUtil;
import com.example.upc.Doit.Service.ClockService;
import com.example.upc.Doit.Widget.RippleWrapper;
import com.example.upc.Doit.Widget.ClockApplication;
import com.example.upc.Doit.Widget.ClockProgressBar;
import com.jaouan.compoundlayout.RadioLayout;

import java.util.Random;

import es.dmoral.toasty.Toasty;
import me.drakeet.materialdialog.MaterialDialog;

public class ClockActivity extends BasicActivity {

    private ClockApplication mApplication;
    private MenuItem mMenuItemIDLE;
    private Button mBtnStart;
    private Button mBtnPause;
    private Button mBtnResume;
    private Button mBtnStop;
    private Button mBtnSkip;
    private TextView mTextCountDown;
    private TextView mTextTimeTile;
    private TextView focus_tint;
    private ClockProgressBar mProgressBar;
    private RippleWrapper mRippleWrapper;
    private long mLastClickTime = 0;
    private String clockTitle;
    private static final String KEY_FOCUS = "focus";
    private ImageView clock_bg;
    private ImageButton bt_music;
    private static int[] imageArray = new int[]{R.drawable.ic_img2,
                                                R.drawable.ic_img3,
                                                R.drawable.ic_img4,
                                                R.drawable.ic_img5,
                                                R.drawable.ic_img6,
                                                R.drawable.ic_img7,
                                                R.drawable.ic_img8,
                                                R.drawable.ic_img9,
                                                R.drawable.ic_img10,
                                                R.drawable.ic_img11,
                                                R.drawable.ic_img12};
    private int bg_id;
    private int workLength, shortBreak,longBreak;
    private long id;
    private RadioLayout river,rain,wave,bird,fire;

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_clock);
        Intent intent = getIntent();
        clockTitle = intent.getStringExtra("clocktitle");
        workLength = intent.getIntExtra("workLength",ClockApplication.DEFAULT_WORK_LENGTH);
        shortBreak = intent.getIntExtra("shortBreak",ClockApplication.DEFAULT_SHORT_BREAK);
        longBreak = intent.getIntExtra("longBreak",ClockApplication.DEFAULT_LONG_BREAK);
        id = intent.getLongExtra("id",1);

        mApplication = (ClockApplication)getApplication();

        mBtnStart = (Button)findViewById(R.id.btn_start);
        mBtnPause = (Button)findViewById(R.id.btn_pause);
        mBtnResume = (Button)findViewById(R.id.btn_resume);
        mBtnStop = (Button)findViewById(R.id.btn_stop);
        mBtnSkip = (Button)findViewById(R.id.btn_skip);
        mTextCountDown = (TextView)findViewById(R.id.text_count_down);
        mTextTimeTile = (TextView)findViewById(R.id.text_time_title);
        mProgressBar = (ClockProgressBar)findViewById(R.id.tick_progress_bar);
        mRippleWrapper = (RippleWrapper)findViewById(R.id.ripple_wrapper);
        focus_tint = (TextView)findViewById(R.id.focus_hint);
        bt_music = (ImageButton) findViewById(R.id.bt_music);
        clock_bg = (ImageView) findViewById(R.id.clock_bg);
        if(isSoundOn()){
            bt_music.setEnabled(true);
            bt_music.setImageDrawable(getResources().getDrawable(R.drawable.ic_music));
        } else {
            bt_music.setEnabled(false);
            bt_music.setImageDrawable(getResources().getDrawable(R.drawable.ic_music_off));
        }
        SPUtils.put(this,"music_id",R.raw.river);
        Toasty.normal(this, "双击界面打开或关闭白噪音", Toast.LENGTH_SHORT).show();
        initActions();
        initBackgroundImage();
    }

    private void initBackgroundImage(){

        Random random = new Random();
        bg_id = imageArray[random.nextInt(11)];
        //内存优化
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true);

        Glide.with(getApplicationContext())
                .load(bg_id)
                .apply(options)
                .into(clock_bg);

    }

    private void initActions() {
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = ClockService.newIntent(getApplicationContext());
                i.setAction(ClockService.ACTION_START);
                i.putExtra("id",id);
                i.putExtra("clockTitle",clockTitle);
                i.putExtra("workLength",workLength);
                i.putExtra("shortBreak",shortBreak);
                i.putExtra("longBreak",longBreak);
                startService(i);
                mApplication.start();
                updateButtons();
                updateTitle();
                updateRipple();
                if (getIsFocus(ClockActivity.this)){
                    startService(new Intent(ClockActivity.this, FocusService.class));
                    focus_tint.setVisibility(View.VISIBLE);
                }
            }
        });

        mBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = ClockService.newIntent(getApplicationContext());
                i.setAction(ClockService.ACTION_PAUSE);
                i.putExtra("time_left", (String) mTextCountDown.getText());
                startService(i);

                mApplication.pause();
                updateButtons();
                updateRipple();
            }
        });

        mBtnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = ClockService.newIntent(getApplicationContext());
                i.setAction(ClockService.ACTION_RESUME);
                startService(i);

                mApplication.resume();
                updateButtons();
                updateRipple();
            }
        });
/////////////////////////////////设置番茄钟///////////
        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MaterialDialog exitDialog = new MaterialDialog(ClockActivity.this);
                exitDialog.setTitle("提示")
                        .setMessage("放弃后，本次番茄钟将作废")
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent2 = new Intent(ClockActivity.this, MainActivity.class);
                                startActivity(intent2);
                                stopService(new Intent(ClockActivity.this, FocusService.class));
                                Glide.get(ClockActivity.this).clearMemory();
                                exitApp();
                            }
                        })
                        .setNegativeButton("取消", new View.OnClickListener() {
                            public void onClick(View view) {
                                exitDialog.dismiss();
                            }
                        });

                exitDialog.show();

            }
        });

        mBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = ClockService.newIntent(getApplicationContext());
                i.setAction(ClockService.ACTION_STOP);
                startService(i);

                mApplication.skip();
                reload();
            }
        });

        mRippleWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - mLastClickTime < 500) {

                    // 修改 SharedPreferences
                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext()).edit();
/////////////////////////////////修改声音///////////
                    if (isSoundOn()) {
                        editor.putBoolean("pref_key_tick_sound", false);

                        Intent i = ClockService.newIntent(getApplicationContext());
                        i.setAction(ClockService.ACTION_TICK_SOUND_OFF);
                        startService(i);
                        bt_music.setImageDrawable(getResources().getDrawable(R.drawable.ic_music_off));
                        bt_music.setEnabled(false);
                        Snackbar.make(view, getResources().getString(R.string.toast_tick_sound_off),
                                Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    } else {
                        editor.putBoolean("pref_key_tick_sound", true);

                        Intent i = ClockService.newIntent(getApplicationContext());
                        i.setAction(ClockService.ACTION_TICK_SOUND_ON);
                        startService(i);
                        bt_music.setImageDrawable(getResources().getDrawable(R.drawable.ic_music));
                        bt_music.setEnabled(true);
                        Snackbar.make(view, getResources().getString(R.string.toast_tick_sound_on),
                                Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                    try {
                        editor.apply();
                    } catch (AbstractMethodError unused) {
                        editor.commit();
                    }

                    updateRipple();
                }

                mLastClickTime = clickTime;
            }
        });

        bt_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(ClockActivity.this);
                View musicView = layoutInflater.inflate(R.layout.dialog_music, null);
                river = musicView.findViewById(R.id.sound_river);//河流音
                rain = musicView.findViewById(R.id.sound_rain);//下雨
                wave = musicView.findViewById(R.id.sound_wave);//风
                bird = musicView.findViewById(R.id.sound_bird);//鸟叫
                fire = musicView.findViewById(R.id.sound_fire);//火
                river.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SPUtils.put(ClockActivity.this,"music_id",R.raw.river);
                        Intent i = ClockService.newIntent(getApplicationContext());
                        i.setAction(ClockService.ACTION_CHANGE_MUSIC);
                        startService(i);
                    }
                });
                rain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SPUtils.put(ClockActivity.this,"music_id",R.raw.rain);
                        Intent i = ClockService.newIntent(getApplicationContext());
                        i.setAction(ClockService.ACTION_CHANGE_MUSIC);
                        startService(i);
                    }
                });
                wave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SPUtils.put(ClockActivity.this,"music_id",R.raw.ocean);
                        Intent i = ClockService.newIntent(getApplicationContext());
                        i.setAction(ClockService.ACTION_CHANGE_MUSIC);
                        startService(i);
                    }
                });
                bird.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SPUtils.put(ClockActivity.this,"music_id",R.raw.bird);
                        Intent i = ClockService.newIntent(getApplicationContext());
                        i.setAction(ClockService.ACTION_CHANGE_MUSIC);
                        startService(i);
                    }
                });
                fire.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SPUtils.put(ClockActivity.this,"music_id",R.raw.fire);
                        Intent i = ClockService.newIntent(getApplicationContext());
                        i.setAction(ClockService.ACTION_CHANGE_MUSIC);
                        startService(i);
                    }
                });
                final MaterialDialog alert = new MaterialDialog(ClockActivity.this);
                alert.setPositiveButton("关闭", new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });
                alert.setContentView(musicView);
                alert.setCanceledOnTouchOutside(true);
                alert.show();

            }

        });
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//    }
///////////////////////白山岭待完成
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN){
//            Snackbar.make(layout, "是否删除？（滑动取消）", Snackbar.LENGTH_LONG)
//                    .setAction("确定", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent intent = new Intent(getApplication(), MainActivity.class);
//                            startActivity(intent);
//                            exitApp();
//                        }
//                    }).show();
            final MaterialDialog exitDialog = new MaterialDialog(this);
            exitDialog.setTitle("提示")
                    .setMessage("本次番茄钟将作废，是否退出")
                    .setPositiveButton("退出", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent2 = new Intent(ClockActivity.this, MainActivity.class);
                            startActivity(intent2);
                            stopService(new Intent(ClockActivity.this, FocusService.class));
                            Glide.get(ClockActivity.this).clearMemory();
                            exitApp();
                        }
                    })
                    .setNegativeButton("取消", new View.OnClickListener() {
                        public void onClick(View view) {
                            exitDialog.dismiss();
                        }
                    });

            exitDialog.show();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onStart() {
        super.onStart();
        reload();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ClockService.ACTION_COUNTDOWN_TIMER);
        registerReceiver(mIntentReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mIntentReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        releaseImageViewResouce(clock_bg);

    }

    private void reload() {
        mApplication.reload();

        mProgressBar.setMaxProgress(mApplication.getMillisInTotal() / 1000);
        mProgressBar.setProgress(mApplication.getMillisUntilFinished() / 1000);

        updateText(mApplication.getMillisUntilFinished());
        updateTitle();
        updateButtons();
        updateScene();
        updateRipple();
        updateAmount();

        if (getSharedPreferences().getBoolean("pref_key_screen_on", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void updateText(long millisUntilFinished) {
        mTextCountDown.setText(TimeFormatUtil.formatTime(millisUntilFinished));
    }

    private void updateTitle() {
        if (mApplication.getState() == ClockApplication.STATE_FINISH) {
            String title;

            if (mApplication.getScene() == ClockApplication.SCENE_WORK) {
                title = getResources().getString(R.string.scene_title_work);
            } else {
                title = getResources().getString(R.string.scene_title_break);
            }

            mTextTimeTile.setText(title);
            mTextTimeTile.setVisibility(View.VISIBLE);
            mTextCountDown.setVisibility(View.GONE);
        } else {
            mTextTimeTile.setVisibility(View.GONE);
            mTextCountDown.setVisibility(View.VISIBLE);
        }
    }

    private void updateButtons() {
        int state = mApplication.getState();
        int scene = mApplication.getScene();
        boolean isPomodoroMode = getSharedPreferences()
                .getBoolean("pref_key_pomodoro_mode", true);

        // 在番茄模式下不能暂停定时器
        mBtnStart.setVisibility(
                state == ClockApplication.STATE_WAIT || state == ClockApplication.STATE_FINISH ?
                        View.VISIBLE : View.GONE);

        if (isPomodoroMode) {
            mBtnPause.setVisibility(View.GONE);
            mBtnResume.setVisibility(View.GONE);
        } else {
            mBtnPause.setVisibility(state == ClockApplication.STATE_RUNNING ?
                    View.VISIBLE : View.GONE);
            mBtnResume.setVisibility(state == ClockApplication.STATE_PAUSE ?
                    View.VISIBLE : View.GONE);
        }

        if (scene == ClockApplication.SCENE_WORK) {
            mBtnSkip.setVisibility(View.GONE);
            if (isPomodoroMode) {
                mBtnStop.setVisibility(!(state == ClockApplication.STATE_WAIT ||
                        state == ClockApplication.STATE_FINISH) ?
                        View.VISIBLE : View.GONE);
            } else {
                mBtnStop.setVisibility(state == ClockApplication.STATE_PAUSE ?
                        View.VISIBLE : View.GONE);
            }

        } else {
            mBtnStop.setVisibility(View.GONE);
            if (isPomodoroMode) {
                mBtnSkip.setVisibility(!(state == ClockApplication.STATE_WAIT ||
                        state == ClockApplication.STATE_FINISH) ?
                        View.VISIBLE : View.GONE);
            } else {
                mBtnSkip.setVisibility(state == ClockApplication.STATE_PAUSE ?
                        View.VISIBLE : View.GONE);
            }

        }
    }
////////////谈劭天待完成//////////
    public void updateScene() {
        int scene = mApplication.getScene();

//        int workLength = getSharedPreferences()
//                .getInt("pref_key_work_length", ClockApplication.DEFAULT_WORK_LENGTH);
//        int shortBreak = getSharedPreferences()
//                .getInt("pref_key_short_break", ClockApplication.DEFAULT_SHORT_BREAK);
//        int longBreak = getSharedPreferences()
//                .getInt("pref_key_long_break", ClockApplication.DEFAULT_LONG_BREAK);

        ((TextView)findViewById(R.id.stage_work_value))
                .setText(getResources().getString(R.string.stage_time_unit, workLength));
        ((TextView)findViewById(R.id.stage_short_break_value))
                .setText(getResources().getString(R.string.stage_time_unit, shortBreak));
        ((TextView)findViewById(R.id.stage_long_break_value))
                .setText(getResources().getString(R.string.stage_time_unit, longBreak));

        findViewById(R.id.stage_work).setAlpha(
                scene == ClockApplication.SCENE_WORK ? 0.9f : 0.5f);
        findViewById(R.id.stage_short_break).setAlpha(
                scene == ClockApplication.SCENE_SHORT_BREAK ? 0.9f : 0.5f);
        findViewById(R.id.stage_long_break).setAlpha(
                scene == ClockApplication.SCENE_LONG_BREAK ? 0.9f : 0.5f);
    }

    private void updateRipple() {
        boolean isPlayOn = getSharedPreferences().getBoolean("pref_key_tick_sound", true);

        if (isPlayOn) {
            if (mApplication.getState() == ClockApplication.STATE_RUNNING) {
                mRippleWrapper.start();
                return;
            }
        }

        mRippleWrapper.stop();
    }

    private void updateAmount() {
        long amount = getSharedPreferences().getLong("pref_key_amount_durations", 0);
        TextView textView = (TextView)findViewById(R.id.amount_durations);
        textView.setText(getResources().getString(R.string.amount_durations, amount));
    }

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ClockService.ACTION_COUNTDOWN_TIMER)) {
                String requestAction = intent.getStringExtra(ClockService.REQUEST_ACTION);

                switch (requestAction) {
                    case ClockService.ACTION_TICK:
                        long millisUntilFinished = intent.getLongExtra(
                                ClockService.MILLIS_UNTIL_FINISHED, 0);
                        mProgressBar.setProgress(millisUntilFinished / 1000);
                        updateText(millisUntilFinished);
                        break;
                    case ClockService.ACTION_FINISH:
                    case ClockService.ACTION_AUTO_START:
                        reload();
                        break;
                }
            }
        }
    };

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void exitApp() {
        stopService(ClockService.newIntent(getApplicationContext()));
        mApplication.exit();
        finish();
    }

    private void setStatusBar(){
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    //判断是否开启专注模式
    private boolean getIsFocus(Context context){

        Boolean isFocus = (Boolean) SPUtils.get(context, KEY_FOCUS, false);

        return isFocus;

    }

    //判断是否开启白噪音
    private boolean isSoundOn(){
        return getSharedPreferences().getBoolean("pref_key_tick_sound", true);
    }

    public static void releaseImageViewResouce(ImageView imageView) {
        if (imageView == null) return;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }
}