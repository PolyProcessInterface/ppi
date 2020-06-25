package org.sar.ppi;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;
import org.sar.ppi.tools.PpiUtils;

public class UtilsTest {

	@Test
	public void concatAllTestBasic() {
		String[] a1 = new String[] { "1", "2" };
		String[] a2 = new String[] { "3" };
		String[] a3 = new String[] { "4" };
		String[] expected = new String[] { "1", "2", "3", "4" };
		assertArrayEquals(expected, PpiUtils.concatAll(String.class, a1, a2, a3));
	}

	@Test
	public void concatAllTestInheritance() {
		Integer[] a1 = new Integer[] { 1, 2 };
		Double[] a2 = new Double[] { 3d };
		Float[] a3 = new Float[] { 4f };
		Number[] expected = new Number[] { 1, 2, 3d, 4f };
		assertArrayEquals(expected, PpiUtils.concatAll(Number.class, a1, a2, a3));
	}

	@Test(expected = NullPointerException.class)
	public void concatAllTestNull() {
		Integer[] a1 = null;
		PpiUtils.concatAll(Integer.class, a1);
	}

	@Test
	public void concatAllTestOnlyOne() {
		Integer[] a1 = new Integer[] { 1, 2 };
		assertArrayEquals(a1, PpiUtils.concatAll(Integer.class, a1));
	}

	@Test
	public void concatAllTestNone() {
		Integer[] expected = new Integer[0];
		assertArrayEquals(expected, PpiUtils.concatAll(Integer.class));
	}
}
