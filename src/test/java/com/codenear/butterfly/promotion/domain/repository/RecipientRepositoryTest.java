package com.codenear.butterfly.promotion.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codenear.butterfly.global.config.QuerydslConfig;
import com.codenear.butterfly.promotion.domain.Recipient;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
class RecipientRepositoryTest {

    @Autowired
    private RecipientRepository recipientRepository;

    @ParameterizedTest(name = "저장된 번호: {0}, 입력한 번호: {1}, 예상 결과: {2}")
    @CsvSource({
            "01012345678, 01012345678, true",
            "01012345678, 01056781234, false"
    })
    void 중복된_전화번호_여부에_따라_BOOLEAN를_판단한다(String storedPhoneNumber, String inputPhoneNumber, boolean expected) {
        // given
        saveRecipient(storedPhoneNumber);

        // when
        boolean result = recipientRepository.existsByPhoneNumber(inputPhoneNumber);

        // then
        assertThat(result)
                .isEqualTo(expected);
    }

    private void saveRecipient(String phoneNumber) {
        Recipient recipient = Recipient.builder()
                .nickname("TEST")
                .phoneNumber(phoneNumber)
                .build();
        recipientRepository.save(recipient);
    }
}