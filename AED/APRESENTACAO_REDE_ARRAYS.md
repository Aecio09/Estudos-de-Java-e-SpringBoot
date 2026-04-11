# Simulador de Tráfego de Rede - Guia técnico para apresentação

## 1) Resumo técnico do que o sistema faz
- Simula uma topologia de rede em mapa 2D.
- Permite cadastrar dispositivos com endereço IP, rede e interfaces.
- Permite conectar cabos especificando interface de saída/entrada.
- Envia pacotes de uma origem para um destino com animação visual.
- Aplica regra de roteamento:
  - mesma rede: envio direto se existir caminho;
  - redes diferentes: caminho precisa passar por roteador.
- Salva e carrega topologias por arquivo.

## 2) Arquitetura do código-fonte
- `Main`:
  - ponto de entrada da aplicação.
- `visual/NetworkApp`:
  - interface gráfica, eventos de usuário e animações.
- `logic/NetworkLogic`:
  - regras de rede, construção de rotas, validações e persistência.
- `logic/Device`:
  - modelo do dispositivo, posição no mapa e vetor de interfaces.
- `logic/Cable`:
  - modelo do cabo com dispositivos e índices das interfaces conectadas.

### Diagrama simples (caixas + ligações)
```text
[Main]
   |
   v
[NetworkApp (UI + animação)]
   |
   v
[NetworkLogic (regras + algoritmos + persistência)]
   |                  |
   v                  v
[Device]            [Cable]
```

## 3) Uso de arrays (foco principal)

### Arrays de domínio (dados da rede)
- `Device[] devices`
  - armazena todos os dispositivos cadastrados.
  - controle manual por índice + `deviceCount`.
- `Cable[] cables`
  - armazena todos os enlaces físicos.
  - cada cabo guarda `(fromId, fromInterfaceIndex, toId, toInterfaceIndex)`.

### Arrays de interfaces por dispositivo
- `String[] interfaces` em `Device` (5 posições).
- Cada posição representa uma interface lógica (`eth0..eth4`, `if0..if4` etc.).
- Regras com arrays:
  - procurar interface livre (`firstAvailableInterface`);
  - verificar se interface já está ocupada (`isInterfaceInUse`);
  - bloquear criação de cabo quando todas as interfaces estão ocupadas.

### Arrays algorítmicos (roteamento)
- `boolean[] visited`: marca os nós visitados.
- `int[] queue`: fila da busca em largura (BFS) com ponteiros `head` e `tail`.
- `int[] parentByIndex`: guarda predecessor para reconstruir caminho.
- `int[] path`: rota final de IDs.

### Arrays visuais
- `Circle[] packetCircles`: objetos gráficos dos pacotes.
- `Animation[] packetAnimations`: controle das animações em execução.

### Operações manuais clássicas aplicadas
- inserção em vetor;
- remoção com deslocamento de elementos;
- realocação manual de array (`growStringArray`);
- corte de array (`trimStringArray`);
- ordenação manual (`sortNames` com bubble sort);
- busca linear com `for`.

## 4) Algoritmos principais

### 4.1 BFS para descobrir conectividade (`findPath`)
- Entrada: `fromId`, `toId`.
- Estrutura:
  1. inicializa `visited[]`, `queue[]`, `parentByIndex[]`;
  2. enfileira origem;
  3. percorre cabos para expandir vizinhos;
  4. ao alcançar destino, reconstrói o caminho.
- Saída: `int[]` com sequência dos dispositivos da rota.

### 4.2 Reconstrução da rota (`buildPath`)
- Usa `parentByIndex[]` para voltar do destino à origem.
- Monta em ordem reversa e depois inverte para produzir rota correta.

### 4.3 Regra de roteamento (`calculateRoute`)
- Valida origem e destino.
- Se redes iguais: tenta caminho direto por topologia.
- Se redes diferentes:
  - precisa existir rota;
  - a rota precisa conter pelo menos um dispositivo do tipo `roteador`.
- Retorna erro descritivo se a regra não for atendida.

## 5) Simulação de “pilha/fila” de pacotes no roteador
- Implementação atual é uma **fila simulada visualmente** (didática), não pilha LIFO real.
- Comportamento:
  - pacote chega ao roteador;
  - aguarda por um tempo curto (simulando fila de processamento);
  - segue para próximo salto.
- O atraso depende do índice do pacote para criar efeito de enfileiramento.
- Isso demonstra claramente:
  - chegada;
  - espera;
  - repasse por interface apropriada.

## 6) Fluxo da transmissão no código
1. Usuário escolhe origem, destino e quantidade.
2. Sistema calcula rota (`int[] route`).
3. Para cada pacote:
   - cria timeline;
   - percorre salto a salto da rota;
   - aplica espera no roteador.
4. Status acompanha progresso:
   - `recebidos/total`;
   - interface de envio e interface de recepção.
5. Ao final:
   - mostra confirmação de conclusão.

## 7) Persistência de redes
- Formato: `networks/*.net`.
- Bloco `DEVICES`:
  - id, tipo, nome, ip, rede, prefixo de interface, posição, fixo.
- Bloco `CABLES`:
  - fromId, fromInterfaceIndex, toId, toInterfaceIndex.
- Carregamento mantém compatibilidade com formato antigo simples.

## 8) Pontos de código para comentar na apresentação
- `NetworkLogic.calculateRoute(...)`
- `NetworkLogic.findPath(...)`
- `NetworkLogic.buildPath(...)`
- `NetworkLogic.isInterfaceInUse(...)` e `firstAvailableInterface(...)`
- `NetworkApp.animatePackets(...)`
- `NetworkApp.showConnectCableDialog(...)`
- `NetworkLogic.saveNetwork(...)` e `loadNetwork(...)`
