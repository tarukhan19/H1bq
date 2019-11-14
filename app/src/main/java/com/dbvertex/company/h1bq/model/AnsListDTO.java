package com.dbvertex.company.h1bq.model;

public class AnsListDTO {

    private String ans;
    private int ansId;
    public AnsListDTO() {
    }
    //int imageid,,String imagepath
    public AnsListDTO(String ans) {
        this.ans = ans;
//        this.Imageid=Imageid;
//        this.imagepath=imagepath;
    }




    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }


    public int getAnsid() {
        return ansId;
    }

    public void setAnsid(int ansId) {
        this.ansId = ansId;
    }
}
