package com.github.florent37.orm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.florent37.orm.model.CatEntityManager;

public class MainActivity extends AppCompatActivity {

    CatEntityManager catORM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queries();
    }

    protected void queries() {
        catORM = new CatEntityManager();

        Log.d("SQL", catORM.select().asList().toString());

    }
}
