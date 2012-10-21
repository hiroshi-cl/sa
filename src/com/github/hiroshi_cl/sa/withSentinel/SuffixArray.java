package com.github.hiroshi_cl.sa.withSentinel;

public abstract class SuffixArray {
	public int[] sa(final String s) throws NoSentinelException, MultipleSentinelsException {
		return sa(s.toCharArray());
	}

	public boolean isImplemented() {
		return true;
	}

	public int[] sa(final char[] cs) throws NoSentinelException, MultipleSentinelsException {
		if (cs[cs.length - 1] != '\0')
			throw new NoSentinelException();
		for (int i = 0; i < cs.length - 1; i++)
			if (cs[i] == '\0')
				throw new MultipleSentinelsException();
		return saWithoutChecking(cs, new int[cs.length]);
	}

	public int[] saWithoutChecking(final char[] cs, final int[] sa) {
		return saInternal(cs, sa);
	}

	protected abstract int[] saInternal(final char[] cs, final int[] sa);

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
