package org.sar.ppi;

import static org.junit.Assert.assertEquals;

import java.util.Scanner;
import org.junit.Assume;
import org.junit.Test;
import org.sar.ppi.mpi.MpiRunner;
import org.sar.ppi.peersim.PeerSimRunner;

/**
 * ExampleNodeProces
 */
public class ArgsTest extends RedirectedTest {

	@Override
	public void init(String[] args) {
		if (infra.getId() == 0) {
			System.out.println(args[0]);
			System.out.println(args[1]);
		}
		infra.exit();
	}

	@Test
	public void mpi() {
		Assume.assumeTrue(EnvUtils.mpirunExist());
		String[] args = { "test", "test" };
		Ppi.main(this.getClass(), new MpiRunner(), args, 2);
		int i = 0;
		Scanner scanner = new Scanner(outContent.toString());
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.isEmpty()) {
				continue;
			} else {
				i++;
				assertEquals("test", line);
			}
		}
		assertEquals(2, i);
	}

	@Test
	public void peersim() {
		String[] args = { "test", "test" };
		Ppi.main(this.getClass(), new PeerSimRunner(), args, 2);
		int i = 0;
		Scanner scanner = new Scanner(outContent.toString());
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.isEmpty()) {
				continue;
			} else {
				i++;
				assertEquals("test", line);
			}
		}
		assertEquals(4, i);
	}
}
