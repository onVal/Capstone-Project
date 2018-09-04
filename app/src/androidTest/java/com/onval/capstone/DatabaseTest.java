package com.onval.capstone;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Context;
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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private MyDao mydao;
    private AppDatabase appDb;

    @Mock
    private Observer<List<Category>> catObs;

    @Mock
    private Observer<List<Record>> recObs;

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

        LiveData<List<Category>> liveCategories = mydao.loadCategories();
        liveCategories.observeForever(catObs);
        verify(catObs).onChanged(any(List.class));

        List<Category> categories = liveCategories.getValue();
        assertTrue("Categories can't be null", categories != null);
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

        LiveData<List<Record>> liveRecords = mydao.loadRecordsFromCategory(math.getId());
        liveRecords.observeForever(recObs);
        verify(recObs).onChanged(any(List.class));

        List<Record> records = liveRecords.getValue();
        assertTrue("Record can't be null", records != null);
        assertEquals(records.size(), 2);
        assertEquals(records.get(0).getName(), rec1.getName());
        assertEquals(records.get(1).getName(), rec3.getName());

        liveRecords = mydao.loadRecordsFromCategory(prog.getId());
        liveRecords.observeForever(recObs);

        records = liveRecords.getValue();
        assertTrue("Record can't be null", records != null);
        assertEquals(records.size(), 1);
        assertEquals(records.get(0).getName(), rec2.getName());
    }

    @Test
    public void testNumberOfRecordingsInCategory() {
        Category math = new Category(1, "math", "green", false);
        Category prog = new Category(2, "prog", "blue", false);
        mydao.insertCategories(math, prog);

        Record rec1 = new Record(1, "lesson math 1", 1);
        Record rec2 = new Record(2, "lesson prog 1", 2);
        Record rec3 = new Record(3, "lesson math 2", 1);
        mydao.insertRecords(rec1, rec2, rec3);

        assertEquals(mydao.numberOfRecordingsInCategory(math.getId()), 2);
        assertEquals(mydao.numberOfRecordingsInCategory(prog.getId()), 1);
    }
}