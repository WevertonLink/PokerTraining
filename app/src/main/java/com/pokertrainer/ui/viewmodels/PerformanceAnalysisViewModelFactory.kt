package com.pokertrainer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pokertrainer.domain.usecases.GeneratePerformanceAnalysisUseCase

class PerformanceAnalysisViewModelFactory(
    private val generatePerformanceAnalysisUseCase: GeneratePerformanceAnalysisUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerformanceAnalysisViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PerformanceAnalysisViewModel(generatePerformanceAnalysisUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
