package com.prm391.project.bingeeproject.Utils;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.snackbar.Snackbar;
import com.prm391.project.bingeeproject.Common.NavigationHost;
import com.prm391.project.bingeeproject.Fragment.HomeFragment;
import com.prm391.project.bingeeproject.Fragment.ProductListFragment;
import com.prm391.project.bingeeproject.Fragment.SearchFragment;
import com.prm391.project.bingeeproject.R;

import xyz.sahildave.widget.SearchViewLayout;

public class HandleSearchComponent {
    private static final String TAG = HandleSearchComponent.class.getSimpleName();

    private static Toolbar toolbar;
    private static SearchViewLayout searchViewLayout;
    public static void handleSearchView(View view, final FragmentActivity activity) {
        toolbar = view.findViewById(R.id.app_bar);
        searchViewLayout = (SearchViewLayout) view.findViewById(R.id.search_view_container);

        searchViewLayout.setVisibility(View.GONE);

        searchViewLayout.setExpandedContentSupportFragment(activity, new SearchFragment());
        searchViewLayout.handleToolbarAnimation(toolbar);
        searchViewLayout.handleToolbarAnimation(toolbar);
        searchViewLayout.setCollapsedHint("Collapsed Hint");
        searchViewLayout.setExpandedHint("Expanded Hint");
//        searchViewLayout.setHint("Global Hint");


        final SearchViewLayout finalSearchViewLayout = searchViewLayout;
        searchViewLayout.setSearchListener(new SearchViewLayout.SearchListener() {
            @Override
            public void onFinished(String searchKeyword) {
                finalSearchViewLayout.collapse();
                Snackbar.make(finalSearchViewLayout, "Start Search for - " + searchKeyword, Snackbar.LENGTH_LONG).show();
                Bundle bundle = new Bundle();
                bundle.putString("searchKeyword", searchKeyword);

                ProductListFragment productListFragment = new ProductListFragment();
//                        productListFragment.setArguments(bundle);

                ((NavigationHost)activity).navigateTo(productListFragment, bundle, true);
            }
        });


        searchViewLayout.setSearchBoxListener(new SearchViewLayout.SearchBoxListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("", "beforeTextChanged: " + s + "," + start + "," + count + "," + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: " + s + "," + start + "," + before + "," + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: " + s);
            }
        });
    }
    public static void toggleSearch(){
        if (searchViewLayout.isShown()) {
            searchViewLayout.setVisibility(View.GONE);
        } else {
            searchViewLayout.setVisibility(View.VISIBLE);

        }
    }
    public static void showSearchAndSetKeyword(String searchKeyword) {
        searchViewLayout.setVisibility(View.VISIBLE);
        searchViewLayout.setCollapsedHint(searchKeyword);
        searchViewLayout.setExpandedHint(searchKeyword);
    }
}
