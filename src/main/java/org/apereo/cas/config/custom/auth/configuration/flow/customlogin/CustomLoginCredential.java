package org.apereo.cas.config.custom.auth.configuration.flow.customlogin;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomLoginCredential extends UsernamePasswordCredential {
}
