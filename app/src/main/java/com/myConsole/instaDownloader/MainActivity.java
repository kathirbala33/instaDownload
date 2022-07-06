package com.myConsole.instaDownloader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.myConsole.instaDownloader.databinding.ActivityMainBinding;
import com.myConsole.instaDownloader.services.ActiveService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        appServiceStart();
        binding.instagramImageView.setOnClickListener(view -> {
            Intent likeIng = new Intent();
            likeIng.setPackage("com.instagram.android");
            startActivity(likeIng);
        });
    }

    private void appServiceStart() {
        Intent intent = new Intent(getApplicationContext(), ActiveService.class);
        intent.setAction("com.action.serviceStart");
        startService(intent);
    }

}