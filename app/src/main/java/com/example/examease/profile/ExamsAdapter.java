package com.example.examease.profile;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.examease.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExamsAdapter extends RecyclerView.Adapter<ExamsAdapter.ExamViewHolder> {

    private Context context;
    private List<Exam> examList;
    private String userEmail;  // User email to use when fetching the score from the history collection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ExamsAdapter(Context context, List<Exam> examList, String userEmail) {
        this.context = context;
        this.examList = examList;
        this.userEmail = userEmail;
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exam_card, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        Exam exam = examList.get(position);

        holder.examTitle.setText(exam.getTitle());
        holder.examDetails.setText(exam.getTotalMarks() + " Questions\nDifficulty: " + getDifficultyLevel(exam.getDifficulty())); // Map difficulty based on value

        holder.itemView.setOnClickListener(v -> {
            // Show dialog with detailed information
            fetchAndShowExamDetailsDialog(exam);
        });

        holder.arrowIcon.setImageResource(R.drawable.ic_right_arrow);
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    private void fetchAndShowExamDetailsDialog(Exam exam) {
        // Fetch the correct score for the clicked exam from the history collection
        db.collection("history").document(userEmail)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> examHistoryList = (List<Map<String, Object>>) documentSnapshot.get("examIds");
                        if (examHistoryList != null) {
                            for (Map<String, Object> entry : examHistoryList) {
                                String examId = (String) entry.get("examId");
                                long start = (Long) entry.get("start");  // The start time for the attempt
                                Long scoreObj = (Long) entry.get("score");  // Check if the score exists
                                Long noQnsAttemptedObj = (Long) entry.get("noQnsAttempted");  // Check if the number of attempted questions exists

                                int score = scoreObj != null ? scoreObj.intValue() : 0;  // Default to 0 if null
                                int noQnsAttempted = noQnsAttemptedObj != null ? noQnsAttemptedObj.intValue() : 0;  // Default to 0 if null

                                // Check if this history entry matches the clicked examId and start time
                                if (examId.equals(exam.getExamId()) && start == exam.getStartTime()) {
                                    // Fetch the duration and total number of questions from the exams collection
                                    fetchDurationAndQuestionsAndShowDialog(exam, score, noQnsAttempted);
                                    break;
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to load score details.", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchDurationAndQuestionsAndShowDialog(Exam exam, int userScore, int noQnsAttempted) {
        db.collection("exams").document(exam.getExamId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long durationObj = documentSnapshot.getLong("duration");
                        int duration = durationObj != null ? durationObj.intValue() : 0;

                        Long totalQuestionsObj = documentSnapshot.getLong("totalMarks");  // Assuming "totalMarks" represents the total number of questions
                        int totalQuestions = totalQuestionsObj != null ? totalQuestionsObj.intValue() : 0;

                        // Calculate the number of questions not attempted
                        int notAttemptedQuestions = totalQuestions - noQnsAttempted;

                        // Show the dialog with the correct score, noQnsAttempted, notAttemptedQuestions, and duration
                        showExamDetailsDialog(exam, userScore, noQnsAttempted, notAttemptedQuestions, duration);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to load exam details.", Toast.LENGTH_SHORT).show();
                });
    }

    private void showExamDetailsDialog(Exam exam, int userScore, int noQnsAttempted, int notAttemptedQuestions, int duration) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_exam_details);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView examTitle = dialog.findViewById(R.id.exam_title_value);
        TextView examDifficulty = dialog.findViewById(R.id.exam_difficulty_value);
        TextView examTotalTime = dialog.findViewById(R.id.exam_total_time_value);
        TextView examScore = dialog.findViewById(R.id.exam_score_value);
        TextView examAttemptedOn = dialog.findViewById(R.id.exam_attempted_on_value);
        TextView examQuestionsAttempted = dialog.findViewById(R.id.exam_questions_attempted_value);
        TextView examQuestionsNotAttempted = dialog.findViewById(R.id.exam_questions_not_attempted_value);
        ImageView closeDialog = dialog.findViewById(R.id.close_dialog);

        // Populate the dialog fields
        examTitle.setText(exam.getTitle());
        examDifficulty.setText(getDifficultyLevel(exam.getDifficulty())); // Map difficulty
        examTotalTime.setText(duration + " minutes");  // Display the fetched total time
        examScore.setText(userScore + "/" + exam.getTotalMarks());  // Display the correct score for this attempt
        examAttemptedOn.setText(getFormattedDate(exam.getStartTime()));
        examQuestionsAttempted.setText(String.valueOf(noQnsAttempted));  // Display the number of questions attempted
        examQuestionsNotAttempted.setText(String.valueOf(notAttemptedQuestions));  // Display the number of questions not attempted

        // Close button action
        closeDialog.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private String getDifficultyLevel(String difficulty) {
        switch (difficulty) {
            case "1":
                return "Easy";
            case "2":
                return "Intermediate";
            case "3":
                return "Hard";
            default:
                return "Unknown";
        }
    }

    // Method to format the timestamp
    private String getFormattedDate(long timestamp) {
        // Convert the timestamp (milliseconds) directly to a Date object
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault());
        Date resultDate = new Date(timestamp);  // No need to multiply by 1000 since it is already in milliseconds
        return sdf.format(resultDate);
    }

    public static class ExamViewHolder extends RecyclerView.ViewHolder {
        TextView examTitle, examDetails;
        ImageView examImage, arrowIcon;

        public ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            examTitle = itemView.findViewById(R.id.exam_title);
            examDetails = itemView.findViewById(R.id.exam_difficulty);
            examImage = itemView.findViewById(R.id.exam_image);
            arrowIcon = itemView.findViewById(R.id.arrow_icon);
        }
    }
}
