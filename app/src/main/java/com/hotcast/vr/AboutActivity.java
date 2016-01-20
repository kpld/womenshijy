package com.hotcast.vr;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.File;
import java.io.IOException;

import butterknife.InjectView;

/**
 * Created by lostnote on 15/12/2.
 */
public class AboutActivity extends BaseActivity {
    @InjectView(R.id.user_agreement)
    RelativeLayout user_agreement;
    @InjectView(R.id.head)
    RelativeLayout head;
    @InjectView(R.id.feedback)
    RelativeLayout feedback;
    @InjectView(R.id.tv_title)
    TextView tv_title;
    @InjectView(R.id.iv_return)
    ImageView iv_return;
    @InjectView(R.id.translucentview)
    FrameLayout advice;
    @InjectView(R.id.ll_tv)
    LinearLayout ll_tv;
    @InjectView(R.id.ll_advice)
    LinearLayout ll_advice;
    @InjectView(R.id.et_advice)
    EditText et_advice;
    @InjectView(R.id.bt_advice)
    Button bt_advice;
    private boolean isEdet = false;
    private boolean isagreement = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_agreement:
                advice.setVisibility(View.VISIBLE);
                ll_tv.setVisibility(View.VISIBLE);
                ll_advice.setVisibility(View.GONE);
                isagreement = true;
                break;
            case R.id.feedback:
                advice.setVisibility(View.VISIBLE);
                ll_advice.setVisibility(View.VISIBLE);
                isEdet = true;
                break;
//            case R.id.et_advice:
//
//                break;
//            case R.id.bt_advice:
//                System.out.println("****你点击了发送信息");
//                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_about;
    }

    @Override
    public void init() {
        tv_title.setText("关于");
        iv_return.setVisibility(View.VISIBLE);
        user_agreement.setOnClickListener(this);
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        feedback.setOnClickListener(this);
        bt_advice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendMsg = et_advice.getText().toString();
                if (!TextUtils.isEmpty(sendMsg)){

                    System.out.println("****建议上传成功" + sendMsg);
                    et_advice.setText("");

                }
                System.out.println("****你点击了发送信息");
            }
        });
    }

    @Override
    public void getIntentData(Intent intent) {

    }
  private void saveFile(String toSaveString, String filePath){
      File saveFile = new File(filePath);
      if (!saveFile.exists()){
          try {File dir = new File(saveFile.getPath());
              dir.mkdirs();
              saveFile.createNewFile();
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  }

    @Override
    public void onBackPressed() {
        if (isEdet || isagreement){
            advice.setVisibility(View.GONE);
            ll_tv.setVisibility(View.GONE);
            isagreement = false;
            isEdet = false;
        }else {
            super.onBackPressed();
        }
        System.out.println("****你点击返回键");
    }
}
