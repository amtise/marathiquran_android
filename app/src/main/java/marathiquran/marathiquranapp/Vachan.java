package marathiquran.marathiquranapp;

public class Vachan {
    int id;
    int ayat_number;
    int surat_id;

    public int getSurat_number() {
        return surat_number;
    }

    public void setSurat_number(int surat_number) {
        this.surat_number = surat_number;
    }

    int surat_number;
    String arabic_text,marathi_text,arabic_audio,marathi_audio;


    public Vachan(int id,int ayat_number,String arabic_text,String marathi_text,String arabic_audio,String marathi_audio,int surat_id,int surat_number){
        this.id=id;
        this.ayat_number=ayat_number;
        this.arabic_text=arabic_text;
        this.marathi_text=marathi_text;
        this.arabic_audio=arabic_audio;
        this.marathi_audio=marathi_audio;
        this.surat_id=surat_id;
        this.surat_number=surat_number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAyat_number() {
        return ayat_number;
    }

    public void setAyat_number(int ayat_number) {
        this.ayat_number = ayat_number;
    }

    public int getSurat_id() {
        return surat_id;
    }

    public void setSurat_id(int surat_id) {
        this.surat_id = surat_id;
    }

    public String getArabic_text() {
        return arabic_text;
    }

    public void setArabic_text(String arabic_text) {
        this.arabic_text = arabic_text;
    }

    public String getMarathi_text() {
        return marathi_text;
    }

    public void setMarathi_text(String marathi_text) {
        this.marathi_text = marathi_text;
    }

    public String getArabic_audio() {
        return arabic_audio;
    }

    public void setArabic_audio(String arabic_audio) {
        this.arabic_audio = arabic_audio;
    }

    public String getMarathi_audio() {
        return marathi_audio;
    }

    public void setMarathi_audio(String marathi_audio) {
        this.marathi_audio = marathi_audio;
    }
}
