package com.github.hiroshi_cl.sa.withSentinel.algorithm.extra;

import com.github.hiroshi_cl.sa.withSentinel.SuffixArray;

public class KoAluruUsingOnlySTypes extends SuffixArray {

	@Override
	protected int[] saInternal(char[] cs, int[] sa) {
		final int N = cs.length;
		final int[] s = new int[N];
		for (int i = 0; i < N; i++)
			s[i] = cs[i];
		return rec(s, sa, N, 1 << Character.SIZE);
	}

	private static int[] rec(final int[] input, final int[] sa, final int N, final int K) {
		// determine L or S and count S
		int nS = 1;
		final boolean[] isS = new boolean[N];
		isS[N - 1] = true;
		for (int i = N - 2; i >= 0; i--)
			if (isS[i] = input[i] < input[i + 1] || input[i] == input[i + 1] && isS[i + 1])
				nS++;

		if (nS > 1) {
			sortss(input, sa, N, K, isS);
			recss(input, sa, N, isS, nS);
		} else
			sa[0] = N - 1;
		copyss(input, sa, N, K, isS, nS);

		return sa;
	}

	private static void sortss(final int[] input, final int[] sa, final int N, final int K, final boolean[] isS) {
		// sort by first character
		{
			final int[] bkt = new int[K];
			for (int i = 0; i < N; i++)
				bkt[input[i]]++;
			for (int i = 1; i < K; i++)
				bkt[i] += bkt[i - 1];
			for (int i = 0; i < N; i++)
				sa[--bkt[input[i]]] = i;
		}

		// make distance list
		final int[] dist = new int[N];
		int maxDist = 0;
		for (int i = 0, d = 1; i < N; i++, d++) {
			maxDist = Math.max(maxDist, dist[i] = d);
			if (isS[i])
				d = 0;
		}

		// sort by distance
		final int[] list = new int[N];
		final int[] b = new int[maxDist + 1];
		{
			for (int i = 0; i < N; i++)
				b[dist[i] - 1]++;
			for (int i = 1; i < maxDist; i++)
				b[i] += b[i - 1];
			for (int i = N - 1; i >= 0; i--)
				list[--b[dist[sa[i]] - 1]] = sa[i];
			System.arraycopy(list, 0, sa, 0, N);
			b[maxDist] = N;
		}

		// sort by other characters
		{
			final int[] bkt = new int[K];
			for (int i = maxDist - 1; i > 0; i--) {
				for (int j = b[i] - 1; j >= b[i - 1]; j--)
					bkt[input[sa[j]]] = j;
				for (int j = b[i]; j < b[i + 1]; j++) {
					final int c = list[j] - 1;
					list[bkt[input[c]]++] = c;
				}
				for (int j = b[i - 1]; j < b[i]; j++) {
					final int c = sa[j];
					if (isS[c])
						list[bkt[input[c]]++] = c;
				}
			}
		}

		// sort by first character (only type S)
		{
			final int[] bkt = new int[K];
			for (int i = 0; i < N; i++)
				if (isS[i])
					bkt[input[i]]++;
			for (int i = 1; i < K; i++)
				bkt[i] += bkt[i - 1];
			for (int i = b[1] - 1; i >= b[0]; i--) {
				final int c = list[i] - 1;
				if (c >= 0)
					sa[--bkt[input[c]]] = c;
			}
			sa[0] = N - 1;
		}
	}

	private static void recss(final int[] input, final int[] sa, final int N, final boolean[] isS, final int nS) {
		// enumerate S suffixes
		final int[] S = new int[nS + 1];
		for (int i = 0, j = 0; i < N; i++)
			if (isS[i])
				S[j++] = i;
		S[nS] = N - 1;

		// renumber
		{
			final int[] rev = new int[N];
			for (int i = 0; i < nS; i++)
				rev[S[i]] = i;
			for (int i = 0; i < nS; i++)
				sa[i] = rev[sa[i]];
		}

		// rename
		final int[] s = new int[nS];
		int Knew = -1;
		for (int i = 0, j = -1, l = -1; i < nS; i++) {
			final int c = sa[i];
			boolean f = S[c + 1] - S[c] == l;
			if (f) {
				final int p = S[sa[i]];
				final int q = S[sa[j]];
				for (int k = 0; f && k <= l; k++)
					f = input[p + k] == input[q + k];
			}
			if (f)
				s[c] = Knew;
			else {
				l = S[c + 1] - S[c];
				j = i;
				s[c] = ++Knew;
			}
		}
		Knew++;

		// unique
		if (nS == Knew)
			for (int i = 0; i < nS; i++)
				sa[s[i]] = S[i];
		// not unique
		else {
			rec(s, sa, nS, Knew);
			for (int i = 0; i < nS; i++)
				sa[i] = S[sa[i]];
		}
	}

	private static void copyss(final int[] input, final int[] sa, final int N, final int K, final boolean[] isS,
			final int nS) {
		// make buckets
		final int[] bkt = new int[K];
		for (int i = 0; i < N; i++)
			bkt[input[i]]++;
		for (int i = 1; i < K; i++)
			bkt[i] += bkt[i - 1];
		final int[] idx = new int[K];

		// arrange S suffixes
		System.arraycopy(bkt, 0, idx, 0, K);
		for (int i = nS - 1; i >= 0; i--)
			sa[--idx[input[sa[i]]]] = sa[i];

		// copy S -> L
		System.arraycopy(bkt, 0, idx, 1, K - 1);
		idx[0] = 0;
		for (int i = 0; i < N; i++) {
			final int k = sa[i] - 1;
			if (k >= 0 && !isS[k])
				sa[idx[input[k]]++] = k;
		}
	}
}
