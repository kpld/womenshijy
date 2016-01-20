package com.hotcast.vr.pageview;


import com.hotcast.vr.DetailActivity;
import com.hotcast.vr.bean.HomeRoll;
import com.hotcast.vr.bean.HomeSubject;
import com.lidroid.xutils.BitmapUtils;

import com.hotcast.vr.R;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


public class ItemView extends LinearLayout {

    private ImageView titleimg;
    private TextView title;
    private LinearLayout ll_img;
    private HorizontalScrollView hs_view;
    private Context context;

    private List<HomeRoll> homeRolls;
    private BitmapUtils bu;

    public interface MovieImgClickLisenter {
        public void movieImgClick();
    }

    public ItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context);
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public ItemView(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    /**
     * ��ʼ��View�ķ���
     *
     * @param context
     */
    public void initView(Context context) {
        bu = new BitmapUtils(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_view, this);
        titleimg = (ImageView) findViewById(R.id.titleimg);
        title = (TextView) findViewById(R.id.title);
        ll_img = (LinearLayout) findViewById(R.id.ll_img);
        hs_view = (HorizontalScrollView) findViewById(R.id.hs_view);
//        hs_view.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("--- 条目被点击了");
//            }
//        });
    }

    public void setItemList(Context context, HomeSubject item) {
        String titleurl = item.getHead().getImage();
        String titleText = item.getHead().getTitle();
//        初始化条目头信息
        title.setText(titleText);
        bu.display(titleimg, titleurl);

//        初始化下面的横向滑动条目
        setItemMovies(context, item.getBody());
    }

    public void setItemMovies(final Context context, List<HomeRoll> rolls) {
        ll_img.removeAllViews();
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 30, 0);
        for (int i = 0; i < rolls.size(); i++) {
            final HomeRoll roll = rolls.get(i);
            System.out.println("---ItemView  roll---" + roll);

            LinearLayout contentView = (LinearLayout) View.inflate(context,
                    R.layout.item_item_img, null);
            ImageView iv_movie = (ImageView) contentView
                    .findViewById(R.id.iv_movie);
            BitmapUtils bitmapUtils = new BitmapUtils(getContext());

            if (roll.getImage() != null) {
                bitmapUtils.display(iv_movie, roll.getImage());
            }
            TextView tv_movie = (TextView) contentView
                    .findViewById(R.id.tv_movie);
            tv_movie.setText(roll.getTitle());
            iv_movie.setFocusable(true);
            iv_movie.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("action",roll.getAction());
                    intent.putExtra("resource",roll.getResource());
                    context.startActivity(intent);
                    System.out.println("---ItemView 条目被点击了---");
                }
            });
            contentView.setLayoutParams(params);
            ll_img.addView(contentView);
        }
    }

    public void setOnMovieImgClickLisenter(MovieImgClickLisenter movieClickLisenter) {
        movieClickLisenter.movieImgClick();
    }

    /**
     * ���ñ�����ߵ�ͼƬ
     *
     * @param id :��:R.drawable.ic_launcher
     */
    public void setTitleImg(int id) {
        titleimg.setBackgroundResource(id);
    }

    /**
     * ���ñ����������ݺʹ�С
     *
     * @param text
     * @param size :�����0.0,������ΪĬ�ϴ�С
     */
    public void setTitleText(String text, float size) {
        title.setText(text);
        if (size != 0.0) {

        }
        title.setTextSize(size);
    }
}
