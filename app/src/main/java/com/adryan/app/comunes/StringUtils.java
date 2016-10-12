package com.adryan.app.comunes;

/*
* Copyright 2012 Ivan Masmitjà
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


/**
 * <p>Operations on {@link java.lang.String} that are
 * <code>null</code> safe.</p>
 * <p/>
 * Based on org.apache.commons.lang.StringUtils
 * see <a href="http://commons.apache.org/lang/api-2.5/org/apache/commons/lang/StringUtils.html">StringUtils Class</a>
 */
public final class StringUtils {

    /**
     * The empty String <code>""</code>.
     *
     * @since 2.0
     */
    public static final String EMPTY = "";
    /**
     * <p>
     * The maximum size to which the padding constant(s) can expand.
     * </p>
     */
    private static final int PAD_LIMIT = 8192;

    private StringUtils() {
    }

    // Empty checks
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Checks if a CharSequence is empty ("") or null.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.isEmpty(null) = true
     * StringUtils.isEmpty("") = true
     * StringUtils.isEmpty(" ") = false
     * StringUtils.isEmpty("bob") = false
     * StringUtils.isEmpty(" bob ") = false
     * </pre>
     * <p/>
     * <p>
     * NOTE: This method changed in Lang version 2.0. It no longer trims the
     * CharSequence. That functionality is available in isBlank().
     * </p>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is empty or null
     * @since 3.0 Changed signature from isEmpty(String) to
     * isEmpty(CharSequence)
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * <p>
     * Checks if a CharSequence is not empty ("") and not null.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.isNotEmpty(null) = false
     * StringUtils.isNotEmpty("") = false
     * StringUtils.isNotEmpty(" ") = true
     * StringUtils.isNotEmpty("bob") = true
     * StringUtils.isNotEmpty(" bob ") = true
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is not empty and not null
     * @since 3.0 Changed signature from isNotEmpty(String) to
     * isNotEmpty(CharSequence)
     */
    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    // Repeat
    // -----------------------------------------------------------------------

    /**
     * <p>Repeat a String <code>repeat</code> times to form a
     * new String.</p>
     * <p/>
     * <pre>
     * StringUtils.repeat(null, 2) = null
     * StringUtils.repeat("", 0) = ""
     * StringUtils.repeat("", 2) = ""
     * StringUtils.repeat("a", 3) = "aaa"
     * StringUtils.repeat("ab", 2) = "abab"
     * StringUtils.repeat("a", -2) = ""
     * </pre>
     *
     * @param str the String to repeat, may be null
     * @param repeat number of times to repeat str, negative treated as zero
     * @return a new String consisting of the original String repeated,
     * <code>null</code> if null String input
     * @since 2.5
     */
    public static String repeat(String str, int repeat) {
        // Performance tuned for 2.0 (JDK1.4)

        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return EMPTY;
        }
        int inputLength = str.length();
        if (repeat == 1 || inputLength == 0) {
            return str;
        }
        if (inputLength == 1 && repeat <= PAD_LIMIT) {
            return padding(repeat, str.charAt(0));
        }

