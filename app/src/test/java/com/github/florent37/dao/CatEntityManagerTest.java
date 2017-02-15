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
    public void shouldAddCats_shouldGenerateIds(){
        //given
        final Cat java = new Cat("Java");
        final Cat bobo = new Cat("Bobo");
        final Cat sisi = new Cat("Sisi");
        final List<Cat> cats = Arrays.asList(java, bobo, sisi);

        //when
        catEntityManager.add(cats);

        //then
        assertThat(catEntityManager.count()).isEqualTo(3);
        assertThat(java.getId()).isEqualTo(1);
        assertThat(bobo.getId()).isEqualTo(2);
        assertThat(sisi.getId()).isEqualTo(3);
    }

    @Test
    public void shouldUpdateCats_becauseAlreadyExists(){
        //given
        {
            final Cat java = new Cat(1, "Java");
            final Cat bobo = new Cat(2, "Bobo");
            final Cat sisi = new Cat(3, "Sisi");
            final List<Cat> cats = Arrays.asList(java, bobo, sisi);
            catEntityManager.add(cats);
        }

        final Cat java = new Cat(1, "Java_e");

        //when
        catEntityManager.add(java);

        //then
        assertThat(catEntityManager.count()).isEqualTo(3);
        final Cat cat = catEntityManager.select().id().equalsTo(1l).first();
        assertThat(cat.getId()).isEqualTo(1);
        assertThat(cat.getShortName()).isEqualTo("Java_e");
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
    public void shouldGetCatWithCustomDate_notEquals(){
        //given
        Date date = new Date(System.currentTimeMillis() - 60 * 1000);

        Cat cat1 = new Cat("Java",date);
        Cat cat2 = new Cat("Blob",new Date(System.currentTimeMillis() + 60 * 1000));

        //when
        catEntityManager.add(cat1);
        catEntityManager.add(cat2);

        List<Cat> cats = catEntityManager.select().date().notEqualsTo(date).asList();

        //then
        assertThat(cats).hasSize(1);
        assertThat(cats.get(0).getShortName()).isEqualTo("Blob");
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
    public void shouldGetCatWithCustomDate_between(){
        //given
        Date now = new Date(System.currentTimeMillis());

        Cat cat1 = new Cat("Java",new Date(now.getTime()));
        Cat cat2 = new Cat("Blob",new Date(now.getTime()));
        Cat cat3 = new Cat("Baba",new Date(now.getTime() - 120 * 1000 * 1000));
        Cat cat4 = new Cat("Cece",new Date(now.getTime() + 120 * 1000 * 1000));

        //when
        catEntityManager.add(cat1);
        catEntityManager.add(cat2);
        catEntityManager.add(cat3);
        catEntityManager.add(cat4);

        List<Cat> cats = catEntityManager.select().date().between(new Date(now.getTime() - 60 * 1000 * 1000), new Date(now.getTime() + 60 * 1000 * 1000)).asList();

        //then
        assertThat(cats).hasSize(2);
        assertThat(cats.get(0).getShortName()).isEqualTo("Java");
        assertThat(cats.get(1).getShortName()).isEqualTo("Blob");
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

    @Test
    public void testDeleteCat() throws Exception{
        //given
        Cat cat1 = new Cat("toto");
        catEntityManager.add(cat1);
        assertThat(catEntityManager.count()).isEqualTo(1);
        assertThat(cat1.getId()).isAtLeast(1l);

        //when
        catEntityManager.delete(cat1);

        //then
        assertThat(catEntityManager.count()).isEqualTo(0);
    }

    @Test
    public void testDeleteCats() throws Exception{
        //given
        Cat cat1 = new Cat("toto");
        Cat cat2 = new Cat("tata");
        catEntityManager.add(Arrays.asList(cat1, cat2));
        assertThat(catEntityManager.count()).isEqualTo(2);
        assertThat(cat1.getId()).isAtLeast(1l);
        assertThat(cat2.getId()).isAtLeast(1l);

        //when
        catEntityManager.delete(Arrays.asList(cat1,cat2));

        //then
        assertThat(catEntityManager.count()).isEqualTo(0);
    }

}
