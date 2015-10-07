/*
 * #%L
 * Utility classes for working with Java 3D.
 * %%
 * Copyright (C) 2015 Board of Regents of the University of
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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.scijava.event.EventHandler;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import org.scijava.ui.DialogPrompt.MessageType;
import org.scijava.ui.UIService;
import org.scijava.ui.event.UIShownEvent;

/**
 * Default service for working with Java 3D.
 * 
 * @author Curtis Rueden
 */
@Plugin(type = Service.class)
public class DefaultJava3DService extends AbstractService implements
	Java3DService
{

	@Parameter
	private UIService uiService;

	@Override
	public List<File> getLibExtLocations() {
		final ArrayList<File> files = new ArrayList<File>();
		final String extDirs = System.getProperty("java.ext.dirs");
		if (extDirs == null) return files;
		for (final String dir : extDirs.split(":")) {
			checkLibExtDirectory(files, dir);
		}
		return files;
	}

	// -- Event handlers --

	/** Checks for obsolete Java 3D installations, warning when any are found. */
	@EventHandler
	protected void onEvent(@SuppressWarnings("unused") final UIShownEvent evt) {
		final List<File> libExtFiles = getLibExtLocations();
		if (libExtFiles.isEmpty()) return;

		final StringBuilder sb = new StringBuilder();
		sb.append("There are obsolete Java 3D libraries installed as Java "
			+ "extensions.");
		sb.append("\nThe following files were detected:\n");
		for (final File libExtFile : libExtFiles) {
			sb.append("\n* " + libExtFile.getAbsolutePath());
		}
		sb.append("\n\nThese libraries will very likely cause problems with "
			+ "3D visualization.\nPlease delete them, then restart the program.");
		uiService.showDialog(sb.toString(), MessageType.WARNING_MESSAGE);
	}

	// -- Helper methods --

	private void checkLibExtDirectory(final ArrayList<File> files,
		final String dirPath)
	{
		final File dirFile = new File(dirPath.isEmpty() ? "." : dirPath);
		if (!dirFile.exists()) return;

		checkFile(files, new File(dirFile, "j3dcore.jar"));
		checkFile(files, new File(dirFile, "vecmath.jar"));
		checkFile(files, new File(dirFile, "j3dutils.jar"));
		checkFilePattern(files, dirFile, "j3d-core.*");
		checkFilePattern(files, dirFile, "vecmath.*");
		checkFilePattern(files, dirFile, "jogl.*");
		// Maybe libJ3DUtils.jnilib libJ3DAudio.jnilib
	}

	private void checkFilePattern(final ArrayList<File> files,
		final File dirFile, final String regex)
	{
		final FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(final File dir, final String name) {
				return name.matches(regex);
			}
		};
		for (final File f : dirFile.listFiles(filter)) {
			files.add(f);
		}
	}

	private void checkFile(ArrayList<File> files, File file) {
		if (file.exists()) files.add(file);
	}

}
