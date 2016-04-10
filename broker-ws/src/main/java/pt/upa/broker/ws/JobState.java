package pt.upa.broker.ws;

public enum JobState {

	REQUESTED,
    BUDGETED,
    FAILED,
    BOOKED,
    HEADING,
    ONGOING,
    COMPLETED;

    public String value() {
        return name();
    }

    public static JobState fromValue(String v) {
        return valueOf(v);
    }

}
