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

import java.io.File;
import java.util.List;

import org.scijava.service.SciJavaService;
import org.scijava.ui.UIService;

/**
 * Interface for services which work with Java 3D.
 * <p>
 * Historically, Java 3D was installed as an extension to the Java Runtime
 * Environment, meaning that JAR files and native libraries were placed in the
 * {@code lib/ext} directory of the JRE installation, or manually appended to
 * the Java extensions via the {@code java.ext.dirs} system property.
 * </p>
 * <p>
 * However, that approach has many downsides:
 * </p>
 * <ul>
 * <li>Users must install Java 3D manually, independent of the application.</li>
 * <li>Thus, at runtime, the application cannot manage the version of Java 3D in
 * the same was that it manages versions of its regular dependencies.</li>
 * <li>Similarly, at build time, the Java 3D dependency must be treated
 * specially (e.g., with Maven, using {@code provided} scope in the POM).</li>
 * </ul>
 * <p>
 * Failure to manage Java 3D installations as needed can result in cryptic
 * version-skew-related error messages, such as {@link NoSuchMethodError} or
 * even native-library-related errors. This situation is especially prevalent on
 * OS X, where Java 3D 1.3 was pre-installed in
 * {@code /System/Library/Java/Extensions} on older versions of the OS, and left
 * in place after OS upgrades (despite Java itself being uninstalled).
 * </p>
 * <p>
 * These days, Java 3D is built on top of JOGL, and <a
 * href="https://github.com/hharrison/java3d-core">available on GitHub</a>. But
 * old installations of Java 3D still lurk, waiting to disrupt applications at
 * runtime: libraries present on the Java extensions path take precedence over
 * those on the regular class path.
 * </p>
 * <p>
 * This service mitigates the issue by warning users (via
 * {@link UIService#showDialog}) if any Java 3D installations are detected as
 * Java extensions, and asking the user to clean them up before proceeding.
 * Consequently, applications can now use the modern JOGL version of Java 3D,
 * without worrying about obsolete versions of Java 3D interfering at runtime.
 * </p>
 * 
 * @author Curtis Rueden
 */
public interface Java3DService extends SciJavaService {

	/**
	 * Gets the locations where Java 3D is installed as an extension.
	 * 
	 * @return a {@link List} of directories containing Java 3D libraries.
	 */
	List<File> getLibExtLocations();

}
