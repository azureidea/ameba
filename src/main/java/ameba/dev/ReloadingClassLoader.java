package ameba.dev;

import ameba.Application;
import ameba.classloading.AmebaClass;
import ameba.exceptions.UnexpectedException;
import ameba.util.UrlExternalFormComparator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * @author icode
 */
public class ReloadingClassLoader extends URLClassLoader {
    private static final Set<URL> urls = new TreeSet<URL>(new UrlExternalFormComparator());

    private static final List<String> patterns = new ArrayList<String>();
    public File packageRoot;

    public ReloadingClassLoader(Application app) {
        this(ReloadingClassLoader.class.getClassLoader(), app);
    }

    public ReloadingClassLoader(ClassLoader parent, Application app) {
        super(new URL[]{}, parent);

        for (URL url : urls) {
        }
        try {
            addURL(app.getPackageRoot().toURI().toURL());
        } catch (MalformedURLException e) {
            //noop
        }
        packageRoot = app.getPackageRoot();
    }

    /**
     * Include a pattern
     *
     * @param pattern the pattern to include
     */
    public static void includePattern(String pattern) {
        patterns.add("+" + pattern);
    }

    /**
     * Exclude a pattern
     *
     * @param pattern the pattern to exclude
     */
    public static void excludePattern(String pattern) {
        patterns.add("-" + pattern);
    }

    /**
     * Returns the list of all configured inclusion or exclusion patterns
     *
     * @return list of patterns as String
     */
    public static List<String> getPatterns() {
        return patterns;
    }

    /**
     * Add the location of a directory containing class files
     *
     * @param url the URL for the directory
     */
    public static void addLocation(URL url) {
        urls.add(url);
    }

    /**
     * Returns the list of all configured locations of directories containing class files
     *
     * @return list of locations as URL
     */
    public static Set<URL> getLocations() {
        return urls;
    }

    @Override
    public final URL getResource(final String name) {
        URL resource = findResource(name);
        ClassLoader parent = getParent();
        if (resource == null && parent != null) {
            resource = parent.getResource(name);
        }

        return resource;
    }

    public synchronized Class<?> defineClass(String name, byte[] code) {
        if (name == null) {
            throw new IllegalArgumentException("");
        }
        return defineClass(name, code, 0, code.length);
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        Class<?> c = findLoadedClass(name);
        if (c != null) {
            return c;
        }

        // First check if it's an application Class
        Class<?> appClass = loadApplicationClass(name);
        if (appClass != null) {
            if (resolve) {
                resolveClass(appClass);
            }
            return appClass;
        }


        // Delegate to the classic classLoader
        return super.loadClass(name, resolve);
    }

    protected boolean tryClassHere(String name) {
        // don't include classes in the java or javax.servlet package
        if (name != null && (name.startsWith("java.") || name.startsWith("javax.servlet"))) {
            return false;
        }
        // Scan includes, then excludes
        boolean tryHere;

        // If no explicit includes, try here
        if (patterns.size() == 0) {
            tryHere = true;
        } else {
            // See if it matches include patterns
            tryHere = false;
            for (String rawpattern : patterns) {
                if (rawpattern.length() <= 1) {
                    continue;
                }
                boolean isInclude = rawpattern.substring(0, 1).equals("+");
                String pattern = rawpattern.substring(1);
                Pattern p = Pattern.compile(pattern);

                if (name != null && p.matcher(name).find()) {
                    tryHere = isInclude;
                } else if (AmebaClass.getJava(name).exists()) {
                    tryHere = isInclude;
                }
            }
        }

        return tryHere;
    }

    private Class<?> loadApplicationClass(String name) {
        Class<?> clazz = findLoadedClass(name);

        if (clazz == null) {
            final ClassLoader parent = getParent();

            if (tryClassHere(name)) {
                try {
                    clazz = findClass(name);
                } catch (ClassNotFoundException cnfe) {
                    if (parent == null) {
                        // Propagate exception
                        return null;
                    }
                }
            }

            if (clazz == null) {
                if (parent == null) {
                    return null;
                } else {
                    // Will throw a CFNE if not found in parent
                    // see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6500212
                    // clazz = parent.loadClass(name);
                    try {
                        clazz = Class.forName(name, false, parent);
                    } catch (ClassNotFoundException e) {
                        //noop
                    }
                }
            }
        }


        return null;
    }

    public void detectChanges() {

    }

    /**
     * 查找类的字节码
     */
    protected byte[] getClassDefinition(String name) {
        name = name.replace(".", "/") + ".class";
        InputStream is = getResourceAsStream(name);
        if (is == null) {
            return null;
        }
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int count;
            while ((count = is.read(buffer, 0, buffer.length)) > 0) {
                os.write(buffer, 0, count);
            }
            return os.toByteArray();
        } catch (Exception e) {
            throw new UnexpectedException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new UnexpectedException(e);
            }
        }
    }


}