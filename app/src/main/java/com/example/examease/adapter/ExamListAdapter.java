package com.example.examease.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.examease.R;

import java.util.ArrayList;
import java.util.Map;

public class ExamListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Map<String, String>> examList;

    public ExamListAdapter(Context context, ArrayList<Map<String, String>> examList) {
        this.context = context;
        this.examList = examList;
    }

    @Override
    public int getCount() {
        return examList.size();
    }

    @Override
    public Object getItem(int position) {
        return examList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.exam_list_item, parent, false);
        }

        TextView title = convertView.findViewById(R.id.exam_title);
        TextView description = convertView.findViewById(R.id.exam_description);

        Map<String, String> exam = examList.get(position);
        title.setText(exam.get("title"));
        description.setText(exam.get("description"));

        return convertView;
    }
}