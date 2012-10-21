package com.github.hiroshi_cl.sa.withSentinel.algorithm;

import java.util.Random;

import com.github.hiroshi_cl.sa.withSentinel.SuffixArray;

public class BentleySedgewick extends SuffixArray {
	private static final Random r = new Random();

	@Override
	protected int[] saInternal(char[] cs, final int[] sa) {
		final int N = cs.length;
		for (int i = 0; i < N; i++)
			sa[i] = i;
		rec(cs, sa, 0, N, 0);
		return sa;
	}

	private static void rec(final char[] cs, final int[] sa, final int lo, final int up, final int depth) {
		if (up - lo > 1) {
			final char pivot = cs[sa[r.nextInt(up - lo) + lo] + depth];
			int lt = lo;
			int gt = up;
			for (int i = lo; i < gt; i++)
				if (cs[sa[i] + depth] > pivot) {
					final int t = sa[--gt];
					sa[gt] = sa[i];
					sa[i--] = t;
				} else if (cs[sa[i] + depth] < pivot) {
					final int t = sa[lt];
					sa[lt++] = sa[i];
					sa[i] = t;
				}
			rec(cs, sa, lo, lt, depth);
			rec(cs, sa, lt, gt, depth + 1);
			rec(cs, sa, gt, up, depth);
		}
	}

}
