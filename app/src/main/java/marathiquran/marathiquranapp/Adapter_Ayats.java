package marathiquran.marathiquranapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Adapter_Ayats extends RecyclerView.Adapter<Adapter_Ayats.ViewHolder>{

    List<Vachan> vachanList;
    Context context;

    boolean isPLAYING = false;
    Vachan vachan ;
    public int currentlyPlaying;
    int row_index;
    MediaPlayer mPlayer ;

    private Handler mHandler = new Handler();

    Adapter_Ayats.ViewHolder holder2;

    String url = "";
    String compare_string = "url";
    String play_str = "";


    public Adapter_Ayats(Context appContext, List<Vachan> vachanList) {
        this.vachanList = vachanList;
        this.context = appContext;
    }

    @NonNull
    @Override
    public Adapter_Ayats.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final Adapter_Ayats.ViewHolder holder, final int position) {
        mPlayer =new MediaPlayer();
        final Vachan vachan = vachanList.get(position);

        holder. vachan_version.setText(vachan.getSurat_number()+":"+vachanList.get(position).getAyat_number());
        holder.arabic_text.setText(vachan.getArabic_text());
        holder. marathi_text.setText(vachan.getMarathi_text());

        holder.ic_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                compare_string = vachanList.get(position).getMarathi_audio(); //1.0

                if (!isPLAYING) {

                    isPLAYING = true;
                    currentlyPlaying = position;

                    playaudio(currentlyPlaying);

                    holder.ic_play.setVisibility(View.GONE);
                    holder.ic_pause.setVisibility(View.VISIBLE);

                } else {
                    isPLAYING = false;
                    stopPlaying();
                }
            }
        });

        holder.ic_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPLAYING = false;
                holder.ic_play.setVisibility(View.VISIBLE);
                holder.ic_pause.setVisibility(View.GONE);

                stopPlaying();

            }
        });


        holder.SeekBarTestPlay.setMax(mPlayer.getDuration());


        ((AppCompatActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(mPlayer != null){
                    int mCurrentPosition = mPlayer.getDuration() / 1000;
                    holder.SeekBarTestPlay.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });


        holder.SeekBarTestPlay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int seeked_progess;
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

                seeked_progess = progress;
                seeked_progess = seeked_progess * 1000;

                if(mPlayer != null && fromTouch){
                    mPlayer.seekTo(progress * 1000);
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPlayer.seekTo(seeked_progess);

            }
        });



        /*if(compare_string.equals(play_str)){

            holder.ic_play.setVisibility(View.GONE);
            holder.ic_pause.setVisibility(View.VISIBLE);

        }else {
            holder.ic_play.setVisibility(View.VISIBLE);
            holder.ic_pause.setVisibility(View.GONE);
        }*/

    }

    @Override
    public int getItemCount() {
        return vachanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ic_play , ic_pause ,ic_share , ic_copy;
        TextView vachan_version , marathi_text,arabic_text;
        SeekBar SeekBarTestPlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

          //  ic_play = itemView.findViewById(R.id.ic_play);
            ic_pause = itemView.findViewById(R.id.ic_pause);
            ic_share = itemView.findViewById(R.id.ic_share);
            ic_copy = itemView.findViewById(R.id.ic_copy);

           // vachan_version = itemView.findViewById(R.id.vachan_version);
            marathi_text = itemView.findViewById(R.id.marathi_text);
            arabic_text = itemView.findViewById(R.id.arabic_text);
          //  SeekBarTestPlay = itemView.findViewById(R.id.SeekBarTestPlay);

        }
    }

    private void stopPlaying() {

        if (mPlayer != null) {

            mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;

        }
    }

    public void playaudio(final int curposition){

        url="https://marathiquran.com/media/"+vachanList.get(curposition).getMarathi_audio().replace(" ", "%20"); ///1.0
        mPlayer =new MediaPlayer();

        try {

            mPlayer.setDataSource(url);
            mPlayer.prepare();
            mPlayer.start();

            isPLAYING=false;

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {

                    notifyDataSetChanged();

                    currentlyPlaying++;
                    if(currentlyPlaying >= vachanList.size())
                    currentlyPlaying = 0;

                    playaudio(currentlyPlaying);
                    notifyDataSetChanged();

                }
            });

        } catch (IOException e) {
        }

    }

}
