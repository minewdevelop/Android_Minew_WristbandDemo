package com.minew.wristbanddemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minew.wristband.ble.bean.WristbandHistory;
import com.minew.wristbanddemo.databinding.ItemHistoryBinding;

import java.util.ArrayList;

public class HistoryDataAdapter extends RecyclerView.Adapter<HistoryDataAdapter.HistoryHolder> {

    private ArrayList<WristbandHistory> dataList = new ArrayList<>();

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryHolder(ItemHistoryBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {
        holder.bindData(dataList.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void addData(ArrayList<WristbandHistory> list) {
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearData() {
        dataList.clear();
        notifyDataSetChanged();
    }

    public WristbandHistory getItem(int position) {
        return dataList.get(position);
    }

    class HistoryHolder extends RecyclerView.ViewHolder {

        private ItemHistoryBinding itemHistoryBinding;
        public HistoryHolder(ItemHistoryBinding binding) {
            super(binding.getRoot());
            itemHistoryBinding = binding;
        }

        public void bindData(WristbandHistory history) {
            itemHistoryBinding.setHistoryData(history);
            itemHistoryBinding.executePendingBindings();
        }
    }
}
