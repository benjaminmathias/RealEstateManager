package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.utils.UtilsKt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.util.Date

class UtilsKtTest {

    private val mUtilsKt = UtilsKt()

    @Test fun convertDollarToEuroReturnExpectedValue() {
        assertEquals(mUtilsKt.convertDollarToEuro(100), 94.0, 0.0)
    }

    @Test fun convertDollarToEuroReturnAssertionError() {
        assertNotEquals(mUtilsKt.convertDollarToEuro(100), 100.0, 0.0)
    }

    @Test fun convertEuroToDollarReturnExpectedValue() {
        assertEquals(mUtilsKt.convertEuroToDollar(100), 107.0, 0.0)
    }

    @Test fun convertEuroToDollarReturnAssertionError() {
        assertNotEquals(mUtilsKt.convertDollarToEuro(100), 100.0, 0.0)
    }

    @Test fun getTodayDateReturnCorrectFormat() {
        val date = Date(1679843261210) // Milliseconds since 1970 still the 26 of March 2023

        val dateInString = mUtilsKt.getTodayDate(date)

        assertEquals("26/03/2023", dateInString)
    }

    @Test fun getTodayDateReturnIncorrectFormat() {
        val date = Date(1679670461210) // Milliseconds since 1970 still the 24 of March 2023

        val dateInString = mUtilsKt.getTodayDate(date)

        assertEquals("24/03/2023", dateInString)
    }
}