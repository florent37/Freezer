package com.github.florent37.dao;

import com.github.florent37.orm.model.Cat;
import com.github.florent37.orm.model.CatEntityManager;
import com.github.florent37.orm.model.CatQueryBuilder;
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
    public void testUpdateUser_onlyFields() throws Exception{
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
    public void testUpdateUser_oneToOne_nullToValue() throws Exception{
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
    public void testUpdateUser_oneToOne_valueToNull() throws Exception{
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
    public void testUpdateUser_oneToOne_updateValue() throws Exception{
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
    public void testUpdateUser_oneToMany_nullToValue() throws Exception{
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
    public void testUpdateUser_oneToMany_valueToNull() throws Exception{
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
    public void testUpdateUser_oneToMany_valueAdded() throws Exception{
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
    public void testUpdateUser_oneToMany_valueRemoved() throws Exception{
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
    public void testUpdateUser_oneToMany_valueUpdated() throws Exception{
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

}
