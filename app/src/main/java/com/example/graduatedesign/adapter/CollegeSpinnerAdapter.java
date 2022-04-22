package com.example.graduatedesign.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.graduatedesign.R;
import com.example.graduatedesign.data.model.College;

import java.util.List;

public class CollegeSpinnerAdapter extends BaseAdapter {
    private List<College> dataList;

    public CollegeSpinnerAdapter( @NonNull List<College> dataList) {
        this.dataList=dataList;
    }

    @Override
    public int getCount() {
        return dataList!=null? dataList.size() : 0;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dataList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CollegeSpinnerViewHolder holder;
        //实现复用
        if (convertView==null){
            holder=new CollegeSpinnerViewHolder();
            convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_college_spinner,parent,false);
            convertView.setTag(holder);
        }else
            holder= (CollegeSpinnerViewHolder) convertView.getTag();
        //绑定数据和视图
        holder.bind(convertView,dataList.get(position));
        return convertView;
    }


    public static class CollegeSpinnerViewHolder{
        private TextView itemTxt;

        public void bind(View convertView,College item){
            itemTxt=convertView.findViewById(R.id.textView3);
            itemTxt.setText(item.getCollege_name());
        }

    }
}
