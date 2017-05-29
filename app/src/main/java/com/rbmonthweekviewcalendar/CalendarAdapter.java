package com.rbmonthweekviewcalendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CalendarAdapter extends BaseAdapter {

    protected ArrayList<CalBean> featureBean;
    protected Context cntx;
    protected ViewHolder holder;


    public CalendarAdapter(Context cntx, ArrayList<CalBean> featureBean) {
        // TODO Auto-generated constructor stub
        this.featureBean = featureBean;
        this.cntx = cntx;
        holder = null;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return featureBean.size();
    }

    @Override
    public Object getItem(int position) {
        return featureBean.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) cntx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_cal_details, parent, false);
            holder = new ViewHolder();
            holder.row_cal_detail_id = (TextView) convertView
                    .findViewById(R.id.row_cal_detail_id);
            holder.row_cal_detail_name = (TextView) convertView
                    .findViewById(R.id.row_cal_detail_name);
            holder.row_cal_detail_type = (TextView) convertView
                    .findViewById(R.id.row_cal_detail_type);
            holder.row_cal_detail_date = (TextView) convertView
                    .findViewById(R.id.row_cal_detail_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }
        final CalBean nevi = featureBean.get(position);
        holder.row_cal_detail_type.setText("Ticket type :" + "  " + nevi.getType());
        holder.row_cal_detail_id.setText("Ticket id :" + "  " + nevi.getId());
        holder.row_cal_detail_name.setText("Ticket name :" + "  " + nevi.getTitle());
        holder.row_cal_detail_date.setText("Ticket date :" + "  " + nevi.getDate());

        return convertView;

    }

    private static class ViewHolder {
        protected TextView row_cal_detail_id;
        protected TextView row_cal_detail_name;
        protected TextView row_cal_detail_type;
        protected TextView row_cal_detail_date;

    }


}