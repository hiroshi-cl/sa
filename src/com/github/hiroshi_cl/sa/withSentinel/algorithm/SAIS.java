package com.github.hiroshi_cl.sa.withSentinel.algorithm;

import java.util.Arrays;

import com.github.hiroshi_cl.sa.withSentinel.SuffixArray;

public class SAIS extends SuffixArray {

	@Override
	protected int[] saInternal(char[] cs, int[] sa) {
		final int N = cs.length;
		final int[] s = new int[N];
		for (int i = 0; i < N; i++)
			s[i] = cs[i];
		return rec(s, sa, N, 1 << Character.SIZE);
	}

	private static int[] rec(final int[] input, final int[] sa, final int N, final int K) {
		// determine L or S and count
		final boolean[] isS = new boolean[N];
		int nS = 1;
		isS[N - 1] = true;
		for (int i = N - 2; i >= 0; i--)
			if (isS[i] = input[i] < input[i + 1] || input[i] == input[i + 1] && isS[i + 1])
				nS++;

		int n = 0;
		if (nS > 1) {
			// step 1
			sort(input, sa, N, K, N, isS);

			// step 2
			{
				final boolean[] isLMS = new boolean[N];
				for (int i = 0; i < N; i++)
					if (isLMS[i] = isS[i] && (i == 0 || !isS[i - 1]))
						n++;

				// renumber
				final int[] LMS = new int[n + 1];
				{
					final int[] rev = new int[N];
					for (int i = 0, j = 0, k = 0; i < N; i++) {
						if (isLMS[i]) {
							rev[i] = j;
							LMS[j] = i;
							j++;
						}
						if (isLMS[sa[i]])
							sa[k++] = sa[i];
					}
					for (int i = 0; i < n; i++)
						sa[i] = rev[sa[i]];
				}
				LMS[n] = N - 1;

				// rename
				final int[] s = new int[n];
				int Knew = -1;
				for (int i = 0, j = -1, l = -1; i < n; i++) {
					final int c = sa[i];
					boolean f = LMS[c + 1] - LMS[c] == l;
					if (f) {
						final int p = LMS[sa[i]];
						final int q = LMS[sa[j]];
						for (int k = 0; f && k <= l; k++)
							f = input[p + k] == input[q + k];
					}
					if (f)
						s[c] = Knew;
					else {
						l = LMS[c + 1] - LMS[c];
						j = i;
						s[c] = ++Knew;
					}
				}
				Knew++;

				Arrays.fill(sa, 0, N, -1);
				// unique
				if (n == Knew)
					for (int i = 0; i < n; i++)
						sa[s[i]] = LMS[i];
				// not unique
				else {
					rec(s, sa, n, Knew);
					for (int i = 0; i < n; i++)
						sa[i] = LMS[sa[i]];
				}
			}
		} else
			sa[n++] = N - 1;

		// step 3
		sort(input, sa, N, K, n, isS);

		return sa;
	}

	private static void sort(final int[] input, final int[] sa, final int N, final int K, final int n, boolean[] isS) {
		// make buckets
		final int[] bkt = new int[K];
		for (int i = 0; i < N; i++)
			bkt[input[i]]++;
		for (int i = 1; i < K; i++)
			bkt[i] += bkt[i - 1];
		final int[] idx = new int[K];

		System.arraycopy(bkt, 0, idx, 0, K);
		// arrange S suffixes
		if (n < N)
			for (int i = n - 1; i >= 0; i--) {
				final int c = sa[i];
				sa[i] = -1;
				sa[--idx[input[c]]] = c;
			}
		// sort by first character
		else
			for (int i = 0; i < N; i++)
				if (isS[i])
					sa[--idx[input[i]]] = i;

		// copy S -> L
		System.arraycopy(bkt, 0, idx, 1, K - 1);
		idx[0] = 0;
		for (int i = 0; i < N; i++) {
			final int k = sa[i] - 1;
			if (k >= 0 && !isS[k])
				sa[idx[input[k]]++] = k;
		}

		// copy L -> S
		System.arraycopy(bkt, 0, idx, 0, K);
		for (int i = N - 1; i >= 0; i--) {
			final int k = sa[i] - 1;
			if (k >= 0 && isS[k])
				sa[--idx[input[k]]] = k;
		}
	}
}
