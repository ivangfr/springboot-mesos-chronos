package com.mycompany.simpleservice.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class JobRunner implements CommandLineRunner, ExitCodeGenerator {

    @Value("${exit.code:0}")
    private int exitCode; // 0 = success or !0 = failure.

    @Value("${sleep:5000}")
    private long sleep; // in milliseconds

    @Override
    public void run(String... arg0) throws Exception {
        log.info("Processing job at {} with SLEEP = {} ms", LocalDateTime.now(), sleep);

        Thread.sleep(sleep);

        log.info("Job finishing with exit code = {}", exitCode);
        System.exit(exitCode);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}