        int outputLength = inputLength * repeat;
        switch (inputLength) {
            case 1:
                char ch = str.charAt(0);
                char[] output1 = new char[outputLength];
                for (int i = repeat - 1; i >= 0; i--) {
                    output1[i] = ch;
                }
                return new String(output1);
            case 2:
                char ch0 = str.charAt(0);
                char ch1 = str.charAt(1);
                char[] output2 = new char[outputLength];
                for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
                    output2[i] = ch0;
                    output2[i + 1] = ch1;
                }
                return new String(output2);
            default:
                StringBuffer buf = new StringBuffer(outputLength);
                for (int i = 0; i < repeat; i++) {
                    buf.append(str);
                }
                return buf.toString();
        }
    }

    /**
     * <p>Repeat a String <code>repeat</code> times to form a
     * new String, with a String separator injected each time. </p>
     * <p/>
     * <pre>
     * StringUtils.repeat(null, null, 2) = null
     * StringUtils.repeat(null, "x", 2) = null
     * StringUtils.repeat("", null, 0) = ""
     * StringUtils.repeat("", "", 2) = ""
     * StringUtils.repeat("", "x", 3) = "xx"
     * StringUtils.repeat("?", ", ", 3) = "?, ?, ?"
     * </pre>
     *
     * @param str the String to repeat, may be null
     * @param separator the String to inject, may be null
     * @param repeat number of times to repeat str, negative treated as zero
     * @return a new String consisting of the original String repeated,
     * <code>null</code> if null String input
     */
    public static String repeat(String str, String separator, int repeat) {
        if (str == null || separator == null) {
            return repeat(str, repeat);
        } else {
            // given that repeat(String, int) is quite optimized, better to rely on it than try and splice this into it
            String result = repeat(str + separator, repeat);
            return removeEnd(result, separator);
        }
    }

    /**
     * <p>
     * Returns padding using the specified delimiter repeated to a given length.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.repeat('e', 0) = ""
     * StringUtils.repeat('e', 3) = "eee"
     * StringUtils.repeat('e',-2) = ""
     * </pre>
     * <p/>
     * <p>
     * Note: this method doesn't not support padding with <a
     * href="http://www.unicode.org/glossary/#supplementary_character">Unicode
     * Supplementary Characters</a> as they require a pair of {@code char}s to
     * be represented. If you are needing to support full I18N of your
     * applications consider using {@link #repeat(String, int)} instead.
     * </p>
     *
     * @param ch character to repeat
     * @param repeat number of times to repeat char, negative treated as zero
     * @return String with repeated character
     * @see #repeat(String, int)
     */
    public static String repeat(char ch, int repeat) {
        if (repeat <= 0) {
            return "";
        }

        char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; i--) {
            buf[i] = ch;
        }
        return new String(buf);
    }

    // Padding
    // -----------------------------------------------------------------------

    /**
     * <p>Returns padding using the specified delimiter repeated
     * to a given length.</p>
     * <p/>
     * <pre>
     * StringUtils.padding(0, 'e') = ""
     * StringUtils.padding(3, 'e') = "eee"
     * StringUtils.padding(-2, 'e') = IndexOutOfBoundsException
     * </pre>
     * <p/>
     * <p>Note: this method doesn't not support padding with
     * <a href="http://www.unicode.org/glossary/#supplementary_character">Unicode Supplementary Characters</a>
     * as they require a pair of <code>char</code>s to be represented.
     * If you are needing to support full I18N of your applications
     * consider using {@link #repeat(String, int)} instead.
     * </p>
     *
     * @param repeat number of times to repeat delim
     * @param padChar character to repeat
     * @return String with repeated character
     * @throws IndexOutOfBoundsException if <code>repeat &lt; 0</code>
     * @see #repeat(String, int)
     */
    private static String padding(int repeat, char padChar) throws IndexOutOfBoundsException {
        if (repeat < 0) {
            throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
        }
        final char[] buf = new char[repeat];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = padChar;
        }
        return new String(buf);
    }

    /**
     * <p>
     * Right pad a String with spaces (' ').
     * </p>
     * <p/>
     * <p>
     * The String is padded to the size of {@code size}.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.padRight(null, *) = null
     * StringUtils.padRight("", 3) = " "
     * StringUtils.padRight("bat", 3) = "bat"
     * StringUtils.padRight("bat", 5) = "bat "
     * StringUtils.padRight("bat", 1) = "bat"
     * StringUtils.padRight("bat", -1) = "bat"
     * </pre>
     *
     * @param str the String to pad out, may be null
     * @param size the size to pad to
     * @return right padded String or original String if no padding is
     * necessary, {@code null} if null String input
     */
    public static String padRight(String str, int size) {
        return padRight(str, size, ' ');
    }

    /**
     * <p>
     * Right pad a String with a specified character.
     * </p>
     * <p/>
     * <p>
     * The String is padded to the size of {@code size}.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.padRight(null, *, *) = null
     * StringUtils.padRight("", 3, 'z') = "zzz"
     * StringUtils.padRight("bat", 3, 'z') = "bat"
     * StringUtils.padRight("bat", 5, 'z') = "batzz"
     * StringUtils.padRight("bat", 1, 'z') = "bat"
     * StringUtils.padRight("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str the String to pad out, may be null
     * @param size the size to pad to
     * @param padChar the character to pad with
     * @return right padded String or original String if no padding is
     * necessary, {@code null} if null String input
     * @since 2.0
     */
    public static String padRight(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return padRight(str, size, String.valueOf(padChar));
        }
        return str.concat(repeat(padChar, pads));
    }

    /**
     * <p>
     * Right pad a String with a specified String.
     * </p>
     * <p/>
     * <p>
     * The String is padded to the size of {@code size}.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.padRight(null, *, *) = null
     * StringUtils.padRight("", 3, "z") = "zzz"
     * StringUtils.padRight("bat", 3, "yz") = "bat"
     * StringUtils.padRight("bat", 5, "yz") = "batyz"
     * StringUtils.padRight("bat", 8, "yz") = "batyzyzy"
     * StringUtils.padRight("bat", 1, "yz") = "bat"
     * StringUtils.padRight("bat", -1, "yz") = "bat"
     * StringUtils.padRight("bat", 5, null) = "bat "
     * StringUtils.padRight("bat", 5, "") = "bat "
     * </pre>
     *
     * @param str the String to pad out, may be null
     * @param size the size to pad to
     * @param padStr the String to pad with, null or empty treated as single space
     * @return right padded String or original String if no padding is
     * necessary, {@code null} if null String input
     */
    public static String padRight(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return padRight(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return str.concat(new String(padding));
        }
    }

    /**
     * <p>
     * Left pad a String with spaces (' ').
     * </p>
     * <p/>
     * <p>
     * The String is padded to the size of {@code size}.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.padLeft(null, *) = null
     * StringUtils.padLeft("", 3) = " "
     * StringUtils.padLeft("bat", 3) = "bat"
     * StringUtils.padLeft("bat", 5) = " bat"
     * StringUtils.padLeft("bat", 1) = "bat"
     * StringUtils.padLeft("bat", -1) = "bat"
     * </pre>
     *
     * @param str the String to pad out, may be null
     * @param size the size to pad to
     * @return left padded String or original String if no padding is necessary,
     * {@code null} if null String input
     */
    public static String padLeft(String str, int size) {
        return padLeft(str, size, ' ');
    }

    /**
     * <p>
     * Left pad a String with a specified character.
     * </p>
     * <p/>
     * <p>
     * Pad to a size of {@code size}.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.padLeft(null, *, *) = null
     * StringUtils.padLeft("", 3, 'z') = "zzz"
     * StringUtils.padLeft("bat", 3, 'z') = "bat"
     * StringUtils.padLeft("bat", 5, 'z') = "zzbat"
     * StringUtils.padLeft("bat", 1, 'z') = "bat"
     * StringUtils.padLeft("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str the String to pad out, may be null
     * @param size the size to pad to
     * @param padChar the character to pad with
     * @return left padded String or original String if no padding is necessary,
     * {@code null} if null String input
     * @since 2.0
     */
    public static String padLeft(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return padLeft(str, size, String.valueOf(padChar));
        }
        return repeat(padChar, pads).concat(str);
    }

    /**
     * <p>
     * Left pad a String with a specified String.
     * </p>
     * <p/>
     * <p>
     * Pad to a size of {@code size}.
     * </p>
     * <p/>
     * <pre>
     * StringUtils.padLeft(null, *, *) = null
     * StringUtils.padLeft("", 3, "z") = "zzz"
     * StringUtils.padLeft("bat", 3, "yz") = "bat"
     * StringUtils.padLeft("bat", 5, "yz") = "yzbat"
     * StringUtils.padLeft("bat", 8, "yz") = "yzyzybat"
     * StringUtils.padLeft("bat", 1, "yz") = "bat"
     * StringUtils.padLeft("bat", -1, "yz") = "bat"
     * StringUtils.padLeft("bat", 5, null) = " bat"
     * StringUtils.padLeft("bat", 5, "") = " bat"
     * </pre>
     *
     * @param str the String to pad out, may be null
     * @param size the size to pad to
     * @param padStr the String to pad with, null or empty treated as single space
     * @return left padded String or original String if no padding is necessary,
     * {@code null} if null String input
     */
    public static String padLeft(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return padLeft(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }


    // Remove
    //-----------------------------------------------------------------------

    /**
     * <p>Removes a substring only if it is at the begining of a source string,
     * otherwise returns the source string.</p>
     * <p/>
     * <p>A <code>null</code> source string will return <code>null</code>.
     * An empty ("") source string will return the empty string.
     * A <code>null</code> search string will return the source string.</p>
     * <p/>
     * <pre>
     * StringUtils.removeStart(null, *) = null
     * StringUtils.removeStart("", *) = ""
     * StringUtils.removeStart(*, null) = *
     * StringUtils.removeStart("www.domain.com", "www.") = "domain.com"
     * StringUtils.removeStart("domain.com", "www.") = "domain.com"
     * StringUtils.removeStart("www.domain.com", "domain") = "www.domain.com"
     * StringUtils.removeStart("abc", "") = "abc"
     * </pre>
     *
     * @param str the source String to search, may be null
     * @param remove the String to search for and remove, may be null
     * @return the substring with the string removed if found,
     * <code>null</code> if null String input
     * @since 2.1
     */
    public static String removeStart(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.startsWith(remove)) {
            return str.substring(remove.length());
        }
        return str;
    }

    /**
     * <p>Case insensitive removal of a substring if it is at the begining of a source string,
     * otherwise returns the source string.</p>
     * <p/>
     * <p>A <code>null</code> source string will return <code>null</code>.
     * An empty ("") source string will return the empty string.
     * A <code>null</code> search string will return the source string.</p>
     * <p/>
     * <pre>
     * StringUtils.removeStartIgnoreCase(null, *) = null
     * StringUtils.removeStartIgnoreCase("", *) = ""
     * StringUtils.removeStartIgnoreCase(*, null) = *
     * StringUtils.removeStartIgnoreCase("www.domain.com", "www.") = "domain.com"
     * StringUtils.removeStartIgnoreCase("www.domain.com", "WWW.") = "domain.com"
     * StringUtils.removeStartIgnoreCase("domain.com", "www.") = "domain.com"
     * StringUtils.removeStartIgnoreCase("www.domain.com", "domain") = "www.domain.com"
     * StringUtils.removeStartIgnoreCase("abc", "") = "abc"
     * </pre>
     *
     * @param str the source String to search, may be null
     * @param remove the String to search for (case insensitive) and remove, may be null
     * @return the substring with the string removed if found,
     * <code>null</code> if null String input
     * @since 2.4
     */
    public static String removeStartIgnoreCase(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (startsWithIgnoreCase(str, remove)) {
            return str.substring(remove.length());
        }
        return str;
    }

    /**
     * <p>Removes a substring only if it is at the end of a source string,
     * otherwise returns the source string.</p>
     * <p/>
     * <p>A <code>null</code> source string will return <code>null</code>.
     * An empty ("") source string will return the empty string.
     * A <code>null</code> search string will return the source string.</p>
     * <p/>
     * <pre>
     * StringUtils.removeEnd(null, *) = null
     * StringUtils.removeEnd("", *) = ""
     * StringUtils.removeEnd(*, null) = *
     * StringUtils.removeEnd("www.domain.com", ".com.") = "www.domain.com"
     * StringUtils.removeEnd("www.domain.com", ".com") = "www.domain"
     * StringUtils.removeEnd("www.domain.com", "domain") = "www.domain.com"
     * StringUtils.removeEnd("abc", "") = "abc"
     * </pre>
     *
     * @param str the source String to search, may be null
     * @param remove the String to search for and remove, may be null
     * @return the substring with the string removed if found,
     * <code>null</code> if null String input
     * @since 2.1
     */
    public static String removeEnd(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    /**
     * <p>Case insensitive removal of a substring if it is at the end of a source string,
     * otherwise returns the source string.</p>
     * <p/>
     * <p>A <code>null</code> source string will return <code>null</code>.
     * An empty ("") source string will return the empty string.
     * A <code>null</code> search string will return the source string.</p>
     * <p/>
     * <pre>
     * StringUtils.removeEndIgnoreCase(null, *) = null
     * StringUtils.removeEndIgnoreCase("", *) = ""
     * StringUtils.removeEndIgnoreCase(*, null) = *
     * StringUtils.removeEndIgnoreCase("www.domain.com", ".com.") = "www.domain.com"
     * StringUtils.removeEndIgnoreCase("www.domain.com", ".com") = "www.domain"
     * StringUtils.removeEndIgnoreCase("www.domain.com", "domain") = "www.domain.com"
     * StringUtils.removeEndIgnoreCase("abc", "") = "abc"
     * StringUtils.removeEndIgnoreCase("www.domain.com", ".COM") = "www.domain"
     * StringUtils.removeEndIgnoreCase("www.domain.COM", ".com") = "www.domain"
     * </pre>
     *
     * @param str the source String to search, may be null
     * @param remove the String to search for (case insensitive) and remove, may be null
     * @return the substring with the string removed if found,
     * <code>null</code> if null String input
     * @since 2.4
     */
    public static String removeEndIgnoreCase(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (endsWithIgnoreCase(str, remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    /**
     * Removes similar characters from the beginning of the string.
     * <p/>
     * <pre>
     * StringUtils.removeFirstN("0000012345", '0') = "1235"
     * </pre>
     *
     * @param str Original string
     * @param ch Character to remove
     * @return The string that remains after removing all occurrences of ch parameter
     * from the start of the initial string.
     */
    public static String removeFirstN(String str, char ch) {
        int start = 0;
        while ((start < str.length()) && (str.charAt(start) == ch)) {
            start++;
        }

        if (start < str.length()) {
            return str.substring(start);
        } else {
            return "";
        }
    }

    // startsWith
    //-----------------------------------------------------------------------

    /**
     * <p>Check if a String starts with a specified prefix.</p>
     * <p/>
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case sensitive.</p>
     * <p/>
     * <pre>
     * StringUtils.startsWith(null, null) = true
     * StringUtils.startsWith(null, "abc") = false
     * StringUtils.startsWith("abcdef", null) = false
     * StringUtils.startsWith("abcdef", "abc") = true
     * StringUtils.startsWith("ABCDEF", "abc") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @param prefix the prefix to find, may be null
     * @return <code>true</code> if the String starts with the prefix, case sensitive, or
     * both <code>null</code>
     * @see java.lang.String#startsWith(String)
     * @since 2.4
     */
    public static boolean startsWith(String str, String prefix) {
        return startsWith(str, prefix, false);
    }

    /**
     * <p>Case insensitive check if a String starts with a specified prefix.</p>
     * <p/>
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case insensitive.</p>
     * <p/>
     * <pre>
     * StringUtils.startsWithIgnoreCase(null, null) = true
     * StringUtils.startsWithIgnoreCase(null, "abc") = false
     * StringUtils.startsWithIgnoreCase("abcdef", null) = false
     * StringUtils.startsWithIgnoreCase("abcdef", "abc") = true
     * StringUtils.startsWithIgnoreCase("ABCDEF", "abc") = true
     * </pre>
     *
     * @param str the String to check, may be null
     * @param prefix the prefix to find, may be null
     * @return <code>true</code> if the String starts with the prefix, case insensitive, or
     * both <code>null</code>
     * @see java.lang.String#startsWith(String)
     * @since 2.4
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return startsWith(str, prefix, true);
    }

    /**
     * <p>Check if a String starts with a specified prefix (optionally case insensitive).</p>
     *
     * @param str the String to check, may be null
     * @param prefix the prefix to find, may be null
     * @param ignoreCase inidicates whether the compare should ignore case
     * (case insensitive) or not.
     * @return <code>true</code> if the String starts with the prefix or
     * both <code>null</code>
     * @see java.lang.String#startsWith(String)
     */
    private static boolean startsWith(String str, String prefix, boolean ignoreCase) {
        if (str == null || prefix == null) {
            return (str == null && prefix == null);
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return str.regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
    }

    //endsWith
    //-----------------------------------------------------------------------

    /**
     * <p>Check if a String ends with a specified suffix.</p>
     * <p/>
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case sensitive.</p>
     * <p/>
     * <pre>
     * StringUtils.endsWith(null, null) = true
     * StringUtils.endsWith(null, "def") = false
     * StringUtils.endsWith("abcdef", null) = false
     * StringUtils.endsWith("abcdef", "def") = true
     * StringUtils.endsWith("ABCDEF", "def") = false
     * StringUtils.endsWith("ABCDEF", "cde") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @param suffix the suffix to find, may be null
     * @return <code>true</code> if the String ends with the suffix, case sensitive, or
     * both <code>null</code>
     * @see java.lang.String#endsWith(String)
     * @since 2.4
     */
    public static boolean endsWith(String str, String suffix) {
        return endsWith(str, suffix, false);
    }

    /**
     * <p>Case insensitive check if a String ends with a specified suffix.</p>
     * <p/>
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case insensitive.</p>
     * <p/>
     * <pre>
     * StringUtils.endsWithIgnoreCase(null, null) = true
     * StringUtils.endsWithIgnoreCase(null, "def") = false
     * StringUtils.endsWithIgnoreCase("abcdef", null) = false
     * StringUtils.endsWithIgnoreCase("abcdef", "def") = true
     * StringUtils.endsWithIgnoreCase("ABCDEF", "def") = true
     * StringUtils.endsWithIgnoreCase("ABCDEF", "cde") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @param suffix the suffix to find, may be null
     * @return <code>true</code> if the String ends with the suffix, case insensitive, or
     * both <code>null</code>
     * @see java.lang.String#endsWith(String)
     * @since 2.4
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        return endsWith(str, suffix, true);
    }

    /**
     * <p>Check if a String ends with a specified suffix (optionally case insensitive).</p>
     *
     * @param str the String to check, may be null
     * @param suffix the suffix to find, may be null
     * @param ignoreCase inidicates whether the compare should ignore case
     * (case insensitive) or not.
     * @return <code>true</code> if the String starts with the prefix or
     * both <code>null</code>
     * @see java.lang.String#endsWith(String)
     */
    private static boolean endsWith(String str, String suffix, boolean ignoreCase) {
        if (str == null || suffix == null) {
            return (str == null && suffix == null);
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        int strOffset = str.length() - suffix.length();
        return str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
    }
}