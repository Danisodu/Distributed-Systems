package pt.upa.transporter.ws;

public enum JobState {

	PROPOSED,
    REJECTED,
    ACCEPTED,
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
