package com.futuretech.eye.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog.Builder;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.hardware.Camera;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video.Media;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.common.SharedPrefsUtil;
import com.crashlytics.android.Crashlytics;
import com.futuretech.eye.api.TapTapSeeApi;
import com.futuretech.eye.appirater.Appirater;
import com.futuretech.eye.application.TapTapSeeApplication;
import com.futuretech.eye.fragment.CameraFragment;
import com.futuretech.eye.fragment.HowToDialogFragment;
import com.futuretech.eye.fragment.HowToDialogFragment.FragmentAlertDialog;
import com.futuretech.eye.fragment.PolicyDialogFragment.FragmentAlertDialog;
import com.futuretech.eye.fragment.WarnDialogFragment;
import com.futuretech.eye.fragment.WarnDialogFragment.FragmentAlertDialog;
import com.futuretech.eye.listener.CameraCallback;
import com.futuretech.eye.models.ChildVideoResponse;
import com.futuretech.eye.models.ImageUploadResponse;
import com.futuretech.eye.models.VideoUploadResponse;
import com.futuretech.eye.preferences.ActiveSession;
import com.futuretech.eye.util.IntentHelper;
import com.futuretech.eye.util.SystemUiHider;
import com.futuretech.eye.util.SystemUiHider.OnVisibilityChangeListener;
import com.futuretech.eye.util.Utils;
import com.google.android.gms.analytics.HitBuilders.EventBuilder;
import com.google.android.gms.analytics.Tracker;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TaptapseeActivity
  extends Activity
  implements CameraCallback, PolicyDialogFragment.FragmentAlertDialog, WarnDialogFragment.FragmentAlertDialog, HowToDialogFragment.FragmentAlertDialog
{
  private static final int HIDER_FLAGS = 6;
  private static final int MAX_QUERY_COUNT = 3;
  public static final int REQUEST_LOAD_IMAGE = 11010;
  public static final int REQUEST_LOAD_VIDEO = 11011;
  public static final String SHOT_STRING_NULL = "null";
  public static final int VIDEO_ID = -1;
  public static final int VIDEO_REQUEST_DELAY = 1000;
  View contentView;
  View controlsView;
  private ExecutorService executor = Executors.newFixedThreadPool(1);
  private TextView firstTimeDescription;
  private Set<Integer> imageIds;
  private boolean isTalkBackEnabled;
  private File lastImage;
  private int lastImageId;
  private LocationManager locationManager;
  private String locationProvider;
  int mControlsHeight;
  int mShortAnimTime;
  private SystemUiHider mSystemUiHider;
  private int navigationBarHeight;
  private Button repeatButton;
  private String searchText;
  private Button shareButton;
  private int statusBarHeight;
  private TextView tapResultText;
  public boolean videoInProgress;
  
  private void announceFirstLaunch()
  {
    new Handler().postDelayed(new Runnable()
    {
      public void run()
      {
        TaptapseeActivity.this.runOnUiThread(new Runnable()
        {
          public void run()
          {
            TaptapseeActivity.this.firstTimeDescription.setContentDescription(TaptapseeActivity.this.getString(2131624033));
            if (Build.VERSION.SDK_INT >= 16)
            {
              TaptapseeActivity.this.firstTimeDescription.announceForAccessibility(TaptapseeActivity.this.getString(2131624033));
              return;
            }
            TaptapseeActivity.this.firstTimeDescription.requestFocus();
          }
        });
      }
    }, 1000L);
  }
  
  private void createCameraFragment()
  {
    FragmentTransaction localFragmentTransaction = getFragmentManager().beginTransaction();
    localFragmentTransaction.add(2131230774, new CameraFragment());
    localFragmentTransaction.commit();
  }
  
  private int getNavigationBarHeight()
  {
    int i = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
    if (i > 0) {
      return getResources().getDimensionPixelSize(i);
    }
    return 0;
  }
  
  private String getReason(String paramString)
  {
    int i = paramString.hashCode();
    if (i != -1385971474)
    {
      if (i != -1380798726)
      {
        if (i != 3075958)
        {
          if ((i == 94756344) && (paramString.equals("close")))
          {
            i = 3;
            break label90;
          }
        }
        else if (paramString.equals("dark"))
        {
          i = 1;
          break label90;
        }
      }
      else if (paramString.equals("bright"))
      {
        i = 2;
        break label90;
      }
    }
    else if (paramString.equals("blurry"))
    {
      i = 0;
      break label90;
    }
    i = -1;
    switch (i)
    {
    default: 
      return getString(2131624022);
    case 3: 
      return getString(2131624081);
    case 2: 
      return getString(2131624080);
    case 1: 
      label90:
      return getString(2131624082);
    }
    return getString(2131624079);
  }
  
  private int getStatusBarHeight()
  {
    int i = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (i > 0) {
      return getResources().getDimensionPixelSize(i);
    }
    return 0;
  }
  
  private boolean isTalkBackEnabled()
  {
    if (Build.VERSION.SDK_INT >= 16)
    {
      localObject1 = (AccessibilityManager)getSystemService("accessibility");
      bool1 = ((AccessibilityManager)localObject1).isEnabled();
      boolean bool2 = ((AccessibilityManager)localObject1).isTouchExplorationEnabled();
      return (bool1) && (bool2);
    }
    Object localObject1 = new Intent("android.accessibilityservice.AccessibilityService");
    ((Intent)localObject1).addCategory("android.accessibilityservice.category.FEEDBACK_SPOKEN");
    Object localObject2 = getPackageManager().queryIntentServices((Intent)localObject1, 0);
    localObject1 = getContentResolver();
    ArrayList localArrayList = new ArrayList();
    Object localObject3 = ((ActivityManager)getSystemService("activity")).getRunningServices(Integer.MAX_VALUE).iterator();
    while (((Iterator)localObject3).hasNext()) {
      localArrayList.add(((ActivityManager.RunningServiceInfo)((Iterator)localObject3).next()).service.getPackageName());
    }
    localObject2 = ((List)localObject2).iterator();
    boolean bool1 = false;
    for (;;)
    {
      if (!((Iterator)localObject2).hasNext()) {
        return bool1;
      }
      localObject3 = (ResolveInfo)((Iterator)localObject2).next();
      Object localObject4 = new StringBuilder();
      ((StringBuilder)localObject4).append("content://");
      ((StringBuilder)localObject4).append(((ResolveInfo)localObject3).serviceInfo.packageName);
      ((StringBuilder)localObject4).append(".providers.StatusProvider");
      localObject4 = ((ContentResolver)localObject1).query(Uri.parse(((StringBuilder)localObject4).toString()), null, null, null, null);
      if ((localObject4 != null) && (((Cursor)localObject4).moveToFirst()))
      {
        int i = ((Cursor)localObject4).getInt(0);
        ((Cursor)localObject4).close();
        if (i != 1) {
          break;
        }
        bool1 = true;
        continue;
      }
      bool1 = localArrayList.contains(((ResolveInfo)localObject3).serviceInfo.packageName);
    }
    return bool1;
  }
  
  private void onKeywordsResponse(int paramInt, String paramString, boolean paramBoolean)
  {
    this.imageIds.remove(Integer.valueOf(paramInt));
    if (paramBoolean)
    {
      StringBuilder localStringBuilder;
      if (paramInt == -1)
      {
        localStringBuilder = new StringBuilder();
        localStringBuilder.append(getString(2131624089));
        localStringBuilder.append(" ");
        localStringBuilder.append(paramString);
        setResultTextDescription(localStringBuilder.toString());
        this.repeatButton.setEnabled(true);
        this.repeatButton.setTextColor(getResources().getColor(17170443));
        return;
      }
      if (!TextUtils.isEmpty(paramString))
      {
        localStringBuilder = new StringBuilder();
        localStringBuilder.append(getString(2131624035, new Object[] { Integer.valueOf(paramInt) }));
        localStringBuilder.append(" ");
        localStringBuilder.append(paramString);
        setResultTextDescription(localStringBuilder.toString());
        this.repeatButton.setEnabled(true);
        this.repeatButton.setTextColor(getResources().getColor(17170443));
        if (paramInt == this.lastImageId)
        {
          this.searchText = paramString;
          this.shareButton.setEnabled(true);
          this.shareButton.setTextColor(getResources().getColor(17170443));
        }
      }
    }
  }
  
  private void onReachMaxRequests()
  {
    Log.d("TaptapseeActivity", "MAX_QUERY count reached!");
    Toast.makeText(this, getResources().getString(2131624039, new Object[] { Integer.valueOf(3) }), 1).show();
  }
  
  private void setResultTextDescription(CharSequence paramCharSequence)
  {
    this.tapResultText.setText(paramCharSequence);
    this.tapResultText.setContentDescription(paramCharSequence);
    this.tapResultText.sendAccessibilityEvent(32768);
  }
  
  private void setViewMarginVertical(View paramView, int paramInt1, int paramInt2)
  {
    RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams)paramView.getLayoutParams();
    if (localLayoutParams != null)
    {
      localLayoutParams.setMargins(0, paramInt1, 0, paramInt2);
      paramView.setLayoutParams(localLayoutParams);
    }
  }
  
  private void showHowToDialog()
  {
    HowToDialogFragment.newInstance(2131624032, 2131624033, 17301569).show(getFragmentManager(), "How To Use TapTapSee");
  }
  
  private void startPictureTag(final int paramInt, String paramString1, String paramString2)
  {
    if ((!TextUtils.isEmpty(paramString1)) && (!TextUtils.isEmpty(paramString2)))
    {
      paramString2 = new StringBuilder();
      paramString2.append("Photo token:  ");
      paramString2.append(paramString1);
      Log.d("test_tag", paramString2.toString());
      TapTapSeeApplication.getApi().getImageInfo(paramString1, false).subscribeOn(Schedulers.from(this.executor)).repeat(5000L).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer()
      {
        private Disposable disposable;
        
        public void onComplete() {}
        
        public void onError(Throwable paramAnonymousThrowable)
        {
          TaptapseeActivity.this.setResultTextDescription(TaptapseeActivity.this.getString(2131624043));
        }
        
        public void onNext(ImageUploadResponse paramAnonymousImageUploadResponse)
        {
          String str2 = "";
          String str1 = paramAnonymousImageUploadResponse.getStatus();
          switch (str1.hashCode())
          {
          default: 
            break;
          case 2147444528: 
            if (str1.equals("skipped")) {
              i = 1;
            }
            break;
          case 2139330878: 
            if (str1.equals("not completed")) {
              i = 0;
            }
            break;
          case 1446940360: 
            if (str1.equals("in progress")) {
              i = 2;
            }
            break;
          case -1313911455: 
            if (str1.equals("timeout")) {
              i = 4;
            }
            break;
          case -1402931637: 
            if (str1.equals("completed")) {
              i = 3;
            }
            break;
          }
          int i = -1;
          str1 = str2;
          switch (i)
          {
          default: 
            str1 = str2;
            break;
          case 4: 
            str1 = "High demand, please retry later";
            this.disposable.dispose();
            break;
          case 3: 
            str1 = paramAnonymousImageUploadResponse.getName();
            this.disposable.dispose();
            break;
          case 2: 
            str1 = paramAnonymousImageUploadResponse.getName();
            break;
          case 1: 
            str1 = TaptapseeActivity.this.getReason(paramAnonymousImageUploadResponse.getReason());
            this.disposable.dispose();
          }
          if (!paramAnonymousImageUploadResponse.getStatus().contains("not completed")) {
            TaptapseeActivity.this.onKeywordsResponse(paramInt, str1, this.disposable.isDisposed());
          }
        }
        
        public void onSubscribe(Disposable paramAnonymousDisposable)
        {
          this.disposable = paramAnonymousDisposable;
        }
      });
    }
  }
  
  private void startVideoTag(String paramString1, String paramString2)
  {
    if ((!TextUtils.isEmpty(paramString1)) && (!TextUtils.isEmpty(paramString2))) {
      TapTapSeeApplication.getApi().getVideoInfo(paramString1, false).subscribeOn(Schedulers.from(this.executor)).repeat(5000L).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer()
      {
        private Disposable disposable;
        
        public void onComplete() {}
        
        public void onError(Throwable paramAnonymousThrowable)
        {
          TaptapseeActivity.this.setResultTextDescription(TaptapseeActivity.this.getString(2131624043));
        }
        
        public void onNext(VideoUploadResponse paramAnonymousVideoUploadResponse)
        {
          String str2 = "";
          if (paramAnonymousVideoUploadResponse.getChildren().size() != 0) {
            str1 = ((ChildVideoResponse)paramAnonymousVideoUploadResponse.getChildren().get(0)).getStatus();
          } else {
            str1 = paramAnonymousVideoUploadResponse.getStatus();
          }
          switch (str1.hashCode())
          {
          default: 
            break;
          case 2147444528: 
            if (str1.equals("skipped")) {
              i = 1;
            }
            break;
          case 2139330878: 
            if (str1.equals("not completed")) {
              i = 0;
            }
            break;
          case 1446940360: 
            if (str1.equals("in progress")) {
              i = 2;
            }
            break;
          case -1313911455: 
            if (str1.equals("timeout")) {
              i = 4;
            }
            break;
          case -1402931637: 
            if (str1.equals("completed")) {
              i = 3;
            }
            break;
          }
          int i = -1;
          String str1 = str2;
          switch (i)
          {
          default: 
            str1 = str2;
            break;
          case 4: 
            str1 = "High demand, please retry later";
            this.disposable.dispose();
            break;
          case 3: 
            str1 = TextUtils.join(", ", paramAnonymousVideoUploadResponse.getChildren());
            this.disposable.dispose();
            break;
          case 2: 
            str1 = TextUtils.join(", ", paramAnonymousVideoUploadResponse.getChildren());
            break;
          case 1: 
            str1 = TaptapseeActivity.this.getReason(((ChildVideoResponse)paramAnonymousVideoUploadResponse.getChildren().get(0)).getReason());
            this.disposable.dispose();
          }
          if (!paramAnonymousVideoUploadResponse.getStatus().contains("not completed")) {
            TaptapseeActivity.this.onKeywordsResponse(-1, str1, this.disposable.isDisposed());
          }
        }
        
        public void onSubscribe(Disposable paramAnonymousDisposable)
        {
          this.disposable = paramAnonymousDisposable;
        }
      });
    }
  }
  
  private void updateActiveLocation(Location paramLocation)
  {
    ActiveSession.setPrefLatitude(paramLocation.getLatitude());
    ActiveSession.setPrefLongitude(paramLocation.getLongitude());
    ActiveSession.setPrefAltitude(paramLocation.getAltitude());
  }
  
  private void uploadImageRequest(byte[] paramArrayOfByte)
  {
    TapTapSeeApplication.getApi().uploadImage(RequestBody.create(MediaType.parse("image/*"), paramArrayOfByte), RequestBody.create(MediaType.parse("text/plain"), ActiveSession.getAltitude()), RequestBody.create(MediaType.parse("text/plain"), ActiveSession.getLatitude()), RequestBody.create(MediaType.parse("text/plain"), ActiveSession.getLongitude()), RequestBody.create(MediaType.parse("text/plain"), ActiveSession.getLocale()), RequestBody.create(MediaType.parse("text/plain"), ActiveSession.getLanguage()), RequestBody.create(MediaType.parse("text/plain"), ActiveSession.getUniqueDeviceId())).subscribeOn(Schedulers.from(this.executor)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer()
    {
      public void onComplete() {}
      
      public void onError(Throwable paramAnonymousThrowable)
      {
        TaptapseeActivity.this.setResultTextDescription(TaptapseeActivity.this.getString(2131624043));
      }
      
      public void onNext(ImageUploadResponse paramAnonymousImageUploadResponse)
      {
        TaptapseeActivity.this.startPictureTag(TaptapseeActivity.this.lastImageId, paramAnonymousImageUploadResponse.getToken(), paramAnonymousImageUploadResponse.getUrl());
        Log.d("uploadImage url", paramAnonymousImageUploadResponse.getUrl());
      }
      
      public void onSubscribe(Disposable paramAnonymousDisposable) {}
    });
  }
  
  private void uploadVideoRequest(final File paramFile)
  {
    if (MediaPlayer.create(this, Uri.fromFile(new File(paramFile.toString()))) != null)
    {
      TapTapSeeApplication.getApi().uploadVideo(RequestBody.create(MediaType.parse("video/*"), paramFile), RequestBody.create(MediaType.parse("text/plain"), ActiveSession.getAltitude()), RequestBody.create(MediaType.parse("text/plain"), ActiveSession.getLatitude()), RequestBody.create(MediaType.parse("text/plain"), ActiveSession.getLongitude()), RequestBody.create(MediaType.parse("text/plain"), ActiveSession.getLocale()), RequestBody.create(MediaType.parse("text/plain"), ActiveSession.getLanguage()), RequestBody.create(MediaType.parse("text/plain"), ActiveSession.getUniqueDeviceId())).subscribeOn(Schedulers.from(this.executor)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer()
      {
        String url;
        
        public void onComplete()
        {
          try
          {
            Object localObject = MediaPlayer.create(TaptapseeActivity.this, Uri.fromFile(new File(paramFile.toString())));
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("Upload File path: ");
            localStringBuilder.append(paramFile.getAbsolutePath());
            Crashlytics.log(localStringBuilder.toString());
            localStringBuilder = new StringBuilder();
            localStringBuilder.append("Upload: File size: ");
            localStringBuilder.append(paramFile.length());
            Crashlytics.log(localStringBuilder.toString());
            if (localObject != null)
            {
              localStringBuilder = new StringBuilder();
              localStringBuilder.append("Upload: Video length: ");
              localStringBuilder.append(((MediaPlayer)localObject).getDuration());
              localStringBuilder.append("ms");
              Crashlytics.log(localStringBuilder.toString());
            }
            localObject = new StringBuilder();
            ((StringBuilder)localObject).append("Upload: File url: ");
            ((StringBuilder)localObject).append(this.url);
            Crashlytics.log(((StringBuilder)localObject).toString());
            Crashlytics.logException(new Exception("End upload video"));
            return;
          }
          catch (Exception localException)
          {
            Crashlytics.logException(localException);
          }
        }
        
        public void onError(Throwable paramAnonymousThrowable)
        {
          TaptapseeActivity.this.setResultTextDescription(TaptapseeActivity.this.getString(2131624043));
        }
        
        public void onNext(VideoUploadResponse paramAnonymousVideoUploadResponse)
        {
          TaptapseeActivity.this.startVideoTag(paramAnonymousVideoUploadResponse.getToken(), paramAnonymousVideoUploadResponse.getUrl());
          this.url = paramAnonymousVideoUploadResponse.getUrl();
        }
        
        public void onSubscribe(Disposable paramAnonymousDisposable) {}
      });
      return;
    }
    setResultTextDescription(getString(2131624088));
  }
  
  public void confirmClick()
  {
    showHowToDialog();
    ActiveSession.setAwareTalkback(true);
  }
  
  public void confirmClickHowTo()
  {
    ActiveSession.setAwareaHowTo(true);
  }
  
  public void doNegativeClick()
  {
    recreate();
  }
  
  public void doPositiveClick()
  {
    ActiveSession.setPolicyAttitude(true);
  }
  
  public String getPath(Uri paramUri)
  {
    paramUri = getContentResolver().query(paramUri, new String[] { "_data" }, null, null, null);
    if (paramUri == null) {
      return null;
    }
    int i = paramUri.getColumnIndexOrThrow("_data");
    paramUri.moveToFirst();
    String str = paramUri.getString(i);
    paramUri.close();
    return str;
  }
  
  public void importImage(View paramView)
  {
    paramView = getResources().getStringArray(2130837504);
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
    localBuilder.setTitle(getResources().getString(2131624026));
    localBuilder.setNegativeButton(17039360, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface.dismiss();
      }
    });
    localBuilder.setItems(paramView, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        switch (paramAnonymousInt)
        {
        default: 
          return;
        case 1: 
          paramAnonymousDialogInterface = new Intent("android.intent.action.PICK", MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
          paramAnonymousDialogInterface.putExtra("android.intent.action.GET_CONTENT", true);
          paramAnonymousDialogInterface.setType("video/mp4");
          TaptapseeActivity.this.startActivityForResult(paramAnonymousDialogInterface, 11011);
          return;
        }
        paramAnonymousDialogInterface = new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        paramAnonymousDialogInterface.putExtra("android.intent.action.GET_CONTENT", true);
        TaptapseeActivity.this.startActivityForResult(paramAnonymousDialogInterface, 11010);
      }
    });
    localBuilder.show();
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    onResume();
    if ((paramInt2 == -1) && (paramIntent != null))
    {
      Object localObject = paramIntent.getData();
      if (paramInt1 == 11010)
      {
        if (this.lastImageId < 3) {
          paramInt1 = this.lastImageId + 1;
        } else {
          paramInt1 = 1;
        }
        if (!this.imageIds.contains(Integer.valueOf(paramInt1)))
        {
          this.imageIds.add(Integer.valueOf(paramInt1));
          this.lastImageId = paramInt1;
          setResultTextDescription(getResources().getString(2131624053, new Object[] { Integer.valueOf(this.lastImageId) }));
          uploadImageRequest(Utils.convertFileToByteArray(new File(getPath((Uri)localObject))));
          return;
        }
        onReachMaxRequests();
        return;
      }
      if (paramInt1 == 11011)
      {
        paramIntent = MediaPlayer.create(this, (Uri)localObject);
        if (paramIntent.getDuration() <= 10000)
        {
          setResultTextDescription(getResources().getString(2131624054));
          onVideoTaken(new File(getPath((Uri)localObject)), null);
          return;
        }
        localObject = TapTapSeeApplication.getDefaultTracker();
        HitBuilders.EventBuilder localEventBuilder = new HitBuilders.EventBuilder();
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("uploaded video is too long, video size-");
        localStringBuilder.append(paramIntent.getDuration() / 1000);
        localStringBuilder.append("seconds");
        ((Tracker)localObject).send(localEventBuilder.setAction(localStringBuilder.toString()).build());
        paramIntent = new AlertDialog.Builder(this);
        paramIntent.setTitle(getResources().getString(2131624090));
        paramIntent.setMessage(getResources().getString(2131624091));
        paramIntent.setNegativeButton(getResources().getString(2131623977), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            paramAnonymousDialogInterface.cancel();
          }
        });
        paramIntent.show();
      }
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2131361820);
    createCameraFragment();
    this.repeatButton = ((Button)findViewById(2131230876));
    this.shareButton = ((Button)findViewById(2131230900));
    this.tapResultText = ((TextView)findViewById(2131230830));
    this.firstTimeDescription = ((TextView)findViewById(2131230811));
    this.firstTimeDescription.setText(2131624033);
    this.isTalkBackEnabled = isTalkBackEnabled();
    if (SharedPrefsUtil.isFirstLaunch(this)) {
      announceFirstLaunch();
    }
    this.imageIds = new HashSet(3);
    if ((!this.isTalkBackEnabled) && (!ActiveSession.awareTalkback())) {
      WarnDialogFragment.newInstance(2131624072, 2131624041, 17301569).show(getFragmentManager(), "Need Talkback AlertDialog");
    } else if (!ActiveSession.awareHowTo()) {
      showHowToDialog();
    }
    this.locationManager = ((LocationManager)getSystemService("location"));
    paramBundle = new Criteria();
    this.locationProvider = this.locationManager.getBestProvider(paramBundle, false);
    paramBundle = new Appirater(this);
    paramBundle.appLaunched(true);
    paramBundle.userDidSignificantEvent(false);
    this.controlsView = findViewById(2131230814);
    this.contentView = findViewById(2131230813);
    this.statusBarHeight = getStatusBarHeight();
    this.navigationBarHeight = getNavigationBarHeight();
    if (ActiveSession.getEnableFullscreen())
    {
      this.mSystemUiHider = SystemUiHider.getInstance(this, this.contentView, 6);
      this.mSystemUiHider.setup();
      this.mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener()
      {
        @TargetApi(13)
        public void onVisibilityChange(boolean paramAnonymousBoolean)
        {
          if (TaptapseeActivity.this.mControlsHeight == 0) {
            TaptapseeActivity.this.mControlsHeight = TaptapseeActivity.this.controlsView.getHeight();
          }
          if (TaptapseeActivity.this.mShortAnimTime == 0) {
            TaptapseeActivity.this.mShortAnimTime = TaptapseeActivity.this.getResources().getInteger(17694720);
          }
          Object localObject = TaptapseeActivity.this.controlsView.animate();
          float f;
          if (paramAnonymousBoolean) {
            f = 0.0F;
          } else {
            f = -TaptapseeActivity.this.mControlsHeight;
          }
          ((ViewPropertyAnimator)localObject).translationY(f).setDuration(TaptapseeActivity.this.mShortAnimTime);
          localObject = TaptapseeActivity.this.controlsView;
          int i;
          if (paramAnonymousBoolean) {
            i = 0;
          } else {
            i = 8;
          }
          ((View)localObject).setVisibility(i);
          localObject = TaptapseeActivity.this;
          TextView localTextView = TaptapseeActivity.this.tapResultText;
          if ((paramAnonymousBoolean & (TaptapseeActivity.this.isTalkBackEnabled ^ true))) {
            i = TaptapseeActivity.this.navigationBarHeight;
          } else {
            i = 0;
          }
          ((TaptapseeActivity)localObject).setViewMarginVertical(localTextView, 0, i);
        }
      });
      this.mSystemUiHider.toggle();
    }
    this.tapResultText.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        TaptapseeActivity.this.repeatLastImage(paramAnonymousView);
      }
    });
    this.shareButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if ((TaptapseeActivity.this.lastImage != null) && (!TextUtils.isEmpty(TaptapseeActivity.this.searchText))) {}
        try
        {
          paramAnonymousView = IntentHelper.buildShareImageIntent(TaptapseeActivity.this, TaptapseeActivity.this.lastImage, TaptapseeActivity.this.searchText, TaptapseeActivity.this.getPackageName());
          TaptapseeActivity.this.startActivity(Intent.createChooser(paramAnonymousView, "Share Image"));
          return;
        }
        catch (IllegalArgumentException paramAnonymousView)
        {
          for (;;) {}
        }
        Toast.makeText(TaptapseeActivity.this, "Image share failed", 1).show();
      }
    });
  }
  
  public void onPictureTaken(byte[] paramArrayOfByte)
  {
    try
    {
      if (!ActiveSession.getSavePicture())
      {
        this.lastImage = File.createTempFile("temp", ".jpg", getCacheDir());
      }
      else
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(Environment.getExternalStorageDirectory());
        localStringBuilder.append(File.separator);
        localStringBuilder.append(Environment.DIRECTORY_DCIM);
        localStringBuilder.append(File.separator);
        localStringBuilder.append(System.currentTimeMillis());
        localStringBuilder.append(".jpg");
        this.lastImage = new File(localStringBuilder.toString());
      }
      new FileOutputStream(this.lastImage).write(paramArrayOfByte);
    }
    catch (IOException localIOException)
    {
      Toast.makeText(this, "Failed to save picture!", 1).show();
      localIOException.printStackTrace();
    }
    int i;
    if (this.lastImageId < 3) {
      i = this.lastImageId + 1;
    } else {
      i = 1;
    }
    this.lastImageId = i;
    if ((!this.imageIds.contains(Integer.valueOf(this.lastImageId))) && (ActiveSession.canTakePicture()))
    {
      uploadImageRequest(paramArrayOfByte);
      this.imageIds.add(Integer.valueOf(this.lastImageId));
      ActiveSession.usePictureCredit();
      setResultTextDescription(getString(2131624053, new Object[] { Integer.valueOf(this.lastImageId) }));
      return;
    }
    onReachMaxRequests();
  }
  
  protected void onPostResume()
  {
    super.onPostResume();
  }
  
  protected void onResume()
  {
    super.onResume();
    if (getFragmentManager().findFragmentById(2131230774) == null) {
      createCameraFragment();
    }
    if ((ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0) && (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") != 0))
    {
      Location localLocation = new Location("");
      localLocation.setLatitude(0.0D);
      localLocation.setLongitude(0.0D);
      localLocation.setAltitude(0.0D);
      updateActiveLocation(localLocation);
    }
    this.locationManager.requestLocationUpdates(this.locationProvider, 0L, 0.0F, new LocationListener()
    {
      public void onLocationChanged(Location paramAnonymousLocation)
      {
        TaptapseeActivity.this.updateActiveLocation(paramAnonymousLocation);
      }
      
      public void onProviderDisabled(String paramAnonymousString) {}
      
      public void onProviderEnabled(String paramAnonymousString) {}
      
      public void onStatusChanged(String paramAnonymousString, int paramAnonymousInt, Bundle paramAnonymousBundle) {}
    });
  }
  
  public void onVideoTaken(File paramFile, Camera paramCamera)
  {
    setResultTextDescription(getResources().getString(2131624054));
    uploadVideoRequest(paramFile);
  }
  
  public void repeatLastImage(View paramView)
  {
    Log.d("TaptapseeActivity", "Repeat last image!");
    paramView = this.tapResultText.getText().toString();
    if (!TextUtils.isEmpty(paramView)) {
      setResultTextDescription(paramView);
    }
  }
  
  public void showAboutInfo(View paramView)
  {
    startActivity(new Intent(this, AppSettingsActivity.class));
  }
}


/* Location:              C:\Users\elcot\Desktop\APP\dex2jar-2.0\classes-dex2jar.jar!\com\futuretech\eye\activity\TaptapseeActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */