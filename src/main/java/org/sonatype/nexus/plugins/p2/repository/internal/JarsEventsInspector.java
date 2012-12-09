/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2012 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugins.p2.repository.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sonatype.nexus.plugins.p2.repository.internal.NexusUtils.retrieveFile;
import static org.sonatype.nexus.plugins.p2.repository.internal.P2ArtifactAnalyzer.getP2Type;

import java.io.File;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.sonatype.nexus.plugins.p2.repository.P2MetadataGenerator;
import org.sonatype.nexus.proxy.events.RepositoryItemEventCache;
import org.sonatype.nexus.proxy.events.RepositoryItemEventDelete;
import org.sonatype.nexus.proxy.events.RepositoryItemEventStore;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;
import org.sonatype.sisu.goodies.eventbus.EventBus;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

@Named
@Singleton
@EventBus.Managed
public class JarsEventsInspector
{

    private final Provider<P2MetadataGenerator> p2MetadataGenerator;

    private final Provider<RepositoryRegistry> repositories;

    @Inject
    public JarsEventsInspector( final Provider<P2MetadataGenerator> p2MetadataGenerator,
                                final Provider<RepositoryRegistry> repositories )
    {
        this.p2MetadataGenerator = checkNotNull( p2MetadataGenerator );
        this.repositories = checkNotNull( repositories );
    }

    @Subscribe
    @AllowConcurrentEvents
    private void onItemStored( final RepositoryItemEventStore event )
    {
        if ( isP2Artifact( event.getItem() ) )
        {
            p2MetadataGenerator.get().generateP2Metadata( event.getItem() );
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    private void onItemCached( final RepositoryItemEventCache event )
    {
        if ( isP2Artifact( event.getItem() ) )
        {
            p2MetadataGenerator.get().generateP2Metadata( event.getItem() );
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    private void onItemRemoved( final RepositoryItemEventDelete event )
    {
        if ( isP2Artifact( event.getItem() ) )
        {
            p2MetadataGenerator.get().removeP2Metadata( event.getItem() );
        }
    }

    // TODO optimize by saving the fact that is a bundle/feature as item attribute and check that one first
    private boolean isP2Artifact( final StorageItem item )
    {
        if ( item == null )
        {
            return false;
        }
        try
        {
            final File file = retrieveFile(
                repositories.get().getRepository( item.getRepositoryId() ), item.getPath()
            );
            return getP2Type( file ) != null;
        }
        catch ( final Exception e )
        {
            return false;
        }
    }
}
