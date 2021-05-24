package com.as4.mrvalet;


/**
 * Created by appleuser on 5/17/17.
 */

public class ErrorHandling {


    public static ErrorHandling errorHandling;
    private final int SUCCESS = 0;

    public static ErrorHandling getInstance() {
        if (errorHandling == null)
            return errorHandling = new ErrorHandling();
        return errorHandling;
    }

    public boolean checkError(int error, String msg) {
        try {
            switch (error) {
                case SUCCESS:
                    return true;

                default:
                    return false;


            }
        } catch (Exception e) {
        }
        return false;
    }
}

