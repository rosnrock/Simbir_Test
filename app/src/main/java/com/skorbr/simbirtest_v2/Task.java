package com.skorbr.simbirtest_v2;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Task implements Comparable<Task> {

    private int id;
    private String name;
    private String description;
    private long dateStart;
    private long dateFinish;
    private boolean flagOfFirst;

    Task(int id, String name, String description, long dateStart) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dateStart = dateStart;
        this.flagOfFirst = true;
    }

    public String getName() {
        return name;
    }

    private long getDateStart() {
        return dateStart;
    }

    private String getDateTask() {
        Date date = new Date(new Timestamp(dateStart).getTime());
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(date);
    }

    private String getDayTask() {
        Date date = new Date(new Timestamp(dateStart).getTime());
        DateFormat format = new SimpleDateFormat("dd/MM");
        return format.format(date);
    }

    String getTimeTask() {
        Date date = new Date(new Timestamp(dateStart).getTime());
        DateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

    int getHourTask() {
        Date date = new Date(new Timestamp(dateStart).getTime());
        DateFormat format = new SimpleDateFormat("HH");
        return Integer.parseInt(format.format(date));
    }

    boolean isFlagOfFirst() {
        return flagOfFirst;
    }

    private void setFlagOfFirst(boolean flagOfFirst) {
        this.flagOfFirst = flagOfFirst;
    }

    // отображение диалога дополнительной информации
    void getDescription(Context context) {
        Dialog dialog;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog);
        TextView dateView = dialog.findViewById(R.id.dialog_date);
        TextView timeView = dialog.findViewById(R.id.dialog_time);
        TextView textView = dialog.findViewById(R.id.dialog_text);
        TextView decsView = dialog.findViewById(R.id.dialog_desc);
        dateView.setText(getDayTask());
        timeView.setText(getTimeTask());
        textView.setText(name);
        decsView.setText(description);
        dialog.show();
    }

    @Override
    public int compareTo(Task o) {
        return dateStart < o.getDateStart() ? -1 : dateStart > o.getDateStart() ? 1 : 0;
    }

    static List<Task> getListByDate(List<Task> listToDate, String date) {
        List<Task> newList = new ArrayList<>();
        for (Task task : listToDate) {
            if (task.getDateTask().equals(date)) {
                newList.add(task);
            }
        }
        setFlags(newList);
        return newList;
    }

    // функция для сорировки переданного списка и для того, чтобы отметить первое "задание" в диапазоне часов (пр. 20:00 - 20:59)
    private static void setFlags(List<Task> listToSet) {
        Collections.sort(listToSet);
        //массив boolean для хранения данных о том, что в данный промежуток времени уже есть первое "задание"
        boolean[] hoursPlaced = {false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false, false, false, false, false};
        //цикл по списку, отмечаем какой элемент списка первый
        for (Task t : listToSet) {
            int hour = t.getHourTask();
            if (!hoursPlaced[hour]) {
                hoursPlaced[hour] = true;
            } else {
                t.setFlagOfFirst(false);
            }
        }
    }
}
