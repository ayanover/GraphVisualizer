package com.di

import com.GraphVisualizerApp
import com.di.modules.AppModule
import com.di.modules.RendererModule
import com.di.modules.UiModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton
import javax.swing.JFrame

@Singleton
@Component(modules = [AppModule::class, RendererModule::class, UiModule::class])

interface AppComponent {
    fun inject(app: GraphVisualizerApp)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(app: JFrame): Builder

        fun build(): AppComponent
    }
}
