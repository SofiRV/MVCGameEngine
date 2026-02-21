package engine.events.domain.ports.payloads;

public final class DamagePayload implements DomainEventPayload {
    private final double amount;
    public DamagePayload(double amount) { this.amount = amount; }
    public double getAmount() { return amount; }
}