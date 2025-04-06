package com.di.modules

import com.renderer.*
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class RendererModule {
    @Provides
    @Singleton
    fun provideMermaidGenerator(): IMermaidGenerator {
        return MermaidGenerator()
    }

    @Provides
    @Singleton
    fun provideGraphRenderer(
        @Named("useExternalRenderer") useExternalRenderer: Boolean,
        simpleRenderer: SimpleGraphRenderer,
        mermaidRenderer: MermaidRenderer
    ): IGraphRenderer {
        return if (useExternalRenderer) mermaidRenderer else simpleRenderer
    }

    @Provides
    @Singleton
    fun provideSimpleGraphRenderer(): SimpleGraphRenderer {
        return SimpleGraphRenderer()
    }

    @Provides
    @Singleton
    fun provideMermaidRenderer(mermaidGenerator: IMermaidGenerator): MermaidRenderer {
        return MermaidRenderer(mermaidGenerator)
    }

    @Provides
    @Named("useExternalRenderer")
    fun provideUseExternalRenderer(): Boolean {
        return false // Default to built-in renderer
    }
}