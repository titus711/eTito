//package com.apo.etito;
//
//import android.content.Intent;
//import android.os.Bundle;
//
//import com.apo.etito.ViewHolder.ProductViewHolder;
//import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.android.material.snackbar.Snackbar;
//
//import android.view.LayoutInflater;
//import android.view.MenuItem;
//import android.view.View;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.ActionBarDrawerToggle;
//import androidx.core.view.GravityCompat;
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//import androidx.navigation.ui.AppBarConfiguration;
//import androidx.navigation.ui.NavigationUI;
//
//import com.google.android.material.navigation.NavigationView;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.squareup.picasso.Picasso;
//
//import androidx.drawerlayout.widget.DrawerLayout;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.Menu;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import Model.Products;
//import Prevalent.Prevalent;
//import de.hdodenhof.circleimageview.CircleImageView;
//import io.paperdb.Paper;
//
//public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
//
//    private AppBarConfiguration mAppBarConfiguration;
//
//    private DatabaseReference productsRef;
//    private RecyclerView recyclerView;
//    RecyclerView.LayoutManager layoutManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
//
//        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
//
//        Paper.init(this);
//
//
//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("Home");
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_cart, R.id.nav_orders, R.id.nav_category,
//                R.id.nav_settings, R.id.nav_logout)
//                .setDrawerLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
//
//        View headerView = navigationView.getHeaderView(0);
//        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
//        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);
//        userNameTextView.setText(Prevalent.currentOnLineUsers.getEmail());
//
//        recyclerView = findViewById(R.id.recycler_menu);
//        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        FirebaseRecyclerOptions<Products> options =
//                new FirebaseRecyclerOptions.Builder<Products>()
//                        .setQuery(productsRef,Products.class)
//                        .build();
//
//        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
//                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
//                    @Override
//                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
//                        holder.txtProductName.setText(model.getPname());
//                        holder.txtProductDescription.setText(model.getDescription());
//                        holder.txtProductPrice.setText("Price = R " + model.getPrice());
//                        Picasso.get().load(model.getImage()).into(holder.imageView);
//
//
//
//
//                    }
//
//                    @NonNull
//                    @Override
//                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
//                        ProductViewHolder holder = new ProductViewHolder(view);
//                        return holder;
//                    }
//                };
//        recyclerView.setAdapter(adapter);
//        adapter.startListening();
//
//
//    }
//
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)){
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        return super.onOptionsItemSelected(item);
//    }
//
//
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//        int id = menuItem.getItemId();
//
//        if (id == R.id.nav_cart){
//
//        }
//
//        else if (id == R.id.nav_orders){
//
//        } else if (id == R.id.nav_category){
//
//        } else if (id == R.id.nav_settings){
//            Intent intent = new Intent(com.apo.etito.HomeActivity.this,SettingsActivity.class);
//            startActivity(intent);
//
//        } else if (id == R.id.nav_logout){
//
//            Paper.book().destroy();
//            Intent intent = new Intent(com.apo.etito.HomeActivity.this,MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
//}
//
//
//
