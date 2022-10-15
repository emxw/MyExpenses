package com.example.myexpenses;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.HolderCategory> {

    private Context context;
    ArrayList<ModelCategory> categoryArrayList;
    private FirebaseFirestore fStore;


    public AdapterCategory( ArrayList<ModelCategory> categoryArrayList, Context context) {
        this.categoryArrayList = categoryArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category, parent, false);
        return new HolderCategory(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {
        //get data
        ModelCategory model = categoryArrayList.get(position);
//        String id = model.getId();
        String category = model.getCategory();
        String uid = model.getUid();
        //long timestamp = model.getTimestamp();

        //set data
        holder.categoryTv.setText(category);

        fStore = FirebaseFirestore.getInstance();

        //handle delete button click
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore.collection("Categories").document(model.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context,"Deleted successfully!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context,AddCategory.class);
                            context.startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    class HolderCategory extends RecyclerView.ViewHolder {

        TextView categoryTv;
        ImageButton deleteBtn;

        public HolderCategory(@NonNull View itemView) {
            super(itemView);

            //init ui views
            categoryTv = itemView.findViewById(R.id.categoryTv);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);

        }
    }
}
