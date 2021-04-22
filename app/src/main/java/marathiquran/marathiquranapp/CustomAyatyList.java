package marathiquran.marathiquranapp;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

public class CustomAyatyList extends ArrayAdapter<Vachan> {

    Context mCtx;
    int listLayoutRes;
    List<Vachan> vachanList;
    int pos=0;
    MediaPlayer mPlayer ;
    boolean playing ;
    boolean isPLAYING = false;
    int suratno,ayatno;
    Vachan vachan ;
    public int currentlyPlaying;
    ImageView ic_play;
     ImageView ic_pause;



    public CustomAyatyList(Context mCtx, int listLayoutRes, List<Vachan> vachanList) {
        super(mCtx, listLayoutRes, vachanList);
        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.vachanList = vachanList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(listLayoutRes, null);

        vachan  = vachanList.get(position);

       // TextView vachan_version = view.findViewById(R.id.vachan_version);
        TextView arabic_text = view.findViewById(R.id.arabic_text);
        TextView marathi_text = view.findViewById(R.id.marathi_text);

      //  vachan_version.setText(vachan.getSurat_number()+":"+vachan.getAyat_number());
        arabic_text.setText(vachan.getArabic_text());
        marathi_text.setText(vachan.getMarathi_text());


        ImageView ic_share = view.findViewById(R.id.ic_share);
     //   ic_play = view.findViewById(R.id.ic_play);
        ic_pause = view.findViewById(R.id.ic_pause);
        final ImageView ic_copy=view.findViewById(R.id.ic_copy);


        //adding a clicklistener to button
        ic_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String marathi_text=vachan.getMarathi_text();
                String arabic_text=vachan.getArabic_text();
                suratno=vachan.getSurat_number();
                ayatno=vachan.getAyat_number();
                String url="https://marathiquran.com?surat_no="+suratno+"&ayat_no="+ayatno;
                String shareBody=arabic_text+"\n"+marathi_text+"\n"+url;
                Intent shareIntent =   new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Marathiquran.com");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,shareBody);
                getContext().startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });


        ic_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String marathi_text=vachan.getMarathi_text();
                String arabic_text=vachan.getArabic_text();
                String copytext=arabic_text+"\n"+marathi_text;
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Marathi Quran",copytext);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(),"Marathi Quran copied",Toast.LENGTH_LONG).show();
            }
        });

        ic_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isPLAYING) {
                    isPLAYING = true;
                    currentlyPlaying = position;
                    playaudio(currentlyPlaying);
                    ic_play.setVisibility(View.GONE);
                    ic_pause.setVisibility(View.VISIBLE);

                } else {
                    isPLAYING = false;
                    stopPlaying();
                }
            }
        });

        ic_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPLAYING = false;

                ic_play.setVisibility(View.VISIBLE);
                ic_pause.setVisibility(View.GONE);

                stopPlaying();

            }
        });

        return view;
    }


    private void stopPlaying() {

        if (mPlayer != null) {

            mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;

        }
    }


    public void playaudio(int curposition){


        final String url="https://marathiquran.com/media/"+vachanList.get(curposition).getMarathi_audio().replace(" ", "%20");
        mPlayer =new MediaPlayer();

        try {
            mPlayer.setDataSource(url);
            mPlayer.prepare();
            mPlayer.start();

            isPLAYING=false;

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {

                    ic_play.setVisibility(View.VISIBLE);
                    ic_pause.setVisibility(View.GONE);

                    currentlyPlaying++;
                    if(currentlyPlaying >= vachanList.size())
                        currentlyPlaying = 0;

                    playaudio(currentlyPlaying);

                    ic_play.setVisibility(View.GONE);
                    ic_pause.setVisibility(View.VISIBLE);



                }
            });

        } catch (IOException e) {
        }


    }


}