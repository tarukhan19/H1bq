package com.dbvertex.company.h1bq.model;

public class Poll_OptionData {

    String option1TV,optionId;
   // int imgradio;
    private boolean isSelected,isChecked = false;

    public Poll_OptionData() {
       // this.imgradio = imgradio;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getOptionTV() {
        return option1TV;
    }

    public void setOptionTV(String option1TV) {
        this.option1TV = option1TV;
    }


    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

//    public int getImgradio() {
//        return imgradio;
//    }
//
//    public void setImgradio(int imgradio) {
//        this.imgradio = imgradio;
//    }

    //    public String getaTV() {
//        return aTV;
//    }
//
//    public void setaTV(String aTV) {
//        this.aTV = aTV;
//    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
