package nextstep.app.domain;


import java.util.Optional;

public interface OAuth2AuthorizationRecordRepository {
    Optional<OAuth2AuthorizationRecord> findByState(String state);

    void save(OAuth2AuthorizationRecord oAuth2AuthorizationRecord);

    void deleteOne(OAuth2AuthorizationRecord oAuth2AuthorizationRecord);
}
