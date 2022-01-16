

## TODO

**Player**
- JPA
- PlayerService Integration Test
- Refactor Events
- Controller
  - Fetch Token

**Round**
- Refactor Events
- RoundService Integration Test
  
**EventStore**
- Integration Test

**Robot**
- JPA
- RobotService Integration Test

**Command**
- JPA
- Check ALL

## Bonus

**Log**
- Separate Log for Controllers
- Separate Log for Events


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

add request string to controller logging \
add game.controller patches