package com.modsen.passengerservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityUtil {
    public String ID_KEY = "id";
    public String USERNAME_KEY = "username";
    public String EMAIL_KEY = "email";
    public String PHONE_KEY = "phone";
    public String NAME_KEY = "name";
    public String SURNAME_KEY = "surname";
    public String ACTUATOR_PATH = "/actuator/**";
    public String RESOURCE = "roles";
    public String RESOURCE_ACCESS = "resource_access";
    public String ROLE_PREFIX = "ROLE_";
    public String UUID_KEY = "sub";
    public String USERNAME_KEY_TOKEN = "preferred_username";
    public String NAME_KEY_TOKEN = "given_name";
    public String SURNAME_KEY_TOKEN = "family_name";
}
