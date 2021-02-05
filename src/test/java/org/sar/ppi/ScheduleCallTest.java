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
 * ExampleNodeProces
 */
public class ScheduleCallTest extends RedirectedTest {
	long before;
	long after;

	@Override
	public void init(String[] args) {
		if (infra.getId() == 0) {
			Object[] callArgs = { new Integer(42), "test" };
			before = infra.currentTime();
			infra.scheduleCall("runTest", callArgs, 100);
		} else {
			infra.exit();
		}
	}

	public void runTest(Integer arg1, String arg2) {
		after = infra.currentTime();
		long diff = after - before;
		System.out.printf("%d %d %s\n", diff, arg1, arg2);
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
		assertTrue(diff >= 100);
		assertTrue(diff < 150);
		assertEquals(42, scanner.nextInt());
		assertEquals("test", scanner.next());
	}
}
