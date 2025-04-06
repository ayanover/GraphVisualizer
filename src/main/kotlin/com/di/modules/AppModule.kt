package com.di.modules
import com.parser.GraphParser
import com.parser.IGraphParser
import com.service.FileService
import com.service.IDialogService
import com.service.IFileService
import com.service.SwingDialogService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import javax.swing.JFrame
@Module
class AppModule {
    @Provides
    @Singleton
    fun provideGraphParser(): IGraphParser {
        return GraphParser()
    }

    @Provides
    @Singleton
    fun provideFileService(): IFileService {
        return FileService()
    }
    @Provides
    @Singleton
    fun provideDialogService(app: JFrame): IDialogService {
        return SwingDialogService(app)
    }
}