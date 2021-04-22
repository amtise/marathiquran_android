package marathiquran.marathiquranapp;

import java.io.Serializable;

public class Surat implements Serializable {
    private int id,surat_number;
    private String surat_name,arabic_name,marathi_name,makki_or_madni,starting_ayat,ending_ayat,full_audio_file;


    public Surat(String marathi_name,String arabic_name)
    {
        this.marathi_name=marathi_name;
        this.arabic_name=arabic_name;
    }
    public Surat(int surat_id,int surat_number,String marathi_name,String arabic_name,String full_audio_file){
        this.id=surat_id;
        this.surat_number=surat_number;
        this.marathi_name=marathi_name;
        this.arabic_name=arabic_name;
        this.full_audio_file=full_audio_file;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSurat_number() {
        return surat_number;
    }

    public void setSurat_number(int surat_number) {
        this.surat_number = surat_number;
    }

    public String getSurat_name() {
        return surat_name;
    }

    public void setSurat_name(String surat_name) {
        this.surat_name = surat_name;
    }

    public String getArabic_name() {
        return arabic_name;
    }

    public void setArabic_name(String arabic_name) {
        this.arabic_name = arabic_name;
    }

    public String getMarathi_name() {
        return marathi_name;
    }

    public void setMarathi_name(String marathi_name) {
        this.marathi_name = marathi_name;
    }

    public String getMakki_or_madni() {
        return makki_or_madni;
    }

    public void setMakki_or_madni(String makki_or_madni) {
        this.makki_or_madni = makki_or_madni;
    }

    public String getStarting_ayat() {
        return starting_ayat;
    }

    public void setStarting_ayat(String starting_ayat) {
        this.starting_ayat = starting_ayat;
    }

    public String getEnding_ayat() {
        return ending_ayat;
    }

    public void setEnding_ayat(String ending_ayat) {
        this.ending_ayat = ending_ayat;
    }

    public String getFull_audio_file() {
        return full_audio_file;
    }

    public void setFull_audio_file(String full_audio_file) {
        this.full_audio_file = full_audio_file;
    }

    @Override
    public String toString() {
        return surat_number+ " ) " + marathi_name+ " -" +arabic_name;
    }
}
