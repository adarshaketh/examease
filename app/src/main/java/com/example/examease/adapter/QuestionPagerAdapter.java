package com.example.examease.adapter;


import static com.example.examease.helpers.Functions.makeBold;

import android.annotation.SuppressLint;
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

import java.util.List;
import java.util.Map;

public class QuestionPagerAdapter extends RecyclerView.Adapter<QuestionPagerAdapter.QuestionViewHolder> {

    private final Context context;
    private final List<Map<String, Object>> questions;
    private final String[] userAnswers; // To store user's answers

    // Constructor to initialize the adapter with context and the list of questions
    public QuestionPagerAdapter(Context context, List<Map<String, Object>> questions) {
        this.context = context;
        this.questions = questions;
        this.userAnswers = new String[questions.size()]; // Array to store selected answers for each question
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the question item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        // Get the current question data from the list
        Map<String, Object> questionData = questions.get(position);

        // Extract question text and options from the question data
        String questionText = (String) questionData.get("questionText");
        List<String> options = (List<String>) questionData.get("options");

        // Set the question text
        holder.tvQuestion.setText(makeBold("Q" + (position + 1) + ": " + questionText));

        // Set options for each radio button
        holder.rbOption1.setText(makeBold(options.get(0)));
        holder.rbOption2.setText(makeBold(options.get(1)));
        holder.rbOption3.setText(makeBold(options.get(2)));
        holder.rbOption4.setText(makeBold(options.get(3)));


        // Restore user's previous selection, if any
        holder.radioGroupOptions.clearCheck(); // Clear any previous selection
        if (userAnswers[position] != null) {
            switch (userAnswers[position]) {
                case "Option1":
                    holder.rbOption1.setChecked(true);
                    break;
                case "Option2":
                    holder.rbOption2.setChecked(true);
                    break;
                case "Option3":
                    holder.rbOption3.setChecked(true);
                    break;
                case "Option4":
                    holder.rbOption4.setChecked(true);
                    break;
            }
        }

        // Handle user's selection and store it in userAnswers array
        holder.radioGroupOptions.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rbOption1:
                    userAnswers[position] = "Option1";
                    break;
                case R.id.rbOption2:
                    userAnswers[position] = "Option2";
                    break;
                case R.id.rbOption3:
                    userAnswers[position] = "Option3";
                    break;
                case R.id.rbOption4:
                    userAnswers[position] = "Option4";
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        // Return the total number of questions
        return questions.size();
    }

    // Method to retrieve all user's answers
    public String[] getUserAnswers() {
        return userAnswers;
    }

    // ViewHolder class for each question item
    static class QuestionViewHolder extends RecyclerView.ViewHolder {

        TextView tvQuestion; // TextView to display the question
        RadioGroup radioGroupOptions; // RadioGroup to hold options
        RadioButton rbOption1, rbOption2, rbOption3, rbOption4; // RadioButtons for the four options

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            radioGroupOptions = itemView.findViewById(R.id.radioGroupOptions);
            rbOption1 = itemView.findViewById(R.id.rbOption1);
            rbOption2 = itemView.findViewById(R.id.rbOption2);
            rbOption3 = itemView.findViewById(R.id.rbOption3);
            rbOption4 = itemView.findViewById(R.id.rbOption4);
        }
    }
}
