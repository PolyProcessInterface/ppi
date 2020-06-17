package org.sar.ppi.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;

public class Utils {
	public static void transferTo(InputStream in, OutputStream out) throws IOException {
		int length;
		byte[] bytes = new byte[1024];
		// copy data from input stream to output stream
		while ((length = in.read(bytes)) != -1) {
			out.write(bytes, 0, length);
		}
	}

	/**
	 * Concat all arrays passed into one.
	 *
	 * @param <T> The returned array's content's type.
	 * @param type The returned array's content's type class.
	 * @param arrays The arrays to concat.
	 * @return An array containing the values of all those passed in parameter.
	 */
	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static <T> T[] concatAll(Class<T> type, T[]... arrays) {
		int totalLength = 0;
		for (T[] array : arrays) {
			totalLength += array.length;
		}
		T[] result = (T[]) Array.newInstance(type, totalLength);
		int offset = 0;
		for (T[] array : arrays) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}
}