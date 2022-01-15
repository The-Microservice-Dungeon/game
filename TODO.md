

## TODO

**Game**
- Controller
- Eventing

**Round**
- RoundService Integration Test

**Player**
- JPA
- PlayerService Integration Test
- Controller
  - Fetch Token

**Robot**
- JPA
- RobotService Integration Test

**Command**
- JPA
- Check ALL

**EventPublisher**
- CronJob 

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