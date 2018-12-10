package com.avasiliuk.testtask.utils;

import lombok.experimental.UtilityClass;
import org.h2.jdbc.JdbcSQLException;
import org.jooq.exception.DataAccessException;

import java.util.regex.Pattern;

@UtilityClass
public class Util {
    public boolean checkConstraintViolationOrThrow(DataAccessException e, String constraintName) {
        if (e.getCause() instanceof JdbcSQLException) {
            final JdbcSQLException cause = (JdbcSQLException) e.getCause();
            final String s = cause.getMessage().toLowerCase();
            if (Pattern.compile("\\W" + constraintName.toLowerCase() + "\\W").matcher(s).find()) {
                return true;
            }
        }
        throw e;
    }
}
