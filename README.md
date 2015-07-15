# README #

This is the readme file of the situation handler. It describes the steps to install and start the Situation Handler shortly.


### Install ###

The situation handler is set up as maven project. Just run "mvn install" (in eclipse or maven terminal) to download dependencies. This will create a .jar file that contains all dependencies.

Note that the project also needs the "Plugin-Project" as dependency. When using the m2e Eclipse plugin, it suffices to have the project in the same workspace (make sure to enable "workspace resolution"). When using plain maven, you have to keep the project in your local repository.

### Run ###

To run the situation handler, use eclipse and run routes.Main or you can just run the jar created from maven with "java -jar SituationHandler.jar".

Note that the situation handler needs its database running. It uses a MySQL Database server. You can use the structure dump to create the required tables. 


### Contribution guidelines ###

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact