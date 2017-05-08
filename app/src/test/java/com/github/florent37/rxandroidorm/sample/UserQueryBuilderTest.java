package com.github.florent37.rxandroidorm.sample;

import com.github.florent37.rxandroidorm.sample.model.Cat;
import com.github.florent37.rxandroidorm.sample.model.CatDatabase;
import com.github.florent37.rxandroidorm.sample.model.Dog;
import com.github.florent37.rxandroidorm.sample.model.DogDatabase;
import com.github.florent37.rxandroidorm.sample.model.User;
import com.github.florent37.rxandroidorm.sample.model.UserColumns;
import com.github.florent37.rxandroidorm.sample.model.UserDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.functions.Consumer;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.spy;

/**
 * Created by florentchampigny on 05/02/2016.
 */
@RunWith(CustomRobolectricTestRunner.class)
public class UserQueryBuilderTest {

    UserDatabase userEntityManager;
    CatDatabase catEntityManager;
    DogDatabase dogEntityManager;

    @Before
    public void setUp() throws Exception {
        userEntityManager = spy(new UserDatabase());
        userEntityManager.deleteAll().subscribe();

        catEntityManager = spy(new CatDatabase());
        catEntityManager.deleteAll().subscribe();

        dogEntityManager = spy(new DogDatabase());
        dogEntityManager.deleteAll().subscribe();
    }

