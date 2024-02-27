package com.example.project1.activites;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.R;
import com.example.project1.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> userList;
    private List<Integer> selectedUsers;

    public void setData(List<User> userList,List<Integer> selectedUsers) {
        this.userList = userList;
        this.selectedUsers = selectedUsers;
        notifyDataSetChanged();
        Log.d("from cons",selectedUsers.toString());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userNameCheckBoox.setText(user.getFirstname() + " " + user.getLastName());

        Log.d("select user",user.getUserId()+"");
        if(selectedUsers.contains(user.getUserId()))
        {
            Log.d("from if select user",user.getUserId()+"");
            holder.userNameCheckBoox.setChecked(true);
        }
        holder.userNameCheckBoox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    selectedUsers.add(user.getUserId());
                }
                else
                {
                    selectedUsers.remove(Integer.valueOf(user.getUserId()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox userNameCheckBoox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameCheckBoox = itemView.findViewById(R.id.userNameTextView1);
        }
    }
}

