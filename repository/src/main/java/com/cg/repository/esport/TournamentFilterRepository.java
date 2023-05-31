package com.cg.repository.esport;

import com.cg.domain.esport.dto.TournamentFilter;
import com.cg.domain.esport.entities.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public interface TournamentFilterRepository extends JpaRepository<Tournament, Integer>, JpaSpecificationExecutor<Tournament> {
    default Page<Tournament> findAllByFilters(TournamentFilter filter, Pageable pageable) {
        return findAll((root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getKeyWord() != null) {
                Predicate predicateNickName = criteriaBuilder.like(root.get("name"), '%' + filter.getKeyWord() + '%');
                Predicate predicateKw = criteriaBuilder.or(predicateNickName);
                predicates.add(predicateKw);
            }
            if(filter.getDeleted() != null){
                Predicate predicateDelete = criteriaBuilder.equal(root.get("deleted"), filter.getDeleted());
                predicates.add(predicateDelete);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, pageable);

    }
}
