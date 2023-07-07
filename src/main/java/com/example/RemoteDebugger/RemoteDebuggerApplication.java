package com.example.RemoteDebugger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RemoteDebuggerApplication {

	public static void main(String[] args)
	{
		// 2769149436
//		long startTime = System.nanoTime();
		SpringApplication.run(RemoteDebuggerApplication.class, args);
//		long endTime = System.nanoTime();
//		long overheadTime = endTime - startTime;
//		System.out.println("Overhead time for class "  + ": " + overheadTime + " nanoseconds");
	}

}
