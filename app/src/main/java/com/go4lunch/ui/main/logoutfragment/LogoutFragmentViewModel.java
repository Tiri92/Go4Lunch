package com.go4lunch.ui.main.logoutfragment;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.go4lunch.di.DI;
import com.google.android.gms.tasks.Task;

public class LogoutFragmentViewModel extends ViewModel {

    public Task<Void> logout(Context context) {
        return DI.getFirestoreRepository().logout(context);
    }


}
