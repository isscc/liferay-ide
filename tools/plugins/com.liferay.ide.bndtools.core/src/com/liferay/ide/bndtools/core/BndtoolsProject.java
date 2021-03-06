/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.bndtools.core;

import aQute.bnd.build.Project;

import com.liferay.ide.core.BaseLiferayProject;
import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import java.io.File;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

/**
 * @author Gregory Amerson
 */
public class BndtoolsProject extends BaseLiferayProject implements IBundleProject {

	public BndtoolsProject(IProject project, Project bndProject) {
		super(project);
		_bndProject = bndProject;
	}

	@Override
	public boolean filterResource(IPath resourcePath) {
		if (filterResource(resourcePath, _ignorePaths)) {
			return true;
		}

		return false;
	}

	@Override
	public String getBundleShape() {
		return "jar";
	}

	@Override
	public IFile getDescriptorFile(String name) {
		return null;
	}

	@Override
	public IPath getLibraryPath(String filename) {
		return null;
	}

	@Override
	public IPath getOutputBundle(boolean cleanBuild, IProgressMonitor monitor) throws CoreException {
		IPath retval = null;

		try {
			if (cleanBuild) {
				_bndProject.clean();
			}

			File[] buildFiles = _bndProject.getBuildFiles(true);

			if (!CoreUtil.isNullOrEmpty(buildFiles)) {
				File buildFile = buildFiles[0];

				if (FileUtil.exists(buildFile)) {
					retval = new Path(buildFile.getCanonicalPath());
				}
			}
		}
		catch (Exception e) {
			BndtoolsCore.logError("Unable to get output jar for " + getProject().getName(), e);
		}

		return retval;
	}

	@Override
	public IPath getOutputBundlePath() {
		IPath retval = null;

		try {
			File[] buildFiles = _bndProject.getBuildFiles(false);

			if (!CoreUtil.isNullOrEmpty(buildFiles)) {
				File buildFile = buildFiles[0];

				if (FileUtil.exists(buildFile)) {
					retval = new Path(buildFile.getCanonicalPath());
				}
			}
		}
		catch (Exception e) {
			BndtoolsCore.logError("Unable to get output jar for " + getProject().getName(), e);
		}

		return retval;
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return null;
	}

	@Override
	public String getSymbolicName() throws CoreException {
		String retval = _bndProject.getName();

		try {
			Collection<String> names = _bndProject.getBsns();

			if ((names != null) && !names.isEmpty()) {
				retval = names.iterator().next();
			}
		}
		catch (Exception e) {
		}

		return retval;
	}

	@Override
	public boolean isFragmentBundle() {
		IFile bndFile = getProject().getFile("bnd.bnd");

		if (FileUtil.exists(bndFile)) {
			try {
				String content = FileUtil.readContents(bndFile.getContents());

				if (content.contains("Fragment-Host")) {
					return true;
				}
			}
			catch (Exception e) {
			}
		}

		return false;
	}

	private static final String[] _ignorePaths = {"generated"};

	private final Project _bndProject;

}