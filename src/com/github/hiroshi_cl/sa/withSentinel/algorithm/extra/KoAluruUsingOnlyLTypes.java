package com.github.hiroshi_cl.sa.withSentinel.algorithm.extra;

import java.util.Arrays;

import com.github.hiroshi_cl.sa.withSentinel.SuffixArray;

public class KoAluruUsingOnlyLTypes extends SuffixArray {

	@Override
	protected int[] saInternal(char[] cs, int[] sa) {
		final int N = cs.length;
		final int[] s = new int[N];
		for (int i = 0; i < N; i++)
			s[i] = cs[i];
		return rec(s, sa, N, 1 << Character.SIZE);
	}

	private static int[] rec(final int[] input, final int[] sa, final int N, final int K) {
		// determine L or S and count L
		int nL = N;
		final boolean[] isS = new boolean[N];
		isS[N - 1] = false;
		for (int i = N - 2; i >= 0; i--)
			if (isS[i] = input[i] < input[i + 1] || input[i] == input[i + 1] && isS[i + 1])
				nL--;

		if (nL > 1) {
			sortls(input, sa, N, K, isS);
			recls(input, sa, N, isS, nL);
		} else
			sa[0] = N - 1;
		copyls(input, sa, N, K, isS, nL);

		return sa;
	}

	private static void sortls(final int[] input, final int[] sa, final int N, final int K, final boolean[] isS) {
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
			if (!isS[i])
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
				for (int j = b[i - 1]; j < b[i]; j++) {
					final int c = sa[j];
					if (!isS[c])
						list[bkt[input[c]]++] = c;
				}
				for (int j = b[i]; j < b[i + 1]; j++) {
					final int c = list[j] - 1;
					list[bkt[input[c]]++] = c;
				}
			}
		}

		// sort by first character (only type L)
		{
			final int[] bkt = new int[K];
			for (int i = 0; i < N; i++)
				if (!isS[i])
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

	private static void recls(final int[] input, final int[] sa, final int N, final boolean[] isS, final int nL) {
		// enumerate S suffixes
		final int[] L = new int[nL + 1];
		for (int i = 0, j = 0; i < N; i++)
			if (!isS[i])
				L[j++] = i;
		L[nL] = N - 1;

		// renumber
		{
			final int[] rev = new int[N];
			for (int i = 0; i < nL; i++)
				rev[L[i]] = i;
			for (int i = 0; i < nL; i++)
				sa[i] = rev[sa[i]];
		}

		// rename
		final int[] s = new int[nL];
		int Knew = -1;
		for (int i = 0, j = -1, l = -1; i < nL; i++) {
			final int c = sa[i];
			boolean f = L[c + 1] - L[c] == l;
			if (f) {
				final int p = L[sa[i]];
				final int q = L[sa[j]];
				for (int k = 0; f && k <= l; k++)
					f = input[p + k] == input[q + k];
			}
			if (f)
				s[c] = Knew;
			else {
				l = L[c + 1] - L[c];
				j = i;
				s[c] = ++Knew;
			}
		}
		Knew++;

		// unique
		if (nL == Knew)
			for (int i = 0; i < nL; i++)
				sa[s[i]] = L[i];
		// not unique
		else {
			rec(s, sa, nL, Knew);
			for (int i = 0; i < nL; i++)
				sa[i] = L[sa[i]];
		}
	}

	private static void copyls(final int[] input, final int[] sa, final int N, final int K, final boolean[] isS,
			final int nL) {
		// make buckets
		final int[] bkt = new int[K];
		for (int i = 0; i < N; i++)
			bkt[input[i]]++;
		for (int i = 1; i < K; i++)
			bkt[i] += bkt[i - 1];
		final int[] idx = new int[K];

		// arrange S suffixes
		System.arraycopy(bkt, 0, idx, 1, K - 1);
		idx[0] = 0;
		{
			final int[] sao = Arrays.copyOf(sa, nL);
			for (int i = 0; i < nL; i++)
				sa[idx[input[sao[i]]]++] = sao[i];
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
