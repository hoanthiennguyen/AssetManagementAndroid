package swd.project.assetmanagement.repository;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import swd.project.assetmanagement.api_util.ConfigApi;
import swd.project.assetmanagement.api_util.ResponseDTO;
import swd.project.assetmanagement.api_util.ResponseListAsset;
import swd.project.assetmanagement.model.Asset;

public interface AssetService {
    @GET(ConfigApi.GET_ALL_ASSETS)
    Call<ResponseListAsset> getAllAsset();
    @PUT(ConfigApi.EDIT_ASSET)
    Call<ResponseDTO> updateAsset(Asset asset);

}