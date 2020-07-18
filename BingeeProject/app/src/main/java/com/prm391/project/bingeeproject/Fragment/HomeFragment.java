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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prm391.project.bingeeproject.Adapter.CategoryCardViewHolder;
import com.prm391.project.bingeeproject.Adapter.GlideApp;
import com.prm391.project.bingeeproject.Adapter.GridItemDecoration;
import com.prm391.project.bingeeproject.Common.NavigationHost;
import com.prm391.project.bingeeproject.Interface.ItemClickListener;
import com.prm391.project.bingeeproject.Model.Category;
import com.prm391.project.bingeeproject.R;


public class HomeFragment extends Fragment {


    private static final String TAG = HomeFragment.class.getSimpleName();
    private FirebaseDatabase mDatabase;
    private DatabaseReference categoryRef;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Category, CategoryCardViewHolder> adapter;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imagesRef;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        storage = FirebaseStorage.getInstance("gs://bingee-358c7.appspot.com");
        storageRef = storage.getReference("category");

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setUpToolbar(view);

        mDatabase = FirebaseDatabase.getInstance();
        categoryRef = mDatabase.getReference().child("Category");

        recyclerView = view.findViewById(R.id.recycler_view_category);
        recyclerView.setHasFixedSize(false);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));

        int largePadding = getResources().getDimensionPixelSize(R.dimen.bin_category_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.bin_category_grid_spacing_small);
        recyclerView.addItemDecoration(new GridItemDecoration(largePadding, smallPadding));

        loadCategory();

        return view;
    }

    private void loadCategory() {

        FirebaseRecyclerOptions<Category> options =
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(categoryRef, Category.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Category, CategoryCardViewHolder>(options) {
            @NonNull
            @Override
            public CategoryCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card, parent, false);
                return new CategoryCardViewHolder(layoutView);
            }

            @Override
            protected void onBindViewHolder(@NonNull final CategoryCardViewHolder holder, int position, @NonNull Category model) {

                holder.categoryTitle.setText(model.getmName());

                imagesRef = storageRef.child(model.getmImage());

                GlideApp.with(holder.categoryImage.getContext()).load(imagesRef).into(holder.categoryImage);

                final Category clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Bundle bundle = new Bundle();
                        bundle.putString("categoryID", adapter.getRef(position).getKey());

                        ProductListFragment productListFragment = new ProductListFragment();
//                        productListFragment.setArguments(bundle);

                        ((NavigationHost) getActivity()).navigateTo(productListFragment, bundle,true);
                    }
                });
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
                ((NavigationHost) getActivity()).navigateTo(new TrackRequestOrderListFragment(), bundle,true);
                break;
            case R.id.shopping_cart:
                ((NavigationHost) getActivity()).navigateTo(new CartFragment(), bundle,true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}