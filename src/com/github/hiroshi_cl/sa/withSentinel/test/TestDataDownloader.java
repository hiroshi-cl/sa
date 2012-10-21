package com.github.hiroshi_cl.sa.withSentinel.test;

import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.zip.*;

public class TestDataDownloader implements Runnable {

	public static class Corpus {
		public final String name;
		public final URL url;

		public Corpus(String name, String url) {
			this.name = name;
			try {
				this.url = new URL(url);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static final Corpus[] corpora = {
			new Corpus("Canterbury", "http://corpus.canterbury.ac.nz/resources/cantrbry.zip"),
			new Corpus("Calgary", "http://corpus.canterbury.ac.nz/resources/calgary.zip"),
			new Corpus("Large", "http://corpus.canterbury.ac.nz/resources/large.zip") };

	private static final String DIR = "tmp";

	private static Map<String, byte[]> extractZipFile(final InputStream is) throws IOException {
		final Map<String, byte[]> ret = new HashMap<String, byte[]>();
		final ZipInputStream zis = new ZipInputStream(is);
		for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()) {
			if (ze.isDirectory())
				continue;
			final String name = ze.getName();
			System.err.println(name);
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			boolean isText = true;
			for (int b = zis.read(); b >= 0; b = zis.read()) {
				if (b == 0)
					isText = false;
				baos.write(b);
			}
			baos.flush();
			baos.close();
			if (isText)
				ret.put(name, baos.toByteArray());
			zis.closeEntry();
		}
		zis.close();
		return ret;
	}

	private static void write(final String corpus, final Map<String, byte[]> map) throws IOException {
		final File dir = new File(DIR);
		if (!dir.exists())
			if (!dir.mkdirs())
				throw new IOException("cannot make " + dir + " directory.");
		final File cdir = new File(dir, corpus);
		if (!cdir.exists())
			if (!cdir.mkdirs())
				throw new IOException("cannot make " + cdir + " directory.");
		for (final String name : map.keySet())
			Files.write(new File(cdir, name).toPath(), map.get(name), StandardOpenOption.CREATE);
	}

	@Override
	public void run() {
		for (final Corpus corpus : corpora)
			try (InputStream is = new BufferedInputStream(corpus.url.openStream())) {
				System.err.println(corpus.name);
				write(corpus.name, extractZipFile(is));
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public static void main(String... args) {
		new TestDataDownloader().run();
	}
}
