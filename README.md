# Graph Visualizer

This is an application made as a task for an Internship application at JetBrains. I used a mermaid graph generator and swing for the UI. 

## Development process

To be completely honest, my experience with Kotlin prior to this was only with a not-so-advanced mobile app that connected to my plant monitoring device, however that isn't even anywhere on git. I decided to go with java's Swing for the UI as it has a visual editor.
Also as I have much experience with .NET and C#, I figured I can handle some java. While the app isn't really pretty and had problem with making it any prettier, it solves its purpose.

At first I decided to create a simple version of the app, where a graph would be put in manually in the format of *A -> B* and used Mermaid graph generator to generate an image of the graph and later display it. Also for I added a checklist so I can select which
nodes I want to be displayed.

Then I figured it'd be nice to import and export graphs. So I added tabs that let me save the file as .txt. Importing takes a .txt file and transfers its text which maintains said *A -> B* and formats it to nodes. Another option is to export the graph itself as an image. 

```Some Time later```

While browsing the internet and doing some research I noticed that some other editors were interavtice and let users modify the graph itself (drag nodes, etc). So I went and added an interactive view alongside the static one. It uses the static graph's nodes positions as
the starting coords.

I feel like the task was simpler for me than it should be as I had already some experience with graphs during alhorithm classes in which we developed algorithms for Shortest path and minimum spanning tree. I had quite a lot of time to play around with the mermaid visualizer itself during that time.
I also implemented tests to see if the renderer and other elemnts work as expected.

Then I decided to make the code more SOLID. However I do not know at all how DI works in java and kotlin so forgive me if I mess something up. I hope the result

I also decided to take it a step further and as the project is supposed to be a Dependency Diagram Visualizer, I wanted to implement importing graph from a .csproj file. I first did in in a separate standalone C# app and was thinking about creating an endpoint that would provide the graph to the main app,
but quickly discarded this idea and decided to just implement it in-app with another option that lets me find a .csproj in files and imports its dependencies (I hope it's pushed when you read it)

At last I'd like to add that I'm preprared to learn a lot more in a small amount of time if it comes to it. So even if my kotlin/java skills are rusty I am ready to improve quickly.
