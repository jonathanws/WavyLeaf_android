package com.towson.wavyleaf;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

public class SuperSecretDeveloperPreference extends Preference implements View.OnLongClickListener {

	public SuperSecretDeveloperPreference(Context context) {
        super(context);
    }

    public SuperSecretDeveloperPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public SuperSecretDeveloperPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

	@Override
    public boolean onLongClick(View v) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
		Editor ed = sp.edit();
		
		// Toggle boolean entry
		ed.putBoolean(Settings.KEY_THEME, (!(sp.getBoolean(Settings.KEY_THEME, true))));
		ed.commit();
		
        Toast.makeText(getContext(), "Settings theme changed", Toast.LENGTH_SHORT).show();
        
        return true;
    }
	
}
