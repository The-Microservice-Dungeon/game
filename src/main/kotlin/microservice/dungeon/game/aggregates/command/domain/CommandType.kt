package microservice.dungeon.game.aggregates.command.domain

enum class CommandType {
    BLOCKING,
    MINING,
    MOVEMENT,
    BATTLE,
    BUYING,
    SELLING,
    REGENERATE,
    BATTLEITEMUSE,
    REPAIRITEMUSE,
    MOVEITEMUSE;

    companion object {
        @Throws(IllegalArgumentException::class)
        fun getTypeFromString(input: String): CommandType {
            return when (input) {
                "blocking" -> BLOCKING
                "buying" -> BUYING
                "selling" -> SELLING
                "movement" -> MOVEMENT
                "battle" -> BATTLE
                "mining" -> MINING
                "regeneration" -> REGENERATE
                "battleItemUse" -> BATTLEITEMUSE
                "repairItemUse" -> REPAIRITEMUSE
                "moveItemUse" -> MOVEITEMUSE
                else -> {
                    throw IllegalArgumentException("$input is not a valid CommandType.")
                }
            }
        }

        fun getStringFromType(input: CommandType): String {
            return when (input) {
                BLOCKING -> "blocking"
                BUYING -> "buying"
                SELLING -> "selling"
                MOVEMENT -> "movement"
                BATTLE -> "battle"
                MINING -> "mining"
                REGENERATE -> "regeneration"
                BATTLEITEMUSE -> "battleItemUse"
                REPAIRITEMUSE -> "repairItemUse"
                MOVEITEMUSE -> "moveItemUse"
            }
        }
    }
}