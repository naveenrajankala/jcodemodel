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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;

import com.helger.jcodemodel.util.ClassNameComparator;

/**
 * Java method.
 */
public class JMethod extends AbstractJGenerifiableImpl implements JAnnotatable, JDocCommentable
{

  /**
   * Modifiers for this method
   */
  private final JMods mods;

  /**
   * Return type for this method
   */
  private AbstractJType type;

  /**
   * Name of this method
   */
  private String name;

  /**
   * List of parameters for this method's declaration
   */
  private final List <JVar> params = new ArrayList <JVar> ();

  /**
   * Set of exceptions that this method may throw. A set instance lazily
   * created.
   */
  private Set <AbstractJClass> _throws;

  /**
   * JBlock of statements that makes up the body this method
   */
  private JBlock body;

  private final JDefinedClass outer;

  /**
   * javadoc comments for this JMethod
   */
  private JDocComment jdoc;

  /**
   * Variable parameter for this method's varargs declaration introduced in J2SE
   * 1.5
   */
  private JVar varParam;

  /**
   * Annotations on this variable. Lazily created.
   */
  private List <JAnnotationUse> annotations;
  /**
   * To set the default value for the annotation member
   */
  private JExpression defaultValue;

  /**
   * JMethod constructor
   * 
   * @param mods
   *        Modifiers for this method's declaration
   * @param type
   *        Return type for the method
   * @param name
   *        Name of this method
   */
  public JMethod (@Nonnull final JDefinedClass outer, final int mods, final AbstractJType type, final String name)
  {
    this.mods = JMods.forMethod (mods);
    this.type = type;
    this.name = name;
    this.outer = outer;
  }

  /**
   * Constructor constructor
   * 
   * @param mods
   *        Modifiers for this constructor's declaration
   * @param _class
   *        JClass containing this constructor
   */
  public JMethod (final int mods, final JDefinedClass _class)
  {
    this.mods = JMods.forMethod (mods);
    this.type = null;
    this.name = _class.name ();
    this.outer = _class;
  }

  public boolean isConstructor ()
  {
    return type == null;
  }

  private Set <AbstractJClass> _getThrows ()
  {
    if (_throws == null)
      _throws = new TreeSet <AbstractJClass> (ClassNameComparator.theInstance);
    return _throws;
  }

  /**
   * Add an exception to the list of exceptions that this method may throw.
   * 
   * @param exception
   *        Name of an exception that this method may throw
   */
  public JMethod _throws (final AbstractJClass exception)
  {
    _getThrows ().add (exception);
    return this;
  }

  public JMethod _throws (final Class <? extends Throwable> exception)
  {
    return _throws (outer.owner ().ref (exception));
  }

  /**
   * Returns the list of variable of this method.
   * 
   * @return List of parameters of this method. This list is not modifiable.
   */
  public List <JVar> params ()
  {
    return Collections.<JVar> unmodifiableList (params);
  }

  public JVar paramAtIndex (final int index)
  {
    return params.get (index);
  }

  /**
   * Add the specified variable to the list of parameters for this method
   * signature.
   * 
   * @param type
   *        JType of the parameter being added
   * @param name
   *        Name of the parameter being added
   * @return New parameter variable
   */
  @Nonnull
  public JVar param (final int mods, final AbstractJType type, final String name)
  {
    final JVar v = new JVar (JMods.forVar (mods), type, name, null);
    params.add (v);
    return v;
  }

  public JVar param (final AbstractJType type, final String name)
  {
    return param (JMod.NONE, type, name);
  }

  public JVar param (final int mods, final Class <?> type, final String name)
  {
    return param (mods, outer.owner ()._ref (type), name);
  }

  public JVar param (final Class <?> type, final String name)
  {
    return param (outer.owner ()._ref (type), name);
  }

  /**
   * @see #varParam(AbstractJType, String)
   */
  public JVar varParam (final Class <?> type, final String name)
  {
    return varParam (outer.owner ()._ref (type), name);
  }

  /**
   * Add the specified variable argument to the list of parameters for this
   * method signature.
   * 
   * @param type
   *        Type of the parameter being added.
   * @param name
   *        Name of the parameter being added
   * @return the variable parameter
   * @throws IllegalStateException
   *         If this method is called twice. varargs in J2SE 1.5 can appear only
   *         once in the method signature.
   */
  public JVar varParam (final AbstractJType type, final String name)
  {
    return varParam (JMod.NONE, type, name);
  }

  /**
   * @see #varParam(int, AbstractJType, String)
   */
  public JVar varParam (final int mods, final Class <?> type, final String name)
  {
    return varParam (mods, outer.owner ()._ref (type), name);
  }

  /**
   * Add the specified variable argument to the list of parameters for this
   * method signature.
   * 
   * @param mods
   *        mods to use
   * @param type
   *        Type of the parameter being added.
   * @param name
   *        Name of the parameter being added
   * @return the variable parameter
   * @throws IllegalStateException
   *         If this method is called twice. varargs in J2SE 1.5 can appear only
   *         once in the method signature.
   */
  public JVar varParam (final int mods, final AbstractJType type, final String name)
  {
    if (hasVarArgs ())
      throw new IllegalStateException ("Cannot have two varargs in a method,\n"
                                       + "Check if varParam method of JMethod is"
                                       + " invoked more than once");

    varParam = new JVar (JMods.forVar (mods), type.array (), name, null);
    return varParam;
  }

