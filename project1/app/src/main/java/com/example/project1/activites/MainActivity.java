package com.example.project1.activites;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.project1.api.FastApiService;
import com.example.project1.databinding.ActivityMainBinding;
import com.example.project1.model.Card;
import com.example.project1.sessionmanagement.UserSharedPreference;
import com.example.project1.ui.main.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private FastApiService Retrofit;
    private RecyclerView recyclerView;
    private static  String BASE_URL = "http://";

    private RunningTaskUser runningTaskUserAdapter;
    private CompalateTaskUser complateTaskUser;
    private ActivityMainBinding binding;  // Binding for data binding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Data binding initialization
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // RecyclerView setup
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrofit setup
        UserSharedPreference sh = new UserSharedPreference(this);

        BASE_URL=BASE_URL+sh.getIP()+"/";
        Log.d("url", "onCreate: "+sh.getIP());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // Replace with your API base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Retrofit = retrofit.create(FastApiService.class);

        // Other UI setup
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle FAB click
                Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);
                startActivity(intent);
            }
        });

        // Add OnPageChangeListener to the ViewPager for swapping functionality
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Empty implementation
            }

            @Override
            public void onPageSelected(int position) {
                // Check which tab is selected and execute code accordingly
                switch (position) {
                    case 0:
                        // "Running" tab is selected
                        new RunningTaskNetworkTask().execute();
                        break;
                    case 1:
                        // "Complete" tab is selected
                        new CompletedTaskNetworkTask().execute();
                        break;
                    // Add more cases if you have additional tabs
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Empty implementation
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Execute AsyncTask to fetch data from the network when the activity is created or resumed
        switch (binding.tabs.getSelectedTabPosition()) {
            case 0:
                new RunningTaskNetworkTask().execute();
                break;
            case 1:
                new CompletedTaskNetworkTask().execute();
                break;
            // Add more cases if you have additional tabs
        }
    }

    private class RunningTaskNetworkTask extends AsyncTask<Void, Void, List<Card>> {
        @Override
        protected List<Card> doInBackground(Void... voids) {
            List<Card> cardList = new ArrayList<>();

            try {
                UserSharedPreference sh = new UserSharedPreference(MainActivity.this);
                int ownerId = sh.getUserDetails();

                // Replace the URL with your FastAPI endpoint for running tasks
                URL url = new URL("http://"+sh.getIP()+"/get_running_tasks?current_user_id=" + ownerId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = connection.getInputStream();
                Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
                String result = scanner.hasNext() ? scanner.next() : "";

                // Parse JSON response
                cardList = parseJson(result);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return cardList;
        }

        @Override
        protected void onPostExecute(List<Card> cardList) {
            // Update UI or perform other tasks with the fetched data
            runningTaskUserAdapter = new RunningTaskUser(MainActivity.this, cardList);
            recyclerView.setAdapter(runningTaskUserAdapter);
        }
    }

    private class CompletedTaskNetworkTask extends AsyncTask<Void, Void, List<Card>> {
        @Override
        protected List<Card> doInBackground(Void... voids) {
            List<Card> cardList = new ArrayList<>();

            try {
                UserSharedPreference sh = new UserSharedPreference(MainActivity.this);
                int ownerId = sh.getUserDetails();

                // Replace the URL with your FastAPI endpoint for completed tasks
                URL url = new URL("http://"+sh.getIP()+"/get_complete_tasks?current_user_id=" + ownerId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = connection.getInputStream();
                Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
                String result = scanner.hasNext() ? scanner.next() : "";

                // Parse JSON response
                cardList = parseJson(result);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return cardList;
        }

        @Override
        protected void onPostExecute(List<Card> cardList) {
            // Update UI or perform other tasks with the fetched data
            // Note: This method runs on the main thread, so it's safe to update UI elements here.
            // For example, you can update your UI with the fetched data.
            // If you are using a RecyclerView, you can set the adapter here.
            // completedTaskUserAdapter = new CompletedTaskUser(MainActivity.this, cardList);
            // recyclerView.setAdapter(completedTaskUserAdapter);
            complateTaskUser  = new CompalateTaskUser(MainActivity.this, cardList);
            recyclerView.setAdapter(complateTaskUser);
        }
    }

    private List<Card> parseJson(String json) {
        List<Card> cardList = new ArrayList<>();

        try {
            // Parse JSON response
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("user_tasks");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject taskObject = jsonArray.getJSONObject(i);
                int taskId = taskObject.getInt("task_id");
                String title = taskObject.getString("title");
                String description = taskObject.getString("description");
                String startDate = taskObject.getString("start_date");
                String endDate = taskObject.getString("end_date");
                String priority = taskObject.getString("priority");
                int owner_id = taskObject.getInt("owner_id");

                // Create a Card object and add it to the list
                Card card = new Card(taskId, owner_id, title, description, startDate, endDate, priority);
                cardList.add(card);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cardList;
    }
}
