package com.stetcho.smartomagneter.framework.measurement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stetcho.smartomagneter.R;
import com.stetcho.smartomagneter.framework.measurement.model.SensorDelayModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter used in a spinner that contains a list with various sensor delays/rates
 */
public class SensorDelayAdapter extends BaseAdapter {
    private final Context mContext;
    private List<SensorDelayModel> mData;

    class ViewHolder {
        @BindView(R.id.tv_title)
        TextView tvTitle;

        ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

    public SensorDelayAdapter(Context context, List<SensorDelayModel> sensorDelayModelList) {
        mData = sensorDelayModelList;
        mContext = context;
    }

    public void setData(List<SensorDelayModel> sensorDelayModelList) {
        mData = sensorDelayModelList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public SensorDelayModel getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SensorDelayModel sensorDelayModel = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.li_sensor_delay, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvTitle.setText(sensorDelayModel.getTitle());

        return convertView;
    }
}
