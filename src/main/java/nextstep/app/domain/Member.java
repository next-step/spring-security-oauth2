package nextstep.app.domain;

import java.util.Set;
import java.util.UUID;

public class Member {
    private final String email;
    private final String password;
    private final String name;
    private final String imageUrl;
    private final Set<String> roles;

    public Member(String email, String password, String name, String imageUrl, Set<String> roles) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.imageUrl = imageUrl;
        this.roles = roles;
    }

    public static Member oAuthMember(String email, String name) {
        String randomPassword = UUID.randomUUID().toString();
        return new Member(email, randomPassword, name, "", Set.of("MEMBER"));
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public boolean matchPassword(String password) {
        return this.password.equals(password);
    }
}
