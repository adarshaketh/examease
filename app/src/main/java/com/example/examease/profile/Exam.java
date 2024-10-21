package com.example.examease.profile;

public class Exam {
    private String examId;
    private String title;
    private int totalMarks;
    private String difficulty;
    private long startTime;
    private int userScore; // Add user score

    // Constructor
    public Exam(String examId, String title, int totalMarks, String difficulty, long startTime) {
        this.examId = examId;
        this.title = title;
        this.totalMarks = totalMarks;
        this.difficulty = difficulty;
        this.startTime = startTime;
        this.userScore = userScore; // Initialize user score
    }

    // Getters
    public String getExamId() {
        return examId;
    }

    public String getTitle() {
        return title;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getUserScore() {
        return userScore;
    }
}
