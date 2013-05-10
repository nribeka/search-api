/**
 * Copyright 2012 Muzima Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mclinic.search.api.util;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Shamelessly taken from the org.apache.commons.lang.StringUtil class with some modification!
 */
public class StringUtil {

    /**
     * An empty immutable <code>String</code> array.
     */
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     * The empty String <code>""</code>.
     */
    public static final String EMPTY = "";

    /**
     * <pre>
     *     StringUtil.escapeUri("localhost:8081/" = "localhost\:8081\/"
     * </pre>
     * @param uri the uri to be escaped
     * @return the escaped uri
     */
    public static String escapeUri(final String uri) {
        StringWriter writer = new StringWriter(uri.length() * 2);

        int size = uri.length();
        for (int i = 0; i < size; i++) {
            char ch = uri.charAt(i);
            switch (ch) {
                case '/':
                    writer.write('\\');
                    writer.write('/');
                    break;
                case ':':
                    writer.write('\\');
                    writer.write(':');
                    break;
                case '-':
                    writer.write('\\');
                    writer.write('-');
                    break;
                default:
                    writer.write(ch);
                    break;
            }
        }

        return writer.toString();
    }

    /**
     * <pre>
     * StringUtils.lowerCase(null)  = null
     * StringUtils.lowerCase("")    = ""
     * StringUtils.lowerCase("aBc") = "abc"
     * </pre>
     *
     * @param str  the String to lower case, may be null
     * @return the lower cased String, <code>null</code> if null String input
     */
    public static String lowerCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    /**
     * <pre>
     * StringUtil.isEmpty(null)      = true
     * StringUtil.isEmpty("")        = true
     * StringUtil.isEmpty(" ")       = false
     * StringUtil.isEmpty("bob")     = false
     * StringUtil.isEmpty("  bob  ") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static boolean isEmpty(final String str) {
        return str == null || str.length() == 0;
    }

    /**
     * Quote the string with single quote.
     *
     * @param str the string to be quoted
     * @return quoted string or null if the input is null
     */
    public static String quote(String str) {
        return str != null ? "\"" + str + "\"" : null;
    }

    /**
     * <pre>
     * StringUtil.equals(null, null)   = true
     * StringUtil.equals(null, "abc")  = false
     * StringUtil.equals("abc", null)  = false
     * StringUtil.equals("abc", "abc") = true
     * StringUtil.equals("abc", "ABC") = false
     * </pre>
     *
     * @param str1 the first String, may be null
     * @param str2 the second String, may be null
     * @return <code>true</code> if the Strings are equal, case sensitive, or both <code>null</code>
     * @see java.lang.String#equals(Object)
     */
    public static boolean equals(final String str1, final String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }

    /**
     * <pre>
     * StringUtil.split(null, *)         = null
     * StringUtil.split("", *)           = []
     * StringUtil.split("abc def", null) = ["abc", "def"]
     * StringUtil.split("abc def", " ")  = ["abc", "def"]
     * StringUtil.split("abc  def", " ") = ["abc", "def"]
     * StringUtil.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
     * </pre>
     *
     * @param str            the String to parse, may be null
     * @param separatorChars the characters used as the delimiters, <code>null</code> splits on whitespace
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    public static String[] split(final String str, final String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    /**
     * <pre>
     * StringUtil.split(null, *, *)            = null
     * StringUtil.split("", *, *)              = []
     * StringUtil.split("ab de fg", null, 0)   = ["ab", "cd", "ef"]
     * StringUtil.split("ab   de fg", null, 0) = ["ab", "cd", "ef"]
     * StringUtil.split("ab:cd:ef", ":", 0)    = ["ab", "cd", "ef"]
     * StringUtil.split("ab:cd:ef", ":", 2)    = ["ab", "cd:ef"]
     * </pre>
     *
     * @param str            the String to parse, may be null
     * @param separatorChars the characters used as the delimiters, <code>null</code> splits on whitespace
     * @param max            the maximum number of elements to include in the array. A zero or negative value implies
     *                       no limit
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    public static String[] split(final String str, final String separatorChars, final int max) {
        return splitWorker(str, separatorChars, max, false);
    }

    /**
     * Performs the logic for the <code>split</code> and
     * <code>splitPreserveAllTokens</code> methods that return a maximum array
     * length.
     *
     * @param str               the String to parse, may be <code>null</code>
     * @param separatorChars    the separate character
     * @param max               the maximum number of elements to include in the array. A zero or negative value
     *                          implies no limit.
     * @param preserveAllTokens if <code>true</code>, adjacent separators are treated as empty token separators;
     *                          if <code>false</code>, adjacent separators are treated as one separator.
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    private static String[] splitWorker(final String str, final String separatorChars,
                                        final int max, final boolean preserveAllTokens) {
        if (str == null)
            return null;

        int len = str.length();
        if (len == 0)
            return EMPTY_STRING_ARRAY;

        List<String> list = new ArrayList<String>();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // Optimise 1 character case
            char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || (preserveAllTokens && lastMatch))
            list.add(str.substring(start, i));

        return list.toArray(new String[list.size()]);
    }

    /**
     * Performs the logic for the <code>split</code> and
     * <code>splitPreserveAllTokens</code> methods that do not return a
     * maximum array length.
     *
     * @param str               the String to parse, may be <code>null</code>
     * @param separatorChar     the separate character
     * @param preserveAllTokens if <code>true</code>, adjacent separators are treated as empty token separators;
     *                          if <code>false</code>, adjacent separators are treated as one separator.
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    private static String[] splitWorker(final String str, final char separatorChar, final boolean preserveAllTokens) {
        if (str == null)
            return null;

        int len = str.length();
        if (len == 0)
            return EMPTY_STRING_ARRAY;

        List<String> list = new ArrayList<String>();
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                start = ++i;
                continue;
            }
            lastMatch = false;
            match = true;
            i++;
        }

        if (match || (preserveAllTokens && lastMatch))
            list.add(str.substring(start, i));

        return list.toArray(new String[list.size()]);
    }
}
