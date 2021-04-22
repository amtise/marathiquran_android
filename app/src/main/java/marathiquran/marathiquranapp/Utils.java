package marathiquran.marathiquranapp;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.fragment.app.Fragment;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils extends Fragment {

    private CircleProgressBar circleProgressBar;
    public static KProgressHUD hud;

    public static boolean isNetworkAvailable(Context mContext) {

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showProgress1(String message, final Context context) {

        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.5f)
                .setLabel(message)
                .setCancellable(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                    }
                });
        hud.show();
    }

    public static void hideProgress1() {
        if (hud != null && hud.isShowing()) {
            hud.dismiss();
            hud = null;
        }
    }
}
