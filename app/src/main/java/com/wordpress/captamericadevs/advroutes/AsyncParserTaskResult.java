package com.wordpress.captamericadevs.advroutes;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Parker on 6/6/2016.
 */
public class AsyncParserTaskResult {
    private List<List<HashMap<String, String>>> result;

    public AsyncParserTaskResult(List<List<HashMap<String, String>>> result) {

        this.result = result;
    }

    public List<List<HashMap<String, String>>> getResult() {
        return result;
    }
}