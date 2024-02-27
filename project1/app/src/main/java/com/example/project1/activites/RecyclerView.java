package com.example.project1.activites;
// YourActivity.java or YourFragment.java
//package com.example.project1.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.project1.R;
import com.example.project1.model.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecyclerView extends AppCompatActivity {

    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<String> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        dataList = new ArrayList<>();
        // Add your data to dataList (e.g., fetch from API, database, etc.)

        adapter = new RecyclerViewAdapter(dataList); // Assuming you have a constructor in your adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
