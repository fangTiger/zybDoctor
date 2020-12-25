package com.zuojianyou.zybdoctor.adapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected BaseAdapter.OnItemViewClickListener onItemViewClickListener;
    protected BaseAdapter.OnLongItemViewClickListener onLongItemViewClickListener;

    public void setOnItemViewClickListener(BaseAdapter.OnItemViewClickListener listener){
        this.onItemViewClickListener = listener;
    }

    public void setOnLongItemViewClickListener(BaseAdapter.OnLongItemViewClickListener listener){
        this.onLongItemViewClickListener = listener;
    }

    public interface OnItemViewClickListener {
        void onItemViewClick(View view, int position);
    }

    public interface OnLongItemViewClickListener {
        void onLongItemViewClick(View view, int position);
    }

}
