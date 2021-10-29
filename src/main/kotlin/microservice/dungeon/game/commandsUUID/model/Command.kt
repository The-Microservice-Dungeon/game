package microservice.dungeon.game.commandsUUID.model

import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "commands")
data class Command(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "command_id")
    val id: UUID = UUID.randomUUID(),

    @get: NotBlank
    val commandBody: String = "",

    @get: NotBlank
    val commandType: String = "",

    @get: NotBlank
    val item: String = "",

    @get: NotBlank
    val playerId: UUID,

    @get: NotBlank
    val robotId: UUID,
)