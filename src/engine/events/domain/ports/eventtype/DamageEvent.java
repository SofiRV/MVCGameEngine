package engine.events.domain.ports.eventtype;

import engine.events.domain.core.AbstractDomainEvent;
import engine.events.domain.ports.BodyRefDTO;
import engine.events.domain.ports.DomainEventType;
import engine.events.domain.ports.payloads.DamagePayload;

public final class DamageEvent extends AbstractDomainEvent<DamagePayload> implements DomainEvent {

    public DamageEvent(BodyRefDTO bodyRef, DamagePayload payload) {
        super(DomainEventType.DAMAGE_RECEIVED, bodyRef, null, payload); // si solo hay 1 bodyRef, secondary es null
    }
    public DamagePayload getPayload() { return payload; }
}