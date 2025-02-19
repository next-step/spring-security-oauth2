package nextstep.app.infrastructure;

import nextstep.app.domain.OAuth2AuthorizationRecord;
import nextstep.app.domain.OAuth2AuthorizationRecordRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InmemoryOAuth2AuthorizationRecordRepository implements OAuth2AuthorizationRecordRepository {

    private static final Map<String, OAuth2AuthorizationRecord> oAuth2AuthorizationRecords = new HashMap<>();

    @Override
    public Optional<OAuth2AuthorizationRecord> findByState(String state) {
        return Optional.ofNullable(oAuth2AuthorizationRecords.get(state));
    }

    @Override
    public void save(OAuth2AuthorizationRecord oAuth2AuthorizationRecord) {
        oAuth2AuthorizationRecords.put(oAuth2AuthorizationRecord.state(), oAuth2AuthorizationRecord);
    }

    @Override
    public void deleteOne(OAuth2AuthorizationRecord oAuth2AuthorizationRecord) {
        oAuth2AuthorizationRecords.remove(oAuth2AuthorizationRecord.state());
    }
}
