package com.github.hiroshi_cl.sa.withSentinel.algorithm.extra;

import java.util.*;

import com.github.hiroshi_cl.sa.withSentinel.SuffixArray;

public class LarssonSadakaneWithoutSkipping extends SuffixArray {
	private static final Random r = new Random();

	@Override
	protected int[] saInternal(char[] cs, final int[] sa) {
		final int N = cs.length;
		for (int i = 0; i < N; i++)
			sa[i] = i;
		int[] s = new int[N];
		int[] t = new int[N];
		for (int i = 0; i < N; i++)
			s[i] = cs[i];
		final int[] upper = new int[N];

		// ternary quick sort and rename
		int K = rec(s, t, sa, upper, 0, N, 0);

		for (int i = 1; i < N; i <<= 1) {
			// branch cut
			if (K == N)
				break;

			{
				final int[] tmp = s;
				s = t;
				t = tmp;
			}

			// ternary quick sort and rename
			K = 0;
			for (int j = 0; j < N;) {
				final int k = upper[j];
				K += rec(s, t, sa, upper, j, k, i);
				j = k;
			}
		}
		return sa;
	}

	private static int rec(final int[] s, final int[] t, final int[] sa, final int[] next, final int lo, final int up,
			final int depth) {
		if (up - lo > 1) {
			final int idx = r.nextInt(up - lo) + lo;
			final int pivot = sa[idx] + depth < s.length ? s[sa[idx] + depth] : 0;
			int lt = lo;
			int gt = up;
			for (int i = lo; i < gt; i++) {
				final int ss = sa[i] + depth < s.length ? s[sa[i] + depth] : 0;
				if (ss > pivot) {
					final int tmp = sa[--gt];
					sa[gt] = sa[i];
					sa[i--] = tmp;
				} else if (ss < pivot) {
					final int tmp = sa[lt];
					sa[lt++] = sa[i];
					sa[i] = tmp;
				}
			}
			int ret = 1;
			for (int i = lt; i < gt; i++)
				t[sa[i]] = lt;
			next[lt] = gt;
			ret += rec(s, t, sa, next, lo, lt, depth);
			ret += rec(s, t, sa, next, gt, up, depth);
			return ret;
		} else if (up > lo) {
			t[sa[lo]] = lo;
			next[lo] = up;
			return 1;
		} else
			return 0;
	}
}
