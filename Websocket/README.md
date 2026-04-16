# Websocket (Socket TCP Java)

Projeto simples de chat via TCP com mensagens em tempo real no cliente:
- `Server.java` (servidor)
- `ClientHandler.java` (atendimento de cada cliente)
- `Cliente.java` (cliente de console)

## Requisitos

- JDK 8+ (recomendado JDK 17)

## Compilar

```bash
javac Server.java ClientHandler.java Cliente.java
```

## Rodar

Terminal 1 (servidor):

```bash
java Server
```

Terminal 2 (cliente):

```bash
java Cliente
```

Digite mensagens no cliente e use `exit` para sair.

