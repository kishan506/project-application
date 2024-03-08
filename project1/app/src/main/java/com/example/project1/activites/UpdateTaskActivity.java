package com.example.project1.activites;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project1.R;
import com.example.project1.ShowUserListActivity;
import com.example.project1.api.FastApiService;
import com.example.project1.model.Card;
import com.example.project1.model.Task;
import com.example.project1.model.TempUser;
import com.example.project1.sessionmanagement.UserSharedPreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateTaskActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    private int REQUEST_CODE = 1;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextStartDate;
    private EditText editTextEndDate;
    private Spinner spinnerPriority;
    boolean radioButtonValue = false;

    private Calendar calendar;
    private int currentYear, currentMonth, currentDay;
    UserSharedPreference sh = new UserSharedPreference(this);
    private  String BASE_URL = "http://";
    private ArrayList<Integer> selectedUsers;
    Button buttonUpdate, buttonReset, buttonAddUser, buttonComplete;
    RadioButton radioButtonComplete, radioButtonRunning;
    private int taskId,owner_id;
    int taskStatus = 0;
    public String startDateString1,endDateString1;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatetask);

        UserSharedPreference sh = new UserSharedPreference(this);
        owner_id = sh.getUserDetails();
//        FloatingActionButton fabChat = findViewById(R.id.fabChat);
        selectedUsers = new ArrayList<>();
        BASE_URL = BASE_URL+sh.getIP()+"/";
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextEndDate = findViewById(R.id.editTextEndDate);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        editTextTitle.setEnabled(false);
//        editTextDescription.setEnabled(false);
        editTextStartDate.setEnabled(false);
        editTextEndDate.setEnabled(false);

        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonReset = findViewById(R.id.buttonReset);
        buttonAddUser = findViewById(R.id.addUsers);
        buttonComplete = findViewById(R.id.buttonComplete);

        taskId = getIntent().getIntExtra("taskId", -1);
        Log.d("card_id from updateTask", taskId + "");
        getTaskByID();

        calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        FloatingActionButton fabChat = findViewById(R.id.fabChat);

        fabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the ChatActivity when the FAB is clicked
                Intent chatIntent = new Intent(UpdateTaskActivity.this,com.example.project1.activites.ChatActivity.class);
                startActivity(chatIntent);
            }
        });
        editTextStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(editTextStartDate);
            }
        });

        editTextEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(editTextEndDate);
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFields();
            }
        });

        buttonAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddUserClick(view);
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    updateData(taskId);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        buttonComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeTask();
            }
        });
    }

    private void setSpinnerSelection(Spinner spinnerPriority, String priority) {
        String[] prioritiesArray = getResources().getStringArray(R.array.priorityarray);
        int index = Arrays.asList(prioritiesArray).indexOf(priority);
        spinnerPriority.setSelection(index);
    }

    private void resetFields() {
        editTextTitle.setText("");
        editTextDescription.setText("");
        editTextStartDate.setText("");
        editTextEndDate.setText("");
        spinnerPriority.setSelection(0);
    }

    private void showDatePickerDialog(final EditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        editText.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                },
                currentYear,
                currentMonth,
                currentDay
        );

        datePickerDialog.show();
    }

    private Date convertStringToDate(String dateString) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.US);

        try {
            Date parsedDate = inputDateFormat.parse(dateString);
            if (parsedDate != null) {
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String formattedDateString = outputDateFormat.format(parsedDate);
                return outputDateFormat.parse(formattedDateString);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void updateData(int ownerId) throws ParseException {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
//        String startDateString1 = editTextStartDate.getText().toString();
//        String endDateString = editTextEndDate.getText().toString();
        String priority = spinnerPriority.getSelectedItem().toString();

        UserSharedPreference sh = new UserSharedPreference(this);
        int owner_id = sh.getUserDetails();

        try{

           startDateString1 = editTextStartDate.getText().toString().trim();
           endDateString1 = editTextEndDate.getText().toString().trim();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date startDate = dateFormat.parse(startDateString1);
            Date enddate = dateFormat.parse(endDateString1);
            if(enddate.before(startDate))
            {
                Toast.makeText(this, "End date cannot be before the start date", Toast.LENGTH_SHORT).show();
                return;
            }

        }catch(ParseException e)
        {
            e.printStackTrace();

        }

    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Log.d("OkHttp", message));
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor);
        Log.d("update", "updateData: "+BASE_URL);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FastApiService fastApiService = retrofit.create(FastApiService.class);
        Task task = new Task(title, description, startDateString1, endDateString1, priority, selectedUsers, owner_id, 0);

        Call<Response<ResponseBody>> call = fastApiService.updateTask(getIntent().getIntExtra("taskId", -1), ownerId, task);
        call.enqueue(new Callback<Response<ResponseBody>>() {
            @Override
            public void onResponse(Call<Response<ResponseBody>> call, Response<Response<ResponseBody>> response) {
                if (response.isSuccessful()) {
                    showToast("Update successfully");
                    Intent intent = new Intent(UpdateTaskActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    showToast("Update failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<Response<ResponseBody>> call, Throwable t) {
                showToast("Network error. Please try again.");
                Log.e("NetworkError", "Failed to make the network request", t);
            }

            private void showToast(String message) {
                Toast.makeText(UpdateTaskActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onAddUserClick(View view) {
        Log.d("onclick", "onAddUserClick: button click");
        Intent intent = new Intent(this, ShowUserListActivity.class);
        intent.putIntegerArrayListExtra("selectedUsers", selectedUsers);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            selectedUsers = data.getIntegerArrayListExtra("resultList");
            Log.d("selectedusers", selectedUsers.toString());
        }
    }

    private void getTaskByID() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FastApiService fastApiService = retrofit.create(FastApiService.class);

        Call<Card> call = fastApiService.getTaskByid(taskId);
        call.enqueue(new Callback<Card>() {
            @Override
            public void onResponse(Call<Card> call, Response<Card> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Card data = response.body();
                    Log.d("from on response body", response.body().toString());
                    if (data != null) {
                        Log.d("from on response", data.toString());
                        setData(data);
                    }
                } else {
                    Log.d("getTask by id ", "fail");
                    Toast.makeText(UpdateTaskActivity.this, "Not able to fetch task detail, server error", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Card> call, Throwable t) {
                Log.d("getTask by id ", "Network error");
                Toast.makeText(UpdateTaskActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void setData(Card card) {
        Log.d("cardId setdata", card.toString());

        editTextTitle.setText(card.getTitle());
        editTextDescription.setText(card.getDescription());
        editTextStartDate.setText(card.getStart_date());
        editTextEndDate.setText(card.getEnd_date());

        UserSharedPreference sh = new UserSharedPreference(this);
        int owner_id = sh.getUserDetails();
        int id = getIntent().getIntExtra("getOwner_id", -1);

        if (id == owner_id) {
            buttonUpdate.setEnabled(true);
            buttonUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        updateData(id);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            buttonComplete.setEnabled(true);
            buttonComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    completeTask();
                }
            });
        } else {
            buttonUpdate.setEnabled(false);
            int white = getResources().getColor(android.R.color.white);
            int textColor = getResources().getColor(android.R.color.black);
            buttonUpdate.setBackgroundColor(white);
            buttonComplete.setBackgroundColor(white);
            buttonUpdate.setTextColor(textColor);
            buttonComplete.setEnabled(false);
        }

        String priority = card.getPriority();
        setSpinnerSelection(spinnerPriority, priority);

        for (TempUser u : card.getAssigned_users()) {
            selectedUsers.add(u.getUser_id());
        }
    }

    private void completeTask() {
        int taskId = getIntent().getIntExtra("taskId", -1);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FastApiService fastApiService = retrofit.create(FastApiService.class);
        Log.d("", "completeTask: "+owner_id);
        Call<Response<ResponseBody>> call = fastApiService.completeTask(taskId,owner_id);
        call.enqueue(new Callback<Response<ResponseBody>>() {
            @Override
            public void onResponse(Call<Response<ResponseBody>> call, Response<Response<ResponseBody>> response) {
                if (response.isSuccessful()) {
                    showToast("Task completed successfully");
                } else {
                    showToast("Failed to complete task. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<Response<ResponseBody>> call, Throwable t) {
                showToast("Network error. Please try again.");
            }

            private void showToast(String message) {
                Toast.makeText(UpdateTaskActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
