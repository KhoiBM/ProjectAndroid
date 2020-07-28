package com.prm391.project.bingeeproject.Fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prm391.project.bingeeproject.Adapter.CategoryCardViewHolder;
import com.prm391.project.bingeeproject.Adapter.GlideApp;
import com.prm391.project.bingeeproject.Adapter.GridItemDecoration;
import com.prm391.project.bingeeproject.Interface.NavigationHost;
import com.prm391.project.bingeeproject.Utils.NavigationIconClickListener;
import com.prm391.project.bingeeproject.Dialog.LoadingDialog;
import com.prm391.project.bingeeproject.Interface.ItemClickListener;
import com.prm391.project.bingeeproject.Model.Category;
import com.prm391.project.bingeeproject.R;
import com.prm391.project.bingeeproject.Utils.HandleNavMenu;
import com.prm391.project.bingeeproject.Utils.HandleSearchComponent;
import com.prm391.project.bingeeproject.Utils.Utils;
import com.prm391.project.bingeeproject.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private FirebaseDatabase mDatabase;
    private DatabaseReference categoryRef;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Category, CategoryCardViewHolder> adapter;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imagesRef;
    private FragmentHomeBinding homeBinding;
    private LoadingDialog loadingDialog;
    private boolean isAuth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        storage = FirebaseStorage.getInstance("gs://bingee-358c7.appspot.com");
        storageRef = storage.getReference("category");

        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        isAuth = bundle.getBoolean("isAuth");
        Log.i(TAG, "isAuth" + isAuth);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = homeBinding.getRoot();

        setUpToolbar(view);

        HandleSearchComponent.handleSearchView(view, getActivity());

        mDatabase = FirebaseDatabase.getInstance();
        categoryRef = mDatabase.getReference().child("Category");

        buttonsSetUp(view);

        recyclerView = homeBinding.recyclerViewCategory;
        recyclerView.setHasFixedSize(false);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));

        int largePadding = getResources().getDimensionPixelSize(R.dimen.bin_category_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.bin_category_grid_spacing_small);
        recyclerView.addItemDecoration(new GridItemDecoration(largePadding, smallPadding));

        loadCategory();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.home_grid).setBackgroundResource(R.drawable.corner_cut_grid_background_shape);
        }

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
                        ((NavigationHost) getActivity()).navigateTo(productListFragment, bundle, true);
                    }
                });

                loadingDialog.dismissDialog();
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.bin_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private Button nav_btn_ingredients, nav_btn_furniture, nav_btn_cart, nav_btn_my_account;

    private void buttonsSetUp(View view) {
        //menu button setup

//        nav_btn_go_home = view.findViewById(R.id.nav_btn_go_home);
//        nav_btn_go_home.setOnClickListener(this);
        nav_btn_ingredients = view.findViewById(R.id.nav_btn_ingredients);
        nav_btn_ingredients.setOnClickListener(this);
        nav_btn_furniture = view.findViewById(R.id.nav_btn_furniture);
        nav_btn_furniture.setOnClickListener(this);
        nav_btn_cart = view.findViewById(R.id.nav_btn_cart);
        nav_btn_cart.setOnClickListener(this);
        nav_btn_my_account = view.findViewById(R.id.nav_btn_my_account);
        setActionForBtnMyAccount();


        //--------------------------------
    }

    private void setActionForBtnMyAccount() {
        if (isAuth) {
            nav_btn_my_account.setText("MY ACCOUNT");
            nav_btn_my_account.setOnClickListener(this);
        } else {
            nav_btn_my_account.setText("LOGIN");
            nav_btn_my_account.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((NavigationHost) getActivity()).login();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        HandleNavMenu.commonNavigationMenuForCategory(v, getActivity());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Utils.handleOnOptionsItemSelected(item, getActivity());
        return super.onOptionsItemSelected(item);
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
                getContext(),
                view.findViewById(R.id.home_grid),
                new AccelerateDecelerateInterpolator(),
                getContext().getResources().getDrawable(R.drawable.bin_menu),
                getContext().getResources().getDrawable(R.drawable.ic_close), view));
    }
}