package com.github.florent37.rxandroidorm.sample;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.Callable;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by florentchampigny on 05/05/2017.
 */

public class RxJavaSchedulersTestRule implements TestRule {
    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                resetPlugins();

                RxJavaPlugins.setIoSchedulerHandler(new Function<Scheduler, Scheduler>() {
                    @Override
                    public Scheduler apply(@NonNull Scheduler scheduler) throws Exception {
                        return Schedulers.trampoline();
                    }
                });

                base.evaluate();

                resetPlugins();
            }
        };
    }

    private void resetPlugins() {
        RxJavaPlugins.reset();
    }
}