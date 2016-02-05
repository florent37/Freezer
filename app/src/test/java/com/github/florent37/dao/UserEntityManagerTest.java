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
    public void shouldAddUser(){
        //given
        User user = new User(21, "florent");

        //when
        userEntityManager.add(user);

        //then
        assertThat(userEntityManager.count()).isEqualTo(1);

    }

    @Test
    public void shouldAddUsers(){
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
    public void shouldAddUsers_withCatDogs(){
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
    public void testSelectUserFromAge(){
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
    public void testSelectUsersFromAge(){
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

}
