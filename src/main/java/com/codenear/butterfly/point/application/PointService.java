package com.codenear.butterfly.point.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.point.domain.Point;
import com.codenear.butterfly.point.domain.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;

    public Point loadPointByMemberId(Long memberId) {
        return pointRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.SERVER_ERROR, null));
    }
}
