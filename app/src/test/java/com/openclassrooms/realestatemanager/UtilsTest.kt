package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.utils.Utils
import org.junit.Assert.assertEquals
import org.junit.Test


class UtilsTest {

    private val utils = Utils()

    @Test fun convertDollarToEuroReturnExpectedValue() {
        assertEquals(utils.convertDollarToEuro(100), 94.0, 0.0)
    }

    @Test fun convertEuroToDollarReturnExpectedValue() {
        assertEquals(utils.convertEuroToDollar(100), 107.0, 0.0)
    }
}