package marathiquran.marathiquranapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PreferenceUtils {

    SharedPreferences preferences;
    Context context;

    public PreferenceUtils(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        this.context = context;
    }

    public void setayatpos(int ayat_position) {
        // TODO Auto-generated method stub
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("ayat_position", ayat_position);
        editor.commit();
    }

    public int getayatpos() {
        return preferences.getInt("ayat_position", 0);
    }

    public void setsurepos(int sure_position) {
        // TODO Auto-generated method stub
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("sure_position", sure_position);
        editor.commit();
    }

    public int getsurepos() {
        return preferences.getInt("sure_position", 0);
    }

    public void seteuratid(int suratid) {
        // TODO Auto-generated method stub
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("suratid", suratid);
        editor.commit();
    }

    public int geteuratid() {
        return preferences.getInt("suratid", 1);
    }


    public void setsurantnum(int suratnum) {
        // TODO Auto-generated method stub
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("suratnum", suratnum);
        editor.commit();
    }

    public int getsurantnum() {
        return preferences.getInt("suratnum", 1);
    }

    public void setvachanpos(int vachanpos) {
        // TODO Auto-generated method stub
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("vachanpos", vachanpos);
        editor.commit();
    }

    public int getvachanpos() {
        return preferences.getInt("vachanpos", 0);
    }




}
