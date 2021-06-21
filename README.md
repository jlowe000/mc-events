This is the beginning of a Minecraft Plugin that is integrated with OCI-Arcade Events.

There current plugin captures events from PlayerMovement and then creates the event payload similar to pacman and then pushes that along. The implementation is still experimental and has code to call the API function like pacman does (which then calls into /serverless or /publishevent functions). It also has a segment of code that calls into a local MQTT (hosted by RabbitMQ) in a local deployed docker container.
