package com.github.hiroshi_cl.sa.withSentinel.test;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import com.github.hiroshi_cl.sa.util.StringGenerator;

public class TestDataGenerator implements Runnable {

	private static final String DIR = "tmp";

	private static void write(final String corpus, final Map<String, String> map) throws IOException {
		final File dir = new File(DIR);
		if (!dir.exists())
			if (!dir.mkdirs())
				throw new IOException("cannot make " + dir + " directory.");
		final File cdir = new File(dir, corpus);
		if (!cdir.exists())
			if (!cdir.mkdirs())
				throw new IOException("cannot make " + cdir + " directory.");
		for (final String name : map.keySet())
			Files.write(new File(cdir, name).toPath(), map.get(name).getBytes(), StandardOpenOption.CREATE);
	}

	@Override
	public void run() {
		final int N = TestConfigure.fibarray[TestConfigure.MAX - 1];
		final Map<String, String> map = new HashMap<>();
		put(map, "Fibonacci", StringGenerator.fibLen("a", "ab", N));
		put(map, "Repeat", StringGenerator.repeat("a", N));
		put(map, "ChallengeMSDRadix", StringGenerator.challengeMSDRadix(N));
		put(map, "ChallengeKA", StringGenerator.challengeKA(N));
		put(map, "Random26", StringGenerator.random("abcdefghijklmnopqrstuvwxyz", N * TestConfigure.REP));
		put(map, "Random2", StringGenerator.random("ab", N * TestConfigure.REP));
		{
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < TestConfigure.REP; i++) {
				sb.append(StringGenerator.randomBible(N, 3));
				System.gc();
			}
			put(map, "RandomBible3", sb.toString());
		}
		try {
			write("Artificial", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static <S, T> void put(final Map<S, T> map, final S key, final T val) {
		System.err.println(key);
		System.gc();
		map.put(key, val);
	}

	public static void main(String... args) {
		new TestDataGenerator().run();
	}
}
