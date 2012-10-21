package com.github.hiroshi_cl.sa.withSentinel.algorithm;

import java.util.Arrays;

import com.github.hiroshi_cl.sa.withSentinel.SuffixArray;

public class ManberMyers extends SuffixArray {

	@Override
	protected int[] saInternal(char[] cs, int[] sa) {
		final int N = cs.length;
		final int[] o = sa;
		// bucket sort
		final int[] s = new int[N];
		final int[] t = new int[N];
		for (int i = 0; i < N; i++)
			t[i] = s[i] = cs[i];
		{
			final int K = 1 << Character.SIZE;
			final int[] bkt = new int[K];
			for (int j = 0; j < N; j++)
				bkt[s[j]]++;
			for (int j = 1; j < K; j++)
				bkt[j] += bkt[j - 1];
			for (int j = N - 1; j >= 0; j--)
				sa[--bkt[s[j]]] = j;
		}
		int[] sao = new int[N];
		final int[] bkt = new int[N];
		for (int i = 1, K = 0; i < N; i <<= 1) {
			// rename and count
			K = 0;
			for (int j = 0, p = 0, q = 0; j < N; j++) {
				final int c = sa[j];
				if (s[c] == p && t[c] == q) {
					s[c] = K;
				} else {
					p = s[c];
					q = t[c];
					s[c] = ++K;
				}
				bkt[K] = j + 1;
			}
			K++;

			// branch cut
			if (K == N)
				break;

			System.arraycopy(s, i, t, 0, N - i);
			Arrays.fill(t, N - i, N, 0);
			// copy
			{
				final int[] tmp = sao;
				sao = sa;
				sa = tmp;

				// copy (2nd char /= '\0')
				for (int j = N - 1; j >= 0; j--)
					if (sao[j] >= i)
						sa[--bkt[s[sao[j] - i]]] = sao[j] - i;

				// copy (2nd char == '\0')
				for (int j = N - i; j < N; j++)
					sa[--bkt[s[j]]] = j;
			}
		}
		if(sa != o)
			System.arraycopy(sa, 0, o, 0, N);
		return o;
	}
}
