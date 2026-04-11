# Simulador de Tráfego de Rede - Pontos para Apresentação (Foco em Arrays)

## Visão geral do projeto
- Simula envio de pacotes em um mapa 2D com dispositivos, cabos e roteador.
- Permite cadastrar dispositivos com:
  - tipo;
  - IP;
  - rede;
  - interface.
- Permite:
  - conectar cabos;
  - escolher origem/destino;
  - definir quantidade de pacotes;
  - salvar/carregar topologias em arquivo.

## Framework e organização do código
- Java + OpenJFX.
- Separação em pacotes:
  - `org.example.logic`: regras, dados, algoritmos e persistência;
  - `org.example.visual`: interface e animações.

## Diagrama simples (caixas conectadas)
```text
[Main]
   |
   v
[NetworkApp - visual]
   |
   v
[NetworkLogic - regras/algoritmos]
   |                  |
   v                  v
[Device[]]         [Cable[]]
```

## Uso de arrays (parte principal da apresentação)
- Estruturas principais são arrays:
  - `Device[] devices` -> guarda todos os dispositivos da topologia;
  - `Cable[] cables` -> guarda conexões físicas da rede;
  - `Circle[] packetCircles` -> guarda representação visual dos pacotes;
  - `Animation[] packetAnimations` -> controla estado de cada animação.
- Arrays auxiliares de algoritmo:
  - `boolean[] visited` (marca visitados na busca);
  - `int[] queue` (fila da BFS);
  - `int[] parentByIndex` (reconstrução do caminho);
  - `int[] path` (rota final de IDs).
- Operações clássicas de arrays usadas no projeto:
  - inserção por índice (`array[count] = valor`);
  - remoção com deslocamento para esquerda;
  - cópia com `System.arraycopy`;
  - varredura com `for`;
  - montagem de rota em novo `int[]`.

## Algoritmos e cálculos importantes
- Cálculo de rota (`calculateRoute`):
  - valida origem/destino;
  - verifica se estão na mesma rede;
  - se redes diferentes, exige roteador conectando ambos;
  - retorna rota como array de IDs (`int[]`).
- Busca de caminho (`findPath`):
  - BFS usando arrays (sem `List`/`Queue` complexas);
  - percorre cabos e descobre conectividade.
- Reconstrução de caminho (`buildPath`):
  - usa array de pais (`parentByIndex`);
  - reconstrói do destino para origem;
  - inverte para produzir rota final.
- Junção de rotas (`joinPaths`):
  - concatena duas partes (`origem->roteador` + `roteador->destino`) sem duplicar o roteador.

## Regra de rede implementada
- Mesma rede:
  - comunicação direta, desde que exista caminho por cabos.
- Redes diferentes:
  - só permite envio se houver roteador e ambos ligados a ele;
  - caso contrário, retorna erro explícito para o usuário.

## Lógica visual de transmissão
- Pacotes percorrem cada salto da rota (`for` sobre `routeDevices[]`).
- Se passar em roteador:
  - faz pequena espera simulando fila.
- Status visual:
  - indicador “✉ Enviando...” enquanto há pacotes em trânsito.

## Persistência (redes prontas para apresentação)
- Salva topologias em `networks/*.net`.
- Carrega redes prontas para demonstrar cenários diferentes sem configurar tudo na hora.
- Formato armazena:
  - dispositivos (id, tipo, IP, rede, interface, posição);
  - cabos (fromId, toId).

## Pontos de código para destacar na apresentação
- `NetworkLogic.calculateRoute(...)` -> regra principal de comunicação.
- `NetworkLogic.findPath(...)` -> BFS com arrays.
- `NetworkLogic.buildPath(...)` -> reconstrução da rota.
- `NetworkApp.animatePackets(...)` -> animação salto a salto pela rota.
- `NetworkLogic.saveNetwork(...)` e `loadNetwork(...)` -> persistência de topologias.
