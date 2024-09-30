package com.codenear.butterfly.member.domain.repository.nickname;

import com.codenear.butterfly.member.domain.ForbiddenWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForbiddenWordRepository extends JpaRepository<ForbiddenWord, Long> {

}
