package com.example.a1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.a1.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    TextView textView;
    private String messages = "Hello world!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.textView.setText(messages);
        binding.button.setOnClickListener(view -> {
            printText();
        });
    }

    private void printText() {
        messages += "\n" + binding.editTextTextPersonName.getText().toString();
        binding.textView.setText(messages);
    }
}