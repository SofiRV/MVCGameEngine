package gamerules;

import java.util.List;

import engine.actions.ActionDTO;
import engine.actions.ActionType;
import engine.controller.ports.ActionsGenerator;
import engine.events.domain.ports.BodyRefDTO;
import engine.events.domain.ports.DomainEventType;
import engine.events.domain.ports.eventtype.CollisionEvent;
import engine.events.domain.ports.eventtype.DomainEvent;
import engine.events.domain.ports.eventtype.EmitEvent;
import engine.events.domain.ports.eventtype.LifeOver;
import engine.events.domain.ports.eventtype.LimitEvent;
import engine.model.bodies.ports.BodyType;

public class DeadInLimitsPlayerImmunity implements ActionsGenerator {

    // *** INTERFACE IMPLEMENTATIONS ***

    @Override //
    public void provideActions(List<DomainEvent> domainEvents, List<ActionDTO> actions) {
        if (domainEvents != null) {
            for (DomainEvent event : domainEvents) {
                this.applyGameRules(event, actions);
            }
        }
    }

    // *** PRIVATE ***

    private void applyGameRules(DomainEvent event, List<ActionDTO> actions) {
        switch (event) {

            case LimitEvent limitEvent -> {
                ActionType action = ActionType.DIE;
                if (limitEvent.primaryBodyRef.type() == BodyType.PLAYER)
                    action = ActionType.NO_MOVE;

                actions.add(new ActionDTO(
                        limitEvent.primaryBodyRef.id(), limitEvent.primaryBodyRef.type(),
                        action, event));
                break;
            }

            case LifeOver lifeOver ->
                actions.add(new ActionDTO(
                        lifeOver.primaryBodyRef.id(), lifeOver.primaryBodyRef.type(),
                        ActionType.DIE, event));

            case EmitEvent emitEvent -> {
                if (emitEvent.type == DomainEventType.EMIT_REQUESTED) {
                    actions.add(new ActionDTO(
                            emitEvent.primaryBodyRef.id(),
                            emitEvent.primaryBodyRef.type(),
                            ActionType.SPAWN_BODY,
                            event));

                } else {
                    actions.add(new ActionDTO(
                            emitEvent.primaryBodyRef.id(),
                            emitEvent.primaryBodyRef.type(),
                            ActionType.SPAWN_PROJECTILE,
                            event));
                }
            }

            case CollisionEvent collisionEvent -> {
                this.resolveCollision(collisionEvent, actions);
            }
            default -> {
                // No action for unhandled event types
            }
        }
    }

    private void resolveCollision(CollisionEvent event, List<ActionDTO> actions) {
    BodyType primaryType = event.primaryBodyRef.type();
    BodyType secondaryType = event.secondaryBodyRef.type();

    if (event.payload.haveImmunity) {
        return;
    }

    // PLAYER (gato) y DYNAMIC (ratón)
    boolean isPlayerAndMouseCollision =
        (primaryType == BodyType.PLAYER && secondaryType == BodyType.DYNAMIC) ||
        (primaryType == BodyType.DYNAMIC && secondaryType == BodyType.PLAYER);

    if (isPlayerAndMouseCollision) {
        BodyRefDTO player = primaryType == BodyType.PLAYER ? event.primaryBodyRef : event.secondaryBodyRef;
        BodyRefDTO mouse  = primaryType == BodyType.DYNAMIC ? event.primaryBodyRef : event.secondaryBodyRef;

        // Acción de daño fijo de 10 para el jugador (gato)
        actions.add(new ActionDTO(
            player.id(), player.type(),
            ActionType.TAKE_DAMAGE,
            new engine.events.domain.ports.eventtype.DamageEvent(
                player,
                new engine.events.domain.ports.payloads.DamagePayload(10.0)
            )
        ));

        // Acción de "morir" para el ratón
        actions.add(new ActionDTO(
            mouse.id(), mouse.type(),
            ActionType.DIE, event
        ));
        return;
    }

    // Lógica original para otras colisiones
    boolean primaryDie   = primaryType   != BodyType.GRAVITY && primaryType   != BodyType.PLAYER;
    boolean secondaryDie = secondaryType != BodyType.GRAVITY && secondaryType != BodyType.PLAYER;

    BodyRefDTO player = primaryType == BodyType.PLAYER ? event.primaryBodyRef
                   : secondaryType == BodyType.PLAYER ? event.secondaryBodyRef : null;

    if (player != null)
        actions.add(new ActionDTO(player.id(), player.type(), ActionType.NO_MOVE, event));

    if (primaryDie)
        actions.add(new ActionDTO(
                event.primaryBodyRef.id(), event.primaryBodyRef.type(), ActionType.DIE, event));

    if (secondaryDie)
        actions.add(new ActionDTO(
                event.secondaryBodyRef.id(), event.secondaryBodyRef.type(), ActionType.DIE, event));
}
}
