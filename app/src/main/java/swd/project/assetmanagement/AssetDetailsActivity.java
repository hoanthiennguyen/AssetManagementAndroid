package swd.project.assetmanagement;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import swd.project.assetmanagement.adapter.StageListViewAdapter;
import swd.project.assetmanagement.api_util.CallbackData;
import swd.project.assetmanagement.model.Asset;
import swd.project.assetmanagement.model.AssetStatus;
import swd.project.assetmanagement.model.Employee;
import swd.project.assetmanagement.model.Firm;
import swd.project.assetmanagement.model.Location;
import swd.project.assetmanagement.model.LoginDTO;
import swd.project.assetmanagement.model.Stage;
import swd.project.assetmanagement.model.TransferRequest;
import swd.project.assetmanagement.presenter.AssetDetailsPresenter;
import swd.project.assetmanagement.presenter.EmployeeListPresenter;
import swd.project.assetmanagement.presenter.StageListPresenter;
import swd.project.assetmanagement.repository.TransferRequestRepositoryImpl;
import swd.project.assetmanagement.view.AssetDetailsView;
import swd.project.assetmanagement.view.EmployeeListView;
import swd.project.assetmanagement.view.LoadingView;
import swd.project.assetmanagement.view.StageListView;

public class AssetDetailsActivity extends AppCompatActivity implements LoadingView, AssetDetailsView, StageListView, PopupLocationDialog.HandleData, EmployeeListView {
    AssetDetailsPresenter assetDetailsPresenter;
    Asset asset;
    List<Stage> stageList;
    ListView stageListView;
    StageListViewAdapter stageListViewAdapter;
    StageListPresenter stageListPresenter;
    Spinner spinnerChangeStatus, spEmp;
    String[] items = AssetStatus.allValues;
    Stage currentStage;
    LoginDTO employee;

    List<Employee> listEmp;
    EmployeeListPresenter employeeListPresenter;
    TransferRequestRepositoryImpl transferRepo = new TransferRequestRepositoryImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_details);
        employee = (LoginDTO) getIntent().getSerializableExtra("employee");

        assetDetailsPresenter = new AssetDetailsPresenter(this, this);
        stageListPresenter = new StageListPresenter(this);
        Asset asset = (Asset) getIntent().getSerializableExtra("asset");
        if (asset != null) {
            this.asset = asset;
            renderView();
        } else {
            int id = getIntent().getIntExtra("assetId", 0);
            assetDetailsPresenter.fetchAssetDetails(id);
        }
    }

    private void renderView() {
        spinnerChangeStatus = findViewById(R.id.spinnerChangeStatus);
        spEmp = findViewById(R.id.spinnerTranfer);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinnerChangeStatus.setAdapter(adapter);
        ImageView assetImage = findViewById(R.id.assetImage);
        TextView assetName = findViewById(R.id.assetName);
        TextView assetType = findViewById(R.id.assetType);
        TextView assetLocation = findViewById(R.id.assetLocation);
        assetName.setText(asset.getName());
        assetType.setText(asset.getAssetType() != null ? asset.getAssetType().getName() : "Unassigned");
        assetImage.setImageResource(R.drawable.ic_tv_black_24dp);
        currentStage = asset.getCurrentStage();
        int position = Arrays.asList(items).indexOf(currentStage.getStatus());
        spinnerChangeStatus.setSelection(position);
        assetLocation.setText(currentStage.getLocation() != null ? currentStage.getLocation().toString() : "Unassigned");

        spinnerChangeStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentStage.setStatus(items[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stageList = new ArrayList<>();
        stageListView = findViewById(R.id.stageListView);
        stageListViewAdapter = new StageListViewAdapter(stageList);
        stageListView.setAdapter(stageListViewAdapter);
        stageListPresenter.fetchListStage(asset.getId());

        employeeListPresenter = new EmployeeListPresenter(this, this);
        employeeListPresenter.fetchListEmployeeFromServer(asset.getAssetType().getDepartment().getId());
    }


    public void onClickToggleHistory(View view) {

        if (stageListView.getVisibility() == View.INVISIBLE) {
            stageListView.setVisibility(View.VISIBLE);
            Button btn = (Button) view;
            btn.setText("Hide History");
        } else {
            stageListView.setVisibility(View.INVISIBLE);
            Button btn = (Button) view;
            btn.setText("Show History");
        }
    }

    public void onClickCancel(View view) {
        finish();
    }

    public void onClickSave(View view) {
        currentStage.setId(0L);
        currentStage.setFromDate(new Date(System.currentTimeMillis()).toString());
        stageListPresenter.addNewStage(asset.getId(), currentStage);
    }

    public void onClickChangeLocation(View view) {
        PopupLocationDialog locationDialog = new PopupLocationDialog(this, currentStage.getLocation());
        locationDialog.show(getSupportFragmentManager(), "ChangeLocation");
    }

    public void onClickBackToHome(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Dialog")
                .setMessage("Are you sure want to leave without saving changes?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    @Override
    public void getAssetDetailsSuccess(Asset asset) {
        this.asset = asset;
        renderView();
    }

    @Override
    public void getAssetDetailsFail(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }


    @Override
    public void onSuccessFetchStageList(List<Stage> stages) {
        Collections.reverse(stages);
        stageList.addAll(stages);
        stageListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailFetchStageList(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessAddNewStage(Stage stage) {
        Toast.makeText(this, "Current stage has been updated", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("updated", true);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onFailAddNewStage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void processData(Location selectedLocation) {
        currentStage.setLocation(selectedLocation);
        TextView assetLocation = findViewById(R.id.assetLocation);
        assetLocation.setText(currentStage.getLocation().toString());
    }

    public void clickToTranfer(View view) {

    }

    @Override
    public void onSuccessFetchListEmployee(List<Employee> employees) {
        listEmp = new ArrayList<>();
        listEmp.clear();
        Employee employee0 = new Employee();
        employee0.setFullName("Choose Employee");
        listEmp.add(0, employee0);
        listEmp.addAll(employees);
        final ArrayAdapter<Employee> adapterTranfer = new ArrayAdapter<Employee>(this, android.R.layout.simple_spinner_item, listEmp);
        adapterTranfer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEmp.setAdapter(adapterTranfer);
        spEmp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TransferRequest transferRequest = new TransferRequest();
                if (i != 0) {
                    String fromEmp = asset.getCurrentStage().getEmployee().getFullName();
                    String assetName = asset.getName();
                    transferRequest.setAsset(asset);
                    transferRequest.setFromEmployee(asset.getCurrentStage().getEmployee());
                    transferRequest.setToEmployee(listEmp.get(i));
                    transferRepo.CreateTransfer(transferRequest, new CallbackData<TransferRequest>() {
                        @Override
                        public void onSuccess(TransferRequest transferRequest) {
                            if (transferRequest != null) {
                                Toast.makeText(AssetDetailsActivity.this, "Transfer Success!!!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AssetDetailsActivity.this, "Transfer Fail!!!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFail(String msg) {
                            if (msg != "") {
                                Toast.makeText(AssetDetailsActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onFailureFetchListEmployee(String msg) {

    }
}
