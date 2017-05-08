package com.github.florent37.rxandroidorm.sample;

import com.github.florent37.rxandroidorm.sample.model.Cat;
import com.github.florent37.rxandroidorm.sample.model.CatDatabase;
import com.github.florent37.rxandroidorm.sample.model.Dog;
import com.github.florent37.rxandroidorm.sample.model.DogDatabase;
import com.github.florent37.rxandroidorm.sample.model.User;
import com.github.florent37.rxandroidorm.sample.model.UserDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.spy;

/**
 * Created by florentchampigny on 05/02/2016.
 */
@RunWith(CustomRobolectricTestRunner.class)
public class UserEntityManagerTest {

    @Rule
    public RxJavaSchedulersTestRule rxJavaSchedulersTestRule = new RxJavaSchedulersTestRule();

    UserDatabase userEntityManager;
    CatDatabase CarEntityManager;
    DogDatabase dogEntityManager;

    private Scheduler scheduler = Schedulers.trampoline();

    @Before
    public void setUp() throws Exception {
        userEntityManager = spy(new UserDatabase());
        userEntityManager.deleteAll().subscribeOn(scheduler).subscribe();

        CarEntityManager = spy(new CatDatabase());
        CarEntityManager.deleteAll().subscribeOn(scheduler).subscribe();

        dogEntityManager = spy(new DogDatabase());
        dogEntityManager.deleteAll().subscribeOn(scheduler).subscribe();
    }

    @Test
    public void shouldAddUser() {
        //given
        final User user = new User(21, "florent");

        //when
        userEntityManager.add(user).subscribe();

        //then
        userEntityManager.count().test().assertValue(1);
    }

    @Test
    public void shouldAddUsers_flatmap() {
        //given
        final User user = new User(21, "florent");
        final User user2 = new User(22, "kevin");

        //when
        userEntityManager.add(user)
                .flatMap(new Function<User, ObservableSource<User>>() {
                    @Override
                    public ObservableSource<User> apply(@NonNull User user) throws Exception {
                        return userEntityManager.add(user2);
                    }
                })
                .subscribe();

        //then
        userEntityManager.count().test().assertValue(2);
    }

