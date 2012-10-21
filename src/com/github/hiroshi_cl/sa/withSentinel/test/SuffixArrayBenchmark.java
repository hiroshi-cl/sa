package com.github.hiroshi_cl.sa.withSentinel.test;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import com.github.hiroshi_cl.sa.util.*;
import com.github.hiroshi_cl.sa.withSentinel.SuffixArray;

import static com.github.hiroshi_cl.sa.withSentinel.test.TestConfigure.*;

public class SuffixArrayBenchmark {
	private static final int L = fibarray[MAX - 1];
	public static final SuffixArray[] methods = TestConfigure.methods;

	public static void main(String... args) {
		new ArtificialTest(ArtificialTest.SetName.Fibonacci, true).run();
		new ArtificialTest(ArtificialTest.SetName.Repeat, true).run();
		new ArtificialTest(ArtificialTest.SetName.RandomBible3, true).run();
		new ArtificialTest(ArtificialTest.SetName.RandomBible3_longLCP, true).run();
		new ArtificialTest(ArtificialTest.SetName.Random26_longLCP, true).run();
		new CorpusTest(CorpusTest.SetName.Canterbury).run();
		new CorpusTest(CorpusTest.SetName.Calgary).run();
		new CorpusTest(CorpusTest.SetName.Large).run();
	}

