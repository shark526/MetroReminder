package com.shark.metroreminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 3/19/16.
 */
public class MyListAdapter extends BaseAdapter {

    private LayoutInflater Inflater;
    private List<HashMap<String, String>> MyList;
    private Boolean[] CheckStatus;
    public MyListAdapter(LayoutInflater inflater, List<HashMap<String, String>> list){
        this.Inflater=inflater;
        this.MyList=list;
        CheckStatus = new Boolean[list.size()];
    }
    private class ViewHolder{
        TextView itemText;
        TextView itemTitle;
        CheckBox cbReminder;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return MyList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return MyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view=null;
        ViewHolder holder=null;
        if(convertView==null){

            convertView= Inflater.inflate(R.layout.my_listitem, null);
            holder=new ViewHolder();
            holder.itemText=(TextView) convertView.findViewById(R.id.ItemText);
            holder.itemTitle=(TextView) convertView.findViewById(R.id.ItemTitle);
            holder.cbReminder=(CheckBox)convertView.findViewById(R.id.cbRemind);
//                holder.check.setTag(index);
            convertView.setTag(holder);
        }
        else{

            view =convertView;
            holder=(ViewHolder)view.getTag();
        }
        holder.itemText.setText(MyList.get(position).get("ItemText"));
        holder.itemTitle.setText(MyList.get(position).get("ItemTitle"));
        holder.cbReminder.setTag(position);
        holder.cbReminder.setChecked(CheckStatus[position]==null?false:CheckStatus[position]);
        addListener(holder,position);//添加事件响应
        return convertView;
    }

    private void addListener(ViewHolder holder,final int position){

        holder.cbReminder.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                CheckStatus[position] = cb.isChecked();
            }
        });
    }
}
