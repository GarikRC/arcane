package org.codehaus.mojo.osxappbundle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Display help information on osxappbundle-maven-plugin.<br/> Call <pre>  mvn osxappbundle:help -Ddetail=true -Dgoal=&lt;goal-name&gt;</pre> to display parameter details.
 *
 * @version generated on Thu May 26 20:55:50 PDT 2011
 * @author org.apache.maven.tools.plugin.generator.PluginHelpGenerator (version 2.7)
 * @goal help
 * @requiresProject false
 * @threadSafe
 */
public class HelpMojo
    extends AbstractMojo
{
    /**
     * If <code>true</code>, display all settable properties for each goal.
     * 
     * @parameter expression="${detail}" default-value="false"
     */
    private boolean detail;

    /**
     * The name of the goal for which to show help. If unspecified, all goals will be displayed.
     * 
     * @parameter expression="${goal}"
     */
    private java.lang.String goal;

    /**
     * The maximum length of a display line, should be positive.
     * 
     * @parameter expression="${lineLength}" default-value="80"
     */
    private int lineLength;

    /**
     * The number of spaces per indentation level, should be positive.
     * 
     * @parameter expression="${indentSize}" default-value="2"
     */
    private int indentSize;


    /** {@inheritDoc} */
    public void execute()
        throws MojoExecutionException
    {
        if ( lineLength <= 0 )
        {
            getLog().warn( "The parameter 'lineLength' should be positive, using '80' as default." );
            lineLength = 80;
        }
        if ( indentSize <= 0 )
        {
            getLog().warn( "The parameter 'indentSize' should be positive, using '2' as default." );
            indentSize = 2;
        }

        StringBuffer sb = new StringBuffer();

        append( sb, "org.codehaus.mojo:osxappbundle-maven-plugin:1.0-alpha-3-SNAPSHOT", 0 );
        append( sb, "", 0 );

        append( sb, "Maven OS X Application Bundle Plugin", 0 );
        append( sb, "A Maven Plugin for generating Mac OS X Application Bundles of Java applications and packaging them as a DMG disk image.", 1 );
        append( sb, "", 0 );

        if ( goal == null || goal.length() <= 0 )
        {
            append( sb, "This plugin has 2 goals:", 0 );
            append( sb, "", 0 );
        }

        if ( goal == null || goal.length() <= 0 || "bundle".equals( goal ) )
        {
            append( sb, "osxappbundle:bundle", 0 );
            append( sb, "Package dependencies as an Application Bundle for Mac OS X.", 1 );
            append( sb, "", 0 );
            if ( detail )
            {
                append( sb, "Available parameters:", 1 );
                append( sb, "", 0 );

                append( sb, "additionalClasspaths", 2 );
                append( sb, "Paths to be put on the classpath in addition to the projects dependencies. Might be useful to specify locations of dependencies in the provided scope that are not distributed with the bundle but have a known location on the system. http://jira.codehaus.org/browse/MOJO-874", 3 );
                append( sb, "", 0 );

                append( sb, "additionalDependencies", 2 );
                append( sb, "Additional files to be included. Might be useful to specify locations of dependencies that are not distributed with any of the bundle (for example custom generated JAR files)", 3 );
                append( sb, "", 0 );

                append( sb, "additionalDMGResources", 2 );
                append( sb, "Additional resources (as a list of FileSet objects) that will be copied into the build directory and included in the .dmg and zip files alongside with the application bundle.", 3 );
                append( sb, "", 0 );

                append( sb, "additionalResources", 2 );
                append( sb, "Additional resources (as a list of FileSet objects) that will be copies into the build directory and included in the Resources directory of the application bundle.", 3 );
                append( sb, "", 0 );

                append( sb, "allowMixedLocalizations (Default: true)", 2 );
                append( sb, "Allow Mixed Localizations.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "", 0 );

                append( sb, "antiAliasGraphics (Default: true)", 2 );
                append( sb, "Turn on anti-aliasing for your application\'s graphics", 3 );
                append( sb, "", 0 );

                append( sb, "antiAliasText (Default: true)", 2 );
                append( sb, "Turn on anti-aliasing for your application\'s text", 3 );
                append( sb, "", 0 );

                append( sb, "buildDirectory (Default: ${project.build.directory}/${project.build.finalName})", 2 );
                append( sb, "The directory where the application bundle will be created", 3 );
                append( sb, "", 0 );

                append( sb, "developmentRegion (Default: English)", 2 );
                append( sb, "The name of the development region.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "", 0 );

                append( sb, "dictionaryFile (Default: org/codehaus/mojo/osxappbundle/Info.plist.template)", 2 );
                append( sb, "The location of the template for Info.plist. Classpath is checked before the file system.", 3 );
                append( sb, "", 0 );

                append( sb, "diskImageFile (Default: ${project.build.directory}/${project.build.finalName}.dmg)", 2 );
                append( sb, "The location of the generated disk image file", 3 );
                append( sb, "", 0 );

                append( sb, "excludeArtifactIds", 2 );
                append( sb, "Comma separated list of ArtifactIds to exclude from the dependency copy.", 3 );
                append( sb, "", 0 );

                append( sb, "getInfoString", 2 );
                append( sb, "Text that appears when the user does a \'Get Info\' on the app.", 3 );
                append( sb, "", 0 );

                append( sb, "iconFile", 2 );
                append( sb, "The icon file for the bundle", 3 );
                append( sb, "", 0 );

                append( sb, "identifier", 2 );
                append( sb, "A java package-like name used to uniquely identify the package.", 3 );
                append( sb, "", 0 );

                append( sb, "includeProjectArtifact (Default: true)", 2 );
                append( sb, "The artifact of the project is included The default is ${true}", 3 );
                append( sb, "", 0 );

                append( sb, "includeProjectDependencies (Default: true)", 2 );
                append( sb, "The dependencies of the project is included The default is ${true}", 3 );
                append( sb, "", 0 );

                append( sb, "infoDictionaryVersion (Default: 6.0)", 2 );
                append( sb, "Version number for the Info.plist format.", 3 );
                append( sb, "", 0 );

                append( sb, "internetEnable (Default: false)", 2 );
                append( sb, "If this is set to true, the generated DMG file will be internet-enabled. The default is ${false}", 3 );
                append( sb, "", 0 );

                append( sb, "javaApplicationStub (Default: /System/Library/Frameworks/JavaVM.framework/Versions/Current/Resources/MacOS/JavaApplicationStub)", 2 );
                append( sb, "The location of the Java Application Stub", 3 );
                append( sb, "", 0 );

                append( sb, "jvmVersion (Default: 1.4+)", 2 );
                append( sb, "A value for the JVMVersion key.", 3 );
                append( sb, "", 0 );

                append( sb, "mainClass", 2 );
                append( sb, "The main class to execute when double-clicking the Application Bundle", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "Expression: ${mainClass}", 3 );
                append( sb, "", 0 );

                append( sb, "name (Default: ${project.name})", 2 );
                append( sb, "The name of the Bundle. This is the name that is given to the application bundle; and it is also what will show up in the application menu, dock etc.", 3 );
                append( sb, "Required: Yes", 3 );
                append( sb, "", 0 );

                append( sb, "packageType (Default: APPL)", 2 );
                append( sb, "Type", 3 );
                append( sb, "", 0 );

                append( sb, "shortVersion", 2 );
                append( sb, "Human-readable version string.", 3 );
                append( sb, "", 0 );

                append( sb, "signature (Default: ????)", 2 );
                append( sb, "Signature", 3 );
                append( sb, "", 0 );

                append( sb, "startOnFirstThread (Default: false)", 2 );
                append( sb, "If this is set to true, the JVM will be launched with flag -XstartOnFirstThread. Use this property instead of vmOptions for this option.", 3 );
                append( sb, "", 0 );

                append( sb, "useScreenMenuBar (Default: true)", 2 );
                append( sb, "If this is set to true, the window\'s menu bar will be integrated into the native system menu bar.", 3 );
                append( sb, "", 0 );

                append( sb, "version (Default: ${project.version})", 2 );
                append( sb, "The version of the project. Will be used as the value of the CFBundleVersion key.", 3 );
                append( sb, "", 0 );

                append( sb, "vmOptions", 2 );
                append( sb, "Options to the JVM, will be used as the value of VMOptions in Info.plist.", 3 );
                append( sb, "", 0 );

                append( sb, "workingDirectory", 2 );
                append( sb, "The working directory of the bundle", 3 );
                append( sb, "", 0 );
            }
        }

        if ( goal == null || goal.length() <= 0 || "help".equals( goal ) )
        {
            append( sb, "osxappbundle:help", 0 );
            append( sb, "Display help information on osxappbundle-maven-plugin.\nCall\n\u00a0\u00a0mvn\u00a0osxappbundle:help\u00a0-Ddetail=true\u00a0-Dgoal=<goal-name>\nto display parameter details.", 1 );
            append( sb, "", 0 );
            if ( detail )
            {
                append( sb, "Available parameters:", 1 );
                append( sb, "", 0 );

                append( sb, "detail (Default: false)", 2 );
                append( sb, "If true, display all settable properties for each goal.", 3 );
                append( sb, "Expression: ${detail}", 3 );
                append( sb, "", 0 );

                append( sb, "goal", 2 );
                append( sb, "The name of the goal for which to show help. If unspecified, all goals will be displayed.", 3 );
                append( sb, "Expression: ${goal}", 3 );
                append( sb, "", 0 );

                append( sb, "indentSize (Default: 2)", 2 );
                append( sb, "The number of spaces per indentation level, should be positive.", 3 );
                append( sb, "Expression: ${indentSize}", 3 );
                append( sb, "", 0 );

                append( sb, "lineLength (Default: 80)", 2 );
                append( sb, "The maximum length of a display line, should be positive.", 3 );
                append( sb, "Expression: ${lineLength}", 3 );
                append( sb, "", 0 );
            }
        }

        if ( getLog().isInfoEnabled() )
        {
            getLog().info( sb.toString() );
        }
    }

    /**
     * <p>Repeat a String <code>n</code> times to form a new string.</p>
     *
     * @param str String to repeat
     * @param repeat number of times to repeat str
     * @return String with repeated String
     * @throws NegativeArraySizeException if <code>repeat < 0</code>
     * @throws NullPointerException if str is <code>null</code>
     */
    private static String repeat( String str, int repeat )
    {
        StringBuffer buffer = new StringBuffer( repeat * str.length() );

        for ( int i = 0; i < repeat; i++ )
        {
            buffer.append( str );
        }

        return buffer.toString();
    }

    /** 
     * Append a description to the buffer by respecting the indentSize and lineLength parameters.
     * <b>Note</b>: The last character is always a new line.
     * 
     * @param sb The buffer to append the description, not <code>null</code>.
     * @param description The description, not <code>null</code>.
     * @param indent The base indentation level of each line, must not be negative.
     */
    private void append( StringBuffer sb, String description, int indent )
    {
        for ( Iterator it = toLines( description, indent, indentSize, lineLength ).iterator(); it.hasNext(); )
        {
            sb.append( it.next().toString() ).append( '\n' );
        }
    }

    /** 
     * Splits the specified text into lines of convenient display length.
     * 
     * @param text The text to split into lines, must not be <code>null</code>.
     * @param indent The base indentation level of each line, must not be negative.
     * @param indentSize The size of each indentation, must not be negative.
     * @param lineLength The length of the line, must not be negative.
     * @return The sequence of display lines, never <code>null</code>.
     * @throws NegativeArraySizeException if <code>indent < 0</code>
     */
    private static List toLines( String text, int indent, int indentSize, int lineLength )
    {
        List lines = new ArrayList();

        String ind = repeat( "\t", indent );
        String[] plainLines = text.split( "(\r\n)|(\r)|(\n)" );
        for ( int i = 0; i < plainLines.length; i++ )
        {
            toLines( lines, ind + plainLines[i], indentSize, lineLength );
        }

        return lines;
    }

    /** 
     * Adds the specified line to the output sequence, performing line wrapping if necessary.
     * 
     * @param lines The sequence of display lines, must not be <code>null</code>.
     * @param line The line to add, must not be <code>null</code>.
     * @param indentSize The size of each indentation, must not be negative.
     * @param lineLength The length of the line, must not be negative.
     */
    private static void toLines( List lines, String line, int indentSize, int lineLength )
    {
        int lineIndent = getIndentLevel( line );
        StringBuffer buf = new StringBuffer( 256 );
        String[] tokens = line.split( " +" );
        for ( int i = 0; i < tokens.length; i++ )
        {
            String token = tokens[i];
            if ( i > 0 )
            {
                if ( buf.length() + token.length() >= lineLength )
                {
                    lines.add( buf.toString() );
                    buf.setLength( 0 );
                    buf.append( repeat( " ", lineIndent * indentSize ) );
                }
                else
                {
                    buf.append( ' ' );
                }
            }
            for ( int j = 0; j < token.length(); j++ )
            {
                char c = token.charAt( j );
                if ( c == '\t' )
                {
                    buf.append( repeat( " ", indentSize - buf.length() % indentSize ) );
                }
                else if ( c == '\u00A0' )
                {
                    buf.append( ' ' );
                }
                else
                {
                    buf.append( c );
                }
            }
        }
        lines.add( buf.toString() );
    }

    /** 
     * Gets the indentation level of the specified line.
     * 
     * @param line The line whose indentation level should be retrieved, must not be <code>null</code>.
     * @return The indentation level of the line.
     */
    private static int getIndentLevel( String line )
    {
        int level = 0;
        for ( int i = 0; i < line.length() && line.charAt( i ) == '\t'; i++ )
        {
            level++;
        }
        for ( int i = level + 1; i <= level + 4 && i < line.length(); i++ )
        {
            if ( line.charAt( i ) == '\t' )
            {
                level++;
                break;
            }
        }
        return level;
    }
}