	private abstract static class Test implements Runnable {
		private static final PrintWriter pw;
		static {
			try {
				pw = new PrintWriter(new File("tmp", System.currentTimeMillis() + "-bench.out"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw null;
			}
		}
		protected static final String DIR = "tmp";
		protected final File dir;
		protected final Map<String, Path> map;

		public Test(Object o) {
			dir = new File(DIR, o.toString());
			map = loadList();
		}

		public abstract void run();

		private Map<String, Path> loadList() {
			if (!dir.exists()) {
				new TestDataDownloader().run();
				new TestDataGenerator().run();
			}

			if (!dir.isDirectory())
				throw null;

			final Map<String, Path> ret = new HashMap<>();
			for (final File file : dir.listFiles())
				ret.put(file.getName(), file.toPath());
			return ret;
		}

		protected String load(final String s) {
			try {
				return new String(Files.readAllBytes(map.get(s)), "utf-8");
			} catch (IOException e) {
				throw null;
			}
		}

		protected void print(final String title, final String[] names, final double[][] ds) {
			final int K = names.length;
			if (ds.length != K)
				throw null;

			pw.print(title);
			for (int i = 0; i < methods.length; i++) {
				pw.print('\t');
				pw.print(methods[i]);
			}
			pw.println();
			for (int i = 0; i < K; i++) {
				pw.printf("%s", names[i]);
				for (int j = 0; j < methods.length; j++) {
					pw.print('\t');
					pw.print(Double.isNaN(ds[i][j]) || ds[i][j] <= .002 ? "" : String.format("% 4.4f", ds[i][j]));
				}
				pw.println();
			}
			pw.println();
			pw.flush();
		}
	}

	private static class CorpusTest extends Test {
		public CorpusTest(SetName o) {
			super(o);
		}

		public enum SetName {
			Canterbury, Calgary, Large
		}

		public void run() {
			final int K = map.size();
			final String[] names = map.keySet().toArray(new String[0]);
			Arrays.sort(names);
			final double[][] ds = new double[K][methods.length];

			// run
			debug(dir.getName());
			for (int i = 0; i < K; i++) {
				debug(names[i]);
				final String s = load(names[i]);
				final String sub = s.substring(0, Math.min(TestRun, s.length()));
				for (int j = 0; j < methods.length; j++) {
					try {
						// dummy run
						test(methods[j], Dummies, TimeOutShort);
						final String[] ss = new String[REP];
						Arrays.fill(ss, sub);
						test(methods[j], ss, TimeOutShort);
						Arrays.fill(ss, s);
						ds[i][j] = test(methods[j], ss, TimeOutHuge);
					} catch (NullPointerException e) {
						debug("Time Out");
					} catch (Exception e) {
						e.printStackTrace();
					} catch (StackOverflowError e) {
						debug("stack over flow");
					}
				}
			}
			print(dir.getName(), names, ds);
		}
	}

	private static class ArtificialTest extends Test {
		private final boolean fib;
		private final String name;

		public ArtificialTest(SetName o, boolean fib) {
			super("Artificial");
			this.fib = fib;
			this.name = o.name();
		}

		public enum SetName {
			Fibonacci, Repeat, Random26, Random2, RandomBible3, ChallengeMSDRadix_double, ChallengeKA_double, Random26_longLCP, Random2_longLCP, RandomBible3_longLCP
		}

		public void run() {
			final String s = load(name.split("_")[0]);
			debug(name);
			if (fib) {
				final double[][] ds = new double[MAX - MIN][methods.length];
				// run
				for (int j = 0; j < methods.length; j++) {
					try {
						// dummy run
						test(methods[j], Dummies, TimeOutShort);
						test(methods[j], getStrings(s, TestRun), TimeOutShort);
						for (int i = MIN; i < MAX; i++)
							ds[i - MIN][j] = test(methods[j], getStrings(s, fibarray[i]), TimeOutLong);
					} catch (Exception e) {
						debug("Time Out");
					} catch (StackOverflowError e) {
						debug("stack over flow");
					}
				}
				// print
				final String[] names = new String[MAX - MIN];
				for (int i = MIN; i < MAX; i++)
					names[i - MIN] = String.format("% 9d", fibarray[i]);
				print(name, names, ds);

			} else {
				final double[][] ds = new double[TIM][methods.length];
				// run
				for (int j = 0; j < methods.length; j++) {
					try {
						// dummy run
						test(methods[j], Dummies, TimeOutShort);
						test(methods[j], getStrings(s, TestRun), TimeOutShort);
						for (int i = 1; i <= TIM; i++)
							ds[i - 1][j] = test(methods[j], getStrings(s, i * LEN), TimeOutShort);
					} catch (Exception e) {
						debug("Time Out");
					} catch (StackOverflowError e) {
						debug("stack over flow");
					}
				}

				// print
				final String[] names = new String[TIM];
				for (int i = 1; i <= TIM; i++)
					names[i - 1] = String.format("% 9d", i * LEN);
				print(name, names, ds);
			}
		}

		private String[] getStrings(String s, int len) {
			final String[] ret = new String[REP];
			if (name.startsWith("Random"))
				for (int i = 0; i < REP; i++)
					ret[i] = s.substring(i * L, i * L + len);
			else {
				final String t = s.substring(0, len);
				Arrays.fill(ret, t);
			}
			final String[] ss = name.split("_");
			if (ss.length > 1)
				switch (ss[1]) {
				case "longLCP":
					for (int i = 0; i < REP; i++)
						ret[i] = StringGenerator.longLCP(len, ret[i]);
					break;
				case "double":
					for (int i = 0; i < REP; i++)
						ret[i] = StringGenerator.repeat(ret[i].substring(0, (len + 1) / 2), 2).substring(0, len);
					break;
				default:
					debug("not implemented");
					break;
				}
			return ret;
		}
	}

	@SuppressWarnings("deprecation")
	private static double test(final SuffixArray method, final String[] inputs, final int timeout) {
		final char[][] css = new char[REP][];
		for (int i = 0; i < REP; i++)
			css[i] = (inputs[i] + "\0").toCharArray();
		final RunTest r = new RunTest(method, css, timeout);
		System.gc();
		if (AnotherThread)
			try {
				final Thread th = new Thread(r);
				th.start();
				th.join(timeout * RRP * REP);
				if (th.isAlive() || Double.isNaN(r.result)) {
					th.stop();
					System.gc();
					throw null;
				}
				System.gc();
				return r.result;
			} catch (InterruptedException e) {
				System.err.println("interrupted");
				throw null;
			}
		else {
			r.run();
			return r.result;
		}
	}

	private static class RunTest implements Runnable {
		private final SuffixArray method;
		private final char[][] inputs;
		private final int timeout;
		public double result = 0.;

		public RunTest(SuffixArray method, char[][] inputs, int timeout) {
			this.method = method;
			this.inputs = inputs;
			this.timeout = timeout;
		}

		@Override
		public void run() {
			final int N = inputs[0].length;
			final int[] sa = new int[N];
			final int[][] iss = new int[REP][RRP];
			for (int j = 0; j < RRP; j++) {
				Collections.shuffle(li);
				for (final int i : li) {
					System.gc();
					final long st = System.currentTimeMillis();
					method.saWithoutChecking(inputs[i], sa);
					final long time = System.currentTimeMillis();
					iss[i][j] = (int) (time - st);
					if (iss[i][j] > timeout)
						throw null;
					Arrays.fill(sa, 0);
				}
			}
			int sum = 0;
			for (final int[] is : iss) {
				Arrays.sort(is);
				sum += is[0];
			}
			// for (final int[] is : iss)
			// debug(is);
			debug(method, N, result = .001 * sum / REP);
		}
	}

	private static void debug(Object... os) {
		System.err.println(Arrays.deepToString(os));
	}
}
