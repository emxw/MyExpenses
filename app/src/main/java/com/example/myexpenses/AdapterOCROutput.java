package com.example.myexpenses;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdapterOCROutput extends RecyclerView.Adapter<AdapterOCROutput.HolderOCR> {

    private Context context;
    ArrayList<ModelOCROutput> ocrArrayList;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String userID;

    public AdapterOCROutput(ArrayList<ModelOCROutput> ocrArrayList, Context context){
        this.ocrArrayList = ocrArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public HolderOCR onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ocr, parent, false);
        return new HolderOCR(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOCROutput.HolderOCR holder, int position) {
        ModelOCROutput model = ocrArrayList.get(position);
        String ocrTitle = model.getOcrTitle();
        String ocrOutput = model.getOcrOutput();

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        holder.ocrTitle.setText(ocrTitle);
        holder.ocrOutput.setText(ocrOutput);

        holder.deleteOCRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore.collection("User Record Information").document(userID)
                        .collection("OCR").document(model.getId())
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context,"Deleted successfully!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context, OCRFragment.class);
                            context.startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return ocrArrayList.size();
    }

    public class HolderOCR extends RecyclerView.ViewHolder {

        TextView ocrOutput, ocrTitle;
        ImageButton deleteOCRBtn;

        public HolderOCR(View itemView) {
            super(itemView);

            ocrOutput = itemView.findViewById(R.id.ocrOutputTv);
            ocrTitle = itemView.findViewById(R.id.ocrTitleTv);
            deleteOCRBtn = itemView.findViewById(R.id.deleteOCRBtn);
        }
    }

}
