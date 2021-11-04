package microservice.dungeon.game.eventstore.data

import microservice.dungeon.game.aggregates.core.Event
import microservice.dungeon.game.aggregates.eventstore.domain.EventDescriptor

fun compareEvents(eventA: Event, eventB: Event): Boolean {
    if (eventA.getId() != eventB.getId()) return false
    if (eventA.getEventName() != eventB.getEventName()) return false
    if (eventA.getOccurredAt().compareTo(eventB.getOccurredAt()) != 0) return false
    if (eventA.getTopic() != eventB.getTopic()) return false
    if (eventA.serialized() != eventB.serialized()) return false
    return true
}

fun compareEventDescriptorWithEvent(eventDescriptor: EventDescriptor, event: Event): Boolean {
    if (eventDescriptor.getId() != event.getId()) return false
    if (eventDescriptor.getType() != event.getEventName()) return false
    if (eventDescriptor.getOccurredAt().compareTo(event.getOccurredAt()) != 0) return false
    if (eventDescriptor.getContent() != event.serialized()) return false
    return true
}

fun compareEventDescriptors(descriptorA: EventDescriptor, descriptorB: EventDescriptor): Int {
    if (descriptorA.getId() != descriptorB.getId()) return 1
    if (descriptorA.getType() != descriptorB.getType()) return 2
    if (descriptorA.getOccurredAt().compareTo(descriptorB.getOccurredAt()) != 0) return 3
    if (descriptorA.getContent() != descriptorB.getContent()) return 4
    if (descriptorA.getStatus() != descriptorB.getStatus()) return 5
    return 0
}