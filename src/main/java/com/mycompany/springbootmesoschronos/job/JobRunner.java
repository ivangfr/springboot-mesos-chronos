package com.mycompany.springbootmesoschronos.job;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

@Component
public class JobRunner implements CommandLineRunner, ExitCodeGenerator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${exit.code:0}")
    private int exitCode; // 0 = success or !0 = failure.

    @Value("${sleep:5000}")
    private long sleep; // in milliseconds

    @Override
    public void run(String... arg0) throws Exception {
        logger.info("Processing job at {} with SLEEP = {} ms", new Date(), sleep);

        Thread.sleep(sleep);

        logger.info("Job finishing with exit code = {}", exitCode);
        System.exit(exitCode);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

}