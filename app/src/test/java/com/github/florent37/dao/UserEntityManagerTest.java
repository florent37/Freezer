package com.github.florent37.dao;

import com.github.florent37.orm.model.Cat;
import com.github.florent37.orm.model.CatEntityManager;
import com.github.florent37.orm.model.Dog;
import com.github.florent37.orm.model.DogEntityManager;
import com.github.florent37.orm.model.User;
import com.github.florent37.orm.model.UserEntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.xebia.android.freezer.async.Callback;
import fr.xebia.android.freezer.async.SimpleCallback;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.spy;

/**
 * Created by florentchampigny on 05/02/2016.
 */
@RunWith(CustomRobolectricTestRunner.class)
public class UserEntityManagerTest {

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
    public void shouldAddUser() {
        //given
        User user = new User(21, "florent");

        //when
        userEntityManager.add(user);

        //then
        assertThat(userEntityManager.count()).isEqualTo(1);

    }

    @Test
    public void shouldAddUsers() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true),
                new User(30, "kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty")), true),
                new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")), false)
        );

        //when
        userEntityManager.add(users);

        //then
        assertThat(userEntityManager.count()).isEqualTo(3);
        assertThat(catEntityManager.count()).isEqualTo(3);
        assertThat(dogEntityManager.count()).isEqualTo(4);

    }

    @Test
    public void shouldAddUsersAsync() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true),
                new User(30, "kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty")), true),
                new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")), false)
        );

        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        //when
        userEntityManager.addAsync(users, new Callback<List<User>>() {
            @Override
            public void onSuccess(List<User> data) {
                if (data != null)
                    atomicBoolean.getAndSet(true);
            }

            @Override
            public void onError(List<User> data) {

            }
        });

        //then
        assertThat(userEntityManager.count()).isEqualTo(3);
        assertThat(catEntityManager.count()).isEqualTo(3);
        assertThat(dogEntityManager.count()).isEqualTo(4);
        assertThat(atomicBoolean.get()).isTrue();
    }

    @Test
    public void shouldAddUsersAsync_NullCallback() {
        //given
        List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true),
                new User(30, "kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty")), true),
                new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")), false)
        );

        //when
        userEntityManager.addAsync(users, null);

        //then
        assertThat(userEntityManager.count()).isEqualTo(3);
        assertThat(catEntityManager.count()).isEqualTo(3);
        assertThat(dogEntityManager.count()).isEqualTo(4);
    }

    @Test
    public void shouldAddUserAsync() {
        //given
        User user = new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true);

        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        //when
        userEntityManager.addAsync(user, new Callback<User>() {
            @Override
            public void onSuccess(User data) {
                if (data != null)
                    atomicBoolean.getAndSet(true);
            }

            @Override
            public void onError(User data) {

            }
        });

        //then
        assertThat(userEntityManager.count()).isEqualTo(1);
        assertThat(atomicBoolean.get()).isTrue();
    }

    @Test
    public void shouldAddUserAsync_NullCallback() {
        //given
        User user = new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true);

        //when
        userEntityManager.addAsync(user, null);

        //then
        assertThat(userEntityManager.count()).isEqualTo(1);
    }

    @Test
    public void shouldAddUsers_withCatDogs() {
        //given
        User user = new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true);

        //when
        userEntityManager.add(user);
        User userFromBase = userEntityManager.select().first();

        //then
        assertThat(userFromBase).isNotNull();
        assertThat(userFromBase.getAge()).isEqualTo(21);
        assertThat(userFromBase.getName()).isEqualTo("florent");
        assertThat(userFromBase.isHacker()).isTrue();
        assertThat(userFromBase.getCat()).isNotNull();
        assertThat(userFromBase.getCat().getShortName()).isEqualTo("Java");
        assertThat(userFromBase.getDogs()).hasSize(1);
        assertThat(userFromBase.getDogs().get(0).getName()).isEqualTo("Loulou");

    }

    @Test
    public void testUpdateUser_onlyFields() throws Exception {
        //given
        userEntityManager.add(new User(30, "blob", null, null, true));

        //when
        User userFromBase = userEntityManager.select().name().equalsTo("blob").first();
        userFromBase.setAge(10);
        userEntityManager.update(userFromBase);

        //then
        assertThat(userEntityManager.count()).isEqualTo(1);
        User userFromBase2 = userEntityManager.select().name().equalsTo("blob").first();
        assertThat(userFromBase2.getAge()).isEqualTo(10);
    }

    @Test
    public void testUpdateUser_oneToOne_nullToValue() throws Exception {
        //given
        userEntityManager.add(new User(30, "blob", null, null, true));

        //when
        User userFromBase = userEntityManager.select().name().equalsTo("blob").first();
        userFromBase.setCat(new Cat("java"));
        userEntityManager.update(userFromBase);

        //then
        assertThat(userEntityManager.count()).isEqualTo(1);
        assertThat(catEntityManager.count()).isEqualTo(1);
        User userFromBase2 = userEntityManager.select().name().equalsTo("blob").first();
        assertThat(userFromBase2.getCat()).isNotNull();
        assertThat(userFromBase2.getCat().getShortName()).isEqualTo("java");
    }

    @Test
    public void testUpdateUser_oneToOne_valueToNull() throws Exception {
        //given
        userEntityManager.add(new User(30, "blob", new Cat("java"), null, true));

        //when
        User userFromBase = userEntityManager.select().name().equalsTo("blob").first();
        userFromBase.setCat(null);
        userEntityManager.update(userFromBase);

        //then
        assertThat(userEntityManager.count()).isEqualTo(1);
        assertThat(catEntityManager.count()).isEqualTo(1);
        User userFromBase2 = userEntityManager.select().name().equalsTo("blob").first();
        assertThat(userFromBase2.getCat()).isNull();
    }

    @Test
    public void testUpdateUser_oneToOne_updateValue() throws Exception {
        //given
        userEntityManager.add(new User(30, "blob", new Cat("java"), null, true));

        //when
        User userFromBase = userEntityManager.select().name().equalsTo("blob").first();
        userFromBase.getCat().setShortName("lili");
        userEntityManager.update(userFromBase);

        //then
        assertThat(userEntityManager.count()).isEqualTo(1);
        assertThat(catEntityManager.count()).isEqualTo(1);
        User userFromBase2 = userEntityManager.select().name().equalsTo("blob").first();
        assertThat(userFromBase2.getCat()).isNotNull();
        assertThat(userFromBase2.getCat().getShortName()).isEqualTo("lili");
    }

    @Test
    public void testUpdateUser_oneToMany_nullToValue() throws Exception {
        //given
        userEntityManager.add(new User(30, "blob", null, null, true));

        //when
        User userFromBase = userEntityManager.select().name().equalsTo("blob").first();
        userFromBase.setDogs(Arrays.asList(new Dog("a"), new Dog("b")));
        userEntityManager.update(userFromBase);

        //then
        assertThat(userEntityManager.count()).isEqualTo(1);
        assertThat(dogEntityManager.count()).isEqualTo(2);
        User userFromBase2 = userEntityManager.select().name().equalsTo("blob").first();
        assertThat(userFromBase2.getDogs()).hasSize(2);
        assertThat(userFromBase2.getDogs().get(0).getName()).isEqualTo("a");
        assertThat(userFromBase2.getDogs().get(1).getName()).isEqualTo("b");
    }

    @Test
    public void testUpdateUser_oneToMany_valueToNull() throws Exception {
        //given
        userEntityManager.add(new User(30, "blob", null, Arrays.asList(new Dog("a"), new Dog("b")), true));

        //when
        User userFromBase = userEntityManager.select().name().equalsTo("blob").first();
        assertThat(userFromBase.getDogs()).hasSize(2);
        userFromBase.setDogs(null);
        userEntityManager.update(userFromBase);

        //then
        assertThat(userEntityManager.count()).isEqualTo(1);
        assertThat(dogEntityManager.count()).isEqualTo(2); //TODO
        User userFromBase2 = userEntityManager.select().name().equalsTo("blob").first();
        assertThat(userFromBase2.getDogs()).isNull();
    }

    @Test
    public void testUpdateUser_oneToMany_valueAdded() throws Exception {
        //given
        userEntityManager.add(new User(30, "blob", null, Arrays.asList(new Dog("a"), new Dog("b")), true));

        //when
        User userFromBase = userEntityManager.select().name().equalsTo("blob").first();
        assertThat(userFromBase.getDogs()).hasSize(2);
        userFromBase.getDogs().add(new Dog("c"));
        userEntityManager.update(userFromBase);

        //then
        assertThat(userEntityManager.count()).isEqualTo(1);
        //TODO assertThat(dogEntityManager.count()).isEqualTo(2);
        User userFromBase2 = userEntityManager.select().name().equalsTo("blob").first();
        assertThat(userFromBase2.getDogs()).hasSize(3);
    }

    @Test
    public void testUpdateUser_oneToMany_valueRemoved() throws Exception {
        //given
        userEntityManager.add(new User(30, "blob", null, Arrays.asList(new Dog("a"), new Dog("b")), true));

        //when
        User userFromBase = userEntityManager.select().name().equalsTo("blob").first();
        assertThat(userFromBase.getDogs()).hasSize(2);
        userFromBase.getDogs().remove(0);
        userEntityManager.update(userFromBase);

        //then
        assertThat(userEntityManager.count()).isEqualTo(1);
        assertThat(dogEntityManager.count()).isEqualTo(2); //TODO
        User userFromBase2 = userEntityManager.select().name().equalsTo("blob").first();
        assertThat(userFromBase2.getDogs()).hasSize(1);
    }

    @Test
    public void testUpdateUser_oneToMany_valueUpdated() throws Exception {
        //given
        userEntityManager.add(new User(30, "blob", null, Arrays.asList(new Dog("a"), new Dog("b")), true));

        //when
        User userFromBase = userEntityManager.select().name().equalsTo("blob").first();
        assertThat(userFromBase.getDogs()).hasSize(2);
        userFromBase.getDogs().get(0).setName("ddd");
        userEntityManager.update(userFromBase);

        //then
        assertThat(userEntityManager.count()).isEqualTo(1);
        assertThat(dogEntityManager.count()).isEqualTo(2);
        User userFromBase2 = userEntityManager.select().name().equalsTo("blob").first();
        assertThat(userFromBase2.getDogs()).hasSize(2);
        assertThat(userFromBase2.getDogs().get(0).getName()).isEqualTo("ddd");
        assertThat(userFromBase2.getDogs().get(1).getName()).isEqualTo("b");
    }

    @Test
    public void testUpdateUser_onlyFields_async() throws Exception {
        //given
        userEntityManager.add(new User(30, "blob", null, null, true));

        //when
        User userFromBase = userEntityManager.select().name().equalsTo("blob").first();
        userFromBase.setAge(10);

        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        userEntityManager.updateAsync(userFromBase, new SimpleCallback<User>() {
            @Override
            public void onSuccess(User data) {
                atomicBoolean.set(true);
            }
        });

        //then
        assertThat(atomicBoolean.get()).isTrue();
    }
}
