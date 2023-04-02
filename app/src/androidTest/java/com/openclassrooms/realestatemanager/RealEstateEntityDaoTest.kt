package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.openclassrooms.realestatemanager.data.RealEstateDao
import com.openclassrooms.realestatemanager.data.RealEstateDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.Executors

class RealEstateEntityDaoTest {

    private lateinit var database: RealEstateDatabase
    private lateinit var dao: RealEstateDao

    private val testDispatcher = StandardTestDispatcher()

   // private val estate1 = RealEstateEntity("Maison 1","Maison",100,100000,10,"Belle maison","L'adresse")
   // private val estate2 = RealEstateEntity("Maison 2","Maison",100,100000,10,"Belle maison","L'adresse")
  //  private val estate3 = RealEstateEntity("Maison 3","Maison",100,100000,10,"Belle maison","L'adresse")

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {

        Dispatchers.setMain(testDispatcher)
        this.database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RealEstateDatabase::class.java
        )
            .allowMainThreadQueries()
            .setTransactionExecutor(Executors.newSingleThreadExecutor())
            .build()

        dao = database.realEstateDao()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun closeDatabase() {
        database.close()
        Dispatchers.resetMain()

    }

 /*  @Test
    fun testInsertNewEstate() = runBlocking {
        dao.insert(estate1)
        val estate = dao.getAllRealEstateFlow().collect(it)

        assertEquals(estate.name, estate1.name)
    }*/

    @Test
    fun testInsertAndGetSpecificNewEstate() = runBlocking {
      //  dao.insert(estate1)
      //  dao.insert(estate2)
      //  dao.insert(estate3)

      //  val expectedEstate = dao.getSpecificRealEstate(2)

       // assertEquals(estate2.name, expectedEstate.)

    }
}