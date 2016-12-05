package com.example.gardo.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gardo.myapplication.Model.FoodModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity {
    private static final int GALLERY_INTENT = 2;
    private StorageReference mStorageReference;
    private ProgressDialog mProgressDialog;
    ImageView imageView;
    String imgPath;
    String catagory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        final String[] array = {"Main", "Dessert", "Drink"};
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        mProgressDialog = new ProgressDialog(this);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item,array);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(array[position].equals("Main")){
                    catagory = "main";
                }
                else if (array[position].equals("Dessert")){
                    catagory = "dessert";
                }
                else if (array[position].equals("Dessert")){
                    catagory = "drink";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("food");
        final EditText price_add = (EditText) findViewById(R.id.price_add);
        final EditText name_add = (EditText) findViewById(R.id.name_add);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        Button img = (Button) findViewById(R.id.set_img);
        imageView = (ImageView) findViewById(R.id.image_view);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });
        Button add = (Button) findViewById(R.id.add_admin);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String name = name_add.getText().toString();
                        Double price = Double.valueOf(price_add.getText().toString());
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("food").child(name);
                        ref.child("name").setValue(name);
                        ref.child("price").setValue(price);
                        ref.child("img").setValue(imgPath);
                        ref.child("like").setValue(0);
                        ref.child("catagory").setValue(catagory);
                    }
                });
                try {
                    thread.start();
                    thread.sleep(1000);
                    Intent i = new Intent(getApplicationContext(), AdminActivity.class);
                    startActivity(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mProgressDialog.setMessage("Upload...");
        mProgressDialog.show();
        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            Uri uri = data.getData();
            StorageReference filePath = mStorageReference.child("Menu").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgressDialog.dismiss();
                    Uri dowloadUri = taskSnapshot.getDownloadUrl();
                    imgPath = dowloadUri.toString();
                    Glide.with(getApplicationContext()).load(dowloadUri).into(imageView);
                }
            });
        }
    }
}
