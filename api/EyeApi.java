package com.futuretech.eye.api;

import com.futuretech.eye.constants.Constants;
import com.futuretech.eye.models.ImageUploadResponse;
import com.futuretech.eye.models.VideoUploadResponse;
import com.futuretech.eye.oauth.OAuthAccessor;
import com.futuretech.eye.oauth.OAuthConsumer;
import com.futuretech.eye.oauth.OAuthException;
import com.futuretech.eye.oauth.OAuthMessage;
import com.futuretech.eye.preferences.ActiveSession;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.reactivex.Observable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public abstract interface TapTapSeeApi
{
  public static final String BASE_URL = "https://api.cloudsight.ai/";
  public static final String URL_PARAM_ALTITUDE = "altitude";
  public static final String URL_PARAM_DEVICE_ID = "device_id";
  public static final String URL_PARAM_LANGUAGE = "language";
  public static final String URL_PARAM_LATITUDE = "latitude";
  public static final String URL_PARAM_LOCALE = "locale";
  public static final String URL_PARAM_LONGITUDE = "longitude";
  
  @GET("v1/images/{token}")
  public abstract Observable<ImageUploadResponse> getImageInfo(@Path("token") String paramString, @Query("partial") boolean paramBoolean);
  
  @GET("v1/videos/{token}")
  public abstract Observable<VideoUploadResponse> getVideoInfo(@Path("token") String paramString, @Query("partial") boolean paramBoolean);
  
  @Multipart
  @POST("v1/images")
  public abstract Observable<ImageUploadResponse> uploadImage(@Part("image\"; filename=\"temp.jpg\" ") RequestBody paramRequestBody1, @Part("altitude") RequestBody paramRequestBody2, @Part("latitude") RequestBody paramRequestBody3, @Part("longitude") RequestBody paramRequestBody4, @Part("locale") RequestBody paramRequestBody5, @Part("language") RequestBody paramRequestBody6, @Part("device_id") RequestBody paramRequestBody7);
  
  @Multipart
  @POST("v1/videos")
  public abstract Observable<VideoUploadResponse> uploadVideo(@Part("video\"; filename=\"video.mp4\" ") RequestBody paramRequestBody1, @Part("altitude") RequestBody paramRequestBody2, @Part("latitude") RequestBody paramRequestBody3, @Part("longitude") RequestBody paramRequestBody4, @Part("locale") RequestBody paramRequestBody5, @Part("language") RequestBody paramRequestBody6, @Part("device_id") RequestBody paramRequestBody7);
  
  public static class Provider
  {
    private static String getAuthorizationHeader(String paramString1, String paramString2)
    {
      OAuthAccessor localOAuthAccessor = new OAuthAccessor(new OAuthConsumer(Constants.CONSUMERKEY, Constants.SECRETKEY));
      HashMap localHashMap = new HashMap();
      if (paramString1.contains("POST"))
      {
        localHashMap.put("device_id", ActiveSession.getUniqueDeviceId());
        localHashMap.put("altitude", ActiveSession.getAltitude());
        localHashMap.put("latitude", ActiveSession.getLatitude());
        localHashMap.put("longitude", ActiveSession.getLongitude());
        localHashMap.put("language", ActiveSession.getLanguage());
        localHashMap.put("locale", ActiveSession.getLocale());
      }
      paramString1 = new OAuthMessage(paramString1, paramString2, localHashMap.entrySet());
      try
      {
        paramString1.addRequiredParameters(localOAuthAccessor);
        paramString1 = paramString1.getAuthorizationHeader();
        return paramString1;
      }
      catch (URISyntaxException paramString1)
      {
        paramString1.printStackTrace();
      }
      catch (OAuthException paramString1)
      {
        paramString1.printStackTrace();
      }
      catch (IOException paramString1)
      {
        paramString1.printStackTrace();
      }
      return "";
    }
    
    public static TapTapSeeApi provideApi()
    {
      OkHttpClient.Builder localBuilder = new OkHttpClient.Builder();
      Object localObject = new HttpLoggingInterceptor();
      ((HttpLoggingInterceptor)localObject).setLevel(HttpLoggingInterceptor.Level.NONE);
      localBuilder.addInterceptor(new Interceptor()
      {
        public Response intercept(Interceptor.Chain paramAnonymousChain)
          throws IOException
        {
          Request localRequest = paramAnonymousChain.request();
          Request.Builder localBuilder = localRequest.newBuilder();
          localBuilder.addHeader("Authorization", TapTapSeeApi.Provider.getAuthorizationHeader(localRequest.method(), localRequest.url().toString()));
          localBuilder.addHeader("device_id", ActiveSession.getUniqueDeviceId());
          localBuilder.addHeader("altitude", ActiveSession.getAltitude());
          localBuilder.addHeader("latitude", ActiveSession.getLatitude());
          localBuilder.addHeader("longitude", ActiveSession.getLongitude());
          localBuilder.addHeader("language", ActiveSession.getLanguage());
          localBuilder.addHeader("locale", ActiveSession.getLocale());
          localBuilder.method(localRequest.method(), localRequest.body());
          return paramAnonymousChain.proceed(localBuilder.build());
        }
      });
      localBuilder.addInterceptor((Interceptor)localObject);
      localObject = new GsonBuilder().create();
      return (TapTapSeeApi)new Retrofit.Builder().baseUrl("https://api.cloudsight.ai/").client(localBuilder.build()).addConverterFactory(GsonConverterFactory.create((Gson)localObject)).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build().create(TapTapSeeApi.class);
    }
  }
}


/* Location:              C:\Users\elcot\Desktop\APP\dex2jar-2.0\classes-dex2jar.jar!\com\futuretech\eye\api\TapTapSeeApi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */