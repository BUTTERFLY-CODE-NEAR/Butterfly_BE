package com.codenear.butterfly.admin.support.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenear.butterfly.admin.exception.AdminException;
import com.codenear.butterfly.admin.support.domain.dto.FAQRequest;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.support.domain.FAQ;
import com.codenear.butterfly.support.domain.FAQRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

    @Test
    void FAQ_전체를_반환한다() {
        // given
        FAQ test1 = createFAQ("테스트 질문 1", "테스트 답변 1");
        FAQ test2 = createFAQ("테스트 질문 2", "테스트 답변 2");
        List<FAQ> faqs = Arrays.asList(test1, test2);

        when(faqRepository.findAll()).thenReturn(faqs);

        // when
        List<FAQ> result = faqService.getFAQList();

        // then
        assertThat(result)
                .hasSize(faqs.size())
                .contains(test1, test2);

        verify(faqRepository).findAll();
    }

    @Test
    void FAQ를_반환한다() {
        // given
        Long id = 1L;
        FAQ test = createFAQ("테스트 질문", "테스트 답변");
        when(faqRepository.findById(id)).thenReturn(Optional.of(test));

        // when
        FAQ result = faqService.getFAQ(id);

        // then
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(test);

        verify(faqRepository).findById(id);
    }

    @Test
    void 존재하지_않는_FAQ를_검색하면_예외가_발생한다() {
        // given
        Long id = 1L;

        doThrow(new AdminException(ErrorCode.SERVER_ERROR, null))
                .when(faqRepository).findById(id);

        // when & then
        assertThatThrownBy(() -> faqService.getFAQ(id))
                .isInstanceOf(AdminException.class)
                .hasMessage(ErrorCode.SERVER_ERROR.getMessage());
    }

    @Test
    void FAQ를_생성한다() {
        // given
        FAQRequest request = new FAQRequest("테스트 질문", "테스트 답변", false);
        FAQ faq = createFAQ(request.question(), request.answer());

        when(faqRepository.save(any(FAQ.class))).thenReturn(faq);

        // when
        FAQ result = faqService.createFAQ(request);

        // then
        assertThat(result)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(faq);

        verify(faqRepository).save(any(FAQ.class));
    }

    @Test
    void FAQ를_수정한다() {
        // given
        Long id = 1L;
        FAQRequest request = new FAQRequest("수정된 질문", "수정된 답변", true);
        FAQ existingFAQ = createFAQ("기존 질문", "기존 답변");

        when(faqRepository.findById(id)).thenReturn(Optional.of(existingFAQ));

        // when
        faqService.updateFAQ(id, request);

        // then
        assertThat(existingFAQ.getQuestion()).isEqualTo(request.question());
        assertThat(existingFAQ.getAnswer()).isEqualTo(request.answer());
        assertThat(existingFAQ.isStatus()).isEqualTo(request.status());

        verify(faqRepository).findById(id);
    }

    @Test
    void FAQ를_삭제한다() {
        // given
        Long id = 1L;

        // when
        faqService.deleteFAQ(id);

        // then
        verify(faqRepository).deleteById(id);
    }

    @Test
    void 존재하지_않는_FAQ를_삭제하면_예외가_발생한다() {
        // given
        Long id = 1L;

        doThrow(new AdminException(ErrorCode.SERVER_ERROR, null))
                .when(faqRepository).deleteById(id);

        // when & then
        assertThatThrownBy(() -> faqService.deleteFAQ(id))
                .isInstanceOf(AdminException.class)
                .hasMessage(ErrorCode.SERVER_ERROR.getMessage());
    }

    private FAQ createFAQ(String question, String answer) {
        return FAQ.builder()
                .question(question)
                .answer(answer)
                .build();
    }
}