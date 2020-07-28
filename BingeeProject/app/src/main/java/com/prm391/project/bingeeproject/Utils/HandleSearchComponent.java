package com.prm391.project.bingeeproject.Utils;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.snackbar.Snackbar;
import com.prm391.project.bingeeproject.Interface.NavigationHost;
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
        searchViewLayout.setCollapsedHint("Search");
        searchViewLayout.setExpandedHint("Enter keywords");
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

                ((NavigationHost)activity).navigateTo(productListFragment, bundle, true);
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
    public static void showSearch(){
        searchViewLayout.setVisibility(View.VISIBLE);
    }
    public static void hideSearch(){
        searchViewLayout.setVisibility(View.GONE);
    }
    public static void showSearchAndSetKeyword(String searchKeyword) {
        searchViewLayout.setVisibility(View.VISIBLE);
        searchViewLayout.setCollapsedHint(searchKeyword);
        searchViewLayout.setExpandedHint(searchKeyword);
    }
}
