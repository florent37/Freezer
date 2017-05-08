package com.github.florent37.rxandroidorm.sample;

import com.github.florent37.rxandroidorm.sample.model.Cat;
import com.github.florent37.rxandroidorm.sample.model.CatDatabase;

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

    CatDatabase carEntityManager;

    @Before
    public void setUp() throws Exception {
        carEntityManager = spy(new CatDatabase());
        carEntityManager.deleteAll();
    }

    @Test
    public void shouldAddCatWithDate() {
        //given
        Date date = new Date(System.currentTimeMillis() - 60 * 1000);
        Cat cat = new Cat("Java", date);

        //when
        carEntityManager.add(cat).subscribe();

        //then
        carEntityManager.count().test().assertValue(1);
        assertThat(cat.getId()).isNotEqualTo(0);
    }

    @Test
    public void shouldAddCats_shouldGenerateIds() {
        //given
        final Cat java = new Cat("Java");
        final Cat bobo = new Cat("Bobo");
        final Cat sisi = new Cat("Sisi");
        final List<Cat> cats = Arrays.asList(java, bobo, sisi);

        //when
        carEntityManager.add(cats).subscribe();

        //then
        carEntityManager.count().test().assertValue(3);
        assertThat(java.getId()).isEqualTo(1);
        assertThat(bobo.getId()).isEqualTo(2);
        assertThat(sisi.getId()).isEqualTo(3);
    }

    @Test
    public void shouldUpdateCats_becauseAlreadyExists() {
        //given
        {
            final Cat java = new Cat(1, "Java");
            final Cat bobo = new Cat(2, "Bobo");
            final Cat sisi = new Cat(3, "Sisi");
            final List<Cat> cats = Arrays.asList(java, bobo, sisi);
            carEntityManager.add(cats).subscribe();
        }

        final Cat java = new Cat(1, "Java_e");

        //when
        carEntityManager.add(java).subscribe();

        //then
        carEntityManager.count().test().assertValue(3);
        final Cat cat = carEntityManager.select().id()
                .equalsTo(1l).first()
                .blockingFirst();

        assertThat(cat.getId()).isEqualTo(1);
        assertThat(cat.getShortName()).isEqualTo("Java_e");
    }

    @Test
    public void shouldGetCatWithAllFields() {
        //given
        Date date = new Date(System.currentTimeMillis() - 60 * 1000);
        Cat cat = new Cat("Java", date);

        //when
        carEntityManager.add(cat).subscribe();
        Cat catFromBase = carEntityManager.select()
                .first()
                .blockingFirst();

        //then
        assertThat(catFromBase).isNotNull();
        assertThat(catFromBase.getShortName()).isEqualTo("Java");
        assertThat(catFromBase.getDate().toString()).isEqualTo(date.toString());
    }

    @Test
    public void shouldGetCatWithCustomDate_equals() {
        //given
        Date date = new Date(System.currentTimeMillis() - 60 * 1000);

        Cat cat1 = new Cat("Java", date);
        Cat cat2 = new Cat("Blob", new Date(System.currentTimeMillis() + 60 * 1000));

        //when
        carEntityManager.add(cat1).subscribe();
        carEntityManager.add(cat2).subscribe();

        List<Cat> cats = carEntityManager.select().date().equalsTo(date).asObservable().blockingFirst();

        //then
        assertThat(cats).hasSize(1);
        assertThat(cats.get(0).getShortName()).isEqualTo("Java");
    }

    @Test
    public void shouldGetCatWithCustomDate_notEquals() {
        //given
        Date date = new Date(System.currentTimeMillis() - 60 * 1000);

        Cat cat1 = new Cat("Java", date);
        Cat cat2 = new Cat("Blob", new Date(System.currentTimeMillis() + 60 * 1000));

        //when
        carEntityManager.add(cat1).subscribe();
        carEntityManager.add(cat2).subscribe();

        List<Cat> cats = carEntityManager.select().date().notEqualsTo(date).asObservable().blockingFirst();

        //then
        assertThat(cats).hasSize(1);
        assertThat(cats.get(0).getShortName()).isEqualTo("Blob");
    }

    @Test
    public void shouldGetCatWithCustomDate_before() {
        //given
        Date now = new Date(System.currentTimeMillis());

        Cat cat1 = new Cat("Java", new Date(now.getTime() - 60 * 1000 * 1000));
        Cat cat2 = new Cat("Blob", new Date(now.getTime() + 60 * 1000 * 1000));

        //when
        carEntityManager.add(cat1).subscribe();
        carEntityManager.add(cat2).subscribe();

        List<Cat> cats = carEntityManager.select()
                .date()
                .before(now)
                .asObservable()
                .blockingFirst();

        //then
        assertThat(cats).hasSize(1);
        assertThat(cats.get(0).getShortName()).isEqualTo("Java");
    }

    @Test
    public void shouldGetCatWithCustomDate_after() {
        //given
        Date now = new Date(System.currentTimeMillis());

        Cat cat1 = new Cat("Java", new Date(now.getTime() - 60 * 1000 * 1000));
        Cat cat2 = new Cat("Blob", new Date(now.getTime() + 60 * 1000 * 1000));

        //when
        carEntityManager.add(cat1).subscribe();
        carEntityManager.add(cat2).subscribe();

        List<Cat> cats = carEntityManager.select().date().after(now).asObservable().blockingFirst();

        //then
        assertThat(cats).hasSize(1);
        assertThat(cats.get(0).getShortName()).isEqualTo("Blob");
    }

    @Test
    public void shouldGetCatWithCustomDate_between() {
        //given
        Date now = new Date(System.currentTimeMillis());

        Cat cat1 = new Cat("Java", new Date(now.getTime()));
        Cat cat2 = new Cat("Blob", new Date(now.getTime()));
        Cat cat3 = new Cat("Baba", new Date(now.getTime() - 120 * 1000 * 1000));
        Cat cat4 = new Cat("Cece", new Date(now.getTime() + 120 * 1000 * 1000));

        //when
        carEntityManager.add(cat1).subscribe();
        carEntityManager.add(cat2).subscribe();
        carEntityManager.add(cat3).subscribe();
        carEntityManager.add(cat4).subscribe();

        List<Cat> cats = carEntityManager.select().date().between(new Date(now.getTime() - 60 * 1000 * 1000), new Date(now.getTime() + 60 * 1000 * 1000)).asObservable().blockingFirst();

        //then
        assertThat(cats).hasSize(2);
        assertThat(cats.get(0).getShortName()).isEqualTo("Java");
        assertThat(cats.get(1).getShortName()).isEqualTo("Blob");
    }

    @Test
    public void testUpdateCat_onlyFields() throws Exception {
        //given
        Cat cat = new Cat("toto");
        carEntityManager.add(cat).subscribe();
        carEntityManager.count().test().assertValue(1);

        //when
        cat.setShortName("mimi");
        carEntityManager.update(cat).subscribe();

        //then
        carEntityManager.count().test().assertValue(1);
        Cat catFromBase = carEntityManager.select().first().blockingFirst();
        assertThat(catFromBase.getShortName()).isEqualTo("mimi");
    }

    @Test
    public void testDeleteCat() throws Exception {
        //given
        Cat cat1 = new Cat("toto");
        carEntityManager.add(cat1).subscribe();
        carEntityManager.count().test().assertValue(1);
        assertThat(cat1.getId()).isAtLeast(1l);

        //when
        carEntityManager.delete(cat1).subscribe();

        //then
        carEntityManager.count().test().assertValue(0);
    }

    @Test
    public void testDeleteCats() throws Exception {
        //given
        Cat cat1 = new Cat("toto");
        Cat cat2 = new Cat("tata");
        carEntityManager.add(Arrays.asList(cat1, cat2)).subscribe();
        carEntityManager.count().test().assertValue(2);
        assertThat(cat1.getId()).isAtLeast(1l);
        assertThat(cat2.getId()).isAtLeast(1l);

        //when
        carEntityManager.delete(Arrays.asList(cat1, cat2)).subscribe();

        //then
        carEntityManager.count().test().assertValue(0);
    }

}
