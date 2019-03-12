package com.futuretech.eye.application;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.multidex.MultiDexApplication;
import com.crashlytics.android.Crashlytics;
import com.crittercism.app.Crittercism;
import com.futuretech.eye.api.TapTapSeeApi;
import com.futuretech.eye.api.TapTapSeeApi.Provider;
import com.futuretech.eye.preferences.ActiveSession;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import java.util.Locale;

public class TapTapSeeApplication
  extends MultiDexApplication
{
  private static final String PROPERTY_ID_RELEASE = "UA-38443534-3";
  private static TapTapSeeApi apiProvider;
  private static Context context;
  private static Tracker mTracker;
  public static String sDefSystemLanguage;
  Locale locale;
  
  public static TapTapSeeApi getApi()
  {
    return apiProvider;
  }
  
  public static Context getAppContext()
  {
    return context;
  }
  
  public static Tracker getDefaultTracker()
  {
    try
    {
      Tracker localTracker = mTracker;
      return localTracker;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private void logUser()
  {
    new ActiveSession();
    Crashlytics.setString("locale", ActiveSession.getLocale());
    Crashlytics.setString("language", ActiveSession.getLanguage());
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    sDefSystemLanguage = paramConfiguration.locale.getLanguage();
    this.locale = new Locale(sDefSystemLanguage);
    paramConfiguration = context.getResources();
    Configuration localConfiguration = paramConfiguration.getConfiguration();
    localConfiguration.locale = this.locale;
    paramConfiguration.updateConfiguration(localConfiguration, null);
  }
  
  public void onCreate()
  {
    super.onCreate();
    try
    {
      ProviderInstaller.installIfNeeded(this);
    }
    catch (GooglePlayServicesRepairableException|GooglePlayServicesNotAvailableException localGooglePlayServicesRepairableException)
    {
      localGooglePlayServicesRepairableException.printStackTrace();
    }
    Crittercism.initialize(getApplicationContext(), "5410e42f83fb7914a1000007");
    Fabric.with(this, new Kit[] { new Crashlytics() });
    logUser();
    apiProvider = TapTapSeeApi.Provider.provideApi();
    context = getApplicationContext();
    sDefSystemLanguage = Locale.getDefault().getLanguage();
    this.locale = new Locale(sDefSystemLanguage);
    mTracker = GoogleAnalytics.getInstance(this).newTracker("UA-38443534-3");
  }
}


/* Location:              C:\Users\elcot\Desktop\APP\dex2jar-2.0\classes-dex2jar.jar!\com\futuretech\eye\application\TapTapSeeApplication.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */