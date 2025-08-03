package org.learning.matchers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;

public class EndsWithPattern extends StringValuePattern {

    private final String testValue;
    private final String endWithSuffix;

    public EndsWithPattern(@JsonProperty("endsWith") String testValue, String endsWithSuffix) {
        super(testValue);
        this.testValue = testValue;
        this.endWithSuffix = endsWithSuffix;

    }

    @Override
    public MatchResult match(String s) {
        var outcome = testValue != null && endWithSuffix != null && testValue.endsWith(endWithSuffix);
        return MatchResult.of(outcome);
    }
}
