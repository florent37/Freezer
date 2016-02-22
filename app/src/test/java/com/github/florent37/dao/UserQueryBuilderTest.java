package com.github.florent37.dao;

import com.github.florent37.orm.model.Cat;
import com.github.florent37.orm.model.CatEntityManager;
import com.github.florent37.orm.model.Dog;
import com.github.florent37.orm.model.DogEntityManager;
import com.github.florent37.orm.model.User;
import com.github.florent37.orm.model.UserColumns;
import com.github.florent37.orm.model.UserEntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import fr.xebia.android.freezer.async.Callback;
import rx.functions.Action1;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.spy;

/**
 * Created by florentchampigny on 05/02/2016.
 */
@RunWith(CustomRobolectricTestRunner.class)
public class UserQueryBuilderTest {

    UserEntityManager userEntityManager;
    CatEntityManager catEntityManager;
    DogEntityManager dogEntityManager;

    @Before
    public void setUp() throws Exception {
        userEntityManager = spy(new UserEntityManager());
        userEntityManager.deleteAll();

        catEntityManager = spy(new CatEntityManager());
        catEntityManager.deleteAll();

        dogEntityManager = spy(new DogEntityManager());
        dogEntityManager.deleteAll();
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
        userEntityManager.add(users);
        User userFromBase = userEntityManager.select().age().equalsTo(21).first();

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
        userEntityManager.add(users);
        List<User> usersFromBase = userEntityManager.select().age().equalsTo(21).asList();

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
        userEntityManager.add(users);
        List<User> usersFromBase = userEntityManager.select().age().notEqualsTo(21).asList();

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
        userEntityManager.add(users);
        List<User> usersFromBase = userEntityManager.select().age().greatherThan(10).asList();

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
        userEntityManager.add(users);
        List<User> usersFromBase = userEntityManager.select().age().lessThan(20).asList();

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
        userEntityManager.add(users);
        List<User> usersFromBase = userEntityManager.select().age().between(9, 20).asList();

        //then
        assertThat(usersFromBase).isNotNull();
        assertThat(usersFromBase).hasSize(1);
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
        userEntityManager.add(users);
        User userFromBase = userEntityManager.select().hacker().equalsTo(true).first();

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
        userEntityManager.add(users);
        List<User> userFromBase = userEntityManager.select()
                .name().equalsTo("florent")
                .or()
                .hacker().isTrue()
                .asList();

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
        userEntityManager.add(users);
        List<User> userFromBase = userEntityManager.select()
                .beginGroup()
                .name().equalsTo("florent")
                .or()
                .hacker().isTrue()
                .endGroup()
                .and()
                .age().equalsTo(20)
                .asList();

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
        userEntityManager.add(users);
        List<User> userFromBase = userEntityManager.select()
                .sortAsc(UserColumns.age)
                .asList();

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
        userEntityManager.add(users);
        List<User> userFromBase = userEntityManager.select()
                .sortDesc(UserColumns.age)
                .asList();

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
        userEntityManager.add(users);
        User userFromBase = userEntityManager.select().hacker().isTrue().first();

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
        userEntityManager.add(users);
        User userFromBase = userEntityManager.select().hacker().equalsTo(false).first();

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
        userEntityManager.add(users);
        User userFromBase = userEntityManager.select().hacker().isFalse().first();

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
        userEntityManager.add(users);
        List<User> usersFromBase = userEntityManager.select().cat(CatEntityManager.where().shortName().equalsTo("Java")).asList();

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
        userEntityManager.add(users);
        List<User> usersFromBase = userEntityManager.select().cat(CatEntityManager.where().shortName().notEqualsTo("Java")).asList();

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
        userEntityManager.add(users);
        List<User> usersFromBase = userEntityManager.select().cat(CatEntityManager.where().shortName().contains("J")).asList();

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
        userEntityManager.add(users);
        List<User> usersFromBase = userEntityManager.select().cat(CatEntityManager.where().shortName().contains("%av%")).asList();

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
        userEntityManager.add(users);

        //when
        List<User> usersFromBase = userEntityManager
                .select()
                .fields(UserColumns.name, UserColumns.cat, UserColumns.dogs, UserColumns.hacker)
                .asList();

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
        userEntityManager.add(users);

        //when
        List<User> usersFromBase = userEntityManager
                .select()
                .fieldsWithout(UserColumns.name)
                .asList();

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
        userEntityManager.add(users);

        final AtomicInteger numberOfUsers = new AtomicInteger();

        //when
        userEntityManager
                .select()
                .fieldsWithout(UserColumns.name)
                .asObservable()
                .subscribe(new Action1<List<User>>() {
                    @Override
                    public void call(List<User> users) {
                        numberOfUsers.set(users.size());
                    }
                });

        //then
        assertThat(numberOfUsers.get()).isEqualTo(3);
    }

    @Test
    public void testSelectUsers_async() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("nnn"), Arrays.asList(new Dog("a"), new Dog("b")), true),
                new User(21, "kevin", null, null, true),
                new User(10, "alex", null, null, false)
        );
        userEntityManager.add(users);

        final AtomicInteger numberOfUsers = new AtomicInteger();

        //when
        userEntityManager
                .select()
                .fieldsWithout(UserColumns.name)
                .async(new Callback<List<User>>() {
                    @Override
                    public void onSuccess(List<User> data) {
                        numberOfUsers.set(data.size());
                    }

                    @Override
                    public void onError(List<User> data) {

                    }
                });

        //then
        assertThat(numberOfUsers.get()).isEqualTo(3);
    }

}
