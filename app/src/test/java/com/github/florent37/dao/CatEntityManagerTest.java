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
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.spy;

/**
 * Created by florentchampigny on 05/02/2016.
 */
@RunWith(CustomRobolectricTestRunner.class)
public class CatEntityManagerTest {

    CatEntityManager catEntityManager;

    @Before
    public void setUp() throws Exception {
        catEntityManager = spy(new CatEntityManager());
        catEntityManager.deleteAll();
    }

    @Test
    public void shouldAddCatWithDate(){
        //given
        Date date = new Date(System.currentTimeMillis() - 60 * 1000);
        Cat cat = new Cat("Java",date);

        //when
        catEntityManager.add(cat);

        //then
        assertThat(catEntityManager.count()).isEqualTo(1);
        assertThat(cat.getId()).isNotEqualTo(0);
    }

    @Test
    public void shouldGetCatWithAllFields(){
        //given
        Date date = new Date(System.currentTimeMillis() - 60 * 1000);
        Cat cat = new Cat("Java",date);

        //when
        catEntityManager.add(cat);
        Cat catFromBase = catEntityManager.select().first();

        //then
        assertThat(catFromBase).isNotNull();
        assertThat(catFromBase.getShortName()).isEqualTo("Java");
        assertThat(catFromBase.getDate().toString()).isEqualTo(date.toString());
    }

    @Test
    public void shouldGetCatWithCustomDate_equals(){
        //given
        Date date = new Date(System.currentTimeMillis() - 60 * 1000);

        Cat cat1 = new Cat("Java",date);
        Cat cat2 = new Cat("Blob",new Date(System.currentTimeMillis() + 60 * 1000));

        //when
        catEntityManager.add(cat1);
        catEntityManager.add(cat2);

        List<Cat> cats = catEntityManager.select().date().equalsTo(date).asList();

        //then
        assertThat(cats).hasSize(1);
        assertThat(cats.get(0).getShortName()).isEqualTo("Java");
    }

    @Test
    public void shouldGetCatWithCustomDate_before(){
        //given
        Date now = new Date(System.currentTimeMillis());

        Cat cat1 = new Cat("Java",new Date(now.getTime() - 60 * 1000 * 1000));
        Cat cat2 = new Cat("Blob",new Date(now.getTime() + 60 * 1000 * 1000));

        //when
        catEntityManager.add(cat1);
        catEntityManager.add(cat2);

        List<Cat> cats = catEntityManager.select().date().before(now).asList();

        //then
        assertThat(cats).hasSize(1);
        assertThat(cats.get(0).getShortName()).isEqualTo("Java");
    }

    @Test
    public void shouldGetCatWithCustomDate_after(){
        //given
        Date now = new Date(System.currentTimeMillis());

        Cat cat1 = new Cat("Java",new Date(now.getTime() - 60 * 1000 * 1000));
        Cat cat2 = new Cat("Blob",new Date(now.getTime() + 60 * 1000 * 1000));

        //when
        catEntityManager.add(cat1);
        catEntityManager.add(cat2);

        List<Cat> cats = catEntityManager.select().date().after(now).asList();

        //then
        assertThat(cats).hasSize(1);
        assertThat(cats.get(0).getShortName()).isEqualTo("Blob");
    }

    @Test
    public void testUpdateCat_onlyFields() throws Exception{
        //given
        Cat cat = new Cat("toto");
        catEntityManager.add(cat);
        assertThat(catEntityManager.count()).isEqualTo(1);

        //when
        cat.setShortName("mimi");
        catEntityManager.update(cat);

        //then
        assertThat(catEntityManager.count()).isEqualTo(1);
        Cat catFromBase = catEntityManager.select().first();
        assertThat(catFromBase.getShortName()).isEqualTo("mimi");
    }

}
