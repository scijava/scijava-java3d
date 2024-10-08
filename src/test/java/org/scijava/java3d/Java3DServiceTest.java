/*
 * #%L
 * Utility classes for working with Java 3D.
 * %%
 * Copyright (C) 2015 - 2016 Board of Regents of the University of
 * Wisconsin-Madison.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package org.scijava.java3d;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.util.FileUtils;

/**
 * Tests {@link Java3DService}.
 *
 * @author Curtis Rueden
 */
public class Java3DServiceTest {

	private Context context;
	private Java3DService j3d;
	private File tmp1;
	private File tmp2;

	@Before
	public void setUp() throws IOException {
		context = new Context(Java3DService.class);
		j3d = context.service(Java3DService.class);

		tmp1 = FileUtils.createTemporaryDirectory("libExt", "1");
		tmp2 = FileUtils.createTemporaryDirectory("libExt", "2");
	}

	@After
	public void tearDown() {
		context.dispose();
		FileUtils.deleteRecursively(tmp1);
		FileUtils.deleteRecursively(tmp2);
	}

	@Test
	public void testGetLibExtLocations() throws IOException {
		final HashSet<String> expected = new HashSet<>();

		System.setProperty("java.ext.dirs", tmp1.getAbsolutePath() + ":" +
			tmp2.getAbsolutePath());

		final String j3dcore1 = createFile(tmp1, "j3dcore.jar");
		expected.add(j3dcore1);
		assertEquals(expected, libExtPaths());

		final String j3dcore2 = createFile(tmp2, "j3dcore.jar");
		expected.add(j3dcore2);
		assertEquals(expected, libExtPaths());

		final String j3dutils = createFile(tmp2, "j3dutils.jar");
		expected.add(j3dutils);
		final String jogl = createFile(tmp1, "jogl-2.2.0.jar");
		expected.add(jogl);
		assertEquals(expected, libExtPaths());

		final String vecmath = createFile(tmp1, "vecmath.jar");
		createFile(tmp2, "red-herring");
		expected.add(vecmath);
		assertEquals(expected, libExtPaths());

		System.setProperty("java.ext.dirs", tmp1.getAbsolutePath());
		expected.remove(j3dcore2);
		expected.remove(j3dutils);
		assertEquals(expected, libExtPaths());

		System.setProperty("java.ext.dirs", "");
		expected.clear();
		assertEquals(expected, libExtPaths());

		System.clearProperty("java.ext.dirs");
		assertEquals(expected, libExtPaths());
	}

	// -- Helper methods --

	private String createFile(final File dir, final String name) throws IOException
	{
		final File file = new File(dir, name);
		assertTrue(file.createNewFile());
		return truePath(file);
	}

	private Set<String> libExtPaths() {
		return j3d.getLibExtLocations().stream()
			.map(f -> truePath(f))
			.collect(Collectors.toSet());
	}

	private static String truePath(File f) {
		try {
			return f.getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
