/*
 * Copyright The Sett Ltd, 2005 to 2009.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thesett.common.jndi;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import javax.naming.OperationNotSupportedException;

/**
 * SimpleContext is a simple JNDI service provider. It implements a flat namespace (with no federation support).
 *
 * <p/>An instance of this context is bound directly as the initial context. The corresponding InitialContextFactory
 * definition can be found in {@link SimpleInitialContextFactory}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SimpleContext implements Context
{
    /** Holds a reference to the name parser. */
    static NameParser myParser = new SimpleNameParser();

    /** Holds the JNDI environment. */
    Hashtable myEnv;

    /** Holds the bindings of names to objects. */
    private Hashtable bindings = new Hashtable(11);

    /**
     * Creates a new SimpleContext.
     *
     * @param environment The JNDI environment for the context.
     */
    SimpleContext(Hashtable environment)
    {
        myEnv = (environment != null) ? (Hashtable) (environment.clone()) : null;
    }

    /**
     * Retrieves the named object. If name is empty, returns a new instance of this context (which represents the same
     * naming context as this context, but its environment may be modified independently and it may be accessed
     * concurrently).
     *
     * @param  name The name to look up.
     *
     * @return The object associated with the name, if there is one.
     *
     * @throws NamingException If the name cannot be found in the context.
     */
    public Object lookup(String name) throws NamingException
    {
        if ("".equals(name))
        {
            // Asking to look up this context itself.  Create and return
            // a new instance with its own independent environment.
            return (new SimpleContext(myEnv));
        }

        Object answer = bindings.get(name);

        if (answer == null)
        {
            throw new NameNotFoundException(name + " not found");
        }

        return answer;
    }

    /**
     * Retrieves the named object. If name is empty, returns a new instance of this context (which represents the same
     * naming context as this context, but its environment may be modified independently and it may be accessed
     * concurrently).
     *
     * @param  name The name to look up.
     *
     * @return The object associated with the name, if there is one.
     *
     * @throws NamingException If the name cannot be found in the context.
     */
    public Object lookup(Name name) throws NamingException
    {
        // Flat namespace; no federation; just call string version
        return lookup(name.toString());
    }

    /**
     * Binds a name to an object. All intermediate contexts and the target context (that named by all but terminal
     * atomic component of the name) must already exist.
     *
     * @param  name The name to bind; may not be empty.
     * @param  obj  The object to bind; possibly null.
     *
     * @throws NamingException If the name already exists.
     */
    public void bind(String name, Object obj) throws NamingException
    {
        if ("".equals(name))
        {
            throw new InvalidNameException("Cannot bind empty name");
        }

        if (bindings.get(name) != null)
        {
            throw new NameAlreadyBoundException("Use rebind to override");
        }

        bindings.put(name, obj);
    }

    /**
     * Binds a name to an object. All intermediate contexts and the target context (that named by all but terminal
     * atomic component of the name) must already exist.
     *
     * @param  name The name to bind; may not be empty.
     * @param  obj  The object to bind; possibly null.
     *
     * @throws NamingException If the name already exists or is illegal.
     */
    public void bind(Name name, Object obj) throws NamingException
    {
        // Flat namespace; no federation; just call string version
        bind(name.toString(), obj);
    }

    /**
     * Binds a name to an object, overwriting any existing binding. All intermediate contexts and the target context
     * (that named by all but terminal atomic component of the name) must already exist.
     *
     * <p/>If the object is a DirContext, any existing attributes associated with the name are replaced with those of
     * the object. Otherwise, any existing attributes associated with the name remain unchanged.
     *
     * @param  name The name to bind; may not be empty.
     * @param  obj  The object to bind; possibly null.
     *
     * @throws NamingException If the name is illegal.
     */
    public void rebind(String name, Object obj) throws NamingException
    {
        if ("".equals(name))
        {
            throw new InvalidNameException("Cannot bind empty name");
        }

        bindings.put(name, obj);
    }

    /**
     * Binds a name to an object, overwriting any existing binding. All intermediate contexts and the target context
     * (that named by all but terminal atomic component of the name) must already exist.
     *
     * <p/>If the object is a DirContext, any existing attributes associated with the name are replaced with those of
     * the object. Otherwise, any existing attributes associated with the name remain unchanged.
     *
     * @param  name The name to bind; may not be empty.
     * @param  obj  The object to bind; possibly null.
     *
     * @throws NamingException If the name is illegal.
     */
    public void rebind(Name name, Object obj) throws NamingException
    {
        // Flat namespace; no federation; just call string version
        rebind(name.toString(), obj);
    }

    /**
     * Unbinds the named object. Removes the terminal atomic name in name from the target context--that named by all but
     * the terminal atomic part of name.
     *
     * <p/>This method is idempotent. It succeeds even if the terminal atomic name is not bound in the target context,
     * but throws NameNotFoundException if any of the intermediate contexts do not exist.
     *
     * <p/>Any attributes associated with the name are removed. Intermediate contexts are not changed.
     *
     * @param  name The name to unbind.
     *
     * @throws NamingException If the name is illegal, or intermediate contexts do not exist.
     */
    public void unbind(String name) throws NamingException
    {
        if ("".equals(name))
        {
            throw new InvalidNameException("Cannot unbind empty name");
        }

        bindings.remove(name);
    }

    /**
     * Unbinds the named object. Removes the terminal atomic name in name from the target context--that named by all but
     * the terminal atomic part of name.
     *
     * <p/>This method is idempotent. It succeeds even if the terminal atomic name is not bound in the target context,
     * but throws NameNotFoundException if any of the intermediate contexts do not exist.
     *
     * <p/>Any attributes associated with the name are removed. Intermediate contexts are not changed.
     *
     * @param  name The name to unbind.
     *
     * @throws NamingException If the name is illegal, or intermediate contexts do not exist.
     */
    public void unbind(Name name) throws NamingException
    {
        // Flat namespace; no federation; just call string version
        unbind(name.toString());
    }

    /**
     * Binds a new name to the object bound to an old name, and unbinds the old name. Both names are relative to this
     * context. Any attributes associated with the old name become associated with the new name. Intermediate contexts
     * of the old name are not changed.
     *
     * @param  oldname The name of the existing binding; may not be empty.
     * @param  newname The new name of the binding; may not be empty.
     *
     * @throws NamingException If newname is bound, oldname cannot be found or one of the names is illegal.
     */
    public void rename(String oldname, String newname) throws NamingException
    {
        if ("".equals(oldname) || "".equals(newname))
        {
            throw new InvalidNameException("Cannot rename empty name");
        }

        // Check if new name exists
        if (bindings.get(newname) != null)
        {
            throw new NameAlreadyBoundException(newname + " is already bound");
        }

        // Check if old name is bound
        Object oldBinding = bindings.remove(oldname);

        if (oldBinding == null)
        {
            throw new NameNotFoundException(oldname + " not bound");
        }

        bindings.put(newname, oldBinding);
    }

    /**
     * Binds a new name to the object bound to an old name, and unbinds the old name. Both names are relative to this
     * context. Any attributes associated with the old name become associated with the new name. Intermediate contexts
     * of the old name are not changed.
     *
     * @param  oldname The name of the existing binding; may not be empty.
     * @param  newname The new name of the binding; may not be empty.
     *
     * @throws NamingException If newname is bound, oldname cannot be found or one of the names is illegal.
     */
    public void rename(Name oldname, Name newname) throws NamingException
    {
        // Flat namespace; no federation; just call string version
        rename(oldname.toString(), newname.toString());
    }

    /**
     * Enumerates the names bound in the named context, along with the class names of objects bound to them. The
     * contents of any subcontexts are not included. If a binding is added to or removed from this context, its effect
     * on an enumeration previously returned is undefined.
     *
     * @param  name The name of the context to list.
     *
     * @return An enumeration of the names and class names of the bindings in this context. Each element of the
     *         enumeration is of type NameClassPair.
     *
     * @throws NamingException If the context is not known.
     */
    public NamingEnumeration list(String name) throws NamingException
    {
        if ("".equals(name))
        {
            // listing this context
            return new FlatNames(bindings.keys());
        }

        // Perhaps `name' names a context
        Object target = lookup(name);

        if (target instanceof Context)
        {
            return ((Context) target).list("");
        }

        throw new NotContextException(name + " cannot be listed");
    }

    /**
     * Enumerates the names bound in the named context, along with the class names of objects bound to them. The
     * contents of any subcontexts are not included. If a binding is added to or removed from this context, its effect
     * on an enumeration previously returned is undefined.
     *
     * @param  name The name of the context to list.
     *
     * @return An enumeration of the names and class names of the bindings in this context. Each element of the
     *         enumeration is of type NameClassPair.
     *
     * @throws NamingException If the context is not known.
     */
    public NamingEnumeration list(Name name) throws NamingException
    {
        // Flat namespace; no federation; just call string version
        return list(name.toString());
    }

    /**
     * Enumerates the names bound in the named context, along with the objects bound to them. The contents of any
     * subcontexts are not included.
     *
     * <p/>If a binding is added to or removed from this context, its effect on an enumeration previously returned is
     * undefined.
     *
     * @param  name The name of the context to list.
     *
     * @return An enumeration of the bindings in this context. Each element of the enumeration is of type Binding.
     *
     * @throws NamingException If the context is not known.
     */
    public NamingEnumeration listBindings(String name) throws NamingException
    {
        if ("".equals(name))
        {
            // listing this context
            return new FlatBindings(bindings.keys());
        }

        // Perhaps `name' names a context
        Object target = lookup(name);

        if (target instanceof Context)
        {
            return ((Context) target).listBindings("");
        }

        throw new NotContextException(name + " cannot be listed");
    }

    /**
     * Enumerates the names bound in the named context, along with the objects bound to them. The contents of any
     * subcontexts are not included.
     *
     * <p/>If a binding is added to or removed from this context, its effect on an enumeration previously returned is
     * undefined.
     *
     * @param  name The name of the context to list.
     *
     * @return An enumeration of the bindings in this context. Each element of the enumeration is of type Binding.
     *
     * @throws NamingException If the context is not known.
     */
    public NamingEnumeration listBindings(Name name) throws NamingException
    {
        // Flat namespace; no federation; just call string version
        return listBindings(name.toString());
    }

    /**
     * Destroys the named context and removes it from the namespace. Any attributes associated with the name are also
     * removed. Intermediate contexts are not destroyed.
     *
     * <p/>This method is idempotent. It succeeds even if the terminal atomic name is not bound in the target context,
     * but throws NameNotFoundException if any of the intermediate contexts do not exist.
     *
     * <p/>In a federated naming system, a context from one naming system may be bound to a name in another. One can
     * subsequently look up and perform operations on the foreign context using a composite name. However, an attempt
     * destroy the context using this composite name will fail with NotContextException, because the foreign context is
     * not a "subcontext" of the context in which it is bound. Instead, use unbind() to remove the binding of the
     * foreign context. Destroying the foreign context requires that the destroySubcontext() be performed on a context
     * from the foreign context's "native" naming system.
     *
     * @param  name The name of the context to be destroyed; may not be empty.
     *
     * @throws NamingException Always throws OperationNotSupportedException.
     */
    public void destroySubcontext(String name) throws NamingException
    {
        throw new OperationNotSupportedException("SimpleContext does not support subcontexts");
    }

    /**
     * Destroys the named context and removes it from the namespace. Any attributes associated with the name are also
     * removed. Intermediate contexts are not destroyed.
     *
     * <p/>This method is idempotent. It succeeds even if the terminal atomic name is not bound in the target context,
     * but throws NameNotFoundException if any of the intermediate contexts do not exist.
     *
     * <p/>In a federated naming system, a context from one naming system may be bound to a name in another. One can
     * subsequently look up and perform operations on the foreign context using a composite name. However, an attempt
     * destroy the context using this composite name will fail with NotContextException, because the foreign context is
     * not a "subcontext" of the context in which it is bound. Instead, use unbind() to remove the binding of the
     * foreign context. Destroying the foreign context requires that the destroySubcontext() be performed on a context
     * from the foreign context's "native" naming system.
     *
     * @param  name The name of the context to be destroyed; may not be empty.
     *
     * @throws NamingException Always throws OperationNotSupportedException.
     */
    public void destroySubcontext(Name name) throws NamingException
    {
        // Flat namespace; no federation; just call string version
        destroySubcontext(name.toString());
    }

    /**
     * Creates and binds a new context. Creates a new context with the given name and binds it in the target context
     * (that named by all but terminal atomic component of the name). All intermediate contexts and the target context
     * must already exist. This operation does nothing on simple context.
     *
     * @param  name The name of the context to create; may not be empty.
     *
     * @return The newly created context.
     */
    public Context createSubcontext(String name)
    {
        return null;
    }

    /**
     * Creates and binds a new context. Creates a new context with the given name and binds it in the target context
     * (that named by all but terminal atomic component of the name). All intermediate contexts and the target context
     * must already exist.
     *
     * @param  name The name of the context to create; may not be empty.
     *
     * @return The newly created context.
     *
     * @throws NamingException Always throws OperationNotSupportedException.
     */
    public Context createSubcontext(Name name) throws NamingException
    {
        // Flat namespace; no federation; just call string version
        return createSubcontext(name.toString());
    }

    /**
     * Retrieves the named object, following links except for the terminal atomic component of the name. If the object
     * bound to name is not a link, returns the object itself.
     *
     * @param  name The name of the object to look up.
     *
     * @return The object bound to name, not following the terminal link (if any).
     *
     * @throws NamingException If the name is illegal or not bound.
     */
    public Object lookupLink(String name) throws NamingException
    {
        // This flat context does not treat links specially
        return lookup(name);
    }

    /**
     * Retrieves the named object, following links except for the terminal atomic component of the name. If the object
     * bound to name is not a link, returns the object itself.
     *
     * @param  name The name of the object to look up.
     *
     * @return The object bound to name, not following the terminal link (if any).
     *
     * @throws NamingException If the name is illegal or not bound.
     */
    public Object lookupLink(Name name) throws NamingException
    {
        // Flat namespace; no federation; just call string version
        return lookupLink(name.toString());
    }

    /**
     * Retrieves the parser associated with the named context. In a federation of namespaces, different naming systems
     * will parse names differently. This method allows an application to get a parser for parsing names into their
     * atomic components using the naming convention of a particular naming system. Within any single naming system,
     * NameParser objects returned by this method must be equal (using the equals() test).
     *
     * @param  name The name of the context from which to get the parser. Ignored.
     *
     * @return The instance of {@link SimpleNameParser}.
     */
    public NameParser getNameParser(String name)
    {
        return myParser;
    }

    /**
     * Retrieves the parser associated with the named context. In a federation of namespaces, different naming systems
     * will parse names differently. This method allows an application to get a parser for parsing names into their
     * atomic components using the naming convention of a particular naming system. Within any single naming system,
     * NameParser objects returned by this method must be equal (using the equals() test).
     *
     * @param  name The name of the context from which to get the parser. Ignored.
     *
     * @return The instance of {@link SimpleNameParser}.
     */
    public NameParser getNameParser(Name name)
    {
        // Flat namespace; no federation; just call string version
        return getNameParser(name.toString());
    }

    /**
     * Composes the name of this context with a name relative to this context. Given a name (name) relative to this
     * context, and the name (prefix) of this context relative to one of its ancestors, this method returns the
     * composition of the two names using the syntax appropriate for the naming system(s) involved. That is, if name
     * names an object relative to this context, the result is the name of the same object, but relative to the ancestor
     * context. None of the names may be null.
     *
     * <p/>For example, if this context is named "wiz.com" relative to the initial context, then
     *
     * <pre>
     *         composeName("east", "wiz.com")
     * </pre>
     *
     * <p/>might return "east.wiz.com". If instead this context is named "org/research", then
     *
     * <pre>
     *         composeName("user/jane", "org/research")
     * </pre>
     *
     * <p/>might return "org/research/user/jane" while
     *
     * <pre>
     *         composeName("user/jane", "research")
     * </pre>
     *
     * <p/>returns "research/user/jane".
     *
     * @param  name   A name relative to this context.
     * @param  prefix The name of this context relative to one of its ancestors.
     *
     * @return The composition of prefix and name.
     *
     * @throws NamingException If a naming exception is encountered.
     */
    public String composeName(String name, String prefix) throws NamingException
    {
        Name result = composeName(new CompositeName(name), new CompositeName(prefix));

        return result.toString();
    }

    /**
     * Composes the name of this context with a name relative to this context. Given a name (name) relative to this
     * context, and the name (prefix) of this context relative to one of its ancestors, this method returns the
     * composition of the two names using the syntax appropriate for the naming system(s) involved. That is, if name
     * names an object relative to this context, the result is the name of the same object, but relative to the ancestor
     * context. None of the names may be null.
     *
     * @param  name   A name relative to this context.
     * @param  prefix The name of this context relative to one of its ancestors.
     *
     * @return The composition of prefix and name.
     *
     * @throws NamingException If a naming exception is encountered.
     */
    public Name composeName(Name name, Name prefix) throws NamingException
    {
        Name result = (Name) (prefix.clone());
        result.addAll(name);

        return result;
    }

    /**
     * Adds a new environment property to the environment of this context. If the property already exists, its value is
     * overwritten. See class description for more details on environment properties.
     *
     * @param  propName The name of the property.
     * @param  propVal  The value of the property.
     *
     * @return The previous value of the property, or null if the property was not in the environment before.
     */
    public Object addToEnvironment(String propName, Object propVal)
    {
        if (myEnv == null)
        {
            myEnv = new Hashtable(5, 0.75f);
        }

        return myEnv.put(propName, propVal);
    }

    /**
     * Removes an environment property from the environment of this context. See class description for more details on
     * environment properties.
     *
     * @param  propName The name of the property to remove.
     *
     * @return The previous value of the property, or null if the property was not in the environment before.
     */
    public Object removeFromEnvironment(String propName)
    {
        if (myEnv == null)
        {
            return null;
        }

        return myEnv.remove(propName);
    }

    /**
     * Retrieves the environment in effect for this context. See class description for more details on environment
     * properties.
     *
     * <p/>The caller should not make any changes to the object returned: their effect on the context is undefined. The
     * environment of this context may be changed using addToEnvironment() and removeFromEnvironment().
     *
     * @return The environment of this context; never null.
     */
    public Hashtable getEnvironment()
    {
        if (myEnv == null)
        {
            // Must return non-null
            return new Hashtable(3, 0.75f);
        }
        else
        {
            return (Hashtable) myEnv.clone();
        }
    }

    /**
     * Retrieves the full name of this context within its own namespace.
     *
     * <p/>Many naming services have a notion of a "full name" for objects in their respective namespaces. For example,
     * an LDAP entry has a distinguished name, and a DNS record has a fully qualified name. This method allows the
     * client application to retrieve this name. The string returned by this method is not a JNDI composite name and
     * should not be passed directly to context methods. In naming systems for which the notion of full name does not
     * make sense, OperationNotSupportedException is thrown.
     *
     * @return This context's name in its own namespace; never null.
     */
    public String getNameInNamespace()
    {
        return "";
    }

    /**
     * Closes this context. This method releases this context's resources immediately, instead of waiting for them to be
     * released automatically by the garbage collector.
     *
     * <p/>This method is idempotent: invoking it on a context that has already been closed has no effect. Invoking any
     * other method on a closed context is not allowed, and results in undefined behaviour.
     */
    public void close()
    {
        myEnv = null;
        bindings = null;
    }

    /**
     * Class for enumerating name/class pairs.
     */
    class FlatNames implements NamingEnumeration
    {
        /** Holds an enumeration of the names. */
        Enumeration names;

        /**
         * Creates an enumeration of names.
         *
         * @param names The enumeration of names.
         */
        FlatNames(Enumeration names)
        {
            this.names = names;
        }

        /**
         * Detects whether or not this enumeration contains more names.
         *
         * @return <tt>true</tt> if it contains more names.
         */
        public boolean hasMoreElements()
        {
            return names.hasMoreElements();
        }

        /**
         * Detects whether or not this enumeration contains more names.
         *
         * @return <tt>true</tt> if it contains more names.
         */
        public boolean hasMore()
        {
            return hasMoreElements();
        }

        /**
         * Gets the next element from this enumeration.
         *
         * @return The next element from this enumeration.
         */
        public Object nextElement()
        {
            String name = (String) names.nextElement();
            String className = bindings.get(name).getClass().getName();

            return new NameClassPair(name, className);
        }

        /**
         * Gets the next element from this enumeration.
         *
         * @return The next element from this enumeration.
         */
        public Object next()
        {
            return nextElement();
        }

        /** Does nothing. */
        public void close()
        {
        }
    }

    /**
     * Class for enumerating bindings.
     */
    class FlatBindings implements NamingEnumeration
    {
        /** Holds an enumeration of the names. */
        Enumeration names;

        /**
         * Creates an enumeration of names.
         *
         * @param names The enumeration of names.
         */
        FlatBindings(Enumeration names)
        {
            this.names = names;
        }

        /**
         * Detects whether or not this enumeration contains more names.
         *
         * @return <tt>true</tt> if it contains more names.
         */
        public boolean hasMoreElements()
        {
            return names.hasMoreElements();
        }

        /**
         * Detects whether or not this enumeration contains more names.
         *
         * @return <tt>true</tt> if it contains more names.
         */
        public boolean hasMore()
        {
            return hasMoreElements();
        }

        /**
         * Gets the next element from this enumeration.
         *
         * @return The next element from this enumeration.
         */
        public Object nextElement()
        {
            String name = (String) names.nextElement();

            return new Binding(name, bindings.get(name));
        }

        /**
         * Gets the next element from this enumeration.
         *
         * @return The next element from this enumeration.
         */
        public Object next()
        {
            return nextElement();
        }

        /** Does nothing. */
        public void close()
        {
        }
    }
}
