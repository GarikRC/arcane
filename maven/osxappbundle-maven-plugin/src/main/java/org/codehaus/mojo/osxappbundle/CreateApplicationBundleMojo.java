package org.codehaus.mojo.osxappbundle;

/*
 * Copyright 2001-2008 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.codehaus.mojo.osxappbundle.encoding.DefaultEncodingDetector;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.velocity.VelocityComponent;

import java.io.*;
import java.util.*;

//import org.codehaus.plexus.archiver.zip.ZipArchiver;

/**
 * Package dependencies as an Application Bundle for Mac OS X.
 *
 * @goal bundle
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class CreateApplicationBundleMojo
        extends AbstractMojo {

    /**
     * Default includes - everything is included.
     */
    private static final String[] DEFAULT_INCLUDES = {"**/**"};

    /**
     * The Maven Project Object
     *
     * @parameter default-value="${project}"
     * @readonly
     */
    private MavenProject project;

    /**
     * The directory where the application bundle will be created
     *
     * @parameter default-value="${project.build.directory}/${project.build.finalName}";
     */
    private File buildDirectory;

    /**
     * The location of the generated disk image file
     *
     * @parameter default-value="${project.build.directory}/${project.build.finalName}.dmg"
     */
    private File diskImageFile;

    /**
     * The location of the Java Application Stub
     *
     * @parameter default-value="/System/Library/Frameworks/JavaVM.framework/Versions/Current/Resources/MacOS/JavaApplicationStub";
     */
    private File javaApplicationStub;

    /**
     * The working directory of the bundle
     *
     * @parameter
     */
    private File workingDirectory;

    /**
     * The name of the development region.
     *
     * @parameter default-value="English"
     * @required
     */
    private String developmentRegion;

    /**
     * Allow Mixed Localizations.
     *
     * @parameter default-value="true"
     * @required
     */
    private boolean allowMixedLocalizations;

    /**
     * The main class to execute when double-clicking the Application Bundle
     *
     * @parameter expression="${mainClass}"
     * @required
     */
    private String mainClass;

    /**
     * The name of the Bundle. This is the name that is given to the application bundle;
     * and it is also what will show up in the application menu, dock etc.
     *
     * @parameter default-value="${project.name}"
     * @required
     */
    private String name;

    /**
     * The icon file for the bundle
     *
     * @parameter
     */
    private File iconFile;

    /**
     * The version of the project. Will be used as the value of the CFBundleVersion key.
     *
     * @parameter default-value="${project.version}"
     */
    private String version;

    /**
     * Human-readable version string.
     *
     * @parameter
     */
    private String shortVersion;

    /**
     * A value for the JVMVersion key.
     *
     * @parameter default-value="1.4+"
     */
    private String jvmVersion;

    /**
     * The location of the produced Zip file containing the bundle.
     *
     * @parameter default-value="${project.build.directory}/${project.build.finalName}-app.zip"
     */
    //private File zipFile;

    /**
     * Paths to be put on the classpath in addition to the projects dependencies.
     * Might be useful to specify locations of dependencies in the provided scope that are not distributed with
     * the bundle but have a known location on the system.
     * {@see http://jira.codehaus.org/browse/MOJO-874}
     *
     * @parameter
     */
    private List additionalClasspaths;

    /**
     * The artifact of the project is included
     * The default is ${true}
     *
     * @parameter default-value="true"
     */
    private boolean includeProjectArtifact;

    /**
     * The dependencies of the project is included
     * The default is ${true}
     *
     * @parameter default-value="true"
     */
    private boolean includeProjectDependencies;

    /**
     * Additional files to be included.
     * Might be useful to specify locations of dependencies that are not distributed with any of the bundle (for example
     * custom generated JAR files)
     *
     * @parameter
     */
    private List additionalDependencies;

    /**
     * Additional resources (as a list of FileSet objects) that will be copied into
     * the build directory and included in the .dmg and zip files alongside with the
     * application bundle.
     *
     * @parameter
     */
    private List additionalDMGResources;

    /**
     * Additional resources (as a list of FileSet objects) that will be copies into
     * the build directory and included in the Resources directory of the
     * application bundle.
     *
     * @parameter
     */
    private List additionalResources;

    /**
     * Velocity Component.
     *
     * @component
     * @readonly
     */
    private VelocityComponent velocity;

    /**
     * The location of the template for Info.plist.
     * Classpath is checked before the file system.
     *
     * @parameter default-value="org/codehaus/mojo/osxappbundle/Info.plist.template"
     */
    private String dictionaryFile;

    /**
     * Type
     *
     * @parameter default-value="APPL"
     */
    private String packageType;

    /**
     * Signature
     *
     * @parameter default-value="????"
     */
    private String signature;

    /**
     * Version number for the Info.plist format.
     *
     * @parameter default-value="6.0"
     */
    private String infoDictionaryVersion;

    /**
     * A java package-like name used to uniquely identify the package.
     *
     * @parameter
     */
    private String identifier;

    /**
     * Options to the JVM, will be used as the value of VMOptions in Info.plist.
     *
     * @parameter
     */
    private String vmOptions;

    /**
     * Text that appears when the user does a "Get Info" on the app.
     *
     * @parameter
     */
    private String getInfoString;

    /**
     * The MavenHelper.
     *
     * @component
     * @readonly
     */
    private MavenProjectHelper projectHelper;

    /**
     * The Zip archiver.
     *
     * @component role="component.org.codehaus.plexus.archiver.Archiver" roleHint="zip"
     * @required
     * @readonly
     */
    //private ZipArchiver zipArchiver;

    /**
     * If this is set to <code>true</code>, the generated DMG file will be internet-enabled.
     * The default is ${false}
     *
     * @parameter default-value="false"
     */
    private boolean internetEnable;

    /**
     * Comma separated list of ArtifactIds to exclude from the dependency copy.
     *
     * @parameter default-value=""
     */
    private Set excludeArtifactIds;

    /**
     * If this is set to <code>true</code>, the window's menu bar will be integrated into the native system menu bar.
     *
     * @parameter default-value="true"
     */
    private boolean useScreenMenuBar;

    /**
     * Turn on anti-aliasing for your application's text
     *
     * @parameter default-value="true"
     */
    private boolean antiAliasText;

    /**
     * Turn on anti-aliasing for your application's graphics
     *
     * @parameter default-value="true"
     */
    private boolean antiAliasGraphics;

    /**
     * If this is set to <code>true</code>, the JVM will be launched with flag -XstartOnFirstThread.
     * Use this property instead of vmOptions for this option.
     *
     * @parameter default-value="false"
     */
    private boolean startOnFirstThread;

    /**
     * The path to the SetFile tool.
     */
    private static final String SET_FILE_PATH = "/Developer/Tools/SetFile";

    private String pkgInfoTemplate = "org/codehaus/mojo/osxappbundle/PkgInfo.template";


    /**
     * Bundle project as a Mac OS X application bundle.
     *
     * @throws MojoExecutionException If an unexpected error occurs during packaging of the bundle.
     */
    public void execute()
            throws MojoExecutionException {

        // Set up and create directories
        buildDirectory.mkdirs();

        File bundleDirectory = new File(buildDirectory, cleanBundleName(name) + ".app");
        bundleDirectory.mkdirs();

        File contentsDirectory = new File(bundleDirectory, "Contents");
        contentsDirectory.mkdirs();

        File resourcesDirectory = new File(contentsDirectory, "Resources");
        resourcesDirectory.mkdirs();

        File javaDirectory = new File(resourcesDirectory, "Java");
        javaDirectory.mkdirs();

        File macOSDirectory = new File(contentsDirectory, "MacOS");
        macOSDirectory.mkdirs();

        File stub = copyJavaApplicationStub(macOSDirectory);

        copyIconFile(resourcesDirectory);

        // Resolve and copy in all dependencies from the pom
        List files = copyArtifacts(javaDirectory);

        // Create and write the Info.plist file
        File infoPlist = new File(bundleDirectory, "Contents/Info.plist");
        File pkgInfo = new File(bundleDirectory, "Contents/PkgInfo");
        writeInfoPlist(infoPlist, files);
        writePkgInfo(pkgInfo);

        // Copy specified additional resources into the top level directory
        if (additionalResources != null && !additionalResources.isEmpty()) {
            copyResources(additionalResources, javaDirectory);
        }

        // Copy specified additional resources into the top level directory
        if (additionalDMGResources != null && !additionalDMGResources.isEmpty()) {
            copyDMGResources(additionalDMGResources);
        }

        if (isMacOSX()) {
            markStubExecutable(stub);
            registerAppBundle(bundleDirectory);
            createDmgFile();

            if (internetEnable) {
                enableInternetDmg();
            }
            projectHelper.attachArtifact(project, "dmg", null, diskImageFile);
        }

        //zipArchiver.setDestFile(zipFile);
        //try {
            //createZipArchive(bundleDirectory);
            //projectHelper.attachArtifact(project, "zip", null, zipFile);
        //} catch (ArchiverException e) {
        //    throw new MojoExecutionException("Could not create zip archive of application bundle in " + zipFile, e);
        //} catch (IOException e) {
        //    throw new MojoExecutionException("IOException creating zip archive of application bundle in " + zipFile,
        //            e);
        //}

    }

    /*private void createZipArchive(File bundleDir) throws ArchiverException, IOException {
        String[] stubPattern = {buildDirectory.getName() + "/" + bundleDir.getName() + "/Contents/MacOS/"
                + javaApplicationStub.getName()};

        zipArchiver.addDirectory(buildDirectory.getParentFile(), new String[]{buildDirectory.getName() + "/**"},
                stubPattern);

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(buildDirectory.getParentFile());
        scanner.setIncludes(stubPattern);
        scanner.scan();

        String[] stubs = scanner.getIncludedFiles();
        for (int i = 0; i < stubs.length; i++) {
            String s = stubs[i];
            zipArchiver.addFile(new File(buildDirectory.getParentFile(), s), s, 0755);
        }

        zipArchiver.createArchive();
    }*/

    private void enableInternetDmg() throws MojoExecutionException {
        try {

            Commandline internetEnable = new Commandline();

            internetEnable.setExecutable("hdiutil");
            internetEnable.createArg().setValue("internet-enable");
            internetEnable.createArg().setValue("-yes");
            internetEnable.createArg().setValue(diskImageFile.getAbsolutePath());

            internetEnable.execute();
        } catch (CommandLineException e) {
            throw new MojoExecutionException("Error internet enabling disk image: " + diskImageFile, e);
        }
    }

    private void createDmgFile() throws MojoExecutionException {
        // Create a .dmg file of the app
        Commandline dmg = new Commandline();
        try {
            dmg.setExecutable("hdiutil");
            dmg.createArg().setValue("create");
            dmg.createArg().setValue("-srcfolder");
            dmg.createArg().setValue(buildDirectory.getAbsolutePath());
            dmg.createArg().setValue(diskImageFile.getAbsolutePath());
            try {
                dmg.execute().waitFor();
            } catch (InterruptedException e) {
                throw new MojoExecutionException("Thread was interrupted while creating DMG " + diskImageFile, e);
            }
        } catch (CommandLineException e) {
            throw new MojoExecutionException("Error creating disk image " + diskImageFile, e);
        }
    }

    private void registerAppBundle(File bundleDir) throws MojoExecutionException {
        // This makes sure that the .app dir is actually registered as an application bundle
        if (new File(SET_FILE_PATH).exists()) {
            Commandline setFile = new Commandline();
            try {
                setFile.setExecutable(SET_FILE_PATH);
                setFile.createArg().setValue("-a");
                setFile.createArg().setValue("B");
                setFile.createArg().setValue(bundleDir.getAbsolutePath());

                setFile.execute();
            } catch (CommandLineException e) {
                throw new MojoExecutionException("Error executing " + setFile, e);
            }
        } else {
            getLog().warn("Could not set 'Has Bundle' attribute. " +
                    SET_FILE_PATH + " not found. Are Developer Tools installed?");
        }
    }

    private void markStubExecutable(File stub) throws MojoExecutionException {
        // Make the stub executable
        Commandline chmod = new Commandline();
        try {
            chmod.setExecutable("chmod");
            chmod.createArg().setValue("755");
            chmod.createArg().setValue(stub.getAbsolutePath());

            chmod.execute();
        } catch (CommandLineException e) {
            throw new MojoExecutionException("Error executing " + chmod + " ", e);
        }
    }

    private void copyIconFile(File resourcesDir) throws MojoExecutionException {
        // Copy icon file to the bundle if specified
        if (iconFile != null) {
            try {
                FileUtils.copyFileToDirectory(iconFile.getPath(), resourcesDir.toString());
            } catch (IOException e) {
                throw new MojoExecutionException("Error copying file " + iconFile + " to " + resourcesDir, e);
            }
        }
    }

    private File copyJavaApplicationStub(File macOSDirectory) throws MojoExecutionException {
        // Copy in the native java application stub
        File stub = new File(macOSDirectory, javaApplicationStub.getName());
        if (!javaApplicationStub.exists()) {
            String message = "Can't find JavaApplicationStub binary. File does not exist: " + javaApplicationStub;

            if (!isMacOSX()) {
                message += "\nNOTICE: You are running the osxappbundle plugin on a non-OSX platform." +
                        "To make this work you need to copy the JavaApplicationStub binary into your source tree " +
                        "and then configure it with the 'javaApplicationStub' configuration property.\n" +
                        "On an OSX machine, the JavaApplicationStub is typically located under " +
                        "/System/Library/Frameworks/JavaVM.framework/Versions/Current/Resources/MacOS/JavaApplicationStub";
            }

            throw new MojoExecutionException(message);

        } else {
            try {
                FileUtils.copyFile(javaApplicationStub, stub);
            } catch (IOException e) {
                throw new MojoExecutionException(
                        "Could not copy file " + javaApplicationStub + " to directory " + macOSDirectory, e);
            }
        }
        return stub;
    }

    /**
     * The bundle name is used in paths, so we need to clean it for
     * unwanted characters, like ":" on Windows.
     *
     * @param bundleName the "unclean" bundle name.
     * @return a clean bundle name
     */
    private String cleanBundleName(String bundleName) {
        return bundleName.replace(':', '-');
    }

    private boolean isMacOSX() {
        String osName = System.getProperty("os.name");
        return osName.startsWith("Mac OS X");
    }

    /**
     * Copy all dependencies into the $JAVAROOT directory
     *
     * @param javaDirectory where to put jar files
     * @return A list of file names added
     * @throws MojoExecutionException
     */
    private List copyArtifacts(File javaDirectory)
            throws MojoExecutionException {

        ArtifactRepositoryLayout layout = new DefaultRepositoryLayout();

        List list = new ArrayList();


        if (includeProjectArtifact) {
            // First, copy the project's own artifact
            File artifactFile = project.getArtifact().getFile();

            // Pom modules have no artifact file
            if (artifactFile != null) {
                list.add(project.getArtifact().getFile().getName());

                try {
                    FileUtils.copyFile(artifactFile, new File(javaDirectory, project.getArtifact().getFile().getName()));
                } catch (IOException e) {
                    throw new MojoExecutionException("Could not copy artifact file " + artifactFile + " to " + javaDirectory);
                }
            }
        }
        if (includeProjectDependencies) {
            Set artifacts = project.getArtifacts();

            Iterator i = artifacts.iterator();

            while (i.hasNext()) {
                Artifact artifact = (Artifact) i.next();

                String artifactId = artifact.getArtifactId();
                if (excludeArtifactIds != null && excludeArtifactIds.contains(artifactId)) {
                    getLog().info("Skipping excluded artifact: " + artifact.toString());
                    continue;
                }

                File file = artifact.getFile();
                File dest = new File(javaDirectory, file.getName());

                getLog().debug("Adding " + file);

                try {
                    FileUtils.copyFile(file, dest);
                } catch (IOException e) {
                    throw new MojoExecutionException("Error copying file " + file + " into " + javaDirectory, e);
                }

                list.add(artifact.getFile().getName());
            }
        }
        if (additionalDependencies != null) {
            for (int i = 0; i < additionalDependencies.size(); i++) {
                String pathElement = (String) additionalDependencies.get(i);

                File file = new File(pathElement);
                File dest = new File(javaDirectory, file.getName());

                getLog().debug("Adding " + file);

                try {
                    FileUtils.copyFile(file, dest);
                } catch (IOException e) {
                    throw new MojoExecutionException("Error copying file " + file + " into " + javaDirectory, e);
                }

                list.add(file.getName());
            }
        }

        return list;
    }

    /**
     * Writes an Info.plist file describing this bundle.
     *
     * @param infoPlist The file to write Info.plist contents to
     * @param files     A list of file names of the jar files to add in $JAVAROOT
     * @throws MojoExecutionException
     */
    private void writeInfoPlist(File infoPlist, List files)
            throws MojoExecutionException {

        VelocityContext velocityContext = new VelocityContext();

        velocityContext.put("mainClass", mainClass);
        velocityContext.put("executable", javaApplicationStub.getName());
        velocityContext.put("vmOptions", vmOptions);
        velocityContext.put("developmentRegion", developmentRegion);
        velocityContext.put("allowMixedLocalizations", Boolean.valueOf(allowMixedLocalizations));
        velocityContext.put("name", cleanBundleName(name));
        velocityContext.put("infoDictionaryVersion", infoDictionaryVersion);
        velocityContext.put("iconFile", iconFile == null ? "GenericJavaApp.icns" : iconFile.getName());
        velocityContext.put("version", version);
        velocityContext.put("shortVersion", shortVersion);
        velocityContext.put("jvmVersion", jvmVersion);
        velocityContext.put("packageType", packageType);
        velocityContext.put("signature", signature);
        velocityContext.put("identifier", identifier);
        velocityContext.put("getInfoString", getInfoString);
        velocityContext.put("workingDirectory", workingDirectory ==
                null ? "$APP_PACKAGE/Contents/Resources/Java" : workingDirectory.getPath());
        velocityContext.put("useScreenMenuBar", Boolean.valueOf(useScreenMenuBar));
        velocityContext.put("startOnFirstThread", Boolean.valueOf(startOnFirstThread));
        velocityContext.put("antiAliasGraphics", Boolean.valueOf(antiAliasGraphics));
        velocityContext.put("antiAliasText", Boolean.valueOf(antiAliasText));

        StringBuffer jarFilesBuffer = new StringBuffer();

        jarFilesBuffer.append("<array>\n");
        for (int i = 0; i < files.size(); i++) {
            String name = (String) files.get(i);
            jarFilesBuffer.append("            <string>");
            jarFilesBuffer.append("$JAVAROOT/").append(name);
            jarFilesBuffer.append("</string>\n");

        }
        if (additionalClasspaths != null) {
            for (int i = 0; i < additionalClasspaths.size(); i++) {
                String pathElement = (String) additionalClasspaths.get(i);
                jarFilesBuffer.append("        <string>");
                jarFilesBuffer.append("            " + pathElement);
                jarFilesBuffer.append("        </string>\n");

            }
        }

        jarFilesBuffer.append("        </array>");

        velocityContext.put("classpath", jarFilesBuffer.toString());

        try {

            String encoding = detectEncoding(dictionaryFile, velocityContext);
            getLog().debug("Detected encoding " + encoding + " for dictionary file " + dictionaryFile);
            Writer writer = new OutputStreamWriter(new FileOutputStream(infoPlist), encoding);
            velocity.getEngine().mergeTemplate(dictionaryFile, encoding, velocityContext, writer);
            writer.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Could not write Info.plist to file " + infoPlist, e);
        } catch (ParseErrorException e) {
            throw new MojoExecutionException("Error parsing " + dictionaryFile, e);
        } catch (ResourceNotFoundException e) {
            throw new MojoExecutionException("Could not find resource for template " + dictionaryFile, e);
        } catch (MethodInvocationException e) {
            throw new MojoExecutionException(
                    "MethodInvocationException occurred merging Info.plist template " + dictionaryFile, e);
        } catch (Exception e) {
            throw new MojoExecutionException("Exception occurred merging Info.plist template " + dictionaryFile, e);
        }

    }

    private void writePkgInfo(File pkgInfo)
            throws MojoExecutionException {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("packageType", packageType);
        velocityContext.put("signature", signature);

        try {
            String encoding = detectEncoding(pkgInfoTemplate, velocityContext);
            getLog().debug("Detected encoding " + encoding + " for pkgInfo file " + pkgInfoTemplate);
            Writer writer = new OutputStreamWriter(new FileOutputStream(pkgInfo), encoding);
            velocity.getEngine().mergeTemplate(pkgInfoTemplate, encoding, velocityContext, writer);
            writer.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Could not write PkgInfo to file " + pkgInfo, e);
        } catch (ParseErrorException e) {
            throw new MojoExecutionException("Error parsing " + pkgInfoTemplate, e);
        } catch (ResourceNotFoundException e) {
            throw new MojoExecutionException("Could not find resource for template " + pkgInfoTemplate, e);
        } catch (MethodInvocationException e) {
            throw new MojoExecutionException(
                    "MethodInvocationException occurred merging PkgInfo template " + pkgInfoTemplate, e);
        } catch (Exception e) {
            throw new MojoExecutionException("Exception occurred merging PkgInfo template " + pkgInfoTemplate, e);
        }
    }

    private String detectEncoding(String dictionaryFile, VelocityContext velocityContext)
            throws Exception {
        StringWriter sw = new StringWriter();
        velocity.getEngine().mergeTemplate(dictionaryFile, "utf-8", velocityContext, sw);
        return new DefaultEncodingDetector().detectXmlEncoding(new ByteArrayInputStream(sw.toString().getBytes("utf-8")));
    }

    /**
     * Copies given resources to the build directory.
     *
     * @param fileSets A list of FileSet objects that represent additional resources to copy.
     * @throws MojoExecutionException In case af a resource copying error.
     */
    private void copyResources(List fileSets, File javaDirectory)
            throws MojoExecutionException {
        final String[] emptyStrArray = {};

        for (Iterator it = fileSets.iterator(); it.hasNext(); ) {
            FileSet fileSet = (FileSet) it.next();

            File resourceDirectory = new File(fileSet.getDirectory());
            if (!resourceDirectory.isAbsolute()) {
                resourceDirectory = new File(project.getBasedir(), resourceDirectory.getPath());
            }

            if (!resourceDirectory.exists()) {
                getLog().info("Additional resource directory does not exist: " + resourceDirectory);
                continue;
            }

            DirectoryScanner scanner = new DirectoryScanner();

            scanner.setBasedir(resourceDirectory);
            if (fileSet.getIncludes() != null && !fileSet.getIncludes().isEmpty()) {
                scanner.setIncludes((String[]) fileSet.getIncludes().toArray(emptyStrArray));
            } else {
                scanner.setIncludes(DEFAULT_INCLUDES);
            }

            if (fileSet.getExcludes() != null && !fileSet.getExcludes().isEmpty()) {
                scanner.setExcludes((String[]) fileSet.getExcludes().toArray(emptyStrArray));
            }

            if (fileSet.isUseDefaultExcludes()) {
                scanner.addDefaultExcludes();
            }

            scanner.scan();

            List includedFiles = Arrays.asList(scanner.getIncludedFiles());

            getLog().info("Copying " + includedFiles.size() + " additional resource"
                    + (includedFiles.size() > 1 ? "s" : ""));

            for (Iterator j = includedFiles.iterator(); j.hasNext(); ) {
                String destination = (String) j.next();
                File source = new File(resourceDirectory, destination);
                File destinationFile = new File(javaDirectory, destination);

                if (!destinationFile.getParentFile().exists()) {
                    destinationFile.getParentFile().mkdirs();
                }

                try {
                    FileUtils.copyFile(source, destinationFile);
                } catch (IOException e) {
                    throw new MojoExecutionException("Error copying additional resource " + source, e);
                }
            }
        }
    }

    /**
     * Copies given resources to the build directory.
     *
     * @param fileSets A list of FileSet objects that represent additional resources to copy.
     * @throws MojoExecutionException In case af a resource copying error.
     */
    private void copyDMGResources(List fileSets)
            throws MojoExecutionException {
        final String[] emptyStrArray = {};

        for (Iterator it = fileSets.iterator(); it.hasNext(); ) {
            FileSet fileSet = (FileSet) it.next();

            File resourceDirectory = new File(fileSet.getDirectory());
            if (!resourceDirectory.isAbsolute()) {
                resourceDirectory = new File(project.getBasedir(), resourceDirectory.getPath());
            }

            if (!resourceDirectory.exists()) {
                getLog().info("Additional resource directory does not exist: " + resourceDirectory);
                continue;
            }

            DirectoryScanner scanner = new DirectoryScanner();

            scanner.setBasedir(resourceDirectory);
            if (fileSet.getIncludes() != null && !fileSet.getIncludes().isEmpty()) {
                scanner.setIncludes((String[]) fileSet.getIncludes().toArray(emptyStrArray));
            } else {
                scanner.setIncludes(DEFAULT_INCLUDES);
            }

            if (fileSet.getExcludes() != null && !fileSet.getExcludes().isEmpty()) {
                scanner.setExcludes((String[]) fileSet.getExcludes().toArray(emptyStrArray));
            }

            if (fileSet.isUseDefaultExcludes()) {
                scanner.addDefaultExcludes();
            }

            scanner.scan();

            List includedFiles = Arrays.asList(scanner.getIncludedFiles());

            getLog().info("Copying " + includedFiles.size() + " additional resource"
                    + (includedFiles.size() > 1 ? "s" : ""));

            for (Iterator j = includedFiles.iterator(); j.hasNext(); ) {
                String destination = (String) j.next();
                File source = new File(resourceDirectory, destination);
                File destinationFile = new File(buildDirectory, destination);

                if (!destinationFile.getParentFile().exists()) {
                    destinationFile.getParentFile().mkdirs();
                }

                try {
                    FileUtils.copyFile(source, destinationFile);
                } catch (IOException e) {
                    throw new MojoExecutionException("Error copying additional resource " + source, e);
                }
            }
        }
    }
}
