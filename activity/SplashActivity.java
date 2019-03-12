package com.futuretech.eye.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import com.futuretech.eye.fragment.WarnDialogFragment;
import com.futuretech.eye.fragment.WarnDialogFragment.FragmentAlertDialog;
import com.futuretech.eye.preferences.ActiveSession;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class SplashActivity
  extends Activity
  implements WarnDialogFragment.FragmentAlertDialog
{
  private static final int PERMISSION_REQUEST = 22222;
  private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1110;
  private static final int SPLASH_DISPLAY_TIME = 5000;
  
  private void checkConnect()
  {
    NetworkInfo localNetworkInfo = ((ConnectivityManager)getSystemService("connectivity")).getActiveNetworkInfo();
    if ((localNetworkInfo != null) && (localNetworkInfo.isConnectedOrConnecting()))
    {
      checkForStart();
      return;
    }
    WarnDialogFragment.newInstance(2131624043, 2131624043, 17301543).show(getFragmentManager(), "NoNetwork AlertDialog");
  }
  
  private void checkForStart()
  {
    if ((ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") == 0) && (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0) && (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) && (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) && (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == 0) && (ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") == 0) && (ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") == 0))
    {
      if (checkPlayServices())
      {
        startActivity(new Intent(this, TaptapseeActivity.class));
        return;
      }
      warnNeedGooglePlay();
      return;
    }
    ActivityCompat.requestPermissions(this, new String[] { "android.permission.CAMERA", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.READ_PHONE_STATE", "android.permission.RECORD_AUDIO" }, 22222);
  }
  
  private boolean checkPlayServices()
  {
    return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == 0;
  }
  
  private void warnNeedGooglePlay()
  {
    WarnDialogFragment.newInstance(2131624044, 2131624040, 17301543).show(getFragmentManager(), "GooglePlay AlertDialog");
  }
  
  public void confirmClick()
  {
    ActiveSession.setActiveSubscription(false);
    checkConnect();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    ActiveSession.setAwareTalkback(false);
    ActiveSession.setAwareaHowTo(false);
    checkConnect();
  }
  
  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (paramInt == 22222) {
      checkForStart();
    }
  }
}


/* Location:              C:\Users\elcot\Desktop\APP\dex2jar-2.0\classes-dex2jar.jar!\com\futuretech\eye\activity\SplashActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */