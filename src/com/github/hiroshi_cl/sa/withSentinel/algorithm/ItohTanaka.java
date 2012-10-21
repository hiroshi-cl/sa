package com.github.hiroshi_cl.sa.withSentinel.algorithm;

import java.util.Arrays;
import java.util.Random;

import com.github.hiroshi_cl.sa.withSentinel.SuffixArray;

public class ItohTanaka extends SuffixArray {
	private static final Random r = new Random();

	@Override
	protected int[] saInternal(char[] cs, final int[] sa) {
		final int N = cs.length;
		final int K = 1 << Character.SIZE;
		// type A
		int k = N;
		for (int i = 0; i < N; i++)
			if (cs[i] == 0 || cs[i] > cs[i + 1])
				sa[--k] = i;
		// ternary quick sort
		rec(cs, sa, k, N, 0);
		// copy
		{
			final int[] bkt = new int[K];
			for (int i = 0; i < N; i++)
				bkt[cs[i]]++;
			// forward
			{
				final int[] beg = new int[K];
				for (int i = 1; i < K; i++)
					beg[i] = beg[i - 1] + bkt[i - 1];
				final int[] wrt = Arrays.copyOf(beg, K);
				for (int i = k; i < N; i++)
					sa[wrt[cs[sa[i]]]++] = sa[i];
				for (int i = 0; i < K; i++)
					for (int j = beg[i]; j < wrt[i]; j++) {
						final int p = sa[j] - 1;
						if (p >= 0 && cs[p] == cs[p + 1])
							sa[wrt[cs[p]]++] = p;
					}
			}
			// backward
			{
				for (int i = 1; i < K; i++)
					bkt[i] += bkt[i - 1];
				for (int i = N - 1; i >= 0; i--) {
					final int p = sa[i] - 1;
					if (p >= 0 && cs[p] <= cs[p + 1])
						sa[--bkt[cs[p]]] = p;
				}
			}
		}

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
