package edu.wsu.eecs.gfc.core;

import java.util.Objects;

/**
 * Creates a preprocessed rdf triple having Subject, Predicate, Object and Truth value
 */
final class PreProcessedTriple {
    private final String subject;
    private final String object;
    private final String predicate;
    private final String truthValue;

    PreProcessedTriple(String subject, String object, String predicate, String truthValue) {
        this.subject = subject;
        this.object = object;
        this.predicate = predicate;
        this.truthValue = truthValue;
    }

    public String subject() {
        return subject;
    }

    public String object() {
        return object;
    }

    public String predicate() {
        return predicate;
    }

    public String truthValue() {
        return truthValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        PreProcessedTriple that = (PreProcessedTriple) obj;
        return Objects.equals(this.subject, that.subject) &&
                Objects.equals(this.object, that.object) &&
                Objects.equals(this.predicate, that.predicate) &&
                Objects.equals(this.truthValue, that.truthValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, object, predicate, truthValue);
    }

    @Override
    public String toString() {
        return "PreProcessedTriple[" +
                "subject=" + subject + ", " +
                "object=" + object + ", " +
                "predicate=" + predicate + ", " +
                "truthValue=" + truthValue + ']';
    }

}
