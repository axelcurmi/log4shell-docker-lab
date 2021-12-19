package com.axelcurmi.log4jvuln;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Log4jVulnerableApplication {

	/**
	 * https://snyk.io/blog/log4j-rce-log4shell-vulnerability-cve-2021-4428/
	 */
	public static void main(String[] args) {
		SpringApplication.run(Log4jVulnerableApplication.class, args);
	}

}
