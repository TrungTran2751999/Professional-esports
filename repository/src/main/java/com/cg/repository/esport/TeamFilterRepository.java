package com.cg.repository.esport;

import com.cg.domain.esport.dto.TeamFilter;
import com.cg.domain.esport.entities.TeamTournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public interface TeamFilterRepository extends JpaRepository<TeamTournament, Long>, JpaSpecificationExecutor<TeamTournament> {
    default Page<TeamTournament> findAllByFilters(TeamFilter filter, Pageable pageable) {
        return findAll((root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getKeyWord() != null) {
                Predicate predicateNickName = criteriaBuilder.like(root.get("nickName"), '%' + filter.getKeyWord() + '%');
                Predicate predicateKw = criteriaBuilder.or(predicateNickName);
                predicates.add(predicateKw);
            }
            if(filter.getDeleted() != null){
                Predicate predicateDeleted = criteriaBuilder.equal(root.get("deleted"), filter.getDeleted());
                predicates.add(predicateDeleted);
            }
            if(filter.getCategoryId() != null){
                Predicate predicateDeleted = criteriaBuilder.equal(root.get("category").get("id"), filter.getCategoryId());
                predicates.add(predicateDeleted);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, pageable);

    }
}
