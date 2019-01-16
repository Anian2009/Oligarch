package com.example.demo.security.auth;

import com.example.demo.domain.Users;
import com.example.demo.repository.UsersRepository;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.CommonProfile;

public class TokenAuthenticator implements Authenticator<TokenCredentials> {

    private final UsersRepository usersRepository;

    public TokenAuthenticator(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public void validate(TokenCredentials credentials, WebContext context) {

        Users user = usersRepository.findByToken(credentials.getToken());



//        System.out.println("Token = "+credentials.getToken());
//        System.out.println("User="+user.getName());
//        System.out.println("UserRole="+user.getUser_role());

        if (user != null) {
            CommonProfile profile = new CommonProfile();
            profile.addRole(user.getUser_role());
            credentials.setUserProfile(profile);
        }
    }
}
