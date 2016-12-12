package com.example.gardo.myapplication.Model;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.gardo.myapplication.R;

import java.util.ArrayList;

/**
 * Created by gardo on 11/12/2016.
 */

public class ListViewCoupon extends ArrayAdapter<CouponItemModel> {
    private final Activity context;
    private final ArrayList<CouponItemModel> coupon;

    public ListViewCoupon(Activity context, ArrayList<CouponItemModel> coupon) {
        super(context, R.layout.coupon_item, coupon);
        this.context = context;
        this.coupon = coupon;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView = inflater.inflate(R.layout.coupon_item, null, true);
        TextView name = (TextView) rowView.findViewById(R.id.name_coupon);
        TextView point = (TextView) rowView.findViewById(R.id.point_coupon);
        name.setText(coupon.get(position).getName());
        point.setText("" + coupon.get(position).getPoint());
        return rowView;
    }
}
