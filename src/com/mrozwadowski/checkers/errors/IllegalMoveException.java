package com.mrozwadowski.checkers.errors;

/**
 * Exception raised when a player tries to perform a move which is against the rules.
 *
 * Created by rozwad on 18.12.16.
 */
public class IllegalMoveException extends Exception {
    public IllegalMoveException(String s) {
        super(s);
    }
}
