package com.teopteop.ecommerce.domain.category.entity;

import com.teopteop.ecommerce.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 화면/관리자용 표시 이름, 예: "남성 의류"

    @Column(unique = true, nullable = false)
    private String code; // 내부 관리용 코드, 예: "MEN_CLOTHES"

    // 최상위 카테고리는 null
    private Long parentId;

    @Column(nullable = false)
    private boolean deleted;

    private Category (String name, String code, Long parentId) {
        this.name = name;
        this.parentId = parentId;
        this.code = code;
        this.deleted = false;
    }

    public static Category create(String name, String code, Long parentId) {
        return new Category(name, code, parentId);
    }

    public void markDeleted() {
        this.deleted = true;
    }

}
