# MapNodes [![CircleCI](https://circleci.com/gh/Year4000/MapNodes.svg?style=svg&circle-token=4cc9f4f8f96660538137825d50f5c9249b4251fe)](https://circleci.com/gh/Year4000/MapNodes)

This is the plugin that handles the games that `Year4000` runs.
There is a submodule within this project so its recommended to clone the repo with the following command.
With this in mind you should also have SSH keys linked with your GitHub account.

> git clone --recursive git@github.com:Year4000/MapNodes.git

## Building

### Gradle

To compile the project we use Gradle and this project contains `gradlew`.
You can compile the entire project with a single command.

> ./gradlew

### Docker

To build the Docker image all you need to do is run the command with `docker-compose`.
Docker compose will build the image with the needed tags and environment vars.
You must also have the compiled version of `MapNodes` before you can build the Docker image.

> docker-compose build

## Running / Development

### Maps

We have included a git submodule for the maps in the directory `run/maps`.
You do not have to keep this directory up to date unless you are testing maps.
Though you do have to have the submodule *inited* and *updated* before you run the development Docker image.

> git submodule init && git submodule update

### Docker

We use Docker to test the MapNodes plugin.
To run the image for development you must have Built the project already.
See `Building/Gradle` and `Building/Docker` before running the command bellow.
If you ran the command bellow with out reading you must *delete* the build folder.

> ./pytasks runDockerImage

You can also use the following command to combine the compiling and running of the docker image.

> ./pytasks devServer

## License

MapNodes is copyright &copy; 2017 [Year4000](https://www.year4000.net/)
