package com.example.gardo.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.rspective.voucherify.android.client.VoucherifyAndroidClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class CouponFragment extends Fragment {


    public CouponFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_coupon, container, false);
    }

}
