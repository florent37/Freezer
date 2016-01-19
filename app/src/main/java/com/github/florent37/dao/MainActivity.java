package com.github.florent37.dao;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.florent37.dao.model.Tree;
import com.github.florent37.dao.model.TreeFridge;

public class MainActivity extends AppCompatActivity {

    TreeFridge treeFridge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        treeFridge = new TreeFridge();

        treeFridge.add(new Tree(3, "chaine"));

        Log.d("DAO all", treeFridge.selectWhere()
                .ageEquals(3)
                .asList()
                .toString());
    }
}
