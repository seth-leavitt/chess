# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQH+iMyzKfMizfGpOx7Es0KPMB4lUEiMAIEJ4oYoJwkEkSYCkm+hgVMO-IMkyU4GXONL7pSy4wGKEpujKcplu8So3sFd5Jr6zpdhu7pbp5gqefUNRIAAZvKJkAqYKrBhqbpGBAagwGgEDMFaaIwNAMDFeu2QwOKIDQCi4Dbl55SgfUACSaBUCaSAcM54rofC5SibU6aZqMYw5qoeaGWMRYlvUXU9Sg4CtfofymXRTb9ZSOVoCgmzPle2jTuWcweallI+fSFE8uel5aPIQW8kl5TLtdmwUPJBTfXqv3AKSxjJe9o7GGQ8hgP0CUA-yyU5UjKNoxdyU6XU01oBkqgAZghMgdUzy4U4MAEQZewuMz3wUVR9aLMzLgNlpyULbT9MrYznMs7Bl7s8hIvc2dDGeN4fj+F4KDoDEcSJErKvOb4WCidl1Olg00gRvxEbtBG3Q9HJqgKcMbOIegs2UoTtQjHbSEWbClDWUi9n2NrkOUfbaAvZ23m3r5RgoNw6Jg9bENu+g-0LqoFRhRwUdMigscKQHEuwzo8Ph-SONyGjCNY6ltQl6jGiYGV6rpVVNV1Q11o5Pjg36yNY0TVNDmFLz83IKm6YAIwEWtG17Nt0D1G8TcCi3MCNTkDb0aYWUE5Zzxa6epPk5TyUdjUtQC8MMAjJp+s+vzeHn5fpjr0xCsouu-jYOKGr8U1ADiSoaLraykkf6mwtvYJUttxZB0dh+beYEE5oA9l+PWnZ6jIByH-HMucg4hxsjuKkRdRwwEZGAO62CkJJxCkuYU4VxRPkvNFeUCDNA6HbAjeod0ZQtR7H2dGycqaoIDAw7QzVYA8P7HXVUGo7oSwXrVeqy825YE3rAz2pZmTTEvNAJAAAvFAHAowxjjIUQ+QDSxjwnvyKeiwZ6lh8JovU2i9G7CvhJeEyZh7YRgBYlak98w2OLLPBUDj4hOP0Wvc6m8CGJQjugsAmC1AYkoYDIU9QIrrhNAgYh-8WFsMIfUOAEA+wHWMc1fKMAEkegRgIvBaVCnFPAApMpFSlSvlet7F0MAABCPhTjrm-hg-++8EDMG6hgE4aBxSOHAXMYa0gVjDE2PEXUKA3ScjWMkUAao1lKjWI8bJcwAByFZ8otWDCAfUMyxAqMqM7ASaIEmqGGYBOBNT-S0wIlc1QCwGjjCuXMhYXMVhjCWSsnZnxahArGJskA2yiKGShVc45EKuadFcYmDxJQwA4TvitL5Py-lKgBZC5mwLQWTnhYC0l0KEBbPBQi6lSLIIkpcGixsstn4BA4AAdjcE4FAdN-ARmCHALiAA2eAE5DAJKKJ4sSKU3H1Ckh0MBECQkSyzEyuY6KMKqK-C8BBawtUoCQVZBVtT6iHjkCgBJGI4BSoSa5NQ7kO4yHycQpkZCEHJIdNQtJdChEvhETFZhxg8kxPpIG+68hRG1V7P2apR8cpRq4WI+NpUpFRtkRwaqi8FEr2Ue0vVcJXQhLCQY6MKBYwKQpq8pNXdvFOHHr4qx-itqBLsWWqAujwk6vcZUBajbm3ZlbZtWxc8u09pceyjN5UYAAFlsjWwcLG8U41kDrmNfjYt-oukhidTkQxVbjG1rUW88xuKR25jbeO2UIYYAokJM6nm18j6DvTPhFt16x0dvqHodcj63IRNllE4+tkf7oEoNa4JSAn0HJQHM40EodC9IQOuSp2hFm6kuUqZFKA9m1RuvAeI-IEl7IlEcWMRVaWWAwqoeyYANCgfYTAK16JbVXOvImyoYVHwtLmB6MNR8WPVzLoQmptlRNMaLbcuBBSHVKmeRif50hSSmPNe8y9YwVMLFHuEYIgQ+2YTlTiz9hLZnSF0-pwzM65bMX8LRvsEBNiqyQAkMAjn7IuYAFIQHFPxww-gYVqllVilBJ9GhNGZDJHoVzIHwSDlmbANHKD1OgEaol0gjM7vgVApCqkUtQDS1ADLFnTW6vNbZAAVn5tAtrfPikdSgJ9JJXUsZIV6vLic+FUJ4zQ9JKbg1MK64UITu4I1EM4SI7h6bE0dLSlNmNM3eGSLnTIoOcil4Fu3bJtRpaKLlqPdW+M6nj7+h8Ve9aN7f3BIO925xL63HGaxWmJtljv3Txu-Yu7U7gOzobou+SK6Wprt7vBxDLVkvAFo0Vop0AdsPD290-dzW3JHZPadoajazOrVHZ9nad6AOo+fdlzFqZsfvauz+gn-6H3E9XjLDe7TokY0jSQjjmWfWLj6-6iUGGY0xRU7k4T7r+fAFjeInrSV5v1DFxL9Nq2G4JM2-mpRCO7l7o4AesA6Oa2Y4bR+yn1j200-vYBknMCydeMN1+qn+Ogm0-Nwz2zoHk1wBI7CunLX0TrihzD4r-2NTa-0a1QrMB6MKI6iaM0NZwwDxk4j-VAYqyhjj7rk7dazGveWpd43t7o-BnNDAWs9ZScDpM42nPuOPsBIJwX6sxe49-ddcmnpfSFTYF+iHuXOo9SDfkC4Aywx-N6kME7n3MAADqLBhpm1jV0-iCg4AAGlQ-Q7owxtr7q-Bd45wJ7QXOBR+slJ36DcvTmwCF2Nlnyd6hSal76mXQpkal2k52dsdyGt1cU-+EZp7kH1qKowAfKjBl7wAV4gEPzrx2YKxeDQ6ubuZwHyiIDBiwDADYDJaEB5DxiAIaYGxGwmxmwWzGAwK7ZJ4oF4Be5uT-5mpgadIgDcB4BJJb4TaWqMFQDMHcapIyAZzoiVhZJvCqClZqDkb0JBryDC7jas5EL35zaVadL343KJ4lrgGoHPI0G6pZ7AGXpgGDqQEyxAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
