package nextstep.security.oauth2.registration;

import nextstep.fixture.TestClientFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;


class InMemoryClientRegistrationRepositoryTest {

    @Test
    @DisplayName("findByRegistrationIds 는 RegistrationId에 맞는 ClientRegistration 를 찾는다")
    void findByRegistrationId() {
        // given
        ClientRegistration mockRegistration = TestClientFixture.create("mock");
        ClientRegistration twoRegistration = TestClientFixture.create("mock2");
        InMemoryClientRegistrationRepository repository = new InMemoryClientRegistrationRepository(List.of(mockRegistration, twoRegistration));

        // when
        final ClientRegistration foundRegistration = repository.findByRegistrationId(twoRegistration.getRegistrationId());

        //then
        assertSoftly(it -> {
            it.assertThat(foundRegistration.getRegistrationId()).isEqualTo(twoRegistration.getRegistrationId());
            it.assertThat(foundRegistration.getClientId()).isEqualTo(twoRegistration.getClientId());
        });
    }

    @Test
    @DisplayName("InMemoryClientRegistrationRepository 에서 ClientRegistration를 찾지 못하면 null을 반환한다")
    void findByRegistrationId_notFound() {
        ClientRegistration registration = TestClientFixture.create("mock");
        InMemoryClientRegistrationRepository repository = new InMemoryClientRegistrationRepository(List.of(registration));

        final ClientRegistration foundRegistration = repository.findByRegistrationId("nonexistent");

        assertThat(foundRegistration).isNull();
    }
}
