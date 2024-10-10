package com.example.examease.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.examease.R;

public class QuestionPagerAdapter extends RecyclerView.Adapter<QuestionPagerAdapter.QuestionViewHolder> {

    private final Context context;
    private final int totalQuestions;

    public QuestionPagerAdapter(Context context, int totalQuestions) {
        this.context = context;
        this.totalQuestions = totalQuestions;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        // Set the question text (for demonstration purposes, using static content)
        holder.tvQuestion.setText("Question " + (position + 1) + ": Which of the following statements is true about the final keyword in Java?");

        // Set up answer options (static for demonstration, should be dynamic in a real scenario)
        holder.radioButton1.setText("a) A final class can be extended.");
        holder.radioButton2.setText("b) A final method can be overridden.");
        holder.radioButton3.setText("c) A final variable can be modified.");
        holder.radioButton4.setText("d) A final class cannot be extended.");

        // Handle answer selection (you can add logic to save the selected answer)
        holder.radioGroupOptions.setOnCheckedChangeListener((group, checkedId) -> {
            // Logic to handle answer selection
        });
    }

    @Override
    public int getItemCount() {
        return totalQuestions;
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion;
        RadioGroup radioGroupOptions;
        RadioButton radioButton1, radioButton2, radioButton3, radioButton4;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            radioGroupOptions = itemView.findViewById(R.id.radioGroupOptions);
            radioButton1 = itemView.findViewById(R.id.rbOption1);
            radioButton2 = itemView.findViewById(R.id.rbOption2);
            radioButton3 = itemView.findViewById(R.id.rbOption3);
            radioButton4 = itemView.findViewById(R.id.rbOption4);
        }
    }
}
