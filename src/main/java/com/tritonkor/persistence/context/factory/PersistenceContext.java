package com.tritonkor.persistence.context.factory;

import com.tritonkor.persistence.context.impl.AnswerContext;
import com.tritonkor.persistence.context.impl.QuestionContext;
import com.tritonkor.persistence.context.impl.ReportContext;
import com.tritonkor.persistence.context.impl.ResultContext;
import com.tritonkor.persistence.context.impl.TagContext;
import com.tritonkor.persistence.context.impl.TestContext;
import com.tritonkor.persistence.context.impl.UserContext;
import org.springframework.stereotype.Component;

@Component
public class PersistenceContext {

    public final TestContext tests;
    public final QuestionContext questions;
    public final AnswerContext answers;
    public final ResultContext results;
    public final ReportContext reports;
    public final TagContext tags;
    public final UserContext users;

    public PersistenceContext(
            UserContext users,
            AnswerContext answers,
            QuestionContext questions,
            ReportContext reports,
            ResultContext results,
            TagContext tags,
            TestContext tests
            ) {
        this.users = users;
        this.tags = tags;
        this.answers = answers;
        this.questions = questions;
        this.reports = reports;
        this.results = results;
        this.tests = tests;
    }
}
