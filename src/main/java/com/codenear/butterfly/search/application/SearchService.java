package com.codenear.butterfly.search.application;

import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.codenear.butterfly.product.domain.dto.ProductViewDTO;
import com.codenear.butterfly.product.domain.repository.FavoriteRepository;
import com.codenear.butterfly.product.domain.repository.KeywordRepository;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import com.codenear.butterfly.product.util.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.codenear.butterfly.product.application.KeywordService.KEYWORDS_PREFIX;

@Service
@RequiredArgsConstructor
public class SearchService {
    public static final String SEARCH_LOG_KEY_PREFIX = "search_user:";
    public static final int SEARCH_LOG_MAX_SIZE = 5;

    private final RedisTemplate<String, Object> redisTemplate;
    private final FavoriteRepository favoriteRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final KeywordRepository keywordRepository;

    private static String getKey(MemberDTO memberDTO) {
        return SEARCH_LOG_KEY_PREFIX + memberDTO.getId();
    }

    public List<String> getRelatedKeywords(String keyword) {
        Set<Object> keywords = redisTemplate.opsForSet().members(KEYWORDS_PREFIX);
        if (keywords == null) {
            return null;
        }

        return keywords.stream()
                .map(Object::toString)
                .filter(key -> key.contains(keyword))
                .collect(Collectors.toList());
    }

    /**
     * keyword에 포함되는 상품을 검색하고, 사용자 검색로그에 저장한다.
     *
     * @param keyword   검색어
     * @param memberDTO 사용자
     * @return 상품 리스트
     */
    @Transactional(readOnly = true)
    public List<ProductViewDTO> search(String keyword, MemberDTO memberDTO) {
        List<String> relatedKeywords = getRelatedKeywords(keyword);
        List<Long> favoriteProductIds = favoriteRepository.findAllProductIdByMemberId(memberDTO.getId());
        Set<Long> favoriteProductIdSet = new HashSet<>(favoriteProductIds);

        List<ProductViewDTO> searchProduct = relatedKeywords.stream()
                .flatMap(relateKeyword -> {
                    List<ProductInventory> products = findKeywordByProductList(relateKeyword);

                    return products.stream()
                            .sorted((p1, p2) -> Boolean.compare(p1.isSoldOut(), p2.isSoldOut()))
                            .map(product -> ProductMapper.toProductViewDTO(product,
                                    favoriteProductIdSet.contains(product.getId()),
                                    product.calculateGauge()));
                })
                .distinct()
                .toList();

        addSearchLog(keyword, memberDTO);
        return searchProduct;
    }

    public List<Object> getSearchList(MemberDTO memberDTO) {
        return redisTemplate.opsForList().range(getKey(memberDTO), 0, -1);
    }

    public void deleteAllSearchLog(MemberDTO memberDTO) {
        redisTemplate.delete(getKey(memberDTO));
    }

    public void deleteSearchLog(String keyword, MemberDTO memberDTO) {
        String key = getKey(memberDTO);
        redisTemplate.opsForList().remove(key, 0, keyword);
    }

    private void addSearchLog(String keyword, MemberDTO memberDTO) {
        String key = getKey(memberDTO);

        if (redisTemplate.opsForList().indexOf(key, keyword) != null)
            redisTemplate.opsForList().remove(key, 0, keyword);

        redisTemplate.opsForList().leftPush(key, keyword);

        Long size = redisTemplate.opsForList().size(key);

        if (size != null && size > SEARCH_LOG_MAX_SIZE)
            redisTemplate.opsForList().rightPop(key);
    }

    private List<ProductInventory> findKeywordByProductList(String keyword) {
        return keywordRepository.findAllProductIdByKeyword(keyword)
                .stream()
                .map(productInventoryRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .toList();
    }
}
