package com.zuojianyou.zybdoctor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.OfficeInfo;

import java.util.List;

public class DepartmentDialogAdapter extends BaseAdapter {
    private Context mContext;
    private List<OfficeInfo> datas;

    public DepartmentDialogAdapter(Context context, List<OfficeInfo> datas) {
        mContext = context;
        this.datas = datas;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_department, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        viewHolder.name.setText(datas.get(position).getOfficeName());
        viewHolder.position = position;
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        int position;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemViewClickListener != null) {
                        onItemViewClickListener.onItemViewClick(v,position);
                    }
                }
            });
        }
    }
}
