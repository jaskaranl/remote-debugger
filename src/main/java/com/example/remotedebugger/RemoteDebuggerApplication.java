package com.example.remotedebugger;
import com.sun.management.OperatingSystemMXBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import java.util.Scanner;

@SpringBootApplication
public class RemoteDebuggerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RemoteDebuggerApplication.class, args);
	}

}
