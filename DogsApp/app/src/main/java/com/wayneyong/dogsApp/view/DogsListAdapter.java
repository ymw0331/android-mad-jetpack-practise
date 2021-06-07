package com.wayneyong.dogsApp.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.wayneyong.dogsApp.R;
import com.wayneyong.dogsApp.databinding.ItemDogBinding;
import com.wayneyong.dogsApp.model.DogBreed;
import com.wayneyong.dogsApp.util.Util;

import java.util.ArrayList;
import java.util.List;

public class DogsListAdapter extends RecyclerView.Adapter<DogsListAdapter.DogViewHolder> implements DogClickListener {

    private ArrayList<DogBreed> dogsList;

    public DogsListAdapter(ArrayList<DogBreed> dogsList) {
        this.dogsList = dogsList;
    }

    public void updateDogsList(List<DogBreed> newDogsList) {
        dogsList.clear();
        dogsList.addAll(newDogsList);
        notifyDataSetChanged(); //tell system data change and redo the whole list
    }


    @NonNull
    @Override
    public DogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //need to return the viewHolder created at the bottom
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemDogBinding view = DataBindingUtil.inflate(inflater, R.layout.item_dog, parent, false);
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dog, parent, false);
        return new DogViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull DogViewHolder holder, int position) {
        holder.itemView.setDog(dogsList.get(position));
        holder.itemView.setListener(this::onDogClicked);

        //attach info form dogList to viewHolder, populate the items/id found in itemView
//        ImageView image = holder.itemView.findViewById(R.id.imageView);
//        TextView name = holder.itemView.findViewById(R.id.name);
//        TextView lifespan = holder.itemView.findViewById(R.id.lifespan);
//        LinearLayout layout = holder.itemView.findViewById(R.id.dogLayout);
//
//        name.setText(dogsList.get(position).dogBreed);
//        lifespan.setText(dogsList.get(position).lifeSpan);
//        //Glide from Util
//        Util.loadImage(image, dogsList.get(position).imageUrl, Util.getProgressDrawable(image.getContext()));
//
//        //dog item cardView
//        layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Move to detail screen
//                ListFragmentDirections.ActionDetail action = ListFragmentDirections.actionDetail();
//                action.setDogUuid(dogsList.get(position).uuid);
//                Navigation.findNavController(layout).navigate(action);
//            }
//        });
    }

    @Override
    public void onDogClicked(View v) {

        //get the uuid from layout
        String uuidString = ((TextView) v.findViewById(R.id.dogId)).getText().toString();
        int uuid = Integer.valueOf(uuidString);
        ListFragmentDirections.ActionDetail action = ListFragmentDirections.actionDetail();
        action.setDogUuid(uuid);
        Navigation.findNavController(v).navigate(action);

    }


    @Override
    public int getItemCount() {
        //it tell system how many view holder needed to be created
        return dogsList.size();
    }


    class DogViewHolder extends RecyclerView.ViewHolder {

//        public View itemView;
//
//        public DogViewHolder(@NonNull View itemView) {
//            super(itemView);
//            this.itemView = itemView;
//        }

        public ItemDogBinding itemView;

        public DogViewHolder(@NonNull ItemDogBinding itemView) {
            super(itemView.getRoot());
            this.itemView = itemView;
        }
    }
}
