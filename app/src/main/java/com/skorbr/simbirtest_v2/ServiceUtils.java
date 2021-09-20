package com.skorbr.simbirtest_v2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static android.content.Context.MODE_PRIVATE;

class ServiceUtils {

    private static Realm realm;
    private static TaskRealmStructure taskRealmStructure = new TaskRealmStructure();

    // запуск приложения
    static List<Task> startingList(Context context, String currentDate) {
        // наполнение базы данных при первом запуске приложения
        SharedPreferences preferences = context.getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = preferences.getBoolean("firstStart", true);
        if (firstStart) {
            firstStart();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firstStart", false);
            editor.apply();
        }
        return getListByDate(currentDate);
    }

    // первичное заполнение списка заданий
    private static void firstStart() {
        String[] name = {"To pack", "Cat", "Dog", "To call Mom", "Doctor visit", "Store", "Investments", "Stuff"};
        String[] description = {"Pack my things for a trip to the sea", "Feed the cat", "Feed the dog"
                , "Call mom and ask how she's doing", "Make an appointment with a therapist"
                , "Go to the grocery store", "Open a brokerage account", "Sell unnecessary things"};
        long[] time = {new Date().getTime() + 3600 * 1000 * 2, new Date().getTime() - 3600 * 1000
                , new Date().getTime() - 3600 * 1000, new Date().getTime() - 3600 * 1000
                , new Date().getTime() + 3600 * 1000 * 2, new Date().getTime(), new Date().getTime(), new Date().getTime()};

        for (int i = 0; i < name.length; i++) {
            addTaskToDataBase(name[i], description[i], time[i]);
        }
    }

    // добавление задания в базу
    private static void addTaskToDataBase(String name, String description, long dateStart) {
        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);
        }
        realm.beginTransaction();
        taskRealmStructure = realm.createObject(TaskRealmStructure.class);
        taskRealmStructure.id = getMaxIdRealm();
        taskRealmStructure.name = name;
        taskRealmStructure.description = description;
        taskRealmStructure.dateStart = dateStart;
        taskRealmStructure.dateFinish = dateStart + 3600 * 1000;
        taskRealmStructure.flagOfFirst = false;
        realm.commitTransaction();
    }

    // выгрузка всего списка заданий из БД
    private static List<Task> loadList() {
        List<Task> uploadedList = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        List<TaskRealmStructure> dataModels = realm.where(TaskRealmStructure.class).findAll();
        for (TaskRealmStructure d : dataModels) {
            uploadedList.add(new Task(d.id, d.name, d.description, d.dateStart));
        }
        realm.commitTransaction();
        return uploadedList;
    }

    // выборка максимального id из БД
    private static int getMaxIdRealm() {
        Number id = realm.where(TaskRealmStructure.class).max("id");
        return (id == null) ? 1 : id.intValue() + 1;
    }

    // выгрузка списка дел на конкретную дату
    static List<Task> getListByDate(String date) {
        return Task.getListByDate(loadList(), date);
    }

    // добавление задания в базу
    static void addTaskToDB(Context context) {
        openDateDialog(context);
    }

    // диалог выбора даты
    private static void openDateDialog(Context context) {
        int y, m, d;
        Calendar calendar = Calendar.getInstance();
        y = calendar.get(Calendar.YEAR);
        m = calendar.get(Calendar.MONTH);
        d = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
            String date = year + "-" + (month > 9 ? (month + 1) : "0" + (month + 1)) + "-" + (dayOfMonth > 9 ? dayOfMonth : "0" + dayOfMonth);
            openTimeDialog(context, date);
        }, y, m, d);
        datePickerDialog.show();
    }

    // диалог выбора времени
    private static void openTimeDialog(Context context, String date) {
        int hour, min;
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR);
        min = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view, hourOfDay, minute) -> {
            String dateHour = date + " " + (hourOfDay < 10 ? "0" + hourOfDay : hourOfDay) + ":" + (minute < 10 ? "0" + minute : minute) + ":" + "00";
            openAddDialog(context, dateHour);
        }, hour, min, true);
        timePickerDialog.show();
    }

    // диалог выбора названия и описания
    private static void openAddDialog(Context context, String dh) {
        Timestamp timestamp = Timestamp.valueOf(dh);
        long time = timestamp.getTime();
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add);
        TextView date = dialog.findViewById(R.id.dialog_date_choose);
        EditText name = dialog.findViewById(R.id.edit_name);
        EditText desc = dialog.findViewById(R.id.edit_text);
        date.setText(dh);
        dialog.show();
        Button buttonConfirm = dialog.findViewById(R.id.confirm_task);
        buttonConfirm.setOnClickListener(v -> {
            if (name.getText().toString().replaceAll(" ", "").isEmpty()
                    || desc.getText().toString().replaceAll(" ", "").isEmpty()) {
                Toast.makeText(context, context.getText(R.string.toast), Toast.LENGTH_SHORT).show();
            } else {
                addTaskToDataBase(name.getText().toString(), desc.getText().toString(), time);
                dialog.hide();
            }
        });
    }
}
