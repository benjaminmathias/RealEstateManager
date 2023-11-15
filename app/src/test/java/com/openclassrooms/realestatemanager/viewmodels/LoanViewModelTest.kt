package com.openclassrooms.realestatemanager.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.viewmodel.LoanViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class LoanViewModelTest {

    private lateinit var  viewModel: LoanViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        viewModel = LoanViewModel()
    }

    @Test
    fun getEmptyLoanDataShouldReturnEmptyValue() = runTest {
        viewModel.loanData.value.amount = ""
        viewModel.loanData.value.rate = ""
        viewModel.loanData.value.duration = ""

        viewModel.computeMortgage()

        assertEquals("", viewModel.loanResult.value.monthlyPayment)
    }

    @Test
    fun getValidLoanDataShouldReturnCorrectValue() = runTest {
        viewModel.loanData.value.amount = "$350,000"
        viewModel.loanData.value.rate = "6"
        viewModel.loanData.value.duration = "25"

        viewModel.computeMortgage()

        assertEquals("$2,255 per months", viewModel.loanResult.value.monthlyPayment)
    }
}