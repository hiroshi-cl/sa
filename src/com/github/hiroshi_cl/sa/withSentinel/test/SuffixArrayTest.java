package com.github.hiroshi_cl.sa.withSentinel.test;

import java.util.*;

import org.junit.*;
import org.junit.rules.*;
import org.junit.runner.*;
import org.junit.experimental.theories.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import com.github.hiroshi_cl.sa.withSentinel.MultipleSentinelsException;
import com.github.hiroshi_cl.sa.withSentinel.NoSentinelException;
import com.github.hiroshi_cl.sa.withSentinel.SuffixArray;

import static com.github.hiroshi_cl.sa.util.StringGenerator.*;
import static com.github.hiroshi_cl.sa.withSentinel.test.TestConfigure.*;

@RunWith(Theories.class)
public class SuffixArrayTest {

	@DataPoints
	public static final SuffixArray[] methods = TestConfigure.methods;

	@DataPoints
	public static final String[] inputs;

	static {
		final List<String> strs = new ArrayList<>();
		strs.add("mississippi");
		// small
		for (final String s : Dummies)
			strs.add(s);
		for (int i = 0; i < 10; i++)
			strs.add(fib("a", "ab", i));
		for (int i = 1; i <= 50; i++)
			strs.add(repeat("a", i));
		for (int i = 1; i <= 50; i++)
			strs.add(random("abcdefghijklmnopqrstuvwxyz", i));
		for (int i = 1; i <= 50; i++)
			strs.add(random("ab", i));
		for (int k = 0; k <= 3; k++)
			for (int i = 1; i <= 50; i++)
				strs.add(randomBible(i, k));

		inputs = strs.toArray(new String[0]);
	}

	public final SuffixArray method;
	public final char[] input;
	public int[] sa;

	public SuffixArrayTest(SuffixArray method, String input) {
		this.method = method;
		this.input = (input + '\0').toCharArray();
	}

	@Rule
	public static final Timeout timeout = new Timeout(TimeOutShort);

	@Theory
	public void theory() {
		assumeTrue(method.isImplemented());
		final String s = new String(input, 0, Math.min(input.length, 50)) + "(" + input.length + ")";
		try {
			System.out.println(method + ": " + s);
			final long st = System.currentTimeMillis();
			sa = method.sa(input);
			final long time = System.currentTimeMillis() - st;
			System.out.println(method + ": " + s + ", " + time + "ms");
		} catch (NoSentinelException e) {
			fail("no sentinel exception");
		} catch (MultipleSentinelsException e) {
			fail("multiple sentinels exception");
		}
		if (input.length <= 100) {
			final int[] bkt = new int[sa.length];
			for (int i = 0; i < sa.length; i++) {
				assertTrue("out of range: " + Arrays.toString(sa), 0 <= sa[i] && sa[i] < sa.length);
				bkt[sa[i]]++;
			}
			for (final int i : bkt)
				assertTrue("not unique: " + Arrays.toString(bkt), i == 1);
			for (int i = 1; i < sa.length; i++)
				for (int j = 0;; j++) {
					assertTrue("wrong sentinel:" + makeMessage(input, sa, i, j), sa[i - 1] + j < input.length
							&& sa[i] + j < input.length);
					assertTrue("wrong order:" + makeMessage(input, sa, i, j), input[sa[i - 1] + j] <= input[sa[i] + j]);
					if (input[sa[i - 1] + j] < input[sa[i] + j])
						break;
				}
		}
	}

	private static String makeMessage(final char[] input, final int[] sa, final int i, final int j) {
		final StringBuilder sb = new StringBuilder().append(i).append(" and ").append(j).append('\n');
		sb.append(replaceAndInsert(new String(input, sa[i - 1], input.length - sa[i - 1]), j)).append('\n');
		sb.append(replaceAndInsert(new String(input, sa[i], input.length - sa[i]), j)).append('\n');
		return sb.toString();
	}

	private static String replaceAndInsert(String s, int j) {
		if (j < s.length())
			return new StringBuilder(s.replace('\0', '$')).insert(j, '[').insert(j + 2, ']').toString();
		else
			return s.replace('\0', '$');
	}
}
