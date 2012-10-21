package com.github.hiroshi_cl.sa.withSentinel.algorithm;

import java.util.*;

import com.github.hiroshi_cl.sa.withSentinel.SuffixArray;

public class LarssonSadakane extends SuffixArray {
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
		final int[] next = new int[N + 1];

		// ternary quick sort and rename
		int K = rec(s, t, sa, upper, next, 0, N, 0, N);

		for (int i = 1, sorted = 0; i < N; i <<= 1) {
			// branch cut
			if (K == N)
				break;

			{
				final int[] tmp = s;
				s = t;
				t = tmp;
			}

			// reduce next
			{
				int p = 0;
				for (int j = next[0]; j < N; j = next[j + 1])
					if (upper[j] - j > 1) {
						next[p] = j;
						p = j + 1;
					} else {
						t[sa[j]] = s[sa[j]];
						sorted++;
					}
				next[p] = N;
			}

			// ternary quick sort and rename
			K = sorted;
			for (int j = next[0]; j < N;) {
				final int k = next[j + 1];
				K += rec(s, t, sa, upper, next, j, upper[j], i, k);
				j = k;
			}
		}
		return sa;
	}

	private static int rec(final int[] s, final int[] t, final int[] sa, final int[] upper, final int[] next,
			final int lo, final int up, final int depth, final int to) {
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
			upper[lt] = gt;
			next[lt + 1] = gt == up ? to : gt;
			ret += rec(s, t, sa, upper, next, lo, lt, depth, lt);
			ret += rec(s, t, sa, upper, next, gt, up, depth, to);
			return ret;
		} else if (up > lo) {
			t[sa[lo]] = lo;
			upper[lo] = up;
			next[lo + 1] = to;
			return 1;
		} else
			return 0;
	}
}
