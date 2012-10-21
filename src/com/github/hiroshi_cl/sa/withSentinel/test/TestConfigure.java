package com.github.hiroshi_cl.sa.withSentinel.test;

import java.util.ArrayList;
import java.util.List;

import com.github.hiroshi_cl.sa.withSentinel.SuffixArray;
import com.github.hiroshi_cl.sa.withSentinel.algorithm.*;
import com.github.hiroshi_cl.sa.withSentinel.algorithm.extra.*;

public class TestConfigure {
	public static final SuffixArray[] methods = { new NaiveStringSort(), new ManberMyers(), new BentleySedgewick(),
			new LarssonSadakane(), new ItohTanaka(), new KaerkkaeinenSanders(), new KoAluru(), new SAIS() };
	public static final SuffixArray[] LSmethods = { new LarssonSadakane(), new LarssonSadakaneWithoutSkipping() };
	public static final SuffixArray[] KAmethods = { new KoAluru(), new KoAluruUsingOnlySTypes(),
			new KoAluruUsingOnlyLTypes() };
	public static final SuffixArray[] SAISmethods = { new SAIS(), new MoriSAIS() };

	public static final boolean AnotherThread = false;
	public static final int TimeOutHuge = 10_000;
	public static final int TimeOutLong = 5_000;
	public static final int TimeOutShort = 1_000;
	public static final int TestRun = 10_000;
	public static final int RRP = 5;
	public static final int REP = 5;
	public static final int MIN = 13;
	public static final int MAX = 38;
	public static final int LEN = 50_000;
	public static final int TIM = 20;
	public static final int[] fibarray;
	public static final List<Integer> li = new ArrayList<>();
	public static final String[] Dummies = new String[REP];

	static {
		fibarray = new int[MAX];
		fibarray[0] = 1;
		fibarray[1] = 2;
		for (int i = 2; i < fibarray.length; i++)
			fibarray[i] = fibarray[i - 1] + fibarray[i - 2];
		for (int i = 0; i < REP; i++)
			li.add(i);
		Dummies[0] = "mississippimississippimississippimississippi";
		Dummies[1] = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		Dummies[2] = "abababababababababababababababababababababab";
		Dummies[3] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQR";
		Dummies[4] = "abcdefghijklmnopqrstuvabcdefghijklmnopqrstuv";
	}
}
