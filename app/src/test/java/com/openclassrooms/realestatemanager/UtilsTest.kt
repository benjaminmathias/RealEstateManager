package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.utils.Utils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.util.*

class UtilsTest {

    private val utils = Utils()

    @Test fun convertDollarToEuroReturnExpectedValue() {
        assertEquals(utils.convertDollarToEuro(100), 94.0, 0.0)
    }

    @Test fun convertDollarToEuroReturnAssertionError() {
        assertNotEquals(utils.convertDollarToEuro(100), 100.0, 0.0)
    }

    @Test fun convertEuroToDollarReturnExpectedValue() {
        assertEquals(utils.convertEuroToDollar(100), 107.0, 0.0)
    }

    @Test fun convertEuroToDollarReturnAssertionError() {
        assertNotEquals(utils.convertDollarToEuro(100), 100.0, 0.0)
    }

    @Test fun getTodayDateReturnCorrectFormat() {
        val date = Date(1679843261210) // Milliseconds since 1970 still the 26 of March 2023

        val dateInString = utils.getTodayDate(date)

        assertEquals("26/03/2023", dateInString)
    }

    @Test fun getTodayDateReturnIncorrectFormat() {
        val date = Date(1679670461210) // Milliseconds since 1970 still the 24 of March 2023

        val dateInString = utils.getTodayDate(date)

        assertEquals("24/03/2023", dateInString)
    }
}