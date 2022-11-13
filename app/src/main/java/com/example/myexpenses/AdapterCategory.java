package com.example.myexpenses;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.HolderCategory> {

    private Context context;
    ArrayList<ModelCategory> categoryArrayList;
    private FirebaseFirestore fStore;
    private String post_key, userID, categoryItem;
    private FirebaseAuth mAuth;


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
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        String category = model.getCategory();
        String uid = model.getUid();

        //set data
        holder.categoryTv.setText(category);

        fStore = FirebaseFirestore.getInstance();

//        holder.editBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                post_key = model.getId();
//                categoryItem = model.getCategory();
//                updateData();
//            }
//        });

        //handle delete button click
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore.collection("Categories").document(userID).collection("Category").document(model.getCategory())
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context,"Deleted successfully!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context, AddCategory.class);
                            context.startActivity(intent);
                        }
                    }
                });
            }
        });
    }

//    private void updateData() {
//        AlertDialog.Builder myDialog= new AlertDialog.Builder(context);
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View mView = inflater.inflate(R.layout.update_category, null);
//
//        myDialog.setView(mView);
//        final  AlertDialog dialog = myDialog.create();
//        mAuth = FirebaseAuth.getInstance();
//        fStore = FirebaseFirestore.getInstance();
//        userID = mAuth.getCurrentUser().getUid();
//        final EditText updateCategory = mView.findViewById(R.id.updateCategory);
//
//        updateCategory.setText(String.valueOf(categoryItem));
//
//        Button btnUpdate = mView.findViewById(R.id.btnUpdate);
//
//
//
//        btnUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                categoryItem = updateCategory.getText().toString();
//
//                if (categoryItem.isEmpty()) {
//                    Toast.makeText(view.getContext(), "Category is required!", Toast.LENGTH_LONG).show();
//                } else {
//                    ModelCategory modelCategory = new ModelCategory(post_key, categoryItem, userID);
//                    fStore.collection("Categories").document(userID).collection("Category").document(post_key)
//                            .update(
//                                    "category", modelCategory.getCategory()
//                            ).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(view.getContext(), "Updated successfully!", Toast.LENGTH_LONG).show();
//                            Intent intent = new Intent(context, AddCategory.class);
//                            context.startActivity(intent);
//                        }
//                    });
//                    dialog.dismiss();
//                }
//            }
//        });
//        dialog.show();
//    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    class HolderCategory extends RecyclerView.ViewHolder {

        TextView categoryTv;
        ImageButton editBtn, deleteBtn;

        public HolderCategory(@NonNull View itemView) {
            super(itemView);

            //init ui views
            categoryTv = itemView.findViewById(R.id.categoryTv);
            //editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);

        }
    }
}
