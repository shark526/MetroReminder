package com.shark.metroreminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by yuw6 on 18/03/2016.
 */
public class MyAdapter extends BaseAdapter {
//    private int[] colors = new int[] { 0xff3cb371, 0xffa0a0a0 };
    private Context mContext;
    private ArrayList<StationItem> dataList;
    private SharedPreferences sharedData;

    public MyAdapter(Context context, ArrayList<StationItem> dataList,SharedPreferences sharedData) {
        this.mContext = context;
        this.dataList = dataList;
        this.sharedData = sharedData;
    }
    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public StationItem getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder  holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.my_listitem, null);
            holder.selected = (CheckBox) convertView.findViewById(R.id.cbRemind);
            holder.itemTitle = (TextView) convertView.findViewById(R.id.ItemTitle);
            holder.itemText = (TextView) convertView.findViewById(R.id.ItemText);
            // bind holder to convertView
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.selected.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
//                itemChecked[position] = cb.isChecked();
                dataList.get(position).needRemind = cb.isChecked();
                String stationCellID =dataList.get(position).StationCellID;
                String stationName = dataList.get(position).StationName +"|" + (cb.isChecked() ?"1":"0");
                SharedPreferences.Editor editor = sharedData.edit();
                if(sharedData.contains(stationCellID)){
                    editor.remove(stationCellID);
                    editor.putString(stationCellID,stationName);
                    editor.commit();
                }
            }
        });
        // add data to ViewHolder
        holder.selected.setChecked(dataList.get(position).needRemind);
        holder.itemTitle.setText(dataList.get(position).StationName);
        holder.itemText.setText(dataList.get(position).StationCellID);
//        int colorPos = position % colors.length;
//        convertView.setBackgroundColor(colors[colorPos]);
        return convertView;
    }
    final class ViewHolder {
        CheckBox selected;
        TextView itemTitle;
        TextView itemText;
    }
}
