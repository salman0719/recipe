package com.example.recipeapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Recipe_Item#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Recipe_Item extends Fragment {

    TextView mNameTextView;
    TextView mInstructionTextView;
    ImageView img;

    private Button CommentButton;

    public static final String NAME_KEY = "Name";
    public static final String INSTRUCTION_KEY = "Instruction";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference recipeCollectionRef = db.collection("recipes");

    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("recipes/Greek lemon roast potatoes");

    private View root = null;

    public Recipe_Item() {
        // Required empty public constructor
    }

    public static Recipe_Item newInstance() {
        return new Recipe_Item();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        root = inflater.inflate(R.layout.fragment_recipe_item, container,false);

        mInstructionTextView = root.findViewById(R.id.Instructions);
        mNameTextView = root.findViewById(R.id.Recipe_Name);
        img = root.findViewById(R.id.Image);

        CommentButton = root.findViewById(R.id.CommentsButton);

        CommentButton.setOnClickListener(this::Go_Comments);

        loadItem();

        return root;

    }

    private void Go_Comments(View view){
        Bundle bundle = new Bundle();

        Navigation.findNavController(view).navigate(R.id.comments, bundle);
    }

    private void loadItem() {
        Bundle bundle = this.getArguments();

        // We will not load anything if no `id` has been passed
        if (!bundle.containsKey("id")) return;

        String id = bundle.getString("id");
        recipeCollectionRef.document(id)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();

                        mNameTextView.setText(String.valueOf(data.get("Name")));                        mNameTextView.setText(String.valueOf(data.get("Name")));

                        Glide.with(img.getContext())
                                .load(String.valueOf(data.get("Image")))
                                .placeholder(R.drawable.recipe_placeholder)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(img);

                        mInstructionTextView.setText(String.valueOf(data.get("Instruction")));

                    } else {
                        Toast.makeText(getActivity(), "No item found!", Toast.LENGTH_LONG).show();
                    }

                    Log.d("Firestore response", String.valueOf(task.getResult()));
                } else {
                    Log.d("Firestore failure", "Error getting documents: ", task.getException());
                }
            });
    }


}