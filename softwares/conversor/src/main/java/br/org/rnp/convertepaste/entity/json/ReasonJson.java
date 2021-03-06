package br.org.rnp.convertepaste.entity.json;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import br.org.rnp.convertepaste.entity.enums.ReasonType;
import br.org.rnp.convertepaste.entity.enums.Status;

/**
 * Entity implementation class for Entity: reason This reason can be used to
 * accept or regect a paste. For instance, may be a regular expression or
 * language.
 * 
 * @author Felipe
 */
public class ReasonJson implements Serializable, Comparable<ReasonJson> {
    private static final long serialVersionUID = -5102447099993373728L;
    @Column(nullable = false, length = 30, unique = true)
    private String reason;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReasonType type;

    public ReasonJson(String reason, ReasonType reasonType, Status reasonStatus) {
        super();
        this.reason = reason;
        this.type = reasonType;
        this.status = reasonStatus;
    }

    public ReasonJson() {
        super();
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason
     *            the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @return the type
     */
    public ReasonType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(ReasonType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((reason == null) ? 0 : reason.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ReasonJson)) {
            return false;
        }
        ReasonJson other = (ReasonJson) obj;
        if (reason == null) {
            if (other.reason != null) {
                return false;
            }
        } else if (!reason.equals(other.reason)) {
            return false;
        }
        if (status != other.status) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Reason: ");
        builder.append(status);
        builder.append(", ");
        builder.append(type);
        builder.append(", ");
        builder.append(reason);
        return builder.toString();
    }

    @Override
    public int compareTo(ReasonJson o) {
        if (o == null) {
            throw new NullPointerException("Can't compareTo a null instance of Reason.class");
        }
        return getReason().compareTo(o.getReason());
    }
}