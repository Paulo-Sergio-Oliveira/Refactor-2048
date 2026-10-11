Java implementation of 2048 game
====
<img src="2048.png" width="50%" height="50%"/>

Clone em Java/Swing do jogo 2048, refatorado com padrões de projeto GoF (Strategy, Factory
Method, State e Observer) a partir do [projeto original de Konstantin
Bulenkov](https://github.com/bulenkov/2048). O relatório técnico descreve o diagnóstico do
sistema legado e cada refatoração realizada.

## Como executar o jogo

Requer um JDK (Java 8+).

**Via Maven:**
```
mvn package
java -jar target/game2048.jar
```

**Sem Maven, direto com `javac`/`java`:**
```
javac -d out $(find src -name "*.java")
java -cp out com.bulenkov.game2048.Game2048
```

**Via IntelliJ:** abra o projeto e rode a configuração `Game2048` já incluída
(`.idea/runConfigurations`).

Controles: setas do teclado para mover; `ESC` reinicia a partida.

## Como rodar os testes automatizados

Os testes (JUnit 5) cobrem `GameModel`, `Direction`, `TileFactory` e `MatchStatus` — o motor
do jogo, que não depende de Swing/AWT e por isso pode ser testado isoladamente.

```
mvn test
```

Ou pelo IntelliJ: clique com o botão direito na pasta `test/` → **Run 'All Tests'**.

## Estrutura do projeto

- `src/com/bulenkov/game2048/` — código-fonte principal (model, view e padrões de projeto).
- `test/com/bulenkov/game2048/` — testes automatizados (JUnit 5).
- `pom.xml` — build Maven (apenas para compilar/empacotar e resolver a dependência de testes).
