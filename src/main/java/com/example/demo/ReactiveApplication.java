package com.example.demo;
import javassist.*;
import com.sun.tools.attach.VirtualMachine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.management.ManagementFactory;

public class ReactiveApplication {

	public static void ma() {
		String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
		try {
			ClassPool cp=ClassPool.getDefault();

		VirtualMachine vm = VirtualMachine.attach(pid);
		vm.loadAgent("/Users/jaskaran.kamboj/Downloads/demo/my-java--agent.jar");

//		vm.detach();
	}
	catch (Exception e)
	{
		throw new RuntimeException(e) ;
	}

//		SpringApplication.run(ReactiveApplication.class, args);
	}

}
