# Spring Boot WebSocket Chat Application

A real-time chat application built with Spring Boot and WebSocket (STOMP protocol over SockJS).

## Features

- Real-time bi-directional communication using WebSocket
- User join/leave notifications
- Simple and responsive web interface
- Support for multiple concurrent users
- SockJS fallback for browsers without WebSocket support

## Technologies Used

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring WebSocket** - WebSocket support
- **STOMP** - Simple Text Oriented Messaging Protocol
- **SockJS** - WebSocket emulation for older browsers
- **Maven** - Dependency management
- **Lombok** - Reduce boilerplate code

## Project Structure

```
java-web-socket/
├── src/
│   ├── main/
│   │   ├── java/com/example/websocket/
│   │   │   ├── WebSocketApplication.java      # Main application class
│   │   │   ├── config/
│   │   │   │   └── WebSocketConfig.java       # WebSocket configuration
│   │   │   ├── controller/
│   │   │   │   └── ChatController.java        # Message handling controller
│   │   │   ├── model/
│   │   │   │   └── ChatMessage.java           # Message model
│   │   │   └── listener/
│   │   │       └── WebSocketEventListener.java # WebSocket events
│   │   └── resources/
│   │       ├── application.properties          # Application configuration
│   │       └── static/
│   │           └── index.html                  # Web chat client
└── pom.xml                                     # Maven dependencies
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+

## Getting Started

### 1. Clone or navigate to the project

```bash
cd d:\Others\java-web-socket
```

### 2. Build the project

```bash
mvn clean install
```

### 3. Run the application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access the chat interface

Open your browser and navigate to:
```
http://localhost:8080
```

## How It Works

### WebSocket Configuration

The [WebSocketConfig.java](src/main/java/com/example/websocket/config/WebSocketConfig.java) class configures:
- `/ws` - WebSocket endpoint with SockJS fallback
- `/app` - Application destination prefix for client messages
- `/topic` - Simple message broker for broadcasting

### Message Flow

1. **Client connects** → Establishes WebSocket connection at `/ws`
2. **User joins** → Sends JOIN message to `/app/chat.addUser`
3. **Server broadcasts** → All connected clients receive the message via `/topic/public`
4. **Send message** → Client sends to `/app/chat.sendMessage`
5. **Receive message** → All subscribers on `/topic/public` get the message
6. **User leaves** → Disconnect event triggers LEAVE message broadcast

### Message Types

- **CHAT** - Regular chat messages
- **JOIN** - User joined notification
- **LEAVE** - User left notification

## API Endpoints

### WebSocket Endpoints

- `ws://localhost:8080/ws` - WebSocket connection endpoint (with SockJS)

### Message Mappings

- `/app/chat.sendMessage` - Send chat message
- `/app/chat.addUser` - User join event
- `/topic/public` - Subscribe to receive messages

## Testing

Open multiple browser windows at `http://localhost:8080` to simulate multiple users chatting.

## Configuration

Edit [application.properties](src/main/resources/application.properties) to customize:

```properties
server.port=8080
spring.application.name=websocket-demo
logging.level.com.example.websocket=DEBUG
```

## Building for Production

```bash
mvn clean package
java -jar target/websocket-demo-1.0.0.jar
```

## Troubleshooting

- **Port already in use**: Change `server.port` in application.properties
- **Connection refused**: Ensure the server is running on port 8080
- **Messages not receiving**: Check browser console for WebSocket errors

## Future Enhancements

- Private messaging between users
- Chat rooms/channels
- Message persistence
- User authentication
- File sharing
- Typing indicators

## License

This project is open source and available for educational purposes.
