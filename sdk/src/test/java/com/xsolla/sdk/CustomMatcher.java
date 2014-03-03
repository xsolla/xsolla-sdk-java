/*
package com.xsolla.sdk;

import com.xsolla.sdk.exception.*;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class CustomMatcher extends TypeSafeMatcher<com.xsolla.sdk.exception.Exception> {

    public static CustomMatcher hasCode(int code) {
        return new CustomMatcher(code);
    }

    private int foundErrorCode;
    private final int expectedErrorCode;

    private CustomMatcher(int expectedErrorCode) {
        this.expectedErrorCode = expectedErrorCode;
    }

    @Override
    protected boolean matchesSafely(final com.xsolla.sdk.exception.Exception exception) {
        foundErrorCode = exception.getCode();
        return foundErrorCode == expectedErrorCode;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(foundErrorCode)
                .appendText(" was found instead of ")
                .appendValue(expectedErrorCode);
    }
}
*/