package com.cg.repository.esport;

import com.cg.domain.esport.dto.CategoryFilter;
import com.cg.domain.esport.entities.Category;
import com.cg.domain.esport.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public interface CategoryFilterRepository extends JpaRepository<Category, Integer>, JpaSpecificationExecutor<Category> {
    default Page<Category> findAllByFilters(CategoryFilter filter, Pageable pageable) {
        return findAll((root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getKeyWord() != null) {
                Predicate predicateNickName = criteriaBuilder.like(root.get("name"), '%' + filter.getKeyWord() + '%');
                Predicate predicateSignal = criteriaBuilder.like(root.get("brief"), '%' + filter.getKeyWord() + '%');
                Predicate predicateKw = criteriaBuilder.or(predicateNickName, predicateSignal);
                predicates.add(predicateKw);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, pageable);

    }
}
