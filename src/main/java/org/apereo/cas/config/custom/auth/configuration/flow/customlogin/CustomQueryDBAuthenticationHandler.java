package org.apereo.cas.config.custom.auth.configuration.flow.customlogin;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apereo.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.CoreAuthenticationUtils;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.model.support.jdbc.authn.QueryJdbcAuthenticationProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.util.CollectionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.sql.DataSource;
import java.security.GeneralSecurityException;
import java.util.*;

@Slf4j
public class CustomQueryDBAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {

    public static final String JPA_PROPERTIES_IS_ALL_ATTRIBUTE = "isAllAttribute";

    public static final String JPA_PROPERTIES_CUSTOM_NAME = "customName";

    public static final String JPA_PROPERTIES_CUSTOM_ORDER = "customOrder";

    private final QueryJdbcAuthenticationProperties properties;

    protected CustomQueryDBAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, int order,
                                                 final QueryJdbcAuthenticationProperties properties, DataSource dataSource) {
        super(name, servicesManager, principalFactory, order, dataSource);
        this.properties = properties;
    }

    @Override
    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credential, String originalPassword) throws GeneralSecurityException, PreventedException {
        val username = credential.getUsername();
        val password = credential.getPassword();
        Map<String, List<Object>> attributes;
        try {
            val dbFields = query(credential);
            if (dbFields.containsKey(properties.getFieldPassword())) {
                onContainsPwdKey(dbFields, originalPassword, password);
            } else {
                onNoContainsPwdKey(dbFields, username);
            }
            onOtherValidate(dbFields);
            attributes = handleAttributes(dbFields);
        } catch (final IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() == 0) {
                throw new AccountNotFoundException(username + " not found with SQL query");
            }
            throw new FailedLoginException("Multiple records found for " + username);
        } catch (final DataAccessException e) {
            throw new PreventedException(e);
        }
        val principal = this.principalFactory.createPrincipal(username, attributes);
        return createHandlerResult(credential, principal, new ArrayList<>(0));
    }

    private Map<String, List<Object>> handleAttributes(Map<String, Object> dbFields) {
        Map<String, List<Object>> attributes = new HashMap<>();
        boolean isAllAttribute = Boolean.parseBoolean(properties.getProperties().get(JPA_PROPERTIES_IS_ALL_ATTRIBUTE));
        if (isAllAttribute) {
            allDbFieldToAttributes(attributes, dbFields);
        } else {
            dbFieldToAttributes(attributes, dbFields);
        }
        return attributes;
    }

    private void onOtherValidate(Map<String, Object> dbFields) throws AccountDisabledException, AccountPasswordMustChangeException {
        if (StringUtils.isNotBlank(properties.getFieldDisabled()) && dbFields.containsKey(properties.getFieldDisabled())) {
            val dbDisabled = dbFields.get(properties.getFieldDisabled()).toString();
            if (BooleanUtils.toBoolean(dbDisabled) || "1".equals(dbDisabled)) {
                throw new AccountDisabledException("Account has been disabled");
            }
        }
        if (StringUtils.isNotBlank(properties.getFieldExpired()) && dbFields.containsKey(properties.getFieldExpired())) {
            val dbExpired = dbFields.get(properties.getFieldExpired()).toString();
            if (BooleanUtils.toBoolean(dbExpired) || "1".equals(dbExpired)) {
                throw new AccountPasswordMustChangeException("Password has expired");
            }
        }
    }

    private void onContainsPwdKey(Map<String, Object> dbFields, String originalPassword, String password) throws FailedLoginException {
        val dbPassword = (String) dbFields.get(properties.getFieldPassword());
        val originalPasswordMatchFails = StringUtils.isNotBlank(originalPassword) && !matches(originalPassword, dbPassword);
        val originalPasswordEquals = StringUtils.isBlank(originalPassword) && !StringUtils.equals(password, dbPassword);
        if (originalPasswordMatchFails || originalPasswordEquals) {
            throw new FailedLoginException("Password does not match value on record.");
        }
    }

    private void onNoContainsPwdKey(Map<String, Object> dbFields, String username) throws FailedLoginException {
        LOGGER.debug("Password field is not found in the query results. Checking for result count...");
        if (!dbFields.containsKey("total")) {
            throw new FailedLoginException("Missing field 'total' from the query results for " + username);
        }
        val count = dbFields.get("total");
        if (count == null || !NumberUtils.isCreatable(count.toString())) {
            throw new FailedLoginException("Missing field value 'total' from the query results for "
                    + username + " or value not parseable as a number");
        }
        val number = NumberUtils.createNumber(count.toString());
        if (number.longValue() != 1) {
            throw new FailedLoginException("No records found for user " + username);
        }
    }

    private Map<String, Object> query(final UsernamePasswordCredential credential) {
        return getJdbcTemplate().queryForMap(properties.getSql(), credential.getUsername());
    }

    private void dbFieldToAttributes(final Map<String, List<Object>> attributes, final Map<String, Object> dbFields) {
        val setAttributes =
                CoreAuthenticationUtils.transformPrincipalAttributesListIntoMultiMap(properties.getPrincipalAttributeList());
        val principalAttributeMap = CollectionUtils.wrap(setAttributes);
        principalAttributeMap.forEach((key, names) -> {
            val attribute = dbFields.get(key);
            if (attribute != null) {
                LOGGER.debug("Found attribute [{}] from the query results", key);
                val attributeNames = (Collection<String>) names;
                attributeNames.forEach(s -> {
                    LOGGER.debug("Principal attribute [{}] is virtually remapped/renamed to [{}]", key, s);
                    attributes.put(s, CollectionUtils.wrap(attribute.toString()));
                });
            } else {
                LOGGER.warn("Requested attribute [{}] could not be found in the query results", key);
            }
        });
    }

    private void allDbFieldToAttributes(final Map<String, List<Object>> attributes, final Map<String, Object> dbFields) {
        dbFields.forEach((key, attribute) -> {
            if (attribute != null) {
                attributes.put(key, CollectionUtils.wrap(attribute.toString()));
            }
        });
    }

    @Override
    public boolean supports(Credential credential) {
        return credential instanceof CustomLoginCredential;
    }

}
