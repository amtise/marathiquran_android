package marathiquran.marathiquranapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.Downloadholder> {

    private Context context;
    private List<Surat> suratList;
    String fileName;
    String PATH;

    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;

    DownloadAdapter(Context context,List<Surat> suratList){
        this.context=context;
        this.suratList = suratList;
    }


    @NonNull
    @Override
    public Downloadholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Downloadholder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dowloadslayout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Downloadholder holder, int position) {
        holder.tvsurahName.setText(suratList.get(position).getSurat_number()+")"+suratList.get(position).getMarathi_name()+"-"+suratList.get(position).getArabic_name());
    }

    @Override
    public int getItemCount() {
        return suratList.size();
    }

    public class Downloadholder extends RecyclerView.ViewHolder {
        TextView tvsurahName;
        ImageView icdowload;
        public Downloadholder(@NonNull View itemView) {
            super(itemView);
            tvsurahName=itemView.findViewById(R.id.surahName);
            icdowload=itemView.findViewById(R.id.ic_download);

            icdowload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fileName="https://marathiquran.com/media/"+suratList.get(getAdapterPosition()).getFull_audio_file();

                    new DownloadFileFromURL().execute(fileName);
                }
            });
        }

    }

    public  class DownloadFileFromURL  extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Bar Dialog
         * */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Downloading file in background thread
         * */
        protected String doInBackground(String... f_url) {
            int count;
            try {
                Log.v("URL",f_url[0]);
                URL url = new URL(f_url[0]);
                Log.e(String.valueOf(f_url),"url");
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();
                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                // Output stream to write file

                PATH = Environment.getExternalStorageDirectory() + "/marathiquran/";
                Log.v("log_tag", "PATH: " + PATH);
                File file = new File(PATH);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File outputFile = new File(file, fileName);
                FileOutputStream output = new FileOutputStream(outputFile);

               /*OutputStream output = new FileOutputStream("/sdcard/"
                        + fileName);*/

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();


            }
            catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded


            // Displaying downloaded image into image view
            // Reading image path from sdcard
            String imagePath = Environment.getExternalStorageDirectory()
                    .toString() + fileName;
            // setting downloaded into image view
            // my_image.setImageDrawable(Drawable.createFromPath(imagePath));
            Toast.makeText(context,"File downloaded and saved to directory==>"+PATH,Toast.LENGTH_LONG).show();
        }

    }
}
