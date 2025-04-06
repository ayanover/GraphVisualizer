package com.di.modules

import com.ui.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Module providing UI component dependencies.
 */
@Module
class UiModule {
    /**
     * Provide the vertex list panel implementation.
     */
    @Provides
    @Singleton
    fun provideVertexListPanel(): IVertexListPanel {
        return VertexListPanel()
    }

    /**
     * Provide the diagram panel implementation.
     */
    @Provides
    @Singleton
    fun provideDiagramPanel(): IDiagramPanel {
        return DiagramPanel()
    }

    /**
     * Provide the interactive graph panel implementation.
     */
    @Provides
    @Singleton
    fun provideInteractiveGraphPanel(): IInteractiveGraphPanel {
        return InteractiveGraphPanel()
    }
}