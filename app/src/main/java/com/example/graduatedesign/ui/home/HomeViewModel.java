package com.example.graduatedesign.ui.home;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.example.graduatedesign.data.MyRepository;
import com.example.graduatedesign.data.model.MyStudentActivity;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;

@HiltViewModel
public class HomeViewModel extends ViewModel {
    private CoroutineScope scope= ViewModelKt.getViewModelScope(this);
    private final MyRepository myRepository;

    @Inject
    public HomeViewModel(MyRepository myRepository) {
        this.myRepository = myRepository;
    }

    public Flowable<PagingData<MyStudentActivity>> getActivities(Integer size){
        return PagingRx.cachedIn(myRepository.getActivities(size),scope);
    }
}
