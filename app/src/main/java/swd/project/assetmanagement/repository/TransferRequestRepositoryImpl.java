package swd.project.assetmanagement.repository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import swd.project.assetmanagement.api_util.CallbackData;
import swd.project.assetmanagement.api_util.RetrofitConfiguration;
import swd.project.assetmanagement.dto.ResponseListTransferRequest;
import swd.project.assetmanagement.dto.ResponseStatus;
import swd.project.assetmanagement.dto.ResponseTransferRequest;
import swd.project.assetmanagement.model.TransferRequest;

public class TransferRequestRepositoryImpl implements TransferRequestRepository{
    TransferRequestService service = RetrofitConfiguration.getRetrofitAdapter().create(TransferRequestService.class);
    @Override
    public void fetchTransferRequestToMe(int employeeId, final CallbackData<List<TransferRequest>> callback) {
        Call<ResponseListTransferRequest> call = service.getListTransferRequestToMe(employeeId);
        call.enqueue(new Callback<ResponseListTransferRequest>() {
            @Override
            public void onResponse(Call<ResponseListTransferRequest> call, Response<ResponseListTransferRequest> response) {
                ResponseListTransferRequest dto = response.body();
                if(dto != null && dto.getStatus().equals(ResponseStatus.OK.toString())){
                    callback.onSuccess(dto.getPayload());
                }
                else {
                    if(dto != null)
                        callback.onFail(dto.getStatus());
                    else
                        callback.onFail("Internal server error");
                }
            }

            @Override
            public void onFailure(Call<ResponseListTransferRequest> call, Throwable t) {
                    callback.onFail(t.getMessage());
            }
        });
    }

    @Override
    public void fetchTransferRequestFromMe(int employeeId,final CallbackData<List<TransferRequest>> callback) {
        Call<ResponseListTransferRequest> call = service.getListTransferRequestFromMe(employeeId);
        call.enqueue(new Callback<ResponseListTransferRequest>() {
            @Override
            public void onResponse(Call<ResponseListTransferRequest> call, Response<ResponseListTransferRequest> response) {
                ResponseListTransferRequest dto = response.body();
                if(dto != null && dto.getStatus().equals(ResponseStatus.OK.toString())){
                    callback.onSuccess(dto.getPayload());
                }
                else {
                    if(dto != null)
                        callback.onFail(dto.getStatus());
                    else
                        callback.onFail("Internal server error");
                }
            }

            @Override
            public void onFailure(Call<ResponseListTransferRequest> call, Throwable t) {
                callback.onFail(t.getMessage());
            }
        });
    }

    @Override
    public void CreateTransfer(TransferRequest transferRequest, final CallbackData<TransferRequest> callBack) {
        Call<ResponseTransferRequest> call = service.makeTrasnfer(transferRequest);
        call.enqueue(new Callback<ResponseTransferRequest>() {
            @Override
            public void onResponse(Call<ResponseTransferRequest> call, Response<ResponseTransferRequest> response) {
                ResponseTransferRequest dto = response.body();
                if(dto != null && dto.getStatus().equals(ResponseStatus.OK.toString())) {
                    callBack.onSuccess(dto.getPayload());
                }else {
                    callBack.onFail("Tranfer Error!!!");
                }
            }

            @Override
            public void onFailure(Call<ResponseTransferRequest> call, Throwable t) {
                if(t != null) {
                    callBack.onFail(t.getMessage());
                }
            }
        });
    }

    @Override
    public void editTransfer(long requestId, TransferRequest transferRequest, final CallbackData<TransferRequest> callBack) {
        Call<ResponseTransferRequest> dto = service.updateTranfer(requestId, transferRequest);
        dto.enqueue(new Callback<ResponseTransferRequest>() {
            @Override
            public void onResponse(Call<ResponseTransferRequest> call, Response<ResponseTransferRequest> response) {
                ResponseTransferRequest dto = response.body();
                if(dto != null && dto.getStatus().equals(ResponseStatus.OK.toString())) {
                    callBack.onSuccess(dto.getPayload());
                }else {
                    callBack.onFail("Update Tranfer Error!!!");
                }
            }

            @Override
            public void onFailure(Call<ResponseTransferRequest> call, Throwable t) {
                if(t != null) {
                    callBack.onFail(t.getMessage());
                }
            }
        });
    }
}