    @Test
    public void shouldAddUsers() {
        //given
        final List<User> users = Arrays.asList(
                new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true),
                new User(30, "kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty")), true),
                new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")), false)
        );

        //when
        userEntityManager.add(users).subscribeOn(scheduler).subscribe();

        //then
        userEntityManager.count().test().assertValue(3);
        CarEntityManager.count().test().assertValue(3);
        dogEntityManager.count().test().assertValue(4);

    }

    @Test
    public void shouldAddUsers_withCatDogs() {
        //given
        User user = new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true);

        //when
        userEntityManager.add(user)
                .subscribeOn(scheduler)
                .subscribe();

        User userFromBase = userEntityManager.select().first().blockingFirst();

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
        userEntityManager.add(new User(30, "blob", null, null, true)).subscribeOn(scheduler).subscribe();

        //when
        User userFromBase = userEntityManager.select().name().equalsTo("blob").first().blockingFirst();
        userFromBase.setAge(10);
        userEntityManager.update(userFromBase).subscribe();

        //then
        userEntityManager.count().test().assertValue(1);
        User userFromBase2 = userEntityManager.select().name().equalsTo("blob").first().blockingFirst();
        assertThat(userFromBase2.getAge()).isEqualTo(10);
    }

    @Test
    public void testUpdateUser_oneToOne_nullToValue() throws Exception {
        //given
        userEntityManager.add(new User(30, "blob", null, null, true)).subscribeOn(scheduler).subscribe();

        //when
        User userFromBase = userEntityManager.select().name().equalsTo("blob").first().blockingFirst();
        userFromBase.setCat(new Cat("java"));
        userEntityManager.update(userFromBase).subscribe();

        //then
        userEntityManager.count().test().assertValue(1);
        CarEntityManager.count().test().assertValue(1);
        User userFromBase2 = userEntityManager.select().name().equalsTo("blob").first().blockingFirst();
        assertThat(userFromBase2.getCat()).isNotNull();
        assertThat(userFromBase2.getCat().getShortName()).isEqualTo("java");
    }

    @Test
    public void testUpdateUser_oneToOne_valueToNull() throws Exception {
        //given
        userEntityManager.add(new User(30, "blob", new Cat("java"), null, true)).subscribeOn(scheduler).subscribe();

        //when
        User userFromBase = userEntityManager.select().name().equalsTo("blob").first().blockingFirst();
        userFromBase.setCat(null);
        userEntityManager.update(userFromBase).subscribe();

        //then
        userEntityManager.count().test().assertValue(1);
        CarEntityManager.count().test().assertValue(1);

        User userFromBase2 = userEntityManager.select().name().equalsTo("blob").first().blockingFirst();
        assertThat(userFromBase2.getCat()).isNull();
    }

    @Test
    public void testUpdateUser_oneToOne_updateValue() throws Exception {
        //given
        userEntityManager.add(new User(30, "blob", new Cat("java"), null, true)).subscribeOn(scheduler).subscribe();

        //when
        User userFromBase = userEntityManager.select().name().equalsTo("blob").first().blockingFirst();
        userFromBase.getCat().setShortName("lili");
        userEntityManager.update(userFromBase).subscribe();

        //then
        userEntityManager.count().test().assertValue(1);
        CarEntityManager.count().test().assertValue(1);
        User userFromBase2 = userEntityManager.select().name().equalsTo("blob").first().blockingFirst();
        assertThat(userFromBase2.getCat()).isNotNull();
        assertThat(userFromBase2.getCat().getShortName()).isEqualTo("lili");
    }

    @Test
    public void testUpdateUser_oneToMany_nullToValue() throws Exception {
        //given
        userEntityManager.add(new User(30, "blob", null, null, true))
                .subscribeOn(scheduler)
                .subscribe();

        //when
        User userFromBase = userEntityManager.select().name().equalsTo("blob").first().blockingFirst();
        userFromBase.setDogs(Arrays.asList(new Dog("a"), new Dog("b")));
        userEntityManager.update(userFromBase).subscribe();

        //then
        userEntityManager.count().test().assertValue(1);
        dogEntityManager.count().test().assertValue(2);
        User userFromBase2 = userEntityManager.select().name().equalsTo("blob").first().blockingFirst();
        assertThat(userFromBase2.getDogs()).hasSize(2);
        assertThat(userFromBase2.getDogs().get(0).getName()).isEqualTo("a");
        assertThat(userFromBase2.getDogs().get(1).getName()).isEqualTo("b");
    }

    @Test
    public void testUpdateUser_oneToMany_valueToNull() throws Exception {
        //given
        userEntityManager.add(new User(30, "blob", null, Arrays.asList(new Dog("a"), new Dog("b")), true))
                .subscribeOn(scheduler)
                .subscribe();

        //when
        User userFromBase = userEntityManager.select().name().equalsTo("blob").first().blockingFirst();
        assertThat(userFromBase.getDogs()).hasSize(2);
        userFromBase.setDogs(null);
        userEntityManager.update(userFromBase).subscribe();

        //then
        userEntityManager.count().test().assertValue(1);
        dogEntityManager.count().test().assertValue(2); //TODO
        User userFromBase2 = userEntityManager.select().name().equalsTo("blob").first().blockingFirst();
        assertThat(userFromBase2.getDogs()).isNull();
    }

    @Test
    public void testUpdateUser_oneToMany_valueAdded() throws Exception {
        //given
        userEntityManager.add(new User(30, "blob", null, Arrays.asList(new Dog("a"), new Dog("b")), true))
                .subscribeOn(scheduler)
                .subscribe();

        //when
        User userFromBase = userEntityManager.select().name().equalsTo("blob").first().blockingFirst();
        assertThat(userFromBase.getDogs()).hasSize(2);
        userFromBase.getDogs().add(new Dog("c"));
        userEntityManager.update(userFromBase).subscribe();

        //then
        userEntityManager.count().test().assertValue(1);
        //TODO assertThat(dogEntityManager.count()).isEqualTo(2);
        User userFromBase2 = userEntityManager.select().name().equalsTo("blob").first().blockingFirst();
        assertThat(userFromBase2.getDogs()).hasSize(3);
    }

    @Test
    public void testUpdateUser_oneToMany_valueRemoved() throws Exception {
        //given
        userEntityManager.add(new User(30, "blob", null, Arrays.asList(new Dog("a"), new Dog("b")), true))
                .subscribeOn(scheduler)
                .subscribe();

        //when
        User userFromBase = userEntityManager.select().name().equalsTo("blob").first().blockingFirst();
        assertThat(userFromBase.getDogs()).hasSize(2);
        userFromBase.getDogs().remove(0);
        userEntityManager.update(userFromBase).subscribe();

        //then
        userEntityManager.count().test().assertValue(1);
        dogEntityManager.count().test().assertValue(2); //TODO
        User userFromBase2 = userEntityManager.select().name().equalsTo("blob").first().blockingFirst();
        assertThat(userFromBase2.getDogs()).hasSize(1);
    }

    @Test
    public void testUpdateUser_oneToMany_valueUpdated() throws Exception {
        //given
        userEntityManager.add(new User(30, "blob", null, Arrays.asList(new Dog("a"), new Dog("b")), true))
                .subscribeOn(scheduler)
                .subscribe();

        //when
        User userFromBase = userEntityManager.select().name().equalsTo("blob").first().blockingFirst();
        assertThat(userFromBase.getDogs()).hasSize(2);
        userFromBase.getDogs().get(0).setName("ddd");
        userEntityManager.update(userFromBase).subscribe();

        //then
        userEntityManager.count().test().assertValue(1);
        dogEntityManager.count().test().assertValue(2);
        User userFromBase2 = userEntityManager.select().name().equalsTo("blob").first().blockingFirst();
        assertThat(userFromBase2.getDogs()).hasSize(2);
        assertThat(userFromBase2.getDogs().get(0).getName()).isEqualTo("ddd");
        assertThat(userFromBase2.getDogs().get(1).getName()).isEqualTo("b");
    }
}
