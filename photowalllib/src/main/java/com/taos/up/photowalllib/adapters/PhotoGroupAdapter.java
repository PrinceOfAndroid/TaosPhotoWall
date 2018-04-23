package com.taos.up.photowalllib.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.taos.up.photowalllib.R;
import com.taos.up.photowalllib.beans.ImageGroupBean;

import java.util.List;

/**
 * Created by PrinceOfAndroid on 2018/4/20 0020.
 */

public class PhotoGroupAdapter extends RecyclerView.Adapter<PhotoGroupAdapter.ViewHolder> {
    private int selectIndex;
    private List<ImageGroupBean> imageGroupBeanList;
    private Context context;
    private GroupClickListener groupClickListener;

    public PhotoGroupAdapter(List<ImageGroupBean> imageGroupBeanList, Context context) {
        this.imageGroupBeanList = imageGroupBeanList;
        this.context = context;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.re_photo_list_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ImageGroupBean groupBean = imageGroupBeanList.get(position);
        holder.tvFolderName.setText(groupBean.getFolderName());
        holder.tvSize.setText(groupBean.getImageCounts() + "张");
        if (position == selectIndex) {
            holder.ivSelect.setVisibility(View.VISIBLE);
        } else {
            holder.ivSelect.setVisibility(View.GONE);
        }
        Glide.with(context)
                .load(groupBean.getTopImagePath())
                .crossFade()
                .centerCrop()
                .into(holder.ivFolder);

        holder.rlContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupClickListener != null) {
                    groupClickListener.groupClick(position, v);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageGroupBeanList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivFolder;

        private TextView tvFolderName;
        private TextView tvSize;
        private ImageView ivSelect;
        private RelativeLayout rlContent;

        public ViewHolder(View view) {
            super(view);

            ivFolder = view.findViewById(R.id.iv_folder);
            ivSelect = view.findViewById(R.id.iv_select);
            tvFolderName = view.findViewById(R.id.tv_folder_name);
            tvSize = view.findViewById(R.id.tv_size);
            ivSelect = view.findViewById(R.id.iv_select);
            rlContent = view.findViewById(R.id.rl_content);
        }
    }

    public interface GroupClickListener {
        void groupClick(int position, View v);
    }

    public void setGroupClickListener(GroupClickListener groupClickListener) {
        this.groupClickListener = groupClickListener;
    }
}
