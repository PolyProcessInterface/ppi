package org.sar.ppi;

import java.util.Scanner;

public class Environment {
	public static boolean mpirunExist() {
		try (Scanner scanner = new Scanner(Runtime.getRuntime().exec("which mpirun").getInputStream())) {
			return !scanner.nextLine().isEmpty();
		} catch (Throwable e) {
			return false;
		}
	}
}