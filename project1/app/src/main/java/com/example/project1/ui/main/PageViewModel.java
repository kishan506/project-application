package com.example.project1.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {
    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, input -> generateTextForIndex(input));

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {
        return mText;
    }
//    get data from backend for running and completed
    private String generateTextForIndex(int index) {
        // Customize the text based on the tab index
        switch (index) {
            case 1:

                return "Content for Tab 1";
            case 2:
                return "Content for Tab 2";
            default:
                return "Default Content";
        }
    }
}