    @Test
    public void testSelectUserFromAge() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true),
                new User(30, "kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty")), true),
                new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")), false)
        );

        //when
        userEntityManager.add(users).subscribe();

        User userFromBase = userEntityManager.select().age().equalsTo(21).first().blockingFirst();

        //then
        assertThat(userFromBase).isNotNull();
        assertThat(userFromBase.getName()).isEqualTo("florent");
    }

    @Test
    public void testSelectUsersFromAge_equals() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true),
                new User(21, "kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty")), true),
                new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")), false)
        );

        //when
        userEntityManager.add(users).subscribe();

        List<User> usersFromBase = userEntityManager.select().age().equalsTo(21).asObservable().blockingFirst();

        //then
        assertThat(usersFromBase).isNotNull();
        assertThat(usersFromBase).hasSize(2);
    }

    @Test
    public void testSelectUsersFromAge_notEquals() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true),
                new User(21, "kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty")), true),
                new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")), false)
        );

        //when
        userEntityManager.add(users).subscribe();

        List<User> usersFromBase = userEntityManager.select().age().notEqualsTo(21).asObservable().blockingFirst();

        //then
        assertThat(usersFromBase).isNotNull();
        assertThat(usersFromBase).hasSize(1);
        assertThat(usersFromBase.get(0).getName()).isEqualTo("alex");
    }

    @Test
    public void testSelectUsersFromAge_greather() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true),
                new User(21, "kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty")), true),
                new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")), false)
        );

        //when
        userEntityManager.add(users).subscribe();

        List<User> usersFromBase = userEntityManager.select().age().greatherThan(10).asObservable().blockingFirst();

        //then
        assertThat(usersFromBase).isNotNull();
        assertThat(usersFromBase).hasSize(2);
    }

    @Test
    public void testSelectUsersFromAge_less() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true),
                new User(21, "kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty")), true),
                new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")), false)
        );

        //when
        userEntityManager.add(users).subscribe();

        List<User> usersFromBase = userEntityManager.select().age().lessThan(20).asObservable().blockingFirst();

        //then
        assertThat(usersFromBase).isNotNull();
        assertThat(usersFromBase).hasSize(1);
    }

    @Test
    public void testSelectUsersFromAge_between() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true),
                new User(21, "kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty")), true),
                new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")), false)
        );

        //when
        userEntityManager.add(users).subscribe();

        List<User> usersFromBase = userEntityManager.select().age().between(9, 20).asObservable().blockingFirst();

        //then
        assertThat(usersFromBase).isNotNull();
        assertThat(usersFromBase).hasSize(1);
    }

    @Test
    public void testSelectUsersFromAge_in() {
        //given
        List<User> users = Arrays.asList(
            new User(1, "a"),
            new User(3, "b"),
            new User(5, "c"),
            new User(1, "d"),
            new User(10, "e"),
            new User(11, "f")
        );

        //when
        userEntityManager.add(users).subscribe();

        List<User> usersFromBase = userEntityManager.select().age().in(1, 10).asObservable().blockingFirst();

        //then
        assertThat(usersFromBase).isNotNull();
        assertThat(usersFromBase).hasSize(3);
    }

    @Test
    public void testSelectUsersFromName_in() {
        //given
        List<User> users = Arrays.asList(
            new User(1, "a"),
            new User(3, "b"),
            new User(5, "a"),
            new User(1, "e"),
            new User(10, "e"),
            new User(11, "f")
        );

        //when
        userEntityManager.add(users).subscribe();

        List<User> usersFromBase = userEntityManager.select().name().in("a", "e").asObservable().blockingFirst();

        //then
        assertThat(usersFromBase).isNotNull();
        assertThat(usersFromBase).hasSize(4);
    }

    @Test
    public void testSelectUserFromBoolean_equalsTrue() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true),
                new User(30, "kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty")), true),
                new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")), false)
        );

        //when
        userEntityManager.add(users).subscribe();

        User userFromBase = userEntityManager.select().hacker()
                .equalsTo(true)
                .first()
                .blockingFirst();

        //then
        assertThat(userFromBase).isNotNull();
        assertThat(userFromBase.getName()).isEqualTo("florent");
    }

    @Test
    public void testSelectUser_or() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", null, null, false),
                new User(30, "kevin", null, null, true),
                new User(10, "alex", null, null, false)
        );

        //when
        userEntityManager.add(users).subscribe();

        List<User> userFromBase = userEntityManager.select()
                .name().equalsTo("florent")
                .or()
                .hacker().isTrue()
                .asObservable().blockingFirst();

        //then
        assertThat(userFromBase).isNotNull();
        assertThat(userFromBase).hasSize(2);
    }

    @Test
    public void testSelectUser_group() {
        //given
        List<User> users = Arrays.asList(
                new User(10, "florent", null, null, false),
                new User(30, "kevin", null, null, true),
                new User(20, "mimi", null, null, true),
                new User(10, "alex", null, null, false)
        );

        //when
        userEntityManager.add(users).subscribe();

        List<User> userFromBase = userEntityManager.select()
                .beginGroup()
                .name().equalsTo("florent")
                .or()
                .hacker().isTrue()
                .endGroup()
                .and()
                .age().equalsTo(20)
                .asObservable().blockingFirst();

        //then
        assertThat(userFromBase).isNotNull();
        assertThat(userFromBase).hasSize(1);
        assertThat(userFromBase.get(0).getName()).isEqualTo("mimi");
    }

    @Test
    public void testSelectUser_orderAgeASC() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", null, null, false),
                new User(30, "kevin", null, null, true),
                new User(10, "alex", null, null, false)
        );

        //when
        userEntityManager.add(users).subscribe();

        List<User> userFromBase = userEntityManager.select()
                .sortAsc(UserColumns.age)
                .asObservable().blockingFirst();

        //then
        assertThat(userFromBase).isNotNull();
        assertThat(userFromBase.get(0).getName()).isEqualTo("alex");
        assertThat(userFromBase.get(1).getName()).isEqualTo("florent");
        assertThat(userFromBase.get(2).getName()).isEqualTo("kevin");
    }

    @Test
    public void testSelectUser_orderAgeDESC() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", null, null, false),
                new User(30, "kevin", null, null, true),
                new User(10, "alex", null, null, false)
        );

        //when
        userEntityManager.add(users).subscribe();
        List<User> userFromBase = userEntityManager.select()
                .sortDesc(UserColumns.age)
                .asObservable().blockingFirst();

        //then
        assertThat(userFromBase).isNotNull();
        assertThat(userFromBase.get(0).getName()).isEqualTo("kevin");
        assertThat(userFromBase.get(1).getName()).isEqualTo("florent");
        assertThat(userFromBase.get(2).getName()).isEqualTo("alex");
    }

    @Test
    public void testSelectUserFromBoolean_isTrue() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true),
                new User(30, "kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty")), true),
                new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")), false)
        );

        //when
        userEntityManager.add(users).subscribe();

        User userFromBase = userEntityManager.select()
                .hacker().isTrue()
                .first()
                .blockingFirst();

        //then
        assertThat(userFromBase).isNotNull();
        assertThat(userFromBase.getName()).isEqualTo("florent");
    }

    @Test
    public void testSelectUserFromBoolean_equalsFalse() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true),
                new User(30, "kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty")), true),
                new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")), false)
        );

        //when
        userEntityManager.add(users).subscribe();

        User userFromBase = userEntityManager.select()
                .hacker().equalsTo(false)
                .first().blockingFirst();

        //then
        assertThat(userFromBase).isNotNull();
        assertThat(userFromBase.getName()).isEqualTo("alex");
    }

    @Test
    public void testSelectUserFromBoolean_isFalse() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true),
                new User(30, "kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty")), true),
                new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")), false)
        );

        //when
        userEntityManager.add(users).subscribe();

        User userFromBase = userEntityManager.select()
                .hacker().isFalse()
                .first()
                .blockingFirst();

        //then
        assertThat(userFromBase).isNotNull();
        assertThat(userFromBase.getName()).isEqualTo("alex");
    }

    @Test
    public void testSelectUsersFromCat_stringEquals() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true),
                new User(21, "kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty")), true),
                new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")), false)
        );

        //when
        userEntityManager.add(users).subscribe();

        List<User> usersFromBase = userEntityManager.select().cat(CatDatabase.where().shortName().equalsTo("Java")).asObservable().blockingFirst();

        //then
        assertThat(usersFromBase).isNotNull();
        assertThat(usersFromBase).hasSize(1);
        assertThat(usersFromBase.get(0).getName()).isEqualTo("florent");
    }

    @Test
    public void testSelectUsersFromCat_stringNotEquals() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true),
                new User(21, "kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty")), true),
                new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")), false)
        );

        //when
        userEntityManager.add(users).subscribe();

        List<User> usersFromBase = userEntityManager.select().cat(CatDatabase.where().shortName().notEqualsTo("Java")).asObservable().blockingFirst();

        //then
        assertThat(usersFromBase).isNotNull();
        assertThat(usersFromBase).hasSize(2);
    }

    @Test
    public void testSelectUsersFromCat_stringContains() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true),
                new User(21, "kevin", new Cat("Jorris"), Arrays.asList(new Dog("Darty")), true),
                new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")), false)
        );

        //when
        userEntityManager.add(users).subscribe();

        List<User> usersFromBase = userEntityManager.select().cat(CatDatabase.where().shortName().contains("J")).asObservable().blockingFirst();

        //then
        assertThat(usersFromBase).isNotNull();
        assertThat(usersFromBase).hasSize(2);
        assertThat(usersFromBase.get(0).getName()).isEqualTo("florent");
        assertThat(usersFromBase.get(1).getName()).isEqualTo("kevin");
    }

    @Test
    public void testSelectUsersFromCat_stringLike() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true),
                new User(21, "kevin", new Cat("Lava"), Arrays.asList(new Dog("Darty")), true),
                new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")), false)
        );

        //when
        userEntityManager.add(users).subscribe();

        List<User> usersFromBase = userEntityManager.select().cat(CatDatabase.where().shortName().contains("%av%")).asObservable().blockingFirst();

        //then
        assertThat(usersFromBase).isNotNull();
        assertThat(usersFromBase).hasSize(2);
        assertThat(usersFromBase.get(0).getName()).isEqualTo("florent");
        assertThat(usersFromBase.get(1).getName()).isEqualTo("kevin");
    }

    @Test
    public void testSelectUsers_withoutAgeColumns() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", null, null, true),
                new User(21, "kevin", null, null, true),
                new User(10, "alex", null, null, false)
        );
        userEntityManager.add(users).subscribe();

        //when
        List<User> usersFromBase = userEntityManager
                .select()
                .fields(UserColumns.name, UserColumns.cat, UserColumns.dogs, UserColumns.hacker)
                .asObservable().blockingFirst();

        //then
        assertThat(usersFromBase).isNotNull();
        assertThat(usersFromBase).hasSize(3);
        assertThat(usersFromBase.get(0).getName()).isEqualTo("florent");
        assertThat(usersFromBase.get(0).getAge()).isEqualTo(0);
        assertThat(usersFromBase.get(1).getName()).isEqualTo("kevin");
        assertThat(usersFromBase.get(1).getAge()).isEqualTo(0);
        assertThat(usersFromBase.get(2).getName()).isEqualTo("alex");
        assertThat(usersFromBase.get(2).getAge()).isEqualTo(0);
    }

    @Test
    public void testSelectUsers_withoutNameCollumns() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("nnn"), Arrays.asList(new Dog("a"), new Dog("b")), true),
                new User(21, "kevin", null, null, true),
                new User(10, "alex", null, null, false)
        );
        userEntityManager.add(users).subscribe();

        //when
        List<User> usersFromBase = userEntityManager
                .select()
                .fieldsWithout(UserColumns.name)
                .asObservable().blockingFirst();

        //then
        assertThat(usersFromBase).isNotNull();
        assertThat(usersFromBase).hasSize(3);
        assertThat(usersFromBase.get(0).getName()).isNull();
        assertThat(usersFromBase.get(0).getAge()).isNotNull();
        assertThat(usersFromBase.get(0).getCat()).isNotNull();
        assertThat(usersFromBase.get(0).getDogs()).isNotNull();

        assertThat(usersFromBase.get(1).getName()).isNull();
        assertThat(usersFromBase.get(2).getName()).isNull();
    }

    @Test
    public void testSelectUsers_asObservable() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("nnn"), Arrays.asList(new Dog("a"), new Dog("b")), true),
                new User(21, "kevin", null, null, true),
                new User(10, "alex", null, null, false)
        );
        userEntityManager.add(users).subscribe();

        final AtomicInteger numberOfUsers = new AtomicInteger();

        //when
        userEntityManager
                .select()
                .asObservable()
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) {
                        numberOfUsers.set(users.size());
                    }
                });

        //then
        assertThat(numberOfUsers.get()).isEqualTo(3);
    }

    @Test
    public void testSelectUsers_limit() {
        //given
        List<User> users = Arrays.asList(
            new User(21, "a", null, null, true),
            new User(21, "b", null, null, true),
            new User(21, "c", null, null, true),
            new User(21, "d", null, null, true),
            new User(21, "e", null, null, true),//4
            new User(21, "f", null, null, true),
            new User(21, "g", null, null, true),
            new User(21, "h", null, null, true),
            new User(21, "i", null, null, true),
            new User(21, "j", null, null, true),
            new User(21, "k", null, null, true),
            new User(21, "l", null, null, true),
            new User(21, "m", null, null, true)
        );
        userEntityManager.add(users).subscribe();

        //when
        List<User> usersFromBase = userEntityManager
            .select()
            .limit(4,5)
            .asObservable().blockingFirst();

        //then
        assertThat(usersFromBase.size()).isEqualTo(5);
        assertThat(usersFromBase.get(0).getName()).isEqualTo("e");
    }

}
