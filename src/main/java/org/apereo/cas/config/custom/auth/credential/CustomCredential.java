package org.apereo.cas.config.custom.auth.credential;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomCredential extends UsernamePasswordCredential {

    private String captcha;

}
