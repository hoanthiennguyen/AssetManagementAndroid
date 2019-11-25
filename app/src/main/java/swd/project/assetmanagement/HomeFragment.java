package swd.project.assetmanagement;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import swd.project.assetmanagement.adapter.AssetListViewAdapter;
import swd.project.assetmanagement.model.Asset;
import swd.project.assetmanagement.presenter.AssetPresenter;
import swd.project.assetmanagement.view.AssetView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements AssetView {

    ListView listViewAsset;
    AssetListViewAdapter assetListViewAdapter;
    List<Asset> assetList;
    AssetPresenter assetPresenter;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        listViewAsset = view.findViewById(R.id.assetContainer);
        assetPresenter = new AssetPresenter(this);
        assetList = new ArrayList<>();
        assetListViewAdapter = new AssetListViewAdapter(assetList);
        listViewAsset.setAdapter(assetListViewAdapter);
        assetPresenter.requestDataFromServer();
        return view;
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void setDataToRecyclerView(List<Asset> movieArrayList) {
        assetList.addAll(movieArrayList);
        assetListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResponseFailure(Throwable throwable) {
        Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }
}