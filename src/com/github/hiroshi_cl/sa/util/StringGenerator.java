package com.github.hiroshi_cl.sa.util;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.security.SecureRandom;

public class StringGenerator {
	private static final SecureRandom r;
	private static final int C;
	private static final int CXT = 3;
	private static final int[] NUM = new int[1 << Byte.SIZE];
	private static final char[] CHR = new char[1 << Byte.SIZE];
	private static final double[][][] CNV = new double[CXT + 1][][];

	static {
		try {
			r = SecureRandom.getInstance("SHA1PRNG");
			r.setSeed("suffix array".getBytes());
			final byte[] bs = Files.readAllBytes(FileSystems.getDefault().getPath("tmp", "Large", "bible.txt"));
			final int N = bs.length;
			final boolean[] chk = new boolean[1 << Byte.SIZE];
			for (final byte b : bs)
				chk[b] = true;
			int cnt = 0;
			for (int i = 0; i < chk.length; i++)
				if (chk[i]) {
					CHR[cnt] = (char) i;
					NUM[i] = cnt;
					cnt++;
				}
			C = cnt;
			for (int i = 0, j = 1; i <= CXT; i++, j *= C)
				CNV[i] = new double[j][C];
			for (int i = 0; i < N; i++)
				for (int j = 0, k = 0, c = NUM[bs[i]]; j <= CXT;) {
					CNV[j][k][c]++;
					if (i - j > 0) {
						k *= C;
						k += NUM[bs[i - ++j]];
					} else
						break;
				}
			for (int i = 0; i < CNV.length; i++)
				for (int j = 0; j < CNV[i].length; j++) {
					final double[] ds = CNV[i][j];
					for (int k = 0; k < CNV[i][j].length; k++)
						ds[k] += i == 0 ? .5 : CNV[i - 1][j / C][k] * C * .5;
					double d = 0.;
					for (int k = 1; k < ds.length; k++)
						d += ds[k];
					for (int k = 0; k < ds.length; k++)
						ds[k] /= d;
				}
			for (int i = 0; i < CNV.length; i++)
				for (int j = 0; j < CNV[i].length; j++) {
					final double[] ds = CNV[i][j];
					for (int k = 1; k < ds.length; k++)
						ds[k] += ds[k - 1];
					ds[ds.length - 1] = 1.;
				}
		} catch (Exception e) {
			e.printStackTrace();
			throw null;
		}
	}

	public static String randomBible(final int len, final int cxt) {
		if (cxt > CXT)
			throw null;
		final char[] ret = new char[len];
		for (int i = 0; i < len; i++) {
			final int o = Math.min(i, cxt);
			int k = 0;
			for (int j = 1; j <= o; j++) {
				k *= C;
				k += NUM[ret[i - j]];
			}
			final double[] table = CNV[o][k];
			final double d = r.nextDouble();
			int p = -1;
			for (int lo = 0, up = C; up - lo > 1;) {
				p = (lo + up + 1) / 2;
				if (d <= table[p])
					up = p;
				else
					lo = p;
			}
			ret[i] = CHR[p];
		}
		return new String(ret);
	}

	public static String random(final String set, final int len) {
		final int N = set.length();
		final char[] cs = set.toCharArray();
		final StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(cs[r.nextInt(N)]);
		return sb.toString();
	}

	public static String fib(final int idx) {
		return fib("0", "01", idx);
	}

	public static String fibLen(final int len) {
		return fibLen("0", "01", len);
	}

	public static String fibLen(final String s0, final String s1, final int len) {
		CharSequence s = s0;
		StringBuilder t = new StringBuilder(s1);
		while (t.length() < len) {
			final int l = t.length();
			t.append(s);
			s = t.subSequence(0, l);
		}
		return t.substring(0, len);

	}

	public static String fib(final String s0, final String s1, final int idx) {
		CharSequence s = s0;
		StringBuilder t = new StringBuilder(s1);
		for (int i = 0; i < idx; i++) {
			final int l = t.length();
			t.append(s);
			s = t.subSequence(0, l);
		}
		return s.toString();
	}

	public static String repeat(final String s, final int rep) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rep; i++)
			sb.append(s);
		return sb.toString();
	}

	public static String challengeMSDRadix(final int len) {
		final char[] cs = new char[len];
		for (int k = 0, p = 0; k < cs.length; k++)
			if ((k & 3) < 2)
				cs[k] = 'a';
			else if ((k & 3) == 2)
				cs[k] = (char) (p / 65535 + 1);
			else
				cs[k] = (char) (p++ % 65535 + 1);
		return new String(cs);
	}

	public static String challengeKA(final int len) {
		final char[] cs = new char[len];
		cs[0] = '\1';
		for (int k = 0, p = 32767 * 32767; k < cs.length - 1; k++)
			if (k % 3 == 0)
				cs[k + 1] = '\1';
			else if (k % 3 == 1)
				cs[k + 1] = (char) (--p / 32767 + 32769);
			else
				cs[k + 1] = (char) (p % 32767 + 2);
		return new String(cs);
	}

	public static String longLCP(final int len, final String o) {
		final String lcp = o.substring(0, len / 3);
		final String pad = o.substring(len / 3, len / 3 + len - len / 3 * 2);
		return new StringBuilder(lcp).append(pad).append(lcp).toString();
	}
}
