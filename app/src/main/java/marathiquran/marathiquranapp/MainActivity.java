package marathiquran.marathiquranapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.multidex.BuildConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/////  android:fontFamily="@font/me_quran"


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    Spinner surat_spinner, ayat_spinner,audio_spinner;
    SqlLiteDbHelper dbHelper;
    List<Surat> suratList;
    List<Ayat> ayatList;
    int surat_number,surat_id;
    List<Vachan> vachanList;
    String fileName=MY_URL;
    private static final String  MY_URL = "https://marathiquran.com/media/audios/full_surat/1.mp3";
    private ProgressDialog pDialog;
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;

    //Adapter_Ayats adapter_ayats;
    RecyclerView listView;
    AudioItemAdapter audioItemAdapter;
    Activity activity;

    String url = "";
    String ur2 = "";
    String PATH = "";

    String audioType = "marathi";
    String trigger = "0";

    ImageView iv_down ,iv_up;
    LinearLayout ll_hideshow;
    private static int SPLASH_DISPLAY_LENGTH = 1000;
    boolean isStop = false;

    ImageView iv_download;

     int list_i ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetVersionCode versionChecker = new GetVersionCode();

        try {
            String latestVersion = versionChecker.execute().get();
            String versionName ;
            PackageManager pm = getPackageManager();
            PackageInfo pInfo = null;

            try {
                pInfo =  pm.getPackageInfo(getPackageName(),0);
            } catch (PackageManager.NameNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            versionName = pInfo.versionName;
            if (latestVersion != null && !latestVersion.isEmpty()) {
                if (!latestVersion.equals(versionName)) {

                    Log.e("TAG" , "TESTVERSION"  + latestVersion);
                    Log.e("TAG" , "TESTVERSION"  + versionName);
                    showUpdateDialog();

                }else{
                    Log.e("TAG" , "TESTVERSION"  + latestVersion);
                    Log.e("TAG" , "TESTVERSION"  + versionName);

                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (Utils.isNetworkAvailable(MainActivity.this)) {
        }else {
            Toast.makeText(MainActivity.this, "Please connect to an internet", Toast.LENGTH_SHORT).show();
        }

        listView = findViewById(R.id.vachan_list);
        iv_down = findViewById(R.id.iv_down);
        iv_up = findViewById(R.id.iv_up);
        ll_hideshow = findViewById(R.id.ll_hideshow);
        iv_download = findViewById(R.id.iv_download);
        activity  =this;

        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));

        toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

        iv_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ll_hideshow.setVisibility(View.VISIBLE);
                iv_up.setVisibility(View.VISIBLE);
                iv_down.setVisibility(View.GONE);

            }
        });

        iv_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_hideshow.setVisibility(View.GONE);
                iv_up.setVisibility(View.GONE);
                iv_down.setVisibility(View.VISIBLE);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.id_navview);

        audio_spinner=findViewById(R.id.audio_spinner);
        final List<String> list=new ArrayList<String>();
        list.add("\nफक्त मराठी ऑडिओ");
        list.add("\nअरबी + मराठी ऑडिओ");
        list.add("\nफक्त अरबी ऑडिओ");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        audio_spinner.setAdapter(dataAdapter);

      /*  ///////////// incoming calling
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {

                    audioItemAdapter.pausePlayer();

                } else if(state == TelephonyManager.CALL_STATE_IDLE) {

                } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {

                    audioItemAdapter.pausePlayer();

                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };//end PhoneStateListener

        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        ///////////// incoming calling
*/
        audio_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                audioItemAdapter.stopPlayer();

                if(i == 0){
                    audioType = "marathi";
                }else if(i == 1){
                    audioType = "arabicmarathi";
                }else {
                    audioType = "arabic";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        surat_spinner= findViewById(R.id.surat_spinner);
        suratList = new ArrayList<>();
        suratList.clear();
        dbHelper = new SqlLiteDbHelper(this);
        dbHelper.openDataBase();
        suratList = dbHelper.getAllSurat();

        ayat_spinner = findViewById(R.id.ayat_spinner);

        ayatList = new ArrayList<>();
        ayatList.clear();
        ayatList= dbHelper.getAllAyats(1);

        ArrayAdapter<Surat> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, suratList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        surat_spinner.setAdapter(adapter);

        vachanList = dbHelper.getAllVachan(1,surat_number);

        //creating the adapter object

        //  vachanAdapter = new CustomAyatyList(this, R.layout.list_row,vachanList);,
        //  listView.setAdapter(vachanAdapter);

        //  adapter_ayats = new Adapter_Ayats(MainActivity.this, vachanList);
        //    listView.setAdapter(adapter_ayats);

        audioItemAdapter = new AudioItemAdapter(MainActivity.this ,  vachanList);
        listView.setAdapter(audioItemAdapter);

        surat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                audioItemAdapter.stopPlayer();
                listView.scrollToPosition(0);

                surat_id = suratList.get(i).getId();
                surat_number = suratList.get(i).getSurat_number();
                fileName = suratList.get(i).getFull_audio_file();

                ayatList.clear();
                ayatList= dbHelper.getAllAyats(surat_id);

                ArrayAdapter<Ayat> adapter2 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, ayatList);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ayat_spinner.setAdapter(adapter2);

                vachanList = dbHelper.getAllVachan(surat_id,surat_number);

                //creating the adapter object
               /* vachanAdapter = new CustomAyatyList(MainActivity.this, R.layout.list_row, vachanList);
                listView.setAdapter(vachanAdapter);*/

                //  adapter_ayats = new Adapter_Ayats(MainActivity.this, vachanList);
                // listView.setAdapter(adapter_ayats);

                audioItemAdapter = new AudioItemAdapter(MainActivity.this , vachanList);
                listView.setAdapter(audioItemAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ayat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                // listView.setSmoothScrollbarEnabled(true);
                // listView.smoothScrollToPositionFromTop(i,0);
                audioItemAdapter.stopPlayer();
                listView.scrollToPosition(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        //direcotry code
//        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
//        File root = new File(Environment.getExternalStorageDirectory(), "MarathiQuran App");
//        if (!root.exists()) {
//            root.mkdirs();
//        }



        //code for navigation drawer
        setSupportActionBar(toolbar);
        //getSupportActionBar(toolbar);
        // toolbar.setNavigationIcon(R.drawable.ic_humburger3);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //  drawer.setDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        drawer.addDrawerListener(toggle);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        iv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        if(!((Activity) MainActivity.this).isFinishing()) {

                            Utils.showProgress1("Downloading... \n please wait" , MainActivity.this);
                            Toast.makeText(MainActivity.this, "Downloading started in background", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        String SDCardRoot = Environment.getExternalStorageDirectory().toString();

                        for(list_i = 0 ; list_i <=vachanList.size()-1 ; list_i++){

                            if(list_i <= vachanList.size()){

                                if (audioType.equals("marathi")) {

                                    url = "https://marathiquran.com/media/" + vachanList.get(list_i).getMarathi_audio().replace(" ", "%20");
                                    downloadFile(url, vachanList.get(list_i).getSurat_number() + "." + vachanList.get(list_i).getAyat_number() + ".mp3", SDCardRoot + "/MarathiQuran/marathi");

                                } else if (audioType.equals("arabic")) {

                                    url = "https://marathiquran.com/media/" + vachanList.get(list_i).getArabic_audio().replace(" ", "%20");
                                    downloadFile(url, vachanList.get(list_i).getSurat_number() + "." + vachanList.get(list_i).getAyat_number() + ".mp3", SDCardRoot + "/MarathiQuran/arabic");

                                } else {

                                    url = "https://marathiquran.com/media/" + vachanList.get(list_i).getMarathi_audio().replace(" ", "%20");
                                    downloadFile(url, vachanList.get(list_i).getSurat_number() + "." + vachanList.get(list_i).getAyat_number() + ".mp3", SDCardRoot + "/MarathiQuran/marathi");

                                    ur2 = "https://marathiquran.com/media/" + vachanList.get(list_i).getArabic_audio().replace(" ", "%20");
                                    downloadFile(ur2, vachanList.get(list_i).getSurat_number() + "." + vachanList.get(list_i).getAyat_number() + ".mp3", SDCardRoot + "/MarathiQuran/arabic");

                                }

                               // url = "https://marathiquran.com/media/" + vachanList.get(list_i).getMarathi_audio().replace(" ", "%20");
                               // downloadFile(url, vachanList.get(list_i).getSurat_number() + "." + vachanList.get(list_i).getAyat_number() + ".mp3", SDCardRoot + "/MarathiQuran/marathi");

                            }

                            if(list_i >= vachanList.size()-1) {
                                Utils.hideProgress1();
                            }

                        }
                    }
                });
                t.start();

               /* runOnUiThread(new Runnable() {
                    public void run() {
                        if(list_i >= vachanList.size()) {
                            Utils.hideProgress1();
                        }
                    }
                });*/

            }
        });

    }



    private void showUpdateDialog(){

        final Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.updateappdialogue);

        Button btn_update;
        TextView txt_nothanks;

        myDialog.setCancelable(false);
        btn_update = myDialog.findViewById(R.id.btn_update);
        txt_nothanks = myDialog.findViewById(R.id.txt_nothanks);
       // myDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_layout_logout);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+ BuildConfig.APPLICATION_ID)));
                myDialog.dismiss();
            }
        });

        txt_nothanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.show();

    }

    //fun for close drawer
    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           // super.onBackPressed();
            Boolean exit = false;

            if (exit) {
                finish();
            }
            else {
                //Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
                exit = true;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // TODO Auto-generated method stub
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }
                }, 100);
            }
        }


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //code for nav menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

          /*  case R.id.id_downloadActivity:
                Intent intentdownload = new Intent(MainActivity.this, CollapseableLayout.class);
                startActivity(intentdownload);
                break;*/

            case R.id.id_aboutapp:
                Intent intentabout = new Intent(MainActivity.this, AboutAppActivity.class);
                startActivity(intentabout);
                break;


            case R.id.id_share:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage = "\nMarathi Quran App\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" +  "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                }
                break;

            case R.id.id_rate:
                try {
                    Uri marketUri = Uri.parse("market://details?id=" + getPackageName());
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);
                } catch (ActivityNotFoundException e) {
                    Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=com.infostack.namazapp&hl=en" + getPackageName());
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);
                }
                break;

        }
        return true;
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.download_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.download:
//                new DownloadFileFromURL().execute(MY_URL);
//               // return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{permission},
                    requestCode);
        }
    }

    /**
     * Showing Dialog
     * */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading Surah. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }


    }

    public void downloadStreams() {
        try {
            URL url = new URL(MY_URL);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            String PATH = Environment.getExternalStorageDirectory()
                    + "/download/";
            Log.v("log_tag", "PATH: " + PATH);
            File file = new File(PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            File outputFile = new File(file, fileName);
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();
        } catch (IOException e) {
            Log.e("log_tag", "Error: " + e);
        }
        Log.v("log_tag", "Check: ");
    }

    //////////////////////////////// Adapter //////////////////////

    public class AudioItemAdapter extends RecyclerView.Adapter<AudioItemAdapter.AudioItemsViewHolder> implements Handler.Callback {

        private static final int MSG_UPDATE_SEEK_BAR = 1845;
        private MediaPlayer mediaPlayer;
        private Handler uiUpdateHandler;
        private List<Vachan> audioItems;
        private int playingPosition;
        private AudioItemsViewHolder playingHolder;

        Context context;
        public int currentlyPlaying;

        AudioItemAdapter(Activity context , List<Vachan> audioItems) {

            this.audioItems = audioItems;
            this.context = context;
            this.playingPosition = -1;
            uiUpdateHandler = new Handler(this);

        }

        @Override
        public AudioItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AudioItemsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false));
        }

        @Override
        public void onBindViewHolder(AudioItemsViewHolder holder, final int position) {

            if(position == 0){
               holder.vachan_version.setVisibility(View.GONE);
            }else {
                holder.vachan_version.setVisibility(View.VISIBLE);
            }

            if(vachanList.get(position).getSurat_number() == 1 &&  vachanList.get(position).getAyat_number() == 6){
                holder.vachan_version.setText(vachanList.get(position).getSurat_number() + ":" + vachanList.get(position).getAyat_number()+"-7");
            }else {
                holder.vachan_version.setText(vachanList.get(position).getSurat_number() + ":" + vachanList.get(position).getAyat_number());
            }

            String arabic = vachanList.get(position).getArabic_text().toString().replaceAll("\\s+", "");
            holder.arabic_text.setText(arabic);
            //  String marathi = vachanList.get(position).getMarathi_text().toString().replaceAll("\\s+", "");
            holder.marathi_text.setText(vachanList.get(position).getMarathi_text());

            if (position == playingPosition) {
                playingHolder = holder;
                updatePlayingView();
            } else {
                updateNonPlayingView(holder);
            }

            holder.ic_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String share_test_marathi = "";
                    String share_test_arabic = "";
                    String share_test_ayatno = "";
                    String share_test_sure = "";

                    share_test_marathi = vachanList.get(position).getMarathi_text();
                    share_test_arabic = vachanList.get(position).getArabic_text();
                    share_test_sure = String.valueOf(vachanList.get(position).getSurat_number());
                    share_test_ayatno = String.valueOf(vachanList.get(position).getAyat_number());

                    String final_share = "\n Marathi Quran Application\n\n" + share_test_sure + "." + share_test_ayatno + " : " + share_test_arabic + "\n\n" + share_test_marathi + "\n\n" + "Arabic mp3 Link : \t\t" + "https://marathiquran.com/media/" + vachanList.get(position).getArabic_audio() + "\n\n" + "Marathi mp3 Link : \t\t" + "https://marathiquran.com/media/" + vachanList.get(position).getMarathi_audio() + "\n\n" + "Play store app link : \t\t" + "https://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID + "\n\n" + "Website Link : \t\t" + "https://marathiquran.com/" + "\n";

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, final_share);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));

                }
            });

        }

        @Override
        public int getItemCount() {
            return audioItems.size();
        }

        @Override
        public void onViewRecycled(AudioItemsViewHolder holder) {
            super.onViewRecycled(holder);

            if (playingPosition == holder.getAdapterPosition()) {
                updateNonPlayingView(playingHolder);
                playingHolder = null;
            }

        }

        private void updateNonPlayingView(AudioItemsViewHolder holder) {

            if (holder == playingHolder) {
                uiUpdateHandler.removeMessages(MSG_UPDATE_SEEK_BAR);
            }

            if (null != holder) {
                holder.sbProgress.setEnabled(false);
                holder.sbProgress.setProgress(0);
                holder.ivPlayPause.setImageResource(R.drawable.ic_play);
                holder.ll_time.setVisibility(View.GONE);
            }

        }

        private void updatePlayingView() {

            if (null != mediaPlayer) {

                if (null != playingHolder) {

                    playingHolder.sbProgress.setMax(mediaPlayer.getDuration());
                    playingHolder.sbProgress.setProgress(mediaPlayer.getCurrentPosition());
                    playingHolder.sbProgress.setEnabled(true);
                    playingHolder.cardView.setCardElevation((float) 15.0);

                    if (mediaPlayer.isPlaying()) {
                        playingHolder.ll_time.setVisibility(View.VISIBLE);
                        uiUpdateHandler.sendEmptyMessageDelayed(MSG_UPDATE_SEEK_BAR, 100);
                        playingHolder.ivPlayPause.setImageResource(R.drawable.ic_pause);

                    } else {
                        uiUpdateHandler.removeMessages(MSG_UPDATE_SEEK_BAR);
                        playingHolder.ivPlayPause.setImageResource(R.drawable.ic_play);
                    }
                    playingHolder.txt_end.setText(String.valueOf(milliSecondsToTimer((long) mediaPlayer.getDuration())));
                }

            }


        }

        void stopPlayer() {
            if (null != mediaPlayer) {
                releaseMediaPlayer();
            }
        }

        void pausePlayer() {
            if (null != mediaPlayer) {
                mediaPlayer.pause();
            }
        }

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_SEEK_BAR: {
                    playingHolder.txt_start.setText(String.valueOf(milliSecondsToTimer((long) mediaPlayer.getCurrentPosition())));
                    playingHolder.sbProgress.setProgress(mediaPlayer.getCurrentPosition());
                    uiUpdateHandler.sendEmptyMessageDelayed(MSG_UPDATE_SEEK_BAR, 100);
                    return true;
                }
            }
            return false;
        }

        class AudioItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
            SeekBar sbProgress;
            ImageView ivPlayPause, ic_share ;
            //TextView tvIndex;
            TextView vachan_version, marathi_text, arabic_text , txt_start,txt_end;;
            CardView cardView;
            ProgressBar progressBar;
            LinearLayout ll_time;

            AudioItemsViewHolder(View itemView) {
                super(itemView);

                ivPlayPause = (ImageView) itemView.findViewById(R.id.ivPlayPause);
                ivPlayPause.setOnClickListener(this);
                sbProgress = (SeekBar) itemView.findViewById(R.id.sbProgress);
                sbProgress.setOnSeekBarChangeListener(this);
                //tvIndex = (TextView) itemView.findViewById(R.id.tvIndex);
                vachan_version = itemView.findViewById(R.id.vachan_version);
                marathi_text = itemView.findViewById(R.id.marathi_text);
                arabic_text = itemView.findViewById(R.id.arabic_text);
                cardView = itemView.findViewById(R.id.cardview1);
                ic_share = itemView.findViewById(R.id.ic_share);
                txt_start = itemView.findViewById(R.id.txt_start);
                txt_end = itemView.findViewById(R.id.txt_end);
                ll_time = itemView.findViewById(R.id.ll_time);
                progressBar = itemView.findViewById(R.id.progressbar);

            }

            @Override
            public void onClick(View v) {

                if (getAdapterPosition() == playingPosition) {

                    // toggle between play/pause of audio

                    if (null != mediaPlayer) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        } else {
                            mediaPlayer.start();
                        }
                        currentlyPlaying = getAdapterPosition();
                    }

                } else {
                    playingPosition = getAdapterPosition();
                    currentlyPlaying = getAdapterPosition();
                    if (mediaPlayer != null) {
                        if (null != playingHolder) {
                            updateNonPlayingView(playingHolder);
                        }
                        mediaPlayer.release();
                    }
                    playingHolder = this;

                    startMediaPlayer(playingPosition);

                }

                updatePlayingView();

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        }

        private void startMediaPlayer(final int current_item) {

            if (audioType.equals("marathi")) {
                url = "https://marathiquran.com/media/" + audioItems.get(current_item).getMarathi_audio().replace(" ", "%20");
            } else if (audioType.equals("arabic")) {
                url = "https://marathiquran.com/media/" + audioItems.get(current_item).getArabic_audio().replace(" ", "%20");
            } else {
                ur2 = "https://marathiquran.com/media/" + audioItems.get(current_item).getMarathi_audio().replace(" ", "%20");
                url = "https://marathiquran.com/media/" + audioItems.get(current_item).getArabic_audio().replace(" ", "%20");
            }

            if (audioType.equals("arabicmarathi")){

                if (trigger.equals("0")) {

                    mediaPlayer = new MediaPlayer();

                    String SDCardRoot2 = Environment.getExternalStorageDirectory().toString();
                    String filepath = audioItems.get(current_item).getSurat_number() + "." + audioItems.get(current_item).getAyat_number() + ".mp3";
                    String audioFilePath = SDCardRoot2 + "/MarathiQuran/arabic/"+filepath;
                    File mp3_file = new File(audioFilePath);

                    if (mp3_file.exists()) {
                        try {
                            if (mediaPlayer != null) {
                                mediaPlayer.release();
                                mediaPlayer = null;

                            }
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(audioFilePath);
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    PhoneStateListener phoneStateListener = new PhoneStateListener() {
                                        @Override
                                        public void onCallStateChanged(int state, String incomingNumber) {

                                            if (state == TelephonyManager.CALL_STATE_RINGING) {

                                                pausePlayer();
                                                updatePlayingView();

                                            } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                                                mediaPlayer.start();
                                                updatePlayingView();
                                            } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {

                                                pausePlayer();
                                                updatePlayingView();

                                            }
                                            super.onCallStateChanged(state, incomingNumber);
                                        }
                                    };

                                    TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                                    if(mgr != null) {
                                        mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                                    }
                                }
                            });

                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {
                                    mediaPlayer.start();
                                    updatePlayingView();
                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mediaPlayer) {
                                            mediaPlayer.stop();
                                            mediaPlayer.release();
                                            mediaPlayer = null;

                                            trigger = "1";
                                            startMediaPlayer(playingPosition);
                                            listView.scrollToPosition(playingPosition);
                                            updatePlayingView();
                                            notifyDataSetChanged();

                                        }
                                    });
                                }
                            });
                            mediaPlayer.prepare();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                     /*       mediaPlayer =new MediaPlayer();

                            mediaPlayer.setDataSource(audioFilePath);
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            Log.e("TAG" , "TEST_FILE === >" + "Offline");*/

                    } else {

                        if (Utils.isNetworkAvailable(MainActivity.this)) {

                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    Utils.showProgress1("Please wait..." , context);
                                }
                            });

                            Thread t = new Thread(new Runnable() {

                                @Override
                                public void run() {

                                    String SDCardRoot = Environment.getExternalStorageDirectory().toString();
                                    downloadFile(url, audioItems.get(current_item).getSurat_number() + "." + audioItems.get(current_item).getAyat_number() + ".mp3", SDCardRoot + "/MarathiQuran/arabic");
                                }
                            });
                            t.start();

                            try {

                                if (mediaPlayer != null) {
                                    mediaPlayer.release();
                                    mediaPlayer = null;
                                }

                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.reset();
                                mediaPlayer.setDataSource(url);
                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        PhoneStateListener phoneStateListener = new PhoneStateListener() {
                                            @Override
                                            public void onCallStateChanged(int state, String incomingNumber) {

                                                if (state == TelephonyManager.CALL_STATE_RINGING) {

                                                    pausePlayer();
                                                    updatePlayingView();

                                                } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                                                    mediaPlayer.start();
                                                    updatePlayingView();
                                                } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {

                                                    pausePlayer();
                                                    updatePlayingView();

                                                }
                                                super.onCallStateChanged(state, incomingNumber);
                                            }
                                        };

                                        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                                        if(mgr != null) {
                                            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                                        }
                                    }
                                });

                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                        mediaPlayer.start();
                                        activity.runOnUiThread(new Runnable() {
                                            public void run() {
                                                Utils.hideProgress1();
                                            }
                                        });
                                        updatePlayingView();
                                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mediaPlayer) {
                                                mediaPlayer.stop();
                                                mediaPlayer.release();
                                                mediaPlayer = null;

                                                trigger = "1";
                                                startMediaPlayer(playingPosition);
                                                listView.scrollToPosition(playingPosition);
                                                updatePlayingView();
                                                notifyDataSetChanged();

                                            }
                                        });
                                    }
                                });
                                mediaPlayer.prepareAsync();

                              //  mediaPlayer.prepare();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {

                            Toast.makeText(MainActivity.this, "Please connect to an internet", Toast.LENGTH_SHORT).show();
                        }

                           /* if (Utils.isNetworkAvailable(MainActivity.this)) {
                                mediaPlayer =new MediaPlayer();
                                String SDCardRoot = Environment.getExternalStorageDirectory().toString();
                                downloadFile(url, audioItems.get(current_item).getSurat_number()+"."+audioItems.get(current_item).getAyat_number()+".mp3", SDCardRoot+"/MarathiQuran/arabic");

                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mediaPlayer.setDataSource(url);
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                                Log.e("TAG" , "TEST_FILE === >" + "Online");
                            }else {
                                stopPlayer();
                                mediaPlayer =new MediaPlayer();
                                Toast.makeText(MainActivity.this, "Please connect to an internet", Toast.LENGTH_SHORT).show();

                            }*/
                    }

                } else {

                    mediaPlayer = new MediaPlayer();

                    String SDCardRoot2 = Environment.getExternalStorageDirectory().toString();
                    String filepath = audioItems.get(current_item).getSurat_number() + "." + audioItems.get(current_item).getAyat_number() + ".mp3";
                    String audioFilePath = SDCardRoot2 + "/MarathiQuran/marathi/" + filepath;
                    File mp3_file = new File(audioFilePath);

                    if (mp3_file.exists()) {
                        try {
                            if (mediaPlayer != null) {
                                mediaPlayer.release();
                                mediaPlayer = null;

                            }
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(audioFilePath);
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    PhoneStateListener phoneStateListener = new PhoneStateListener() {
                                        @Override
                                        public void onCallStateChanged(int state, String incomingNumber) {

                                            if (state == TelephonyManager.CALL_STATE_RINGING) {

                                                pausePlayer();
                                                updatePlayingView();

                                            } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                                                mediaPlayer.start();
                                                updatePlayingView();
                                            } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {

                                                pausePlayer();
                                                updatePlayingView();

                                            }
                                            super.onCallStateChanged(state, incomingNumber);
                                        }
                                    };

                                    TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                                    if(mgr != null) {
                                        mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                                    }
                                }
                            });

                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {
                                    mediaPlayer.start();
                                    updatePlayingView();
                                    trigger = "1";
                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mediaPlayer) {
                                            mediaPlayer.stop();
                                            mediaPlayer.release();
                                            mediaPlayer = null;

                                            trigger = "0";

                                            playingPosition++;
                                            if (playingPosition >= vachanList.size())
                                                playingPosition = 0;

                                            startMediaPlayer(playingPosition);
                                            listView.scrollToPosition(playingPosition);
                                            updatePlayingView();
                                            notifyDataSetChanged();


                                        }
                                    });
                                }
                            });
                            mediaPlayer.prepare();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {

                        if (Utils.isNetworkAvailable(MainActivity.this)) {

                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    Utils.showProgress1("Please wait..." , context);
                                }
                            });

                            Thread t = new Thread(new Runnable() {

                                @Override
                                public void run() {

                                    String SDCardRoot = Environment.getExternalStorageDirectory().toString();
                                    downloadFile(ur2, audioItems.get(current_item).getSurat_number() + "." + audioItems.get(current_item).getAyat_number() + ".mp3", SDCardRoot + "/MarathiQuran/marathi");

                                }
                            });
                            t.start();

                            try {
                                if (mediaPlayer != null) {
                                    mediaPlayer.release();
                                    mediaPlayer = null;
                                }

                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.reset();
                                mediaPlayer.setDataSource(ur2);
                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        PhoneStateListener phoneStateListener = new PhoneStateListener() {
                                            @Override
                                            public void onCallStateChanged(int state, String incomingNumber) {

                                                if (state == TelephonyManager.CALL_STATE_RINGING) {

                                                    pausePlayer();
                                                    updatePlayingView();

                                                } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                                                    mediaPlayer.start();
                                                    updatePlayingView();
                                                } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {

                                                    pausePlayer();
                                                    updatePlayingView();

                                                }
                                                super.onCallStateChanged(state, incomingNumber);
                                            }
                                        };

                                        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                                        if(mgr != null) {
                                            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                                        }
                                    }
                                });

                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                        mediaPlayer.start();

                                        activity.runOnUiThread(new Runnable() {
                                            public void run() {
                                                Utils.hideProgress1();
                                            }
                                        });

                                        updatePlayingView();
                                        trigger = "1";
                                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mediaPlayer) {
                                                mediaPlayer.stop();
                                                mediaPlayer.release();
                                                mediaPlayer = null;

                                                trigger = "0";

                                                playingPosition++;
                                                if (playingPosition >= vachanList.size())
                                                    playingPosition = 0;

                                                startMediaPlayer(playingPosition);
                                                listView.scrollToPosition(playingPosition);
                                                updatePlayingView();
                                                notifyDataSetChanged();

                                            }
                                        });
                                    }
                                });
                                mediaPlayer.prepareAsync();
                                //mediaPlayer.prepare();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {

                            Toast.makeText(MainActivity.this, "Please connect to an internet", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                /*   if (mp3_file.exists()) {
                            mediaPlayer =new MediaPlayer();

                            mediaPlayer.setDataSource(audioFilePath);
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            trigger = "1";
                            Log.e("TAG" , "TEST_FILE === >" + "Offline");

                        }else {

                            if (Utils.isNetworkAvailable(MainActivity.this)) {
                                mediaPlayer =new MediaPlayer();

                                String SDCardRoot = Environment.getExternalStorageDirectory().toString();
                                downloadFile(ur2, audioItems.get(current_item).getSurat_number()+"."+audioItems.get(current_item).getAyat_number()+".mp3", SDCardRoot+"/MarathiQuran/marathi");


                                mediaPlayer.setDataSource(ur2);
                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                                trigger = "1";
                                Log.e("TAG" , "TEST_FILE === >" + "Online");

                            }else {
                                stopPlayer();
                                mediaPlayer =new MediaPlayer();
                                Toast.makeText(MainActivity.this, "Please connect to an internet", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {

                            if(trigger.equals("0")){

                                trigger = "1";
                                startMediaPlayer(playingPosition);
                                listView.scrollToPosition(playingPosition);
                                updatePlayingView();
                                notifyDataSetChanged();

                            }else {

                                trigger = "0";

                                playingPosition++;
                                if (playingPosition >= vachanList.size())
                                    playingPosition = 0;

                                startMediaPlayer(playingPosition);
                                listView.scrollToPosition(playingPosition);
                                updatePlayingView();
                                notifyDataSetChanged();
                            }

                        }
                    });*/
                }else{

                    File mp3_file;
                    String audioFilePath = "";
                    String SDCardRoot2 = Environment.getExternalStorageDirectory().toString();
                    String filepath = audioItems.get(current_item).getSurat_number() + "." + audioItems.get(current_item).getAyat_number() + ".mp3";

                    if (audioType.equals("marathi")) {
                        audioFilePath = SDCardRoot2 + "/MarathiQuran/marathi/" + filepath;
                        mp3_file = new File(audioFilePath);
                    } else {
                        audioFilePath = SDCardRoot2 + "/MarathiQuran/arabic/" + filepath;
                        mp3_file = new File(audioFilePath);
                    }

                    if (mp3_file.exists()) {

                        try {
                            if (mediaPlayer != null) {
                                mediaPlayer.release();
                                mediaPlayer = null;
                            }

                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(audioFilePath);
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    PhoneStateListener phoneStateListener = new PhoneStateListener() {
                                        @Override
                                        public void onCallStateChanged(int state, String incomingNumber) {

                                            if (state == TelephonyManager.CALL_STATE_RINGING) {

                                               pausePlayer();
                                               updatePlayingView();

                                            } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                                                mediaPlayer.start();
                                                updatePlayingView();
                                            } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {

                                               pausePlayer();
                                               updatePlayingView();

                                            }
                                            super.onCallStateChanged(state, incomingNumber);
                                        }
                                    };

                                    TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                                    if(mgr != null) {
                                        mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                                    }
                                }
                            });

                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {

                                    mediaPlayer.start();
                                    updatePlayingView();

                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mediaPlayer) {

                                            mediaPlayer.stop();
                                            mediaPlayer.release();
                                            mediaPlayer = null;

                                            playingPosition++;
                                            if (playingPosition >= vachanList.size()){
                                                playingPosition = 0;

                                            }

                                            startMediaPlayer(playingPosition);
                                            listView.scrollToPosition(playingPosition);
                                            updatePlayingView();
                                            notifyDataSetChanged();

                                        }
                                    });
                                }
                            });
                            mediaPlayer.prepare();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        /* mediaPlayer.reset();
                        mediaPlayer =new MediaPlayer();
                        mediaPlayer.setDataSource(audioFilePath);
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.prepare();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                            }
                        });

                       // mediaPlayer.start();

                        Log.e("TAG" , "TEST_FILE === >" + "Offline");

                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {

                                playingPosition++;
                                if (playingPosition >= vachanList.size())
                                    playingPosition = 0;

                                startMediaPlayer(playingPosition);
                                listView.scrollToPosition(playingPosition);
                                updatePlayingView();
                                notifyDataSetChanged();
                            }
                        });
*/

                    } else {

                           if (Utils.isNetworkAvailable(activity)) {

                               activity.runOnUiThread(new Runnable() {
                                   public void run() {
                                        Utils.showProgress1("Please wait..." , context);
                                   }
                               });

                               Thread t = new Thread(new Runnable() {

                                   @Override
                                   public void run() {

                                       String SDCardRoot = Environment.getExternalStorageDirectory().toString();
                                       if (audioType.equals("marathi")) {
                                           downloadFile(url, audioItems.get(current_item).getSurat_number() + "." + audioItems.get(current_item).getAyat_number() + ".mp3", SDCardRoot + "/MarathiQuran/marathi");
                                       } else {

                                           downloadFile(url, audioItems.get(current_item).getSurat_number() + "." + audioItems.get(current_item).getAyat_number() + ".mp3", SDCardRoot + "/MarathiQuran/arabic");
                                       }
                                   }
                               });
                               t.start();

                            try {

                                if (mediaPlayer != null) {
                                    mediaPlayer.release();
                                    mediaPlayer = null;
                                }

                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.reset();
                                mediaPlayer.setDataSource(url);
                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        PhoneStateListener phoneStateListener = new PhoneStateListener() {
                                            @Override
                                            public void onCallStateChanged(int state, String incomingNumber) {

                                                if (state == TelephonyManager.CALL_STATE_RINGING) {

                                                    pausePlayer();
                                                    updatePlayingView();

                                                } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                                                    mediaPlayer.start();
                                                    updatePlayingView();
                                                } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {

                                                    pausePlayer();
                                                    updatePlayingView();

                                                }
                                                super.onCallStateChanged(state, incomingNumber);
                                            }
                                        };

                                        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                                        if(mgr != null) {
                                            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                                        }
                                    }
                                });

                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {

                                        mediaPlayer.start();

                                        activity.runOnUiThread(new Runnable() {
                                            public void run() {
                                               Utils.hideProgress1();
                                            }
                                        });

                                        updatePlayingView();

                                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mediaPlayer) {
                                                
                                                mediaPlayer.stop();
                                                mediaPlayer.release();
                                                mediaPlayer = null;

                                                playingPosition++;
                                                if (playingPosition >= vachanList.size()){
                                                    playingPosition = 0;
                                                }

                                                startMediaPlayer(playingPosition);
                                                listView.scrollToPosition(playingPosition);
                                                updatePlayingView();
                                                notifyDataSetChanged();

                                            }
                                        });
                                    }
                                });
                                mediaPlayer.prepareAsync();
                              //  mediaPlayer.prepare();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(MainActivity.this, "Please connect to an internet", Toast.LENGTH_SHORT).show();
                        }

                          /* if (Utils.isNetworkAvailable(MainActivity.this)) {

                            Utils.showProgress1("Please wait" , context);
                            mediaPlayer =new MediaPlayer();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                               try {
                                   mediaPlayer.setDataSource(url);
                                   mediaPlayer.prepare();
                                   mediaPlayer.start();
                               } catch (IOException e) {
                                   e.printStackTrace();
                               }

                          //  Utils.hideProgress1();

                            Log.e("TAG" , "TEST_FILE === >" + "Online");

                            String SDCardRoot = Environment.getExternalStorageDirectory().toString();

                            if(audioType.equals("marathi")){
                                downloadFile(url,  audioItems.get(current_item).getSurat_number()+"."+audioItems.get(current_item).getAyat_number()+".mp3", SDCardRoot+"/MarathiQuran/marathi");
                            }else {
                                downloadFile(url, audioItems.get(current_item).getSurat_number()+"."+audioItems.get(current_item).getAyat_number()+".mp3", SDCardRoot+"/MarathiQuran/arabic");
                            }

                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    playingPosition++;
                                    if (playingPosition >= vachanList.size())
                                        playingPosition = 0;

                                    startMediaPlayer(playingPosition);
                                    listView.scrollToPosition(playingPosition);
                                    updatePlayingView();
                                    notifyDataSetChanged();
                                }
                            });

                        }else {

                            Toast.makeText(MainActivity.this, "Please connect to an internet", Toast.LENGTH_SHORT).show();
                        }*/

                    }
                }
        }

        private void releaseMediaPlayer () {
                if (null != playingHolder) {
                    updateNonPlayingView(playingHolder);
                } else {
                    updatePlayingView();
                }
                mediaPlayer.release();
                mediaPlayer = null;
                playingPosition = -1;

            }

    }

   /* @Override
    protected void onResume() {
        super.onResume();

    }*/

       /*   @Override
    protected void onPause() {
        super.onPause();
        audioItemAdapter.stopPlayer();
    }*/

    void downloadFile(String dwnload_file_path, String fileName, String pathToSave) {
        int downloadedSize = 0;
        int totalSize = 0;

        try {
            if (isStoragePermissionGranted()) {
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }else {

                    URL url = new URL(dwnload_file_path);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();

                    File myDir;
                    myDir = new File(pathToSave);
                    myDir.mkdirs();

                    String mFileName = fileName;
                    File file = new File(myDir, mFileName);

                    FileOutputStream fileOutput = new FileOutputStream(file);
                    InputStream inputStream = urlConnection.getInputStream();

                    byte[] buffer = new byte[1024];
                    int bufferLength = 0;

                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fileOutput.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;

                    }

                    fileOutput.close();

                }
            }


        } catch (final MalformedURLException e) {
            // showError("Error : MalformedURLException " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            // showError("Error : IOException " + e);
            e.printStackTrace();
        } catch (final Exception e) {
            // showError("Error : Please check your internet connection " + e);
        }
    }

    public  String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

}






/*

    final ProgressDialog ringProgressDialog = ProgressDialog.show(context, "Please wait ...", "Fetching your contact..", true);
                               ringProgressDialog.setCancelable(true);
                                       new Thread(new Runnable() {
@Override
public void run() {
        try {
        ringProgressDialog.show();
        } catch (Exception e) {

        }
        ringProgressDialog.dismiss();
        }
        }).start();


        runOnUiThread(new Runnable() {

@Override
public void run() {
        Utils.showProgress1("TEST ..." , context);

        }
        });*/
