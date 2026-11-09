package equipment_service.example.demo.db.equipment;

public enum ReservationStatus {
    CANCELLED,
    WAIT_PAYMENT,
    PAID,
    DELIVERED,
    FAILED;

    public boolean toTransactionTo(ReservationStatus newStatus){
        return switch (this){
            case WAIT_PAYMENT ->
                newStatus == PAID ||
                newStatus == FAILED ||
                newStatus == CANCELLED;
            case PAID ->
                newStatus == DELIVERED;
            case DELIVERED, CANCELLED, FAILED -> false;
        };
    }
}
