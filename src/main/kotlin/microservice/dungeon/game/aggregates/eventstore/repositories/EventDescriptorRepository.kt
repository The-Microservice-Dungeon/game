package microservice.dungeon.game.aggregates.eventstore.repositories

import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptor
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptorStatus
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface EventDescriptorRepository: CrudRepository<EventDescriptor, UUID> {

    fun findByStatus(status: EventDescriptorStatus): List<EventDescriptor>

    @Modifying
    @Query("update EventDescriptor e set e.status='PUBLISHED' where e.id in :eventIds")
    fun markAsPublished(@Param("eventIds") eventIds: List<UUID> )

    @Modifying
    @Query("DELETE FROM EventDescriptor e WHERE e.id in :eventIds")
    fun deletePublished(@Param("eventIds") eventIds: List<UUID>)
}