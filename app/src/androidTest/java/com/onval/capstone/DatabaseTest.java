package com.onval.capstone;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.onval.capstone.room.AppDatabase;
import com.onval.capstone.room.Category;
import com.onval.capstone.room.MyDao;
import com.onval.capstone.room.Record;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

class RecObserver implements Observer<List<Record>> {
    boolean isCalled = false;
    @Override
    public void onChanged(@Nullable List<Record> records) {
        isCalled = true;
    }
}

class CatObserver implements Observer<List<Category>> {
    boolean isCalled = false;
    @Override
    public void onChanged(@Nullable List<Category> records) {
        isCalled = true;
    }
}

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private MyDao mydao;
    private AppDatabase appDb;

    Observer<List<Category>> catObs;
    Observer<List<Record>> recObs;

    @Rule
    public TestRule taskExec = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Context context = InstrumentationRegistry.getTargetContext();
        appDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                    .allowMainThreadQueries().build();
        mydao = appDb.getDao();
    }

    @After
    public void closeDb() {
        appDb.close();
    }

    @Test
    public void testLoadCategories() {
        Category math = new Category(1, "math", "green", false);
        Category prog = new Category(2, "prog", "blue", false);

        mydao.insertCategories(math, prog);

        CatObserver obs = new CatObserver();

        LiveData<List<Category>> liveCategories = mydao.loadCategories();
        liveCategories.observeForever(obs);
        assertTrue(obs.isCalled);

        List<Category> categories = liveCategories.getValue();
        assertEquals(categories.size(), 2);
        assertEquals(categories.get(0).getId(), 1);
        assertEquals(categories.get(0).getName(), "math");
        assertEquals(categories.get(1).getId(), 2);
        assertEquals(categories.get(1).getColor(), "blue");
    }

    @Test
    public void testLoadRecordsFromCategory() {
        Category math = new Category(1, "math", "green", false);
        Category prog = new Category(2, "prog", "blue", false);
        mydao.insertCategories(math, prog);

        Record rec1 = new Record(1, "lesson math 1", 1);
        Record rec2 = new Record(2, "lesson prog 1", 2);
        Record rec3 = new Record(3, "lesson math 2", 1);
        mydao.insertRecords(rec1, rec2, rec3);

        RecObserver obs = new RecObserver();

        LiveData<List<Record>> liveRecords = mydao.loadRecordsFromCategory(math.getId());
        liveRecords.observeForever(obs);
        List<Record> records;

        assertTrue(obs.isCalled);
        records = liveRecords.getValue();
        assertTrue("Record can't be null", records != null);
        assertEquals(records.size(), 2);
        assertEquals(records.get(0).getName(), rec1.getName());
        assertEquals(records.get(1).getName(), rec3.getName());

        obs.isCalled = false;
        liveRecords.removeObserver(obs);
        liveRecords = mydao.loadRecordsFromCategory(prog.getId());
        liveRecords.observeForever(obs);

        assertTrue(obs.isCalled);
        records = liveRecords.getValue();
        assertTrue("Record can't be null", records != null);
        assertEquals(records.size(), 1);
        assertEquals(records.get(0).getName(), rec2.getName());
    }
}