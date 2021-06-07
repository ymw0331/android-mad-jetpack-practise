package com.wayneyong.dogsApp.viewmodel;


import android.app.Application;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.wayneyong.dogsApp.model.DogBreed;
import com.wayneyong.dogsApp.model.DogDatabase;

public class DetailViewModel extends AndroidViewModel {

    public MutableLiveData<DogBreed> dogLiveData = new MutableLiveData<DogBreed>();
    private RetrieveDogTask task;


    public DetailViewModel(@androidx.annotation.NonNull Application application) {
        super(application);
    }

    public void fetch(int uuid) {
//        DogBreed dog1 = new DogBreed("1", "corgi", "15 years", "na", "na", "na", "na");
//        dogLiveData.setValue(dog1);

        task = new RetrieveDogTask();
        task.execute(uuid);
        Toast.makeText(getApplication(), "DogId: " + uuid + " data retrieved", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (task != null) {
            task.cancel(true);
            task = null;
        }
    }

    private class RetrieveDogTask extends AsyncTask<Integer, Void, DogBreed> {

        @Override
        protected DogBreed doInBackground(Integer... integers) {
            int uuid = integers[0];
            return DogDatabase.getInstance(getApplication()).dogDao().getDog(uuid);
        }

        @Override
        protected void onPostExecute(DogBreed dogBreed) {
            dogLiveData.setValue(dogBreed);
        }
    }

}
