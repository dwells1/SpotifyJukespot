package com.example.jukespot.spotifyjukespot.Classes;

import java.util.List;

/**
 * Created by Dominique on 11/5/2017.
 */

public class JukeResponse {
    private String result;
    private List<JukeBoxResponse> rows;

    public JukeResponse(String result, List<JukeBoxResponse> rows){
        this.result = result;
        this.rows = rows;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<JukeBoxResponse> getRows() {
        return rows;
    }

    public void setRows(List<JukeBoxResponse> rows) {
        this.rows = rows;
    }
}
