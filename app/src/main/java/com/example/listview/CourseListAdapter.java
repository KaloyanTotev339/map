package com.example.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CourseListAdapter extends ArrayAdapter<Course> {
    private Context mContext;
    int mResource;
    public CourseListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Course> courses) {
        super(context, resource, courses);
        mContext=context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String teacher = getItem(position).getTeacher();
        String startTime = getItem(position).getStartTime();
        String courseName = getItem(position).getName();
        String roomNumber = getItem(position).getRoomNumber();

        Course cr = new Course(courseName,startTime,teacher,roomNumber);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView tvCourseName = (TextView) convertView.findViewById(R.id.courseNameTV_id);
        TextView tvTeacher = (TextView) convertView.findViewById(R.id.teacherNameTV_id);
        TextView tvStartTime = (TextView) convertView.findViewById(R.id.startTimeTV_id);
        TextView tvRoomNumber = (TextView) convertView.findViewById(R.id.roomTV_id);

        tvTeacher.setText(teacher);
        tvStartTime.setText(startTime);
        tvCourseName.setText(courseName);
        tvRoomNumber.setText(roomNumber);



        return convertView;
    }
}
