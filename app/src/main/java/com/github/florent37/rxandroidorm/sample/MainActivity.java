package com.github.florent37.rxandroidorm.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.florent37.rxandroidorm.sample.model.Computer;
import com.github.florent37.rxandroidorm.sample.model.ComputerDatabase;
import com.github.florent37.rxandroidorm.sample.model.Software;
import com.github.florent37.rxandroidorm.sample.model.SoftwareDatabase;

import java.util.Arrays;

import io.reactivex.Observable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ComputerDatabase computerDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        computerDb = new ComputerDatabase();

        queries();
    }

    protected void queries() {

        Observable.fromArray(
                new Computer(Computer.WINDOWS, "MasterRace", Arrays.asList(new Software("Photoshop"))),
                new Computer(Computer.WINDOWS, "Gamerz"),
                new Computer(Computer.LINUX, "MasterRace", Arrays.asList(new Software("Gimp"))))
                .flatMap(computerDb::add)
                .subscribe();

        Observable.just(new Computer(Computer.MAC, "Mac Mini"))
                .flatMap(computerDb::add)
                .doOnNext(computer -> computer.getSoftwares().add(new Software("Photoshop")))
                .flatMap(computerDb::update)
                .subscribe();

        computerDb.select()
                .label().equalsTo("MasterRace")
                .or()
                .softwares(SoftwareDatabase.where().name().equalsTo("Photoshop"))

                .asObservable()
                .subscribe(computers -> Log.d(TAG, computers.toString()));
    }
}