  /**
   * Adds an annotation to this variable.
   * 
   * @param clazz
   *        The annotation class to annotate the field with
   */
  public JAnnotationUse annotate (final AbstractJClass clazz)
  {
    if (annotations == null)
      annotations = new ArrayList <JAnnotationUse> ();
    final JAnnotationUse a = new JAnnotationUse (clazz);
    annotations.add (a);
    return a;
  }

  /**
   * Adds an annotation to this variable.
   * 
   * @param clazz
   *        The annotation class to annotate the field with
   */
  public JAnnotationUse annotate (final Class <? extends Annotation> clazz)
  {
    return annotate (owner ().ref (clazz));
  }

  public <W extends JAnnotationWriter <?>> W annotate2 (final Class <W> clazz)
  {
    return TypedAnnotationWriter.create (clazz, this);
  }

  public Collection <JAnnotationUse> annotations ()
  {
    if (annotations == null)
      annotations = new ArrayList <JAnnotationUse> ();
    return Collections.unmodifiableList (annotations);
  }

  /**
   * Check if there are any varargs declared for this method signature.
   */
  public boolean hasVarArgs ()
  {
    return this.varParam != null;
  }

  public String name ()
  {
    return name;
  }

  /**
   * Changes the name of the method.
   */
  public void name (final String n)
  {
    this.name = n;
  }

  /**
   * Returns the return type.
   */
  public AbstractJType type ()
  {
    return type;
  }

  /**
   * Overrides the return type.
   */
  public void type (final AbstractJType t)
  {
    this.type = t;
  }

  /**
   * Returns all the parameter types in an array.
   * 
   * @return If there's no parameter, an empty array will be returned.
   */
  public AbstractJType [] listParamTypes ()
  {
    final AbstractJType [] r = new AbstractJType [params.size ()];
    for (int i = 0; i < r.length; i++)
      r[i] = params.get (i).type ();
    return r;
  }

  /**
   * Returns the varags parameter type.
   * 
   * @return If there's no vararg parameter type, null will be returned.
   */
  public AbstractJType listVarParamType ()
  {
    if (varParam != null)
      return varParam.type ();
    else
      return null;
  }

  /**
   * Returns all the parameters in an array.
   * 
   * @return If there's no parameter, an empty array will be returned.
   */
  public JVar [] listParams ()
  {
    return params.toArray (new JVar [params.size ()]);
  }

  /**
   * Returns the variable parameter
   * 
   * @return If there's no parameter, null will be returned.
   */
  public JVar listVarParam ()
  {
    return varParam;
  }

  /**
   * Returns true if the method has the specified signature.
   */
  public boolean hasSignature (final AbstractJType [] argTypes)
  {
    final JVar [] p = listParams ();
    if (p.length != argTypes.length)
      return false;

    for (int i = 0; i < p.length; i++)
      if (!p[i].type ().equals (argTypes[i]))
        return false;

    return true;
  }

  /**
   * Get the block that makes up body of this method
   * 
   * @return Body of method
   */
  public JBlock body ()
  {
    if (body == null)
      body = new JBlock ();
    return body;
  }

  /**
   * Specify the default value for this annotation member
   * 
   * @param value
   *        Default value for the annotation member
   */
  public void declareDefaultValue (final JExpression value)
  {
    this.defaultValue = value;
  }

  /**
   * Creates, if necessary, and returns the class javadoc for this JDefinedClass
   * 
   * @return JDocComment containing javadocs for this class
   */
  public JDocComment javadoc ()
  {
    if (jdoc == null)
      jdoc = new JDocComment (owner ());
    return jdoc;
  }

  @Override
  public void declare (final JFormatter f)
  {
    if (jdoc != null)
      f.generable (jdoc);

    if (annotations != null)
    {
      for (final JAnnotationUse a : annotations)
        f.generable (a).newline ();
    }

    f.generable (mods);

    // declare the generics parameters
    super.declare (f);

    if (!isConstructor ())
      f.generable (type);
    f.id (name).print ('(').indent ();
    // when parameters are printed in new lines, we want them to be indented.
    // there's a good chance no newlines happen, too, but just in case it does.
    boolean first = true;
    for (final JVar var : params)
    {
      if (!first)
        f.print (',');
      if (var.isAnnotated ())
        f.newline ();
      f.var (var);
      first = false;
    }
    if (hasVarArgs ())
    {
      if (!first)
        f.print (',');
      for (final JAnnotationUse annotation : varParam.annotations ())
        f.generable (annotation).newline ();
      f.generable (varParam.mods ()).generable (varParam.type ().elementType ());
      f.print ("... ");
      f.id (varParam.name ());
    }

    f.outdent ().print (')');
    if (_throws != null && !_throws.isEmpty ())
    {
      f.newline ().indent ().print ("throws").g (_throws).newline ().outdent ();
    }

    if (defaultValue != null)
    {
      f.print ("default ");
      f.generable (defaultValue);
    }
    if (body != null)
    {
      f.statement (body);
    }
    else
      if (!outer.isInterface () && !outer.isAnnotationTypeDeclaration () && !mods.isAbstract () && !mods.isNative ())
      {
        // Print an empty body for non-native, non-abstract methods
        f.statement (new JBlock ());
      }
      else
      {
        f.print (';').newline ();
      }
  }

  /**
   * @return the current modifiers of this method. Always return non-null valid
   *         object.
   */
  public JMods mods ()
  {
    return mods;
  }

  @Override
  protected JCodeModel owner ()
  {
    return outer.owner ();
  }
}
