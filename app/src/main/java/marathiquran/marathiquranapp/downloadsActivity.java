package marathiquran.marathiquranapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.List;

public class downloadsActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView Downloadlist;
    DownloadAdapter downloadAdapter;
    SqlLiteDbHelper dbHelper;
    List<Surat> suratList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);
        toolbar=findViewById(R.id.id_Toolbartrash);
        toolbar.setTitle("Surah Downloads");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Downloadlist = findViewById(R.id.surah_list);
        Downloadlist.setHasFixedSize(true);
        Downloadlist.setLayoutManager(new LinearLayoutManager(downloadsActivity.this, LinearLayoutManager.VERTICAL, false));

        dbHelper = new SqlLiteDbHelper(this);
        dbHelper.openDataBase();

        suratList = dbHelper.getAllSurat();

        downloadAdapter=new DownloadAdapter(getApplicationContext(),suratList);
        Downloadlist.setAdapter(downloadAdapter);

    }
}