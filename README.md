# scala-μservices-spike
Scala 3 and micro services related technologies in a sample application. The project is built around cats-effect, fs2 etc.

## Goals

This is my personal project to play with the technologies used for microservice development in the context of Scala 3

## Non goals
No particulair effort has been made to make the code optimal.

## Structure
- `grpc` protobuf protocols
- `server` HTTP server/ gRPC client
- `worker` gRPC server
- `common` utils used in microservice 
development
- `docker` anything related to Docker deployment