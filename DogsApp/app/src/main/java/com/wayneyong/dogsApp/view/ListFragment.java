package com.wayneyong.dogsApp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wayneyong.dogsApp.R;
import com.wayneyong.dogsApp.databinding.FragmentListBinding;
import com.wayneyong.dogsApp.model.DogBreed;
import com.wayneyong.dogsApp.viewmodel.ListViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    FragmentListBinding mBinding;
    private ListViewModel viewModel;
    private DogsListAdapter dogsListAdapter = new DogsListAdapter(new ArrayList<>());

    public ListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentListBinding.inflate(inflater, container, false);
        View view = mBinding.getRoot();
        setHasOptionsMenu(true); //set the menu item
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        ListFragmentDirections.ActionDetail action = ListFragmentDirections.actionDetail();
//        Navigation.findNavController(view).navigate(action);

        //        viewModel = ViewModelProvider.of(this).get(ListViewModel.class);
        viewModel = new ViewModelProvider(this).get(ListViewModel.class);
        viewModel.refresh();

        //bind with view element
        mBinding.dogsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.dogsList.setAdapter(dogsListAdapter);
        observeViewModel();

        mBinding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBinding.dogsList.setVisibility(View.GONE);
                mBinding.listError.setVisibility(View.GONE);
                mBinding.loadingView.setVisibility(View.VISIBLE);
                viewModel.refreshByPassCache();
                mBinding.refreshLayout.setRefreshing(false);
            }
        });
    }

    private void observeViewModel() {
        //live datas are observable
        viewModel.dogs.observe(getViewLifecycleOwner(), new Observer<List<DogBreed>>() {
            @Override
            public void onChanged(List<DogBreed> dogs) {
                if (dogs != null && dogs instanceof List) {
                    mBinding.dogsList.setVisibility(View.VISIBLE);
                    dogsListAdapter.updateDogsList(dogs);
                }
            }
        });

        viewModel.dogLoadError.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isError) {
                if (isError != null && isError instanceof Boolean) {
                    mBinding.listError.setVisibility(isError ? View.VISIBLE : View.GONE);
                }
            }
        });

        viewModel.loading.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (isLoading != null && isLoading instanceof Boolean) {
                    mBinding.loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                    if (isLoading) {
                        mBinding.listError.setVisibility(View.GONE);
                        mBinding.dogsList.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSettings: {
                if (isAdded()) {
                    Navigation.findNavController(getView()).navigate(ListFragmentDirections.actionSettings());
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}