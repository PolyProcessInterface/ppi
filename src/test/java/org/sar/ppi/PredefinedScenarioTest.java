package org.sar.ppi;

import static org.junit.Assert.assertTrue;

import java.io.File;
import org.junit.Assume;
import org.junit.Test;
import org.sar.ppi.events.Message;
import org.sar.ppi.mpi.MpiRunner;
import org.sar.ppi.peersim.PeerSimRunner;

public class PredefinedScenarioTest extends NodeProcess {
	static String fileName = System.getProperty("user.dir") + "/testeJson.json";

	@Override
	public void processMessage(Message message) {
		super.processMessage(message);
		System.out.println(" I am not called ");
	}

	@Override
	public void init(String[] args) {
		System.out.println("my id " + infra.getId());
	}

	public void callMe(String arg1, Integer arg2) {
		System.out.println("callMe by " + infra.getId());
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			infra.exit();
			System.out.printf("%d called exit\n", infra.getId());
		}
	}

	@Test
	public void mpi() {
		Assume.assumeTrue(EnvUtils.mpirunExist());
		Ppi.main(
			this.getClass(),
			new MpiRunner(),
			new String[0],
			3,
			new File("src/test/resources/PredefinedScenarioTest.json")
		);
		assertTrue(true);
		System.out.println("Teste Sceneario from Json mpi ok");
	}

	@Test
	public void peersim() {
		Ppi.main(
			this.getClass(),
			new PeerSimRunner(),
			new String[0],
			3,
			new File("src/test/resources/PredefinedScenarioTest.json")
		);
		assertTrue(true);
		System.out.println("Teste Sceneario from Json Peersim ok");
	}
}
