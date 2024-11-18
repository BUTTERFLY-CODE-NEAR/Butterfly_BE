package com.codenear.butterfly.support.domain.dto;

import com.codenear.butterfly.support.domain.FAQ;

public record FAQResponse(
        Long id,
        Contents contents
){

    public record Contents(
            String question,
            String answer
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
