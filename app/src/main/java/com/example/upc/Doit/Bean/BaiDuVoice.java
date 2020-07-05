package com.example.upc.Doit.Bean;

import java.util.List;



public class BaiDuVoice {


    public OriginResultBean origin_result;
    public int error;
    public String best_result;
    public String result_type;
    public List<String> results_recognition;

    public static class OriginResultBean {

        public long corpus_no;
        public int err_no;
        public ResultBean result;
        public String sn;

        public static class ResultBean {
            public List<String> word;
        }

        @Override
        public String toString() {
            return "OriginResultBean{" +
                    "corpus_no=" + corpus_no +
                    ", err_no=" + err_no +
                    ", result=" + result +
                    ", sn='" + sn + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BaiDuVoice{" +
                "origin_result=" + origin_result +
                ", error=" + error +
                ", best_result='" + best_result + '\'' +
                ", result_type='" + result_type + '\'' +
                ", results_recognition=" + results_recognition +
                '}';
    }
}
