/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.helger.jcodemodel;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

/**
 * Utility methods that convert arbitrary strings into Java identifiers.
 */
public class JJavaName
{
  private static class Entry
  {
    private final Pattern pattern;
    private final String replacement;

    public Entry (final String pattern, final String replacement)
    {
      this.pattern = Pattern.compile (pattern, Pattern.CASE_INSENSITIVE);
      this.replacement = replacement;
    }

    String apply (final String word)
    {
      final Matcher m = pattern.matcher (word);
      if (m.matches ())
      {
        final StringBuffer buf = new StringBuffer ();
        m.appendReplacement (buf, replacement);
        return buf.toString ();
      }
      else
      {
        return null;
      }
    }
  }

  private static final Entry [] TABLE;

  static
  {
    final String [] source = { "(.*)child",
                              "$1children",
                              "(.+)fe",
                              "$1ves",
                              "(.*)mouse",
                              "$1mise",
                              "(.+)f",
                              "$1ves",
                              "(.+)ch",
                              "$1ches",
                              "(.+)sh",
                              "$1shes",
                              "(.*)tooth",
                              "$1teeth",
                              "(.+)um",
                              "$1a",
                              "(.+)an",
                              "$1en",
                              "(.+)ato",
                              "$1atoes",
                              "(.*)basis",
                              "$1bases",
                              "(.*)axis",
                              "$1axes",
                              "(.+)is",
                              "$1ises",
                              "(.+)ss",
                              "$1sses",
                              "(.+)us",
                              "$1uses",
                              "(.+)s",
                              "$1s",
                              "(.*)foot",
                              "$1feet",
                              "(.+)ix",
                              "$1ixes",
                              "(.+)ex",
                              "$1ices",
                              "(.+)nx",
                              "$1nxes",
                              "(.+)x",
                              "$1xes",
                              "(.+)y",
                              "$1ies",
                              "(.+)",
                              "$1s", };

    TABLE = new Entry [source.length / 2];

    for (int i = 0; i < source.length; i += 2)
    {
      TABLE[i / 2] = new Entry (source[i], source[i + 1]);
    }
  }

  /** All reserved keywords of Java. */
  private static HashSet <String> reservedKeywords = new HashSet <String> ();

  static
  {
    // see
    // http://java.sun.com/docs/books/tutorial/java/nutsandbolts/_keywords.html
    final String [] words = new String [] { "abstract",
                                           "boolean",
                                           "break",
                                           "byte",
                                           "case",
                                           "catch",
                                           "char",
                                           "class",
                                           "const",
                                           "continue",
                                           "default",
                                           "do",
                                           "double",
                                           "else",
                                           "extends",
                                           "final",
                                           "finally",
                                           "float",
                                           "for",
                                           "goto",
                                           "if",
                                           "implements",
                                           "import",
                                           "instanceof",
                                           "int",
                                           "interface",
                                           "long",
                                           "native",
                                           "new",
                                           "package",
                                           "private",
                                           "protected",
                                           "public",
                                           "return",
                                           "short",
                                           "static",
                                           "strictfp",
                                           "super",
                                           "switch",
                                           "synchronized",
                                           "this",
                                           "throw",
                                           "throws",
                                           "transient",
                                           "try",
                                           "void",
                                           "volatile",
                                           "while",

                                           // technically these are not reserved
                                           // words but they cannot be used as
                                           // identifiers.
                                           "true",
                                           "false",
                                           "null",

                                           // and I believe assert is also a new
                                           // keyword
                                           "assert",

                                           // and 5.0 keywords
                                           "enum" };
    for (final String w : words)
      reservedKeywords.add (w);
  }

  /**
   * Checks if a given string is usable as a Java identifier.
   */
  public static boolean isJavaIdentifier (@Nonnull final String s)
  {
    if (s.length () == 0)
      return false;
    if (reservedKeywords.contains (s))
      return false;

    if (!Character.isJavaIdentifierStart (s.charAt (0)))
      return false;

    for (int i = 1; i < s.length (); i++)
      if (!Character.isJavaIdentifierPart (s.charAt (i)))
        return false;

    return true;
  }

  /**
   * Checks if the given string is a valid fully qualified name.
   */
  public static boolean isFullyQualifiedClassName (final String s)
  {
    return isJavaPackageName (s);
  }

  /**
   * Checks if the given string is a valid Java package name.
   */
  public static boolean isJavaPackageName (final String sName)
  {
    String s = sName;
    while (s.length () != 0)
    {
      int idx = s.indexOf ('.');
      if (idx == -1)
        idx = s.length ();
      if (!isJavaIdentifier (s.substring (0, idx)))
        return false;

      s = s.substring (idx);
      if (s.length () != 0)
        s = s.substring (1); // remove '.'
    }
    return true;
  }

  /**
   * <b>Experimental API:</b> converts an English word into a plural form.
   * 
   * @param word
   *        a word, such as "child", "apple". Must not be null. It accepts word
   *        concatanation forms that are common in programming languages, such
   *        as "my_child", "MyChild", "myChild", "MY-CHILD", "CODE003-child",
   *        etc, and mostly tries to do the right thing.
   *        ("my_children","MyChildren","myChildren", and "MY-CHILDREN",
   *        "CODE003-children" respectively)
   *        <p>
   *        Although this method only works for English words, it handles
   *        non-English words gracefully (by just returning it as-is.) For
   *        example, &#x65E5;&#x672C;&#x8A9E; will be returned as-is without
   *        modified, not "&#x65E5;&#x672C;&#x8A9E;s"
   *        <p>
   *        This method doesn't handle suffixes very well. For example, passing
   *        "person56" will return "person56s", not "people56".
   * @return always non-null.
   */
  public static String getPluralForm (final String word)
  {
    // remember the casing of the word
    boolean allUpper = true;

    // check if the word looks like an English word.
    // if we see non-ASCII characters, abort
    for (int i = 0; i < word.length (); i++)
    {
      final char ch = word.charAt (i);
      if (ch >= 0x80)
        return word;

      // note that this isn't the same as allUpper &= Character.isUpperCase(ch);
      allUpper &= !Character.isLowerCase (ch);
    }

    for (final Entry e : TABLE)
    {
      String r = e.apply (word);
      if (r != null)
      {
        if (allUpper)
          r = r.toUpperCase ();
        return r;
      }
    }

    // failed
    return word;
  }
}
