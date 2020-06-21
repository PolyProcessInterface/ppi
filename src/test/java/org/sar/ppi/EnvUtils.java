package org.sar.ppi;

import java.util.Scanner;

public class EnvUtils {

	public static boolean mpirunExist() {
		try (Scanner sc = new Scanner(Runtime.getRuntime().exec("which mpirun").getInputStream())) {
			return !sc.nextLine().isEmpty();
		} catch (Throwable e) {
			return false;
		}
	}
}
