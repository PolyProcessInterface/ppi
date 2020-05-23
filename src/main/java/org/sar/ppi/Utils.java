package org.sar.ppi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
	public static void transferTo(InputStream in, OutputStream out) throws IOException {
		int length;
		byte[] bytes = new byte[1024];
		// copy data from input stream to output stream
		while ((length = in.read(bytes)) != -1) {
			out.write(bytes, 0, length);
		}
	}
}