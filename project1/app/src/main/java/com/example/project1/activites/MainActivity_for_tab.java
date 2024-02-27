package com.example.project1.activites;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.project1.databinding.ActivityMain2ForTabBinding;
import com.example.project1.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity_for_tab extends AppCompatActivity {

    private ActivityMain2ForTabBinding binding;
    private RecyclerView recyclerView;
    private RunningTaskUser cardViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMain2ForTabBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        // Add a TabLayout.OnTabSelectedListener to handle tab selection events
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Switch between activities based on the selected tab
                switch (tab.getPosition()) {
                    case 0:
                        // Start ActivityForTab1 when the first tab is selected
                        startActivity(new Intent(MainActivity_for_tab.this, MainActivity.class));
                        break;
                    case 1:
                        // Start ActivityForTab2 when the second tab is selected
                        startActivity(new Intent(MainActivity_for_tab.this, UpdateTaskActivity.class));
                        break;
                    // Add more cases for additional tabs if needed
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle tab unselection if needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab reselection if needed
            }
        });
    }
}
