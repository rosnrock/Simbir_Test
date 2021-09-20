package com.skorbr.simbirtest_v2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter {

    private List<Task> taskList;
    private final Listener onTaskClick;
    private final String[] hourToDisplay = {"00:00 - 01:00", "01:00 - 02:00", "02:00 - 03:00", "03:00 - 04:00",
            "04:00 - 05:00", "05:00 - 06:00", "06:00 - 07:00", "07:00 - 08:00", "08:00 - 09:00",
            "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "12:00 - 13:00", "13:00 - 14:00",
            "14:00 - 15:00", "15:00 - 16:00", "16:00 - 17:00", "17:00 - 18:00", "18:00 - 19:00",
            "19:00 - 20:00", "20:00 - 21:00", "21:00 - 22:00", "22:00 - 23:00", "23:00 - 00:00"};

    public TaskAdapter(List<Task> taskList, Listener onTaskClick) {
        this.taskList = taskList;
        this.onTaskClick = onTaskClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == 0) {
            view = layoutInflater.inflate(R.layout.recycler_item_head, parent, false);
            view.setOnClickListener((v -> onTaskClick.onTaskClick((Task) v.getTag())));
            return new TaskHolderHead(view);
        }

        view = layoutInflater.inflate(R.layout.recycler_item, parent, false);
        view.setOnClickListener((v -> onTaskClick.onTaskClick((Task) v.getTag())));
        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Task task = taskList.get(position);

        if (task.isFlagOfFirst()) {
            TaskHolderHead taskHolderHead = (TaskHolderHead) holder;
            taskHolderHead.bind(task);
        } else {
            TaskHolder taskHolder = (TaskHolder) holder;
            taskHolder.bind(task);
        }

        holder.itemView.setTag(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return taskList.get(position).isFlagOfFirst() ? 0 : 1;
    }

    class TaskHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView time;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.task_name);
            time = itemView.findViewById(R.id.task_time);
        }

        public void bind(Task task) {
            name.setText(task.getName());
            time.setText(task.getTimeTask());
        }
    }

    class TaskHolderHead extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView time;
        private TextView head;

        public TaskHolderHead(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.task_name_head);
            time = itemView.findViewById(R.id.task_time_head);
            head = itemView.findViewById(R.id.head_time);
        }

        public void bind(Task task) {
            name.setText(task.getName());
            time.setText(task.getTimeTask());
            head.setText(hourToDisplay[task.getHourTask()]);
        }
    }

    interface Listener {
        void onTaskClick(Task task);
    }
}
