package com.hotcast.vr;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hotcast.vr.bean.Classify;
import com.hotcast.vr.pageview.LandscapeView;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.SharedPreUtil;
import com.hotcast.vr.tools.Utils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;


public abstract class BaseActivity extends Activity implements View.OnClickListener {
    //     正常toast
    private Toast toast;
    //    显示吐司
    public static final int MESSAGE_SHOWTOAST = 0;
    //    显示加载对话框
    public static final int MESSAGE_SHOWLOADING = 1;
    //    关闭加载对话框
    public static final int MESSAGE_CLOSINGLOADING = 2;
    public static final int CODE_GET_PIC_FOR_ID = 101;
    public static final int CODE_GET_PIC_FOR_STU = 102;
    public static final int CODE_GET_PIC_FOR_SCHOOL = 103;
    public static final int CODE_GET_PIC_FOR_DORM = 104;
    public static final int CODE_GET_PIC_FOR_CHSI = 105;

    public SharedPreUtil sp;


    private Handler messageHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_SHOWTOAST:
                    String message = (String) msg.obj;
                    if (null == toast) {
                        toast = Toast.makeText(BaseActivity.this, message, Toast.LENGTH_LONG);
                    }
                    toast.setText(message);
                    toast.show();
                    break;
                case MESSAGE_SHOWLOADING:
                    dialog = new SpotsDialog(BaseActivity.this, (String) msg.obj);
                    dialog.show();
                    break;
                case MESSAGE_CLOSINGLOADING:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    break;
            }
        }
    };


    /**
     * 显示loading
     *
     * @param message 显示文案
     */
    boolean isShowing = false;
    public void showLoading(String message) {
        if (!isShowing) {
            Message msg = new Message();
            msg.obj = message;
            msg.what = MESSAGE_SHOWLOADING;
            isShowing = true;
            messageHandler.sendMessage(msg);
        }

    }

    /**
     * 关闭loading
     */
    public void hideLoading() {
//        System.out.println("---隐藏加载对话框的时间 = " + System.currentTimeMillis());
        if (isShowing) {
            isShowing = false;
            messageHandler.sendEmptyMessage(MESSAGE_CLOSINGLOADING);
        }
    }

    /**
     * 显示toast
     *
     * @param message 显示文案
     */
    public void showToast(String message) {

        //L.e("提示的数据 ======   "+message);
        Message msg = new Message();
        msg.obj = message;
        msg.what = MESSAGE_SHOWTOAST;
        messageHandler.sendMessage(msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 4.设置布局
        setContentView(getLayoutId());
        ButterKnife.inject(this);
        sp = SharedPreUtil.getInstance(this);
        // 5.获取页面传入数据
        getIntentData(getIntent());
        // 6.初始化


        init();
    }


    /**
     * alertdialog确定按钮点击监听
     *
     * @author seefuture
     */
    public interface OnAlertSureClickListener {
        void onclick();
    }

    //显示基本的AlertDialog
    public void showDialog(String title, String msg, String sure, String cancel, final OnAlertSureClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (!Utils.textIsNull(title)) {
            builder.setTitle(title);
        }
        if (!Utils.textIsNull(msg)) {
            builder.setMessage(msg);
        }
        String s = "确认";
        String c = "取消";
        if (!Utils.textIsNull(sure)) {
            s = sure;
        }
        if (!Utils.textIsNull(cancel)) {
            c = cancel;
        }
        builder.setPositiveButton(s,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        listener.onclick();
                    }
                });
        builder.setNegativeButton(c, null);
        builder.show();
    }

    @Override
    public boolean onSearchRequested() {
        return false;
    }

    public void hideInput(EditText et) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        et.setCursorVisible(false);// 失去光标
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    public void showInput(final EditText et) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(et, 0);
                           }
                       },
                500);
    }


    @Override
    public void onClick(View v) {
        KeyBoardCancle();
    }


    /**
     * 关闭软键盘
     */
    public void KeyBoardCancle() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public abstract int getLayoutId();

    public abstract void init();

    public abstract void getIntentData(Intent intent);


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }


    public void httpGet(String url, RequestCallBack callback) {
        L.e("httpGet url=" + url);
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET, url, callback);
    }

    public void httpPost(String url, RequestParams params, RequestCallBack callBack) {
        L.e("httoPost url=" + url);
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, url, params, callBack);
    }

    private AlertDialog dialog;


    public void clickVrMode() {
        Intent intent = new Intent(this, LandscapeActivity.class);
        intent.putExtra("classifies", (Serializable) BaseApplication.netClassifys);

        startActivity(intent);
    }

    //    判断是否有个网络连接
    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    //判断WIFI网络是否可用
    public boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    //判断MOBILE网络是否可用
    public boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    //获取当前网络连接的类型信息
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

}
