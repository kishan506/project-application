// Import statements
package com.example.project1.activites;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.R;
import com.example.project1.model.Card;

import java.util.Arrays;
import java.util.List;

public class CompalateTaskUser extends RecyclerView.Adapter<CompalateTaskUser.CardViewHolder> {

    private List<Card> dataList; // Use your Card model here
    private Context context;
    // Constructor
    public CompalateTaskUser(Context context, List<Card> dataList) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        // Bind data to the views in the CardView
        String[] priorities = {"High", "Medium", "Low"};
        Card card = dataList.get(position);

        int indexofpriority = Arrays.asList(priorities).indexOf(card.getPriority());
        Log.d("", "onBindViewHolder: "+card);
        // Assuming your Card model has a getTitle() method
        holder.textTitle.setText(card.getTitle());
        holder.textViewdescription.setText(card.getDescription());
        holder.textViewdstartdate.setText(card.getStart_date());
        holder.textViewdenddate.setText(card.getEnd_date());
        if(indexofpriority>3)
        {
            holder.textViewdenddate.setText(card.getEnd_date());
        }
//        holder.spinnerPriority.setSelection(indexofpriority);
//        holder.spinnerPriority.setAdapter();
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateTaskActivity.class);
                intent.putExtra("taskId",card.getTaskId());
                intent.putExtra("getOwner_id",card.getOwner_id());

                Log.d("card_id",card.getTaskId()+"");
                context.startActivity(intent);
            }
        });
        // Add binding for other views as needed
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // ViewHolder class
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle; // Add other views as needed
        TextView textViewdescription; // Add other views as needed
        TextView textViewdstartdate; // Add other views as needed
        TextView textViewdenddate; // Add other views as needed
        Spinner spinnerPriority;


        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textViewTitle);
            textViewdescription = itemView.findViewById(R.id.textViewDescription);
            textViewdstartdate = itemView.findViewById(R.id.textViewStartDate);
            textViewdenddate = itemView.findViewById(R.id.textViewEndDate);
            spinnerPriority = itemView.findViewById(R.id.spinnerPriority);

            // Initialize other views
        }
        public void setOnClickListener(View.OnClickListener onClickListener) {
            itemView.setOnClickListener(onClickListener);
        }
    }
}
