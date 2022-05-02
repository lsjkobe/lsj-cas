package org.apereo.cas.config.custom.auth.configuration.flow.lsjtest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;

@EqualsAndHashCode(callSuper = true)
@Data
public class LsjTestCredential extends UsernamePasswordCredential {

    private String captcha;

}
