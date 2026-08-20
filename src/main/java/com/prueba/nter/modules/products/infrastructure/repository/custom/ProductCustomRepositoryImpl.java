package com.prueba.nter.modules.products.infrastructure.repository.custom;

import com.prueba.nter.commons.Constants;
import com.prueba.nter.modules.products.domain.entity.ProductEntity;
import com.prueba.nter.modules.products.infrastructure.dto.input.ProductFilterDto;
import com.prueba.nter.modules.users.domain.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Criteria API based implementation of {@link ProductCustomRepository}.
 */
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ProductEntity> searchByNameAndCategory(String name, String category) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductEntity> query = builder.createQuery(ProductEntity.class);
        Root<ProductEntity> root = query.from(ProductEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.hasText(name)) {
            predicates.add(builder.like(builder.lower(root.get(Constants.FIELD_NAME)), like(name)));
        }
        if (StringUtils.hasText(category)) {
            predicates.add(builder.equal(root.get(Constants.FIELD_CATEGORY), category));
        }
        query.select(root)
                .where(builder.and(predicates.toArray(new Predicate[0])))
                .orderBy(builder.asc(root.get(Constants.FIELD_ID)));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<ProductEntity> searchByPriceLowerThan(BigDecimal price) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductEntity> query = builder.createQuery(ProductEntity.class);
        Root<ProductEntity> root = query.from(ProductEntity.class);
        query.select(root)
                .where(builder.lessThan(root.get(Constants.FIELD_PRICE), price))
                .orderBy(builder.asc(root.get(Constants.FIELD_PRICE)));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<ProductEntity> searchByExpirationDateRange(LocalDate startDate, LocalDate endDate) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductEntity> query = builder.createQuery(ProductEntity.class);
        Root<ProductEntity> root = query.from(ProductEntity.class);
        query.select(root)
                .where(builder.between(root.get(Constants.FIELD_EXPIRATION_DATE), startDate, endDate))
                .orderBy(builder.desc(root.get(Constants.FIELD_ID)));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<ProductEntity> searchByUserEmail(String email, String category, String brand) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductEntity> query = builder.createQuery(ProductEntity.class);
        Root<ProductEntity> root = query.from(ProductEntity.class);
        Join<ProductEntity, UserEntity> user = root.join(Constants.FIELD_USER);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(
                builder.lower(user.get(Constants.FIELD_EMAIL)),
                builder.lower(builder.literal(email))));
        if (StringUtils.hasText(category)) {
            predicates.add(builder.equal(root.get(Constants.FIELD_CATEGORY), category));
        }
        if (StringUtils.hasText(brand)) {
            predicates.add(builder.equal(root.get(Constants.FIELD_BRAND), brand));
        }
        query.select(root)
                .where(builder.and(predicates.toArray(new Predicate[0])))
                .orderBy(builder.asc(root.get(Constants.FIELD_ID)));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<ProductEntity> searchByOldestUsers(int limit) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> userQuery = builder.createQuery(Long.class);
        Root<UserEntity> userRoot = userQuery.from(UserEntity.class);
        userQuery.select(userRoot.get(Constants.FIELD_ID))
                .orderBy(builder.asc(userRoot.get(Constants.FIELD_CREATED_AT)),
                        builder.asc(userRoot.get(Constants.FIELD_ID)));
        List<Long> userIds = entityManager.createQuery(userQuery)
                .setMaxResults(limit)
                .getResultList();
        if (userIds.isEmpty()) {
            return List.of();
        }

        CriteriaQuery<ProductEntity> query = builder.createQuery(ProductEntity.class);
        Root<ProductEntity> root = query.from(ProductEntity.class);
        Join<ProductEntity, UserEntity> user = root.join(Constants.FIELD_USER);
        query.select(root)
                .where(user.get(Constants.FIELD_ID).in(userIds))
                .orderBy(builder.asc(user.get(Constants.FIELD_CREATED_AT)),
                        builder.asc(root.get(Constants.FIELD_ID)));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public Page<ProductEntity> searchAdvanced(ProductFilterDto filter, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<ProductEntity> query = builder.createQuery(ProductEntity.class);
        Root<ProductEntity> root = query.from(ProductEntity.class);
        query.select(root)
                .where(builder.and(buildPredicates(builder, root, filter)))
                .orderBy(buildOrders(builder, root, pageable.getSort()));

        List<ProductEntity> content = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        return new PageImpl<>(content, pageable, count(builder, filter));
    }

    private long count(CriteriaBuilder builder, ProductFilterDto filter) {
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<ProductEntity> countRoot = countQuery.from(ProductEntity.class);
        countQuery.select(builder.count(countRoot))
                .where(builder.and(buildPredicates(builder, countRoot, filter)));
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private Predicate[] buildPredicates(CriteriaBuilder builder, Root<ProductEntity> root, ProductFilterDto filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter == null) {
            return predicates.toArray(new Predicate[0]);
        }
        if (StringUtils.hasText(filter.getName())) {
            predicates.add(builder.like(builder.lower(root.get(Constants.FIELD_NAME)), like(filter.getName())));
        }
        if (StringUtils.hasText(filter.getCategory())) {
            predicates.add(builder.equal(root.get(Constants.FIELD_CATEGORY), filter.getCategory()));
        }
        if (StringUtils.hasText(filter.getBrand())) {
            predicates.add(builder.equal(root.get(Constants.FIELD_BRAND), filter.getBrand()));
        }
        if (filter.getMinPrice() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get(Constants.FIELD_PRICE), filter.getMinPrice()));
        }
        if (filter.getMaxPrice() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get(Constants.FIELD_PRICE), filter.getMaxPrice()));
        }
        if (filter.getStartDate() != null) {
            predicates.add(builder.greaterThanOrEqualTo(
                    root.get(Constants.FIELD_EXPIRATION_DATE), filter.getStartDate()));
        }
        if (filter.getEndDate() != null) {
            predicates.add(builder.lessThanOrEqualTo(
                    root.get(Constants.FIELD_EXPIRATION_DATE), filter.getEndDate()));
        }
        if (filter.getProviderId() != null) {
            predicates.add(builder.equal(
                    root.get(Constants.FIELD_PROVIDER).get(Constants.FIELD_ID), filter.getProviderId()));
        }
        return predicates.toArray(new Predicate[0]);
    }

    private List<Order> buildOrders(CriteriaBuilder builder, Root<ProductEntity> root, Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return List.of(builder.asc(root.get(Constants.FIELD_ID)));
        }
        List<Order> orders = new ArrayList<>();
        for (Sort.Order order : sort) {
            Path<Object> path = root.get(order.getProperty());
            orders.add(order.isAscending() ? builder.asc(path) : builder.desc(path));
        }
        return orders;
    }

    private String like(String value) {
        return "%" + value.toLowerCase() + "%";
    }
}
