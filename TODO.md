

## TODO

**Robot**
- RobotService Integration Test
- RobotConsumer

**Command**
- JPA



## Bonus
**Kafka**
- Integration Test

**EventStore**
- Integration Test

**Game**
- Patch RoundTime
- Patch MaxRounds

**Command**
- Check ALL

**Log**
- Separate Log for Controllers
- Separate Log for Events

**JPA**
- Indexes


## API Changes

**Game**
- **POST /games**
  - 406 removed
  - 403 when action not allowed
- **POST /games/{gameId}/gameCommands/start**
  - 406 removed
  - 404 when game not found
  - 403 when action not allowed
- **POST /games/{gameId}/gameCommands/end**
  - 406 removed
  - 404 when game not found
  - 403 when action not allowed
- **PUT /games/{gameId}/players/{playerToken}**
  - 406 removed
  - 404 when player or game not found
  - 403 when action not allowed
- **GET /games/{gameId}/time**
  - 404 when game not found
- **GET /games**
  - whole responseBody

PlayerStatusEvent
  - _userId to playerId_
  - _lobbyaction removed_

**Player**
- **POST /players**
  - 409 removed
  - 403 when player already exists
- **GET /players?name=&mail=
  - 200
  - 404
