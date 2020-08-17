# Image that runs the server
FROM openjdk:8-jre-alpine AS minecraft

# Install python for the minecraft server wrapper
RUN apk add --update --no-cache python3

# Download the Sponge and Minecraft jar
WORKDIR /opt/year4000/minecraft
ENV MC_VERSION=1.16.1
ENV PAPER_VERSION=115
ADD https://launcher.mojang.com/v1/objects/a412fd69db1f81db3f511c1463fd304675244077/server.jar cache/mojang_${MC_VERSION}.jar
ADD https://papermc.io/api/v1/paper/${MC_VERSION}/${PAPER_VERSION}/download paper.jar
ADD https://gistcdn.githack.com/ewized/8599d0d846830af079d7/raw/4dbafb0e395e6dc15eb1794392bfc5b2d4d8d4a8/wrapper.py wrapper.py

# Set up the command that will run the server
EXPOSE 25565/tcp
EXPOSE 5005
ENTRYPOINT [ "python3", "/opt/year4000/minecraft/wrapper.py", "--no-restart" ]
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005", "-jar", "paper.jar"]

# Get the image as an import to copy the mod over
#FROM year4000/utilities:spongevanilla as utilities
# Build the mapnodes project
#FROM openjdk:8 AS mapnodes

#RUN chmod +x paper.jar && ls -lash

# Build the mapnodes project
#WORKDIR /opt/year4000/mapnodes
#COPY . .
#RUN ./gradlew --no-daemon :paper:assemble

# Back to the minecraft server instance and copy the mods over
#FROM minecraft

# Copy the defaults into the root of the folder
#COPY --from=mapnodes /opt/year4000/mapnodes/sponge/build/libs/mapnodes-*-all.jar mods/mapnodes.jar
#COPY --from=utilities /opt/year4000/minecraft/mods/utilities.jar mods/utilities.jar
COPY ./paper/docker/ ./
