package de.edvschule_plattling.trfi.testprojekt;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

/**
 * Created by spfahl on 26.04.2018.
 */

public abstract class HilfsActivityClass extends Activity {

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }
}
