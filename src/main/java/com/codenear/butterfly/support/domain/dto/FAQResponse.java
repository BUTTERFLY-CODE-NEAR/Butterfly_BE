package com.codenear.butterfly.support.domain.dto;

import com.codenear.butterfly.support.domain.FAQ;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "FAQ 리스트 JSON", description = "FAQ 리스트 요청 시 반환되는 응답 JSON 데이터 입니다.")
public record FAQResponse(
        @Schema(description = "FAQ 순번") Long id,
        @Schema(description = "FAQ 내용") Contents contents
){

    @Schema(title = "FAQ 내용 JSON", description = "FAQ 내용과 관련된 응답 데이터 입니다.")
    public record Contents(
            @Schema(description = "Q. 질문") String question,
            @Schema(description = "A. 답변") String answer
    ) {
    }

    public static FAQResponse fromEntity(FAQ faq) {
        Contents contents = new Contents(
                faq.getQuestion(),
                faq.getAnswer()
        );

        return new FAQResponse(
                faq.getId(),
                contents
        );
    }
}
