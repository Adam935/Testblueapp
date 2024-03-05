package com.example.testblueapp.ble;/*
 * Copyright 2019 Punch Through Design LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import android.bluetooth.le.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testblueapp.R;

import java.util.List;

public class ScanResultAdapter extends RecyclerView.Adapter<ScanResultAdapter.ViewHolder> {

    private final List<ScanResult> items;
    private final OnItemClickListener onClickListener;

    public ScanResultAdapter(List<ScanResult> items, OnItemClickListener onClickListener) {
        this.items = items;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_scan_result,
                parent,
                false
        );
        return new ViewHolder(view, onClickListener, items);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScanResult item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView deviceNameTextView;
        private final TextView macAddressTextView;
        private final TextView signalStrengthTextView;
        private final OnItemClickListener onClickListener;

        ViewHolder(View view, OnItemClickListener onClickListener, List<ScanResult> items) {
            super(view);
            deviceNameTextView = view.findViewById(R.id.device_name);
            macAddressTextView = view.findViewById(R.id.mac_address);
            signalStrengthTextView = view.findViewById(R.id.signal_strength);
            this.onClickListener = onClickListener;
            view.setOnClickListener(v -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onClickListener.onItemClick(items.get(getAdapterPosition()));
                }
            });
        }


        void bind(ScanResult result) {
            if (result != null && result.getDevice() != null) {
                deviceNameTextView.setText(result.getDevice().getName() != null ? result.getDevice().getName() : "Unnamed");
                macAddressTextView.setText(result.getDevice().getAddress());
                signalStrengthTextView.setText(result.getRssi() + " dBm");
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ScanResult device);
    }
}
