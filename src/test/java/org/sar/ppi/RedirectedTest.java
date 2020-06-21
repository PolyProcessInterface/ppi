package org.sar.ppi;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;

public abstract class RedirectedTest extends NodeProcess {
	
	protected final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	protected final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	protected final PrintStream originalOut = System.out;
	protected final PrintStream originalErr = System.err;
	protected Scanner scanner = null;

	public String nextNonEmpty() throws EOFException {
		if (scanner == null) {
			scanner = new Scanner(outContent.toString());
		}
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (!line.isEmpty()) {
				return line;
			}
		}
		throw new EOFException("No more lines");
	}

	public boolean hasNextNonEmpty() {
		return scanner.hasNext("[^\\s]");
	}
	
	@Before
	public void setUpStreams() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
		scanner = null;
	}

	@After
	public void restoreStreams() {
		System.setOut(originalOut);
		System.setErr(originalErr);
	}
}