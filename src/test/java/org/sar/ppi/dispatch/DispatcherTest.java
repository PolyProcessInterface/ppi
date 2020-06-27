package org.sar.ppi.dispatch;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sar.ppi.NodeProcess;
import org.sar.ppi.events.Message;

public class DispatcherTest {
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	public class TestMessage extends Message {
		private static final long serialVersionUID = 1L;

		public TestMessage(int idsrc, int iddest) {
			super(idsrc, iddest);
		}
	}

	public class UnhandledMessage extends Message {
		private static final long serialVersionUID = 1L;

		public UnhandledMessage(int idsrc, int iddest) {
			super(idsrc, iddest);
		}
	}

	public class MultipleParametersHandler extends NodeProcess {

		@MessageHandler
		public void test(TestMessage a, int b) {}

		@Override
		public void init(String[] args) {}
	}

	@Test
	public void multipleParametersHandlerTest() {
		exceptionRule.expect(DispatcherException.class);
		exceptionRule.expectMessage("should have a single parameter");
		new Dispatcher(new MultipleParametersHandler());
	}

	public class NotMessage extends NodeProcess {

		@MessageHandler
		public void test(int a) {}

		@Override
		public void init(String[] args) {}
	}

	@Test
	public void notMessageTest() {
		exceptionRule.expect(DispatcherException.class);
		exceptionRule.expectMessage("param must extend Message");
		new Dispatcher(new NotMessage());
	}

	public class MultipleHandlers extends NodeProcess {

		@MessageHandler
		public void testA(TestMessage a) {}

		@MessageHandler
		public void testB(TestMessage b) {}

		@Override
		public void init(String[] args) {}
	}

	@Test
	public void multipleHandlersTest() {
		exceptionRule.expect(DispatcherException.class);
		exceptionRule.expectMessage("More than one handler for");
		new Dispatcher(new MultipleHandlers());
	}

	public class Correct extends NodeProcess {

		@MessageHandler
		public void existingMethod(TestMessage a) {}

		@Override
		public void init(String[] args) {}
	}

	@Test
	public void methodForTest() throws NoSuchMethodException {
		Dispatcher d = new Dispatcher(new Correct());
		Method m = d.methodFor(new TestMessage(0, 0));
		assertEquals("existingMethod", m.getName());
		exceptionRule.expect(NoSuchMethodException.class);
		d.methodFor(new UnhandledMessage(0, 0));
	}
}
