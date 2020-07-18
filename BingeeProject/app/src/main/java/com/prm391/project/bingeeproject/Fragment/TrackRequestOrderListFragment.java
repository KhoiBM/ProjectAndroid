package com.prm391.project.bingeeproject.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.prm391.project.bingeeproject.Adapter.GridItemDecoration;
import com.prm391.project.bingeeproject.Adapter.RequestCardViewHolder;
import com.prm391.project.bingeeproject.Common.NavigationHost;
import com.prm391.project.bingeeproject.Interface.ItemClickListener;
import com.prm391.project.bingeeproject.Model.Request;
import com.prm391.project.bingeeproject.R;


public class TrackRequestOrderListFragment extends Fragment {


    private static final String TAG = TrackRequestOrderListFragment.class.getSimpleName();
    private FirebaseDatabase mDatabase;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Request, RequestCardViewHolder> adapter;
    private String phoneUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        phoneUser = bundle.getString("phoneUser");

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_track_request_order_list, container, false);

        setUpToolbar(view);

        mDatabase = FirebaseDatabase.getInstance();

        recyclerView = view.findViewById(R.id.recycler_view_requests_order);
        recyclerView.setHasFixedSize(false);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));

        int largePadding = getResources().getDimensionPixelSize(R.dimen.bin_item_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.bin_category_grid_spacing_small);
        recyclerView.addItemDecoration(new GridItemDecoration(largePadding, smallPadding));

        loadRequestOrderList();

        return view;
    }

    private void loadRequestOrderList() {

        Query requestList = mDatabase.getReference().child("Requests").orderByChild("mPhoneUser").equalTo(phoneUser);
        FirebaseRecyclerOptions<Request> options =
                new FirebaseRecyclerOptions.Builder<Request>()
                        .setQuery(requestList, Request.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Request, RequestCardViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestCardViewHolder holder, int position, @NonNull Request model) {
               int ordinalNumbers=position +1;
                holder.ordinalNumbers.setText(ordinalNumbers + ".");
                holder.title.setText(adapter.getRef(position).getKey());
                holder.date.setText(model.getmDateOrder());
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Bundle bundle = new Bundle();
                        bundle.putString("requestId",adapter.getRef(position).getKey());
                        ((NavigationHost) getActivity()).navigateTo(new ViewDetailRequestOrderFragment(), bundle,true);

                    }
                });
            }
            @NonNull
            @Override
            public RequestCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_card, parent, false);
                return new RequestCardViewHolder(layoutView);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.bin_toolbar_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bundle = new Bundle();
        switch (item.getItemId()) {
            case R.id.search:
                break;
            case R.id.shopping_cart:
                ((NavigationHost) getActivity()).navigateTo(new CartFragment(), bundle,true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}