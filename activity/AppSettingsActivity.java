package com.futuretech.eye.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import com.futuretech.eye.constants.Constants;
import com.futuretech.eye.customview.TextViewFonted;
import com.futuretech.eye.preferences.ActiveSession;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@SuppressLint({"SimpleDateFormat"})
public class AppSettingsActivity
  extends Activity
{
  private Calendar calender;
  private SimpleDateFormat dateFormat;
  private View.OnClickListener onClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (paramAnonymousView.getId() == AppSettingsActivity.this.txtPrivacyPolicy.getId())
      {
        AppSettingsActivity.this.openWebViewUrl("http://www.taptapseeapp.com/legal/privacy.html");
        return;
      }
      if (paramAnonymousView.getId() == AppSettingsActivity.this.txtTermsOfUse.getId())
      {
        AppSettingsActivity.this.openWebViewUrl("http://www.taptapseeapp.com/legal/terms_of_service.html");
        return;
      }
      if (paramAnonymousView.getId() == AppSettingsActivity.this.txtContactUs.getId()) {
        AppSettingsActivity.this.contactMail();
      }
    }
  };
  private TextViewFonted txtContactUs;
  private TextViewFonted txtPrivacyPolicy;
  private Button txtRemaining;
  private TextViewFonted txtTermsOfUse;
  private TextViewFonted txtVersionName;
  
  private void contactMail()
  {
    Intent localIntent = new Intent("android.intent.action.SEND");
    localIntent.setType(getResources().getString(2131624003));
    localIntent.putExtra("android.intent.extra.EMAIL", new String[] { getResources().getString(2131623997) });
    localIntent.putExtra("android.intent.extra.SUBJECT", getResources().getString(2131623998));
    String str2;
    try
    {
      String str1 = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      localNameNotFoundException.printStackTrace();
      str2 = null;
    }
    String str3 = Build.MODEL;
    Object localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append("");
    ((StringBuilder)localObject1).append(Locale.getDefault());
    localObject1 = ((StringBuilder)localObject1).toString();
    if (Constants.ISNETAVAILABLE)
    {
      localObject2 = ((ConnectivityManager)getSystemService("connectivity")).getActiveNetworkInfo();
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("");
      localStringBuilder.append(((NetworkInfo)localObject2).getTypeName());
      localStringBuilder.toString();
    }
    else
    {
      getResources().getString(2131624043);
    }
    Object localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append(getResources().getString(2131624000));
    ((StringBuilder)localObject2).append(str2);
    ((StringBuilder)localObject2).append(getResources().getString(2131624002));
    ((StringBuilder)localObject2).append(str3);
    ((StringBuilder)localObject2).append(getResources().getString(2131623999));
    ((StringBuilder)localObject2).append((String)localObject1);
    ((StringBuilder)localObject2).append("\n");
    localIntent.putExtra("android.intent.extra.TEXT", ((StringBuilder)localObject2).toString());
    startActivity(Intent.createChooser(localIntent, getResources().getString(2131624063)));
  }
  
  private void init()
  {
    this.txtPrivacyPolicy = ((TextViewFonted)findViewById(2131230954));
    this.txtTermsOfUse = ((TextViewFonted)findViewById(2131230958));
    this.txtContactUs = ((TextViewFonted)findViewById(2131230945));
    this.txtVersionName = ((TextViewFonted)findViewById(2131230961));
    this.txtRemaining = ((Button)findViewById(2131230956));
  }
  
  private void openWebViewUrl(String paramString)
  {
    try
    {
      startActivity(new Intent("android.intent.action.VIEW", Uri.parse(paramString)));
      return;
    }
    catch (ActivityNotFoundException paramString)
    {
      paramString.printStackTrace();
    }
  }
  
  private void setUpDefaults()
  {
    try
    {
      String str = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
      this.txtVersionName.setText(str);
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      localNameNotFoundException.printStackTrace();
    }
    if (ActiveSession.getPictureCount() == 0)
    {
      this.txtRemaining.setText(getResources().getString(2131624056));
      return;
    }
    Button localButton = this.txtRemaining;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(ActiveSession.getPictureCount());
    localStringBuilder.append(getResources().getString(2131624060));
    localButton.setText(localStringBuilder.toString());
  }
  
  private void setUpEvents()
  {
    this.txtPrivacyPolicy.setOnClickListener(this.onClickListener);
    this.txtTermsOfUse.setOnClickListener(this.onClickListener);
    this.txtContactUs.setOnClickListener(this.onClickListener);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2131361819);
    init();
    setUpDefaults();
    setUpEvents();
    paramBundle = (Switch)findViewById(2131230917);
    paramBundle.setChecked(ActiveSession.getSound());
    paramBundle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    {
      public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
      {
        ActiveSession.setSound(paramAnonymousBoolean);
      }
    });
    paramBundle = (Switch)findViewById(2131230919);
    paramBundle.setChecked(ActiveSession.getFlash());
    paramBundle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    {
      public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
      {
        ActiveSession.setFlash(paramAnonymousBoolean);
      }
    });
    paramBundle = (Switch)findViewById(2131230921);
    paramBundle.setChecked(ActiveSession.getSavePicture());
    paramBundle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    {
      public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
      {
        ActiveSession.setSavePicture(paramAnonymousBoolean);
      }
    });
    paramBundle = (Switch)findViewById(2131230920);
    paramBundle.setChecked(ActiveSession.getEnableFullscreen());
    paramBundle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    {
      public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
      {
        ActiveSession.setEnableFullScreen(paramAnonymousBoolean);
      }
    });
  }
  
  protected void onResume()
  {
    super.onResume();
    if (ActiveSession.getLifeTimeVip())
    {
      this.txtRemaining.setText("VIP");
      return;
    }
    if (ActiveSession.isValidSubscription())
    {
      this.txtRemaining.setText(getResources().getString(2131624045));
      return;
    }
    Button localButton = this.txtRemaining;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(ActiveSession.getPictureCount());
    localStringBuilder.append(" ");
    localStringBuilder.append(getResources().getString(2131624060));
    localButton.setText(localStringBuilder.toString());
  }
}


/* Location:              C:\Users\elcot\Desktop\APP\dex2jar-2.0\classes-dex2jar.jar!\com\futuretech\eye\activity\AppSettingsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */