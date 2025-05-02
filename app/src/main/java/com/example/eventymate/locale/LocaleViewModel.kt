package com.example.eventymate.locale

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.util.Locale

class LocaleViewModel : ViewModel() {
    private val _currentLanguage = mutableStateOf(Locale.getDefault().language)
    val currentLanguage: State<String> = _currentLanguage

    fun setLanguage(language: String) {
        _currentLanguage.value = language
    }
}