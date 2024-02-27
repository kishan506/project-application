package com.example.project1.activites;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project1.R;
import com.example.project1.api.FastApiService;
import com.example.project1.model.Task;
import com.example.project1.sessionmanagement.UserSharedPreference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class NewTaskActivity extends AppCompatActivity {

    private int REQUEST_CODE = 1;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextStartDate;
    private EditText editTextEndDate;
    private Spinner spinnerPriority;

    private Calendar calendar;
    private int currentYear, currentMonth, currentDay;
    private static String BASE_URL = "http://";
    private ArrayList<Integer> selectedUsers;
    private Button buttonSave,buttonReset,buttonAddUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtask);

        // list of selected user: by default blank
        selectedUsers = new ArrayList<>();

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextEndDate = findViewById(R.id.editTextEndDate);
        spinnerPriority = findViewById(R.id.spinnerPriority);


        buttonSave = findViewById(R.id.buttonSave);
        buttonReset = findViewById(R.id.buttonReset);
        buttonAddUser = findViewById(R.id.addUsers); // assuming you have this button

        calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);

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

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
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
    }

    private void resetFields() {
        editTextTitle.setText("");
        editTextDescription.setText("");
        editTextStartDate.setText("");
        editTextEndDate.setText("");
        spinnerPriority.setSelection(0); // Set the spinner to the default position

        // Reset the background color when resetting fields
        resetFieldBackground();
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
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);

        try {
            Date parsedDate = inputDateFormat.parse(dateString);
            if (parsedDate != null) {
                // Use a formatter to parse the parsed date into a string and then parse it back to a Date
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String formattedDateString = outputDateFormat.format(parsedDate);
                return outputDateFormat.parse(formattedDateString);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void saveData() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String startDateString = editTextStartDate.getText().toString().trim();
        String endDateString = editTextEndDate.getText().toString().trim();
        String priority = spinnerPriority.getSelectedItem().toString().trim();

        if (title.isEmpty() || description.isEmpty() || startDateString.isEmpty() || endDateString.isEmpty()) {
            showToast("Please fill in all the fields");
            Log.d("", "saveData: "+editTextStartDate.getText());


            highlightEmptyFields();
            return;
        }
        try{

            String startDateString1 = editTextStartDate.getText().toString().trim();
            String endDateString1 = editTextEndDate.getText().toString().trim();
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


        if(selectedUsers.isEmpty()==true)
        {
            showToast("At least one user need to add in the task");
            return;
        }

        // Reset the background color when all fields are filled
        resetFieldBackground();

        UserSharedPreference sh = new UserSharedPreference(this);
        int owner_id = sh.getUserDetails();
//        Date startDate = convertStringToDate(startDateString);
//        Date endDate = convertStringToDate(endDateString);
        String startDate = startDateString;
        String endDate = endDateString;

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Log.d("OkHttp", message));
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor);
        BASE_URL=BASE_URL+sh.getIP()+"/";
        Log.d("addNewTask", "saveData: "+BASE_URL);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FastApiService fastApiService = retrofit.create(FastApiService.class);

        Task task = new Task(title, description, startDate, endDate, priority, selectedUsers, owner_id, 0);
        Log.d("TAG", "saveData: " + task);

        Call<Response<ResponseBody>> call = fastApiService.addtask(task);
        call.enqueue(new Callback<Response<ResponseBody>>() {
            @Override
            public void onResponse(Call<Response<ResponseBody>> call, Response<Response<ResponseBody>> response) {
                if (response.isSuccessful()) {
                    finish();
                    showToast("Data Add successfully");
                    Log.d("from if", "onResponse: ");
                } else {
                    showToast(" failed. Please try again.");
                }
            }

            public void onFailure(Call<Response<ResponseBody>> call, Throwable t) {
                showToast("Network error. Please try again.");
                Log.e("NetworkError", "Failed to make the network request", t);
            }

            private void showToast(String message) {
                Toast.makeText(NewTaskActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(NewTaskActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void highlightEmptyFields() {
        if (editTextTitle.getText().toString().trim().isEmpty()) {
            editTextTitle.setError("Title is required");
        }

        if (editTextDescription.getText().toString().trim().isEmpty()) {
            editTextDescription.setError("Description is required");
        }

        if (editTextStartDate.getText().toString().trim().isEmpty()) {
            editTextStartDate.setError("StartDate is required");
        }

        if (editTextEndDate.getText().toString().trim().isEmpty()) {
            editTextEndDate.setError("EndDate is required");
        }
    }

    private void resetFieldBackground() {
        editTextTitle.setBackground(null);
        editTextDescription.setBackground(null);
        editTextStartDate.setBackground(null);
        editTextEndDate.setBackground(null);
    }

    public void onAddUserClick(View view) {
//        Log.d("onclick", "onAddUserClick: button click");
        Intent intent = new Intent(this, UserListActivity.class);

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
}
