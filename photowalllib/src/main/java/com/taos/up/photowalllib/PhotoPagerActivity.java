package com.taos.up.photowalllib;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.shizhefei.view.largeimage.LargeImageView;
import com.taos.up.photowalllib.beans.ImageBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by PrinceOfAndroid on 2018/4/20 0020.
 */

public class PhotoPagerActivity extends AppCompatActivity {
    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvCount;
    private ViewPager vpPhoto;
    private RelativeLayout rlTitle;

    private int index;
    private int max;
    private List<ImageBean> imgs;
    private int selectNum;
    private MyPagerAdapter pagerAdapter;
    private List<Integer> selectIndexs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pager);
        initData();
        initView();
    }

    private void initData() {
        selectIndexs = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        index = bundle.getInt("index", 0);
        imgs = (List<ImageBean>) bundle.getSerializable("imgs");
        selectNum = bundle.getInt("selectNum", 0);
        max = bundle.getInt("max");
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvCount = (TextView) findViewById(R.id.tv_count);
        vpPhoto = (ViewPager) findViewById(R.id.vp_photo);
        rlTitle = (RelativeLayout) findViewById(R.id.rl_title);

        rlTitle.setBackgroundColor(Color.parseColor("#64585858"));
        tvTitle.setText(index + 1 + "/" + imgs.size());
        tvCount.setText("(" + selectNum + "/" + max + ")");
        pagerAdapter = new MyPagerAdapter();
        vpPhoto.setPageTransformer(true, new ZoomOutTransformer());
        vpPhoto.setAdapter(pagerAdapter);

        vpPhoto.setCurrentItem(index);

        vpPhoto.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                index = position;
                tvTitle.setText(index + 1 + "/" + imgs.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("select", (Serializable) selectIndexs);
                intent.putExtra("selectNum", selectNum);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }


    public class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imgs.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final ImageBean ib = imgs.get(position);
            View v = getLayoutInflater().inflate(R.layout.item_photo, null);
            final LargeImageView ivPhoto = v.findViewById(R.id.iv_photo);
            final ImageView ivChoose = v.findViewById(R.id.iv_choose);
            if (ib.isChoose()) {
                ivChoose.setImageResource(R.mipmap.ic_checked);
            } else {
                ivChoose.setImageResource(R.mipmap.ic_uncheck);
            }

            //加载大图
            Glide.with(PhotoPagerActivity.this)
                    .load(imgs.get(position).getImgPath()).asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            int imageHeight = resource.getHeight();
                            int imageWidth = resource.getWidth();
                            //图片超过最大处理尺寸 进行缩放
                            if (imageWidth > 8192f || imageHeight > 8192f) {
                                if (imageWidth > imageHeight) {
                                    resource = small(resource, 8192f / imageWidth);
                                } else {
                                    resource = small(resource, 8192f / imageHeight);
                                }
                            }
                            ivPhoto.setImage(resource);
                        }
                    });

            ivChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView iv = (ImageView) v;
                    boolean select = !ib.isChoose();
                    if (select) {
                        if (selectNum == max) {
                            Toast.makeText(PhotoPagerActivity.this, "最多可选" + max + "张图片", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        iv.setImageResource(R.mipmap.ic_checked);
                        selectNum++;
                        selectIndexs.add(position);
                    } else {
                        iv.setImageResource(R.mipmap.ic_uncheck);
                        selectNum--;
                        selectIndexs.remove(Integer.valueOf(position));
                    }
                    imgs.get(position).setChoose(select);
                    tvCount.setText("(" + selectNum + "/" + max + ")");
                }
            });
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("select", (Serializable) selectIndexs);
        intent.putExtra("selectNum", selectNum);
        setResult(RESULT_OK, intent);
        finish();
    }

    private static Bitmap small(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }
}
