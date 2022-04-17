package org.apereo.cas.config.custom.auth.credential;

import lombok.Getter;
import org.apereo.cas.authentication.Credential;

@Getter
public class MobileIdCredential implements Credential {

    private final String phoneNumber;

    private final String validateCode;

    public MobileIdCredential(String phoneNumber, String validateCode) {
        this.phoneNumber = phoneNumber;
        this.validateCode = validateCode;
    }

    /**
     * Gets a credential identifier that is safe to record for logging, auditing, or presentation to the user.
     * In most cases this has a natural meaning for most credential types (e.g. username, certificate DN), while
     * for others it may be awkward to construct a meaningful identifier. In any case credentials require some means
     * of identification for a number of cases and implementers should make a best effort to satisfy that need.
     *
     * @return Non-null credential identifier. Implementers should return {@link #UNKNOWN_ID} for cases where an ID
     * is not readily available or meaningful.
     */
    @Override
    public String getId() {
        return phoneNumber;
    }
}
