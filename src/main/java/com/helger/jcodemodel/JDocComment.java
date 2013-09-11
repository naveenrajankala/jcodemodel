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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * JavaDoc comment.
 * <p>
 * A javadoc comment consists of multiple parts. There's the main part (that
 * comes the first in in the comment section), then the parameter parts
 * (@param), the return part (@return), and the throws parts (@throws). TODO: it
 * would be nice if we have JComment class and we can derive this class from
 * there.
 */
public class JDocComment extends JCommentPart implements JGenerable
{
  private static final String INDENT = " *     ";

  private static final long serialVersionUID = 1L;

  private final JCodeModel owner;

  /** list of @param tags */
  private final Map <String, JCommentPart> atParams = new LinkedHashMap <String, JCommentPart> ();

  /** list of xdoclets */
  private final Map <String, Map <String, String>> atXdoclets = new LinkedHashMap <String, Map <String, String>> ();

  /** list of @throws tags */
  private final Map <AbstractJClass, JCommentPart> atThrows = new LinkedHashMap <AbstractJClass, JCommentPart> ();

  /**
   * The @return tag part.
   */
  private JCommentPart atReturn = null;

  /**
   * The @author tag part.
   */
  private JCommentPart atAuthor = null;

  /** The @deprecated tag */
  private JCommentPart atDeprecated = null;

  protected JDocComment (@Nonnull final JCodeModel owner)
  {
    this.owner = owner;
  }

  @Nonnull
  public JCodeModel owner ()
  {
    return owner;
  }

  @Override
  public JDocComment append (final Object o)
  {
    add (o);
    return this;
  }

  /**
   * Append a text to a @param tag to the javadoc
   */
  public JCommentPart addParam (final String param)
  {
    JCommentPart p = atParams.get (param);
    if (p == null)
      atParams.put (param, p = new JCommentPart ());
    return p;
  }

  /**
   * Append a text to an @param tag.
   */
  public JCommentPart addParam (@Nonnull final JVar param)
  {
    return addParam (param.name ());
  }

  @Nullable
  public JCommentPart removeParam (final String param)
  {
    return atParams.remove (param);
  }

  @Nullable
  public JCommentPart removeParam (@Nonnull final JVar param)
  {
    return removeParam (param.name ());
  }

  public void removeAllParams ()
  {
    atParams.clear ();
  }

  @Nullable
  public JCommentPart getParam (@Nullable final String param)
  {
    return atParams.get (param);
  }

  @Nullable
  public JCommentPart getParam (@Nonnull final JVar param)
  {
    return getParam (param.name ());
  }

  /**
   * add an @throws tag to the javadoc
   */
  public JCommentPart addThrows (@Nonnull final Class <? extends Throwable> exception)
  {
    return addThrows (owner.ref (exception));
  }

  /**
   * add an @throws tag to the javadoc
   */
  public JCommentPart addThrows (final AbstractJClass exception)
  {
    JCommentPart p = atThrows.get (exception);
    if (p == null)
    {
      p = new JCommentPart ();
      atThrows.put (exception, p);
    }
    return p;
  }

  @Nullable
  public JCommentPart removeThrows (@Nonnull final Class <? extends Throwable> exception)
  {
    return removeThrows (owner.ref (exception));
  }

  @Nullable
  public JCommentPart removeThrows (final AbstractJClass exception)
  {
    return atThrows.remove (exception);
  }

  public void removeAllThrows ()
  {
    atThrows.clear ();
  }

  @Nullable
  public JCommentPart getThrows (@Nonnull final Class <? extends Throwable> exception)
  {
    return getThrows (owner.ref (exception));
  }

  @Nullable
  public JCommentPart getThrows (final AbstractJClass exception)
  {
    return atThrows.get (exception);
  }

  /**
   * Appends a text to @return tag.
   */
  @Nonnull
  public JCommentPart addReturn ()
  {
    if (atReturn == null)
      atReturn = new JCommentPart ();
    return atReturn;
  }

  public void removeReturn ()
  {
    atReturn = null;
  }

  /**
   * Appends a text to @author tag.
   */
  @Nonnull
  public JCommentPart addAuthor ()
  {
    if (atAuthor == null)
      atAuthor = new JCommentPart ();
    return atAuthor;
  }

  public void removeAuthor ()
  {
    atAuthor = null;
  }

  /**
   * add an @deprecated tag to the javadoc, with the associated message.
   */
  @Nonnull
  public JCommentPart addDeprecated ()
  {
    if (atDeprecated == null)
      atDeprecated = new JCommentPart ();
    return atDeprecated;
  }

  public void removeDeprecated ()
  {
    atDeprecated = null;
  }

  /**
   * add an xdoclet.
   */
  @Nonnull
  public Map <String, String> addXdoclet (final String name)
  {
    Map <String, String> p = atXdoclets.get (name);
    if (p == null)
    {
      p = new LinkedHashMap <String, String> ();
      atXdoclets.put (name, p);
    }
    return p;
  }

  /**
   * add an xdoclet.
   */
  @Nonnull
  public Map <String, String> addXdoclet (final String name, final Map <String, String> attributes)
  {
    final Map <String, String> p = addXdoclet (name);
    p.putAll (attributes);
    return p;
  }

  /**
   * add an xdoclet.
   */
  @Nonnull
  public Map <String, String> addXdoclet (final String name, final String attribute, final String value)
  {
    final Map <String, String> p = addXdoclet (name);
    p.put (attribute, value);
    return p;
  }

  @Nullable
  public Map <String, String> removeXdoclet (final String name)
  {
    return atXdoclets.remove (name);
  }

  public void removeAllXdoclets ()
  {
    atXdoclets.clear ();
  }

  public void generate (@Nonnull final JFormatter f)
  {
    // I realized that we can't use StringTokenizer because
    // this will recognize multiple \n as one token.

    f.print ("/**").newline ();

    format (f, " * ");

    f.print (" * ").newline ();
    for (final Map.Entry <String, JCommentPart> e : atParams.entrySet ())
    {
      f.print (" * @param ").print (e.getKey ()).newline ();
      e.getValue ().format (f, INDENT);
    }
    if (atReturn != null)
    {
      f.print (" * @return").newline ();
      atReturn.format (f, INDENT);
    }
    if (atAuthor != null)
    {
      f.print (" * @author").newline ();
      atAuthor.format (f, INDENT);
    }
    for (final Map.Entry <AbstractJClass, JCommentPart> e : atThrows.entrySet ())
    {
      f.print (" * @throws ").type (e.getKey ()).newline ();
      e.getValue ().format (f, INDENT);
    }
    if (atDeprecated != null)
    {
      f.print (" * @deprecated").newline ();
      atDeprecated.format (f, INDENT);
    }
    for (final Map.Entry <String, Map <String, String>> e : atXdoclets.entrySet ())
    {
      f.print (" * @").print (e.getKey ());
      if (e.getValue () != null)
      {
        for (final Map.Entry <String, String> a : e.getValue ().entrySet ())
        {
          f.print (" ").print (a.getKey ()).print ("= \"").print (a.getValue ()).print ("\"");
        }
      }
      f.newline ();
    }
    f.print (" */").newline ();
  }
}
