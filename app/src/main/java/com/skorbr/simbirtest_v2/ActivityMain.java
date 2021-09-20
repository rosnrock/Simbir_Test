package com.skorbr.simbirtest_v2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;

public class ActivityMain extends Activity {

    private static List<Task> taskList = new ArrayList<>();
    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private ImageView noneView;
    private Button addTask;
    private String currentDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);
        initialize();
    }

    private void initialize() {
        // текущая дата
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        currentDate = sdf.format(calendar.getTime());
        taskList.addAll(ServiceUtils.startingList(ActivityMain.this, currentDate));
        // инициализация view
        recyclerView = findViewById(R.id.task_list);
        TaskAdapter taskAdapter = new TaskAdapter(taskList, task ->
                task.getDescription(ActivityMain.this));
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityMain.this));
        //
        noneView = findViewById(R.id.none_image);
        //
        calendarView = findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            currentDate = (dayOfMonth > 9 ? dayOfMonth : "0" + dayOfMonth) + "/" + (month > 9 ? (month + 1) : "0" + (month + 1)) + "/" + year;
            updateList(currentDate);
            taskAdapter.notifyDataSetChanged();
        });
        //
        addTask = findViewById(R.id.add_task);
        addTask.setOnClickListener(v -> {
            ServiceUtils.addTaskToDB(ActivityMain.this);
            updateList(currentDate);
            taskAdapter.notifyDataSetChanged();
        });
    }

    private void updateList(String d) {
        taskList.clear();
        taskList.addAll(ServiceUtils.getListByDate(d));
        noneView.setVisibility(taskList.isEmpty() ? View.VISIBLE : View.INVISIBLE);
    }
}
