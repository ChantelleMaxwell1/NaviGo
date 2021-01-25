package com.example.navigoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FavouritesListActivity extends AppCompatActivity {

    private DatabaseReference mDBRef;
    private FirebaseDatabase mDB;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private FavouritesRecyclerAdapter favouritesRecyclerAdapter;
    private List<Favourites> favouritesList;

    private ImageButton btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites_list);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String userID = mAuth.getCurrentUser().getUid();

        mDB = FirebaseDatabase.getInstance();
        mDBRef = mDB.getReference().child("Favourites").child(userID);

        favouritesList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.starred_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnMenu = (ImageButton) findViewById(R.id.btn_nav2);


        //favouritesRecyclerAdapter = new FavouritesRecyclerAdapter(this, favouritesList);
        //recyclerView.setAdapter(favouritesRecyclerAdapter);

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(FavouritesListActivity.this, btnMenu);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu2, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_back:
                                startActivity(new Intent(FavouritesListActivity.this, MapActivity.class));
                                return true;
                            case R.id.menu_logout:
                                mAuth.signOut();
                                startActivity(new Intent(FavouritesListActivity.this, MainActivity.class));
                                return true;
                            default:
                                return false;

                        }
                    }
                });

                popup.show(); //showing popup menu
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mDBRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Favourites favourites = snapshot.getValue(Favourites.class);

                favouritesList.add(favourites);

                favouritesRecyclerAdapter = new FavouritesRecyclerAdapter(FavouritesListActivity.this, favouritesList);
                recyclerView.setAdapter(favouritesRecyclerAdapter);
                favouritesRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}