package com.codenear.butterfly.support.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenear.butterfly.support.domain.FAQ;
import com.codenear.butterfly.support.domain.dto.FAQResponse;
import com.codenear.butterfly.support.domain.repositroy.FAQRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FAQServiceTest {

    @Mock
    private FAQRepository faqRepository;

    @InjectMocks
    private FAQService faqService;

    private FAQ test1;
    private FAQ test2;

    @BeforeEach
    void setup() {
        test1 = createFAQ("테스트 질문 1", "테스트 답변 1", true);
        test2 = createFAQ("테스트 질문 2", "테스트 답변 2", true);
    }

    @Test
    void 활성화된_FAQ_목록만_반환한다() {
        // given
        List<FAQ> faqs = List.of(test1, test2);
        when(faqRepository.findAll()).thenReturn(faqs);

        // when
        List<FAQResponse> result = faqService.getFAQs();

        // then
        assertThat(result)
                .hasSize(faqs.size());

        assertThat(result.get(0).contents().question()).isEqualTo(test1.getQuestion());
        assertThat(result.get(0).contents().answer()).isEqualTo(test1.getAnswer());
        assertThat(result.get(1).contents().question()).isEqualTo(test2.getQuestion());
        assertThat(result.get(1).contents().answer()).isEqualTo(test2.getAnswer());

        verify(faqRepository).findAll();
    }

    @Test
    void 비활성_FAQ를_제외하고_반환된다() {
        // given
        test2 = createFAQ("테스트 질문 2", "테스트 답변 2", false);
        List<FAQ> faqs = List.of(test1, test2);

        when(faqRepository.findAll()).thenReturn(faqs);

        // when
        List<FAQResponse> result = faqService.getFAQs();

        // then
        assertThat(result)
                .hasSize(1);

        assertThat(result.get(0).contents().question()).isEqualTo(test1.getQuestion());
        assertThat(result.get(0).contents().answer()).isEqualTo(test1.getAnswer());

        verify(faqRepository).findAll();
    }

    private FAQ createFAQ(String question, String answer, boolean status) {
        return FAQ.builder()
                .question(question)
                .answer(answer)
                .status(status)
                .build();
    }
}