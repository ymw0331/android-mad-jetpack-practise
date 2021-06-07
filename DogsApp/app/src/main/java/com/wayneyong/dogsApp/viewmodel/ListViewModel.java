package com.wayneyong.dogsApp.viewmodel;

import android.app.Application;
import android.app.Notification;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.wayneyong.dogsApp.model.DogBreed;
import com.wayneyong.dogsApp.model.DogDao;
import com.wayneyong.dogsApp.model.DogDatabase;
import com.wayneyong.dogsApp.model.DogsApiService;
import com.wayneyong.dogsApp.util.NotificationHelper;
import com.wayneyong.dogsApp.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

//one viewModel only correspond to one view, in this case list fragment
public class ListViewModel extends AndroidViewModel {

    //live data can be changed
    public MutableLiveData<List<DogBreed>> dogs = new MutableLiveData<List<DogBreed>>();
    public MutableLiveData<Boolean> dogLoadError = new MutableLiveData<Boolean>(); //return error message if server went wrong
    public MutableLiveData<Boolean> loading = new MutableLiveData<Boolean>(); //provide info whether it is being loaded

    private DogsApiService dogsService = new DogsApiService();
    private CompositeDisposable disposable = new CompositeDisposable(); //collect disposable single observer and dispose them whenever needed

    /*android system can kill application at any time, we don't want asyncTask to remain in background memory, not executed if app is destroyed
    make a asyncTask variable to maintain a variable*/
    private AsyncTask<List<DogBreed>, Void, List<DogBreed>> insertTask;

    private AsyncTask<Void, Void, List<DogBreed>> retriveTask;

    private SharedPreferencesHelper prefHelper = SharedPreferencesHelper.getInstance(getApplication());
    private long refreshTime = 5 * 60 * 1000 * 1000 * 1000L; //5 minutes * 60 minute * 1000 millisecond * 1000 microsecond * 1000 nanosecond, convert to *10-9 because system run at nanoseconds
    /*
     * refreshTime is how long the information to be considered valid
     * saveUpdateTime in SharedPreferenceHelper is last time we retrieved data from api
     * every time we need to do refresh we will check time by getUpdateTime in SPHelper against refreshTime, more than 5 min, we fetchFromRemote otherwise within fetchFromDatabase
     * */

    public ListViewModel(@NonNull Application application) {
        super(application);
    }

    public void refresh() {
        /* Mock datas
            DogBreed dog1 = new DogBreed("1", "corgi", "15 years", "", "", "", "");
            ArrayList<DogBreed> dogsList = new ArrayList<>();
            dogsList.add(dog3);*/
        //first retrieve data from web server then save into database
        checkCacheDuration(); //put in settings
        long updateTime = prefHelper.getUpdateTime();
        long currentTime = System.nanoTime();
        //if diff between currentTime and updateTime, we fetch from database else remote
        if (updateTime != 0 && currentTime - updateTime < refreshTime) { //within refreshTime
            fetchFromDatabase();
        } else {
            fetchFromRemote();
        }
    }

    // Automatically refresh the data from endpoint and bypass the cache
    public void refreshByPassCache() {
        fetchFromRemote();
        Toast.makeText(getApplication(), "Fetching from remote after refresh", Toast.LENGTH_LONG).show();
    }

    //settings.xml
    private void checkCacheDuration() {
        String cachePreference = prefHelper.getCacheDuration();

        if (!cachePreference.equals("")) {
            try {
                int cachePreferenceInt = Integer.parseInt(cachePreference);
                refreshTime = cachePreferenceInt * 1000 * 1000 * 1000L;
            } catch (NumberFormatException e) {
                e.printStackTrace(); //handling error for not enter number
            }
        }
    }

    /*
     *Switch between retrieving data from remote server or database gracefully to prevent (Shared Preferences)
     * (1) Inconsistent data
     * (2) Overuse device resources
     *
     * We use simple caching, we retrieve data from database from where we assume that particular data is valid for certain amount of time
     * If we do any request within the time, we get data from database, else after the time (expired), we get data from remote endpoint
     * */

    private void fetchFromDatabase() {

        loading.setValue(true);

        retriveTask = new RetrieveDogsTask();
        retriveTask.execute();
    }


