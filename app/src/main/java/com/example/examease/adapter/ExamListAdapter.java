package com.example.examease.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.examease.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Map;

public class ExamListAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String, String>> examList;

    public ExamListAdapter(Context context, List<Map<String, String>> examList) {
        this.context = context;
        this.examList = examList;
    }

    @Override
    public int getCount() {
        return examList.size(); // Return the size of the exam list
    }

    @Override
    public Object getItem(int position) {
        return examList.get(position); // Return the exam item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position; // Return the position as the item ID
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the layout for each exam item if convertView is null
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.exam_list_item, parent, false);
        }

        // Get references to the UI elements
        ImageView examImage = convertView.findViewById(R.id.exam_image);
        TextView examTitle = convertView.findViewById(R.id.exam_title);
        TextView examDescription = convertView.findViewById(R.id.exam_description);

        // Get the current exam data
        Map<String, String> exam = examList.get(position);

        // Set the title and description
        examTitle.setText(exam.get("title"));
        examDescription.setText(exam.get("description"));

        // Get the image URL and load the image using Glide
        String imageUrl = exam.get("imageUrl");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(imageUrl);

            Glide.with(context)
                    .load(storageReference)
                    .placeholder(R.drawable.exam_icon)  // Placeholder while loading
                    .error(R.drawable.exam_icon)       // Error image if something goes wrong
                    .into(examImage);
        } else {
            // If no image is available, load a default placeholder
            examImage.setImageResource(R.drawable.exam_icon);
        }

        return convertView;
    }
}
