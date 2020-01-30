package com.github.danshan.asrassist.cli;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
public class AsrApplicationRunner implements CommandLineRunner, ExitCodeGenerator {

	private final AsrCommand asrCommand;

	private final CommandLine.IFactory factory; // auto-configured to inject PicocliSpringFactory

	private int exitCode;

	public AsrApplicationRunner(AsrCommand asrCommand, CommandLine.IFactory factory) {
		this.asrCommand = asrCommand;
		this.factory = factory;
	}

	@Override
	public void run(String... args) throws Exception {
		exitCode = new CommandLine(asrCommand, factory).execute(args);
	}

	@Override
	public int getExitCode() {
		return exitCode;
	}
}
