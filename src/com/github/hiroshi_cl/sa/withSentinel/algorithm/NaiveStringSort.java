package com.github.hiroshi_cl.sa.withSentinel.algorithm;

import java.util.Arrays;

import com.github.hiroshi_cl.sa.withSentinel.SuffixArray;

public class NaiveStringSort extends SuffixArray {

	@Override
	protected int[] saInternal(char[] cs, int[] sa) {
		final int N = cs.length;
		final String s = new String(cs);
		final String[] ss = new String[N];
		for (int i = 0; i < N; i++)
			ss[i] = s.substring(i);
		Arrays.sort(ss);
		for (int i = 0; i < N; i++)
			sa[i] = N - ss[i].length();
		return sa;
	}

}