    private void fetchFromRemote() {

        //when starting the process
        loading.setValue(true);

        disposable.add(
                dogsService.getDogs()
                        //get from single observable
                        .subscribeOn(Schedulers.newThread())
                        //android don't allow app to perform on main thread, we don't know how long it takes to fetch, we use another thread
                        .observeOn(AndroidSchedulers.mainThread())
                        //we do operation on new thread, but get result on main thread
                        .subscribeWith(new DisposableSingleObserver<List<DogBreed>>() {
                            //observer can be disposed of any point, app can be killed at any point, we don't want some object remain in memory
                            @Override
                            public void onSuccess(@io.reactivex.annotations.NonNull List<DogBreed> dogBreeds) {
                                //store data into database first
                                //store info into database, is not allowed on main thread, because we cannot how long operation to take, we need to do in background thread

                                //refer to asyncTask method below
                                insertTask = new InsertDogsTask();
                                insertTask.execute(dogBreeds);
                                Toast.makeText(getApplication(), "Dogs breed retrieved from web server endpoint", Toast.LENGTH_LONG).show();
                                Log.e("onSuccessFetchFromRemote", "Dogs breed retrieved from web server endpoint");

                                //pop up notification when sucessful access
                                NotificationHelper.getInstance(getApplication()).createNotification();
                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                //display error
                                dogLoadError.setValue(true);
                                loading.setValue(false);
                                e.printStackTrace();
                                Log.e("onErrorFetchFromRemote", e.getMessage());
                            }
                        })
        );
    }

    private void dogsRetrieved(List<DogBreed> dogList) {
        //get list of dog
        dogs.setValue(dogList);
        dogLoadError.setValue(false);
        loading.setValue(false);
//        Toast.makeText(getApplication(), "Database stored locally", Toast.LENGTH_LONG).show();
//        Log.e("dogsRetrieved", "Database stored locally");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();

        // when app is destroyed, prevent it from running in background, get rid of memory crash
        //get rid of it if insertTask got problem
        if (insertTask != null) {
            insertTask.cancel(true);
            insertTask = null;
        }

        if (retriveTask != null) {
            retriveTask.cancel(true);
            retriveTask = null;
        }
    }

    //whenever we do anything with the database, we do asyncTask, because android system don't allow main thread to do the operation
    //asyncTask, background thread can also access to main thread after done load
    //insertDogTask after retrieve data from web server
    private class InsertDogsTask extends AsyncTask<List<DogBreed>, Void, List<DogBreed>> {

        @Override
        protected List<DogBreed> doInBackground(List<DogBreed>... lists) {

            //perform operation on bg thread
            List<DogBreed> list = lists[0];
            DogDao dao = DogDatabase.getInstance(getApplication()).dogDao();
            dao.deleteAllDogs();
            //have a clean table before store database, otherwise more and more are stored

            ArrayList<DogBreed> newList = new ArrayList<>(list);
            //insert info into database, convert list into dogBreed object
            //list of uuid result
            List<Long> result = dao.insertAll(newList.toArray(new DogBreed[0]));

            //update result with identifier
            //go through whole list and attach result to uuid
            int i = 0;
            while (i < list.size()) {
                list.get(i).uuid = result.get(i).intValue();
                ++i;
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<DogBreed> dogBreeds) {
            //main thread access after done background thread
            //execute on foreground thread
            //call dogBreed function to update the interface
            dogsRetrieved(dogBreeds);
            prefHelper.saveUpdateTime(System.nanoTime()); //save the last time we fetch data from API
        }
    }

    //get data from database
    private class RetrieveDogsTask extends AsyncTask<Void, Void, List<DogBreed>> {

        @Override
        protected List<DogBreed> doInBackground(Void... voids) {
            return DogDatabase.getInstance(getApplication()).dogDao().getAllDogs();
        }

        @Override
        protected void onPostExecute(List<DogBreed> dogBreeds) {
            dogsRetrieved(dogBreeds);
            Toast.makeText(getApplication(), "Dogs breed retrieved from database", Toast.LENGTH_LONG).show();
        }
    }
}
