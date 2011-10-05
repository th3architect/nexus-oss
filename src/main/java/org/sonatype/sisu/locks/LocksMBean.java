/*******************************************************************************
 * Copyright (c) 2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * The Apache License v2.0 is available at
 *   http://www.apache.org/licenses/LICENSE-2.0.html
 * You may elect to redistribute this code under either of these licenses.
 *******************************************************************************/
package org.sonatype.sisu.locks;

public interface LocksMBean
{
    String[] listResourceNames();

    String[] findOwningThreads( String name );

    String[] findWaitingThreads( String name );

    String[] findOwnedResources( String tid );

    String[] findWaitedResources( String tid );

    void releaseResource( String name );
}
