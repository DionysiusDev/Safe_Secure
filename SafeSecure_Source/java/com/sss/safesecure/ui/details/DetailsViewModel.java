package com.sss.safesecure.ui.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DetailsViewModel extends ViewModel {

    private final MutableLiveData<String> WebsiteText, emailText, additionalText, pwText;

    public DetailsViewModel() {
        WebsiteText = new MutableLiveData<>();
        emailText = new MutableLiveData<>();
        additionalText = new MutableLiveData<>();
        pwText = new MutableLiveData<>();
    }

    public LiveData<String> getWsText() {
        return WebsiteText;
    }
    public LiveData<String> getEmText() {
        return emailText;
    }
    public LiveData<String> getAdText() {
        return additionalText;
    }
    public LiveData<String> getPwText() {
        return pwText;
    }
}