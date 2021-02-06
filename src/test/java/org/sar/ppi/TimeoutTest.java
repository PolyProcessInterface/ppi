package org.sar.ppi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.OutputStream;
import java.util.Scanner;
import org.junit.Assume;
import org.junit.Test;
import org.sar.ppi.mpi.MpiRunner;
import org.sar.ppi.peersim.PeerSimRunner;

/**
 * TimeoutTest
 */
public class TimeoutTest extends RedirectedTest {
	long before;
	long after;
	final boolean FALSE = false;
	final long DELAY = 100;

	@Override
	public void init(String[] args) {
		if (infra.getId() == 0) {
			infra.serialThreadRun(() -> runTest());
		} else {
			infra.exit();
		}
	}

	public void runTest() {
		before = infra.currentTime();
		try {
			boolean result = infra.waitFor(() -> FALSE == true, DELAY);
			after = infra.currentTime();
			long diff = after - before;
			System.out.printf("%d %s\n", diff, result ? "true" : "false");
		} catch (InterruptedException e) {
			System.out.println("error");
		}
		infra.exit();
	}

	@Test
	public void mpi() {
		Assume.assumeTrue(EnvUtils.mpirunExist());
		Ppi.main(this.getClass(), new MpiRunner(), new String[0], 2);
		verify(outContent);
	}

	@Test
	public void peersim() {
		Ppi.main(this.getClass(), new PeerSimRunner(), new String[0], 2);
		verify(outContent);
	}

	public void verify(OutputStream os) {
		Scanner scanner = new Scanner(os.toString());
		long diff = scanner.nextLong();
		assertTrue(diff >= DELAY);
		assertEquals("false", scanner.next());
	}
}
