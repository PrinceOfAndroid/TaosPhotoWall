package com.taos.up.photowalllib.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.taos.up.photowalllib.R;
import com.taos.up.photowalllib.utils.ScreenUtils;
import com.taos.up.photowalllib.utils.SizeUtils;
import com.taos.up.photowalllib.beans.ImageBean;

import java.util.List;

/**
 * Created by PrinceOfAndroid on 2018/4/20 0020.
 */

public class PhotoWallAdapter extends RecyclerView.Adapter<PhotoWallAdapter.ViewHolder> {
    private List<ImageBean> imageBeanList;
    private Context context;
    private boolean canMulti;

    private OnPhotoChooseClickListener onPhotoChooseClickListener;
    private OnPhotoClickListener onPhotoClickListener;

    public void setOnPhotoChooseClickListener(OnPhotoChooseClickListener onPhotoChooseClickListener) {
        this.onPhotoChooseClickListener = onPhotoChooseClickListener;
    }

    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        this.onPhotoClickListener = onPhotoClickListener;
    }

    public PhotoWallAdapter(Context context, List<ImageBean> imageBeanList,
                            boolean canMulti) {
        this.context = context;
        this.imageBeanList = imageBeanList;
        this.canMulti = canMulti;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.re_photo_wall_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //设置图片宽高
        holder.ivImg.setLayoutParams(new RelativeLayout.LayoutParams((ScreenUtils.getScreenWidth(context) - SizeUtils.dp2px(24, context)) / 3,
                (ScreenUtils.getScreenWidth(context) - SizeUtils.dp2px(24, context)) / 3));
        Glide.with(context).load(imageBeanList.get(position).getImgPath())
                .crossFade().centerCrop().into(holder.ivImg);

        if (canMulti) {
            holder.ivChoose.setVisibility(View.VISIBLE);
        } else {
            holder.ivChoose.setVisibility(View.GONE);
        }
        if (imageBeanList.get(position).isChoose()) {
            holder.ivChoose.setImageResource(R.mipmap.ic_checked);
        } else {
            holder.ivChoose.setImageResource(R.mipmap.ic_uncheck);
        }

        holder.ivChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPhotoChooseClickListener != null) {
                    onPhotoChooseClickListener.photoChooseClick(v, position);
                }
            }
        });

        holder.ivImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPhotoClickListener != null) {
                    onPhotoClickListener.photoClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageBeanList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivImg;
        public ImageView ivChoose;

        public ViewHolder(View view) {
            super(view);
            ivImg = (ImageView) view.findViewById(R.id.iv_img);
            ivChoose = (ImageView) view.findViewById(R.id.iv_choose);
        }
    }

    public interface OnPhotoChooseClickListener {
        void photoChooseClick(View view, int position);
    }

    public interface OnPhotoClickListener {
        void photoClick(View view, int position);
    }
}
