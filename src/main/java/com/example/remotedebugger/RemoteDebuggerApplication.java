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

//		long startTime = System.nanoTime();
		OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
		double startCpuLoad = osBean.getSystemCpuLoad();
		long startTime = System.nanoTime();
		SpringApplication.run(RemoteDebuggerApplication.class, args);

		double endCpuLoad = osBean.getSystemCpuLoad();
		long endTime = System.nanoTime();
		long overheadTime = endTime - startTime;
		double cpuLoad = endCpuLoad - startCpuLoad;
		System.out.println(" time for class "  + ": " + overheadTime*1.0e-9 + " seconds");
		System.out.println("CPU load during operation: " + cpuLoad);

	}

}
