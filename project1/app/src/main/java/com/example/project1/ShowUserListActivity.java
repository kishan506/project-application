package com.example.project1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.activites.UserAdapter;
import com.example.project1.model.User;
import com.example.project1.sessionmanagement.UserSharedPreference;

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

public class ShowUserListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<Integer> selectedUsers;

    private Button addUsers;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);

        Intent intent = getIntent();
        selectedUsers = intent.getIntegerArrayListExtra("selectedUsers");

        addUsers = findViewById(R.id.addusers);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter();
        recyclerView.setAdapter(userAdapter);

        // Execute AsyncTask to retrieve data from the FastAPI
        new FetchDataAsyncTask().execute();

        addUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putIntegerArrayListExtra("resultList",selectedUsers);
                setResult(RESULT_OK,resultIntent);
                finish();
            }
        });
    }

    private class FetchDataAsyncTask extends AsyncTask<Void, Void, List<User>> {
        @Override
        protected List<User> doInBackground(Void... voids) {
            List<User> userList = new ArrayList<>();

            try {

                UserSharedPreference sh = new UserSharedPreference(ShowUserListActivity.this);
                int owenerId = sh.getUserDetails();
                // Replace the URL with your FastAPI endpoint
                URL url = new URL("http://192.168.29.223:8000/users?current_user_id="+owenerId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = connection.getInputStream();
                Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
                String result = scanner.hasNext() ? scanner.next() : "";

                // Parse JSON response
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject userObject = jsonArray.getJSONObject(i);
                    int userId = userObject.getInt("id");
                    String firstName = userObject.getString("firstname");
                    String lastName = userObject.getString("lastname");

                    User user = new User();
                    user.setUserId(userId);
                    user.setFirstname(firstName);
                    user.setLastName(lastName);
                    userList.add(user);
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return userList;
        }

        @Override
        protected void onPostExecute(List<User> result) {
//            selectedUsers = new ArrayList<>();
            userAdapter.setData(result,selectedUsers);
        }
    }
}
