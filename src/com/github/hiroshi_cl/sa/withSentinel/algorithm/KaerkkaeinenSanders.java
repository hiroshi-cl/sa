package com.github.hiroshi_cl.sa.withSentinel.algorithm;

import java.util.Arrays;

import com.github.hiroshi_cl.sa.withSentinel.SuffixArray;

public class KaerkkaeinenSanders extends SuffixArray {

	@Override
	protected int[] saInternal(char[] cs, final int[] sa) {
		final int N = cs.length;
		final int[] s = new int[N + 3];
		for (int i = 0; i < N; i++)
			s[i] = cs[i];
		return rec(s, 1 << Character.SIZE, N, sa);
	}

	private static int[] rec(final int[] S, final int K, final int N, final int[] SA) {
		final int n0 = (N + 2) / 3;
		final int n1 = (N + 1) / 3;
		final int n2 = N / 3;
		final int n01 = n0 + n1;
		final int[] sa01 = new int[n01 + 3];
		// sort 0, 1 suffixes in modulo 3 (recursive)
		{
			// enumerate 0, 1 suffixes in modulo 3
			for (int i = 0, j = 0; i < N; i++)
				if (i % 3 != 2)
					SA[j++] = i;
			// radix sort
			bucketSort(S, sa01, SA, 2, n01, K);
			bucketSort(S, SA, sa01, 1, n01, K);
			bucketSort(S, sa01, SA, 0, n01, K);

			// rename
			final int mod = n0;
			final int[] s01 = new int[n01 + 3];
			int k = 0;
			for (int i = 0, p0 = -1, p1 = -1, p2 = -1; i < n01; i++) {
				final int c = sa01[i];
				if (S[c] != p0 || S[c + 1] != p1 || S[c + 2] != p2) {
					k++;
					p0 = S[c];
					p1 = S[c + 1];
					p2 = S[c + 2];
				}
				s01[c / 3 + c % 3 * mod] = k;
			}
			k++;

			// not unique
			if (k != n01 + 1)
				rec(s01, k, n01, SA);
			// unique
			else
				for (int i = 0; i < n01; i++)
					SA[s01[i] - 1] = i;
			for (int i = 0; i < n01; i++)
				sa01[i] = SA[i] / mod + SA[i] % mod * 3;
		}

		// sort 2 suffixes in modulo 3 (using previous result)
		final int[] sa2 = new int[n2];
		{
			int j = 0;
			if (N % 3 == 0)
				SA[j++] = N - 1;
			for (int i = 0; i < n01; i++)
				if (sa01[i] % 3 == 0 && sa01[i] > 0)
					SA[j++] = sa01[i] - 1;
			bucketSort(S, sa2, SA, 0, n2, K);
		}

		// merge
		{
			final int[] sai = new int[N + 3];
			Arrays.fill(sai, -1);
			for (int i = 0; i < n01; i++)
				sai[sa01[i]] = i;

			for (int i = 0, j01 = 0, j2 = 0; i < N; i++) {
				boolean is01 = false;
				if (j01 < n01 && j2 < n2) {
					int v = 0;
					for (int k01 = sa01[j01], k2 = sa2[j2]; v == 0; k01++, k2++)
						v = (sai[k01] >= 0 && sai[k2] >= 0 ? sai[k01] - sai[k2] : S[k01] - S[k2]);
					is01 = v < 0;
				} else
					is01 = j2 == n2;
				SA[i] = is01 ? sa01[j01++] : sa2[j2++];
			}
		}
		return SA;
	}

	private static void bucketSort(final int[] s, final int[] sa, final int[] sao, final int o, final int N,
			final int K) {
		final int[] bkt = new int[K];
		for (int i = 0; i < N; i++)
			bkt[s[sao[i] + o]]++;
		for (int i = 1; i < K; i++)
			bkt[i] += bkt[i - 1];
		for (int i = N - 1; i >= 0; i--)
			sa[--bkt[s[sao[i] + o]]] = sao[i];
	}
}
