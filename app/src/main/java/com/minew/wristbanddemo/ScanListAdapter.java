package com.minew.wristbanddemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minew.wristband.ble.bean.WristbandModule;
import com.minew.wristbanddemo.databinding.ItemScanDeviceBinding;

import java.util.ArrayList;

public class ScanListAdapter extends RecyclerView.Adapter<ScanListAdapter.ScanHolder> {
    private ArrayList<WristbandModule> dataList = new ArrayList<>();

    /*private static DiffUtil.ItemCallback<WristbandModule> diffCallback = new DiffUtil.ItemCallback<WristbandModule>() {
        @Override
        public boolean areItemsTheSame(@NonNull WristbandModule oldItem, @NonNull WristbandModule newItem) {
            return oldItem.getMacAddress().equals(newItem.getMacAddress());
        }

        @Override
        public boolean areContentsTheSame(@NonNull WristbandModule oldItem, @NonNull WristbandModule newItem) {
            return oldItem.equals(newItem);
        }
    };*/

    public void addNewData(@NonNull ArrayList<WristbandModule> list) {
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearData() {
        dataList.clear();
        notifyDataSetChanged();
    }

    public WristbandModule getItem(int position) {
        return dataList.get(position);
    }

    @NonNull
    @Override
    public ScanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ScanHolder(ItemScanDeviceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ScanHolder holder, int position) {
        holder.bindData(dataList.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ScanHolder extends RecyclerView.ViewHolder {

        private ItemScanDeviceBinding binding;
        public ScanHolder(@NonNull final ItemScanDeviceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.setOnActivatedListener(view -> {
                if (onItemChildClickListener != null) {
                    onItemChildClickListener.onItemChildClick(view, getAdapterPosition());
                }
            });
            this.binding.setOnItemClickListener(view -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(view, getAdapterPosition());
                }
            });
        }

        void bindData(WristbandModule module) {
            binding.setWristbandModule(module);
            binding.executePendingBindings();
        }
    }

    public interface OnItemChildClickListener {
        void onItemChildClick(View view, int position);
    }

    private OnItemChildClickListener onItemChildClickListener;

    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        this.onItemChildClickListener = onItemChildClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
