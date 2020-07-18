package com.prm391.project.bingeeproject.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prm391.project.bingeeproject.R;


public class SearchFragment extends Fragment {

    private String keywords;
//
//    public SearchFragment(String keywords) {
//        this.keywords = keywords;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("SearchKeyword","SearchKeyword: "+ keywords);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }
}