package com.example.myexpenses;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterExpenseRecord extends RecyclerView.Adapter<AdapterExpenseRecord.HolderExpenseRecord> {

    private Context context;
    ArrayList<ModelExpenseRecord> expenseRecordArrayList;
    private String post_key, userID, titleItem, dateItem, categoryItem, amountItem;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    ArrayList<String> spinnerList;
    private Spinner categorySpinner;
    ArrayAdapter<String> spinnerAdapter;


    public AdapterExpenseRecord(ArrayList<ModelExpenseRecord> expenseRecordArrayList, Context context){
        this.expenseRecordArrayList = expenseRecordArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public HolderExpenseRecord onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_expense_record, parent, false);
        return new HolderExpenseRecord(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterExpenseRecord.HolderExpenseRecord holder, int position) {
        ModelExpenseRecord model = expenseRecordArrayList.get(position);
        String image = model.getImage();
        String title = model.getTitle();
        String date = model.getDate();
        String category = model.getCategory();
        String amount = model.getAmount();

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        Picasso.get().load(image).into(holder.imageER);
        holder.titleER.setText(title);
        holder.dateER.setText(date);
        holder.categoryER.setText(category);
        holder.amountER.setText(amount);

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post_key = model.getId();
                titleItem = model.getTitle();
                dateItem = model.getDate();
                categoryItem = model.getCategory();
                amountItem = model.getAmount();
                updateData();
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference category = fStore.collection("Categories").document(userID).collection("Category").document(model.getCategory());
                category.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String totalAmountString = documentSnapshot.getString("total amount");
                        Integer totalAmountInt = Integer.parseInt(totalAmountString);
                        Integer amountStringInt = Integer.parseInt(model.getAmount());
                        Integer totalAmountAdded = totalAmountInt - amountStringInt;
                        category.update("total amount", totalAmountAdded + "");
                    }
                });

                fStore.collection("User Record Information").document(userID)
                        .collection("Records").document(model.getId())
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context,"Deleted successfully!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context, MainActivity.class);
                            context.startActivity(intent);
                        }
                    }
                });
            }
        });

    }

    private void updateData() {
        AlertDialog.Builder myDialog= new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View mView = inflater.inflate(R.layout.update_expense_record, null);

        myDialog.setView(mView);
        final  AlertDialog dialog = myDialog.create();
        mAuth = FirebaseAuth.getInstance();

//        spinnerList = new ArrayList<String>();
//        spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, spinnerList);
//        categorySpinner.setAdapter(spinnerAdapter);

        userID = mAuth.getCurrentUser().getUid();

        final EditText updateTitle = mView.findViewById(R.id.updateTitle);
        final EditText updateDate = mView.findViewById(R.id.updateDate);
        //final Spinner updateCategory = mView.findViewById(R.id.updateCategory);
        final EditText updateAmount = mView.findViewById(R.id.updateAmount);

        updateTitle.setText(String.valueOf(titleItem));
        updateDate.setText(String.valueOf(dateItem));
        //updateCategory.setSelection(spinnerList.indexOf(categoryItem));
        updateAmount.setText(String.valueOf(amountItem));

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        updateDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        String dateString = day+"/"+month+"/"+year;
                        updateDate.setText(dateString);

                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        //loadCategorySpinner();

        Button btnUpdate = mView.findViewById(R.id.btnUpdate);

        fStore = FirebaseFirestore.getInstance();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleItem = updateTitle.getText().toString();
                dateItem = updateDate.getText().toString();
                //categoryItem = updateCategory.getSelectedItem().toString();
                amountItem = updateAmount.getText().toString();

                if (titleItem.isEmpty()) {
                    Toast.makeText(view.getContext(), "title is required!", Toast.LENGTH_LONG).show();
                } else {
                    ModelExpenseRecord modelExpenseRecord = new ModelExpenseRecord(post_key, null, titleItem, dateItem, null, amountItem);
                    fStore.collection("User Record Information").document(userID).collection("Records").document(post_key)
                            .update(
                                    "Title", modelExpenseRecord.getTitle(),
                                    "Date", modelExpenseRecord.getDate(),
                                    //"category", modelExpenseRecord.getCategory(),
                                    "Amount", modelExpenseRecord.getAmount()
                            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(view.getContext(), "Updated successfully!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context, MainActivity.class);
                            context.startActivity(intent);

                        }
                    });
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

//    private void loadCategorySpinner() {
//        fStore.collection("Categories").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()){
//                    for (QueryDocumentSnapshot document : task.getResult()){
//                        String category = document.getString("category");
//
//                        spinnerList.add(category);
//                    }
//                    spinnerAdapter.notifyDataSetChanged();
//                }
//            }
//        });
//    }

    @Override
    public int getItemCount() {
        return expenseRecordArrayList.size();
    }

    public class HolderExpenseRecord extends RecyclerView.ViewHolder {

        ImageView imageER;
        TextView dateER, titleER, categoryER, amountER;
        ImageButton editBtn, deleteBtn;

        public HolderExpenseRecord(View itemView) {
            super(itemView);

            imageER = itemView.findViewById(R.id.imageER);
            dateER = itemView.findViewById(R.id.dateER);
            titleER = itemView.findViewById(R.id.titleER);
            categoryER = itemView.findViewById(R.id.categoryER);
            amountER = itemView.findViewById(R.id.amountER);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
