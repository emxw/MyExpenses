package com.example.myexpenses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdapterAnalytics extends RecyclerView.Adapter<AdapterAnalytics.HolderAnalytics> {

    private Context context;
    ArrayList<ModelAnalytics> analyticsArrayList;

    public AdapterAnalytics( ArrayList<ModelAnalytics> analyticsArrayList, Context context) {
        this.analyticsArrayList = analyticsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public HolderAnalytics onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_history, parent, false);
        return new HolderAnalytics(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAnalytics holder, int position) {
        ModelAnalytics model = analyticsArrayList.get(position);
        String id = model.getId();
        String category = model.getCategory();
        String totalAmount = model.getTotalAmount();

        holder.categoryTv.setText(category);
        holder.totalAmountTv.setText(totalAmount);

    }

    @Override
    public int getItemCount() {
        return analyticsArrayList.size();
    }

    class HolderAnalytics extends RecyclerView.ViewHolder {

        TextView categoryTv, totalAmountTv;

        public HolderAnalytics(@NonNull View itemView) {
            super(itemView);

            //init ui views
            categoryTv = itemView.findViewById(R.id.categoryTv);
            totalAmountTv = itemView.findViewById(R.id.totalAmountTv);

        }
    }
}
