package com.github.florent37.rxandroidorm.sample;

import android.util.Log;

import com.github.florent37.rxandroidorm.sample.model.CatDatabase;
import com.github.florent37.rxandroidorm.sample.model.Computer;
import com.github.florent37.rxandroidorm.sample.model.ComputerDatabase;
import com.github.florent37.rxandroidorm.sample.model.DogDatabase;
import com.github.florent37.rxandroidorm.sample.model.Software;
import com.github.florent37.rxandroidorm.sample.model.SoftwareDatabase;
import com.github.florent37.rxandroidorm.sample.model.User;
import com.github.florent37.rxandroidorm.sample.model.UserColumns;
import com.github.florent37.rxandroidorm.sample.model.UserDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.spy;

/**
 * Created by florentchampigny on 05/02/2016.
 */
@RunWith(CustomRobolectricTestRunner.class)
public class RxUserTest {

    UserDatabase userEntityManager;
    ComputerDatabase computerDb;

    @Before
    public void setUp() throws Exception {
        userEntityManager = spy(new UserDatabase());
        userEntityManager.deleteAll().subscribe();

        computerDb = spy(new ComputerDatabase());

        Observable.zip(
                userEntityManager.deleteAll(),
                computerDb.deleteAll(),
                (aBoolean, aBoolean2) -> aBoolean)
                .subscribe();
    }

    @Test
    public void testAdd_one() {
        userEntityManager.add(new User(21, "florent"))
                .flatMap(user -> userEntityManager.count())
                .test()
                .assertValue(1);
    }

    @Test
    public void testAdd_two() {
        userEntityManager.add(new User(1, "florent"))
                .flatMap(user -> userEntityManager.add(new User(2, "kevin")))
                .flatMap(user1 -> userEntityManager.count())
                .test()
                .assertValue(2);
    }

    @Test
    public void testAdd_list() {
        userEntityManager.add(Arrays.asList(new User(21, "florent"), new User(22, "kevin")))
                .flatMap(user -> userEntityManager.count())
                .test()
                .assertValue(2);
    }

    @Test
    public void testAdd_then_select() {
        userEntityManager.add(Arrays.asList(new User(21, "florent"), new User(22, "kevin")))
                .flatMap(user -> userEntityManager.select().age().equalsTo(21).first())
                .map(User::getName)
                .test()
                .assertValue("florent");
    }

    @Test
    public void testComputer() {
        Observable.fromArray(
                new Computer(Computer.WINDOWS, "MasterRace", Arrays.asList(new Software("Photoshop"))),
                new Computer(Computer.WINDOWS, "Gamerz"),
                new Computer(Computer.LINUX, "MasterRace", Arrays.asList(new Software("Gimp"))))
                .flatMap(computerDb::add)
                .subscribe();

        Observable.just(new Computer(Computer.MAC, "Mac Mini"))
                .flatMap(computerDb::add)
                .doOnNext(computer -> computer.setSoftwares(Arrays.asList(new Software("Photoshop"))))
                .flatMap(computerDb::update)
                .subscribe();

        computerDb.select()
                .label().equalsTo("MasterRace")
                .or()
                .softwares(SoftwareDatabase.where().name().equalsTo("Photoshop"))

                .asObservable()
                .test()

                .assertValue(new Predicate<List<Computer>>() {
                    @Override
                    public boolean test(@NonNull List<Computer> computers) throws Exception {
                        return computers.size() == 3;
                    }
                });
    }
}
