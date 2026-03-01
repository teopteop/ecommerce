-- test-sample.sql

-- categories
INSERT INTO categories (name, code, parent_id, deleted, created_by, updated_by, created_at, updated_at) VALUES
                                                                                                            ('남성', 'MEN', NULL, false, 1, 1, NOW(), NOW()),
                                                                                                            ('여성', 'WOMEN', NULL, false, 1, 1, NOW(), NOW()),
                                                                                                            ('남성 상의', 'MEN_TOP', 1, false, 1, 1, NOW(), NOW()),
                                                                                                            ('여성 상의', 'WOMEN_TOP', 2, false, 1, 1, NOW(), NOW()),
                                                                                                            ('티셔츠', 'MEN_TOP_TSHIRT', 3, false, 1, 1, NOW(), NOW()),
                                                                                                            ('셔츠/블라우스', 'MEN_TOP_SHIRT', 3, false, 1, 1, NOW(), NOW()),
                                                                                                            ('티셔츠', 'WOMEN_TOP_TSHIRT', 4, false, 1, 1, NOW(), NOW()),
                                                                                                            ('블라우스', 'WOMEN_TOP_BLOUSE', 4, false, 1, 1, NOW(), NOW());

-- products
INSERT INTO products (name, price, status, category_id, deleted, created_by, updated_by, created_at, updated_at) VALUES
                                                                                                                     ('나이키 드라이핏 티셔츠', 45000, 'SELLING', 5, false, 1, 1, NOW(), NOW()),
                                                                                                                     ('무신사 스탠다드 베이직 티', 19000, 'SELLING', 5, false, 1, 1, NOW(), NOW()),
                                                                                                                     ('유니클로 옥스포드 셔츠', 39000, 'SELLING', 6, false, 1, 1, NOW(), NOW()),
                                                                                                                     ('자라 크롭 티셔츠', 39000, 'SELLING', 7, false, 1, 1, NOW(), NOW()),
                                                                                                                     ('자라 새틴 블라우스', 69000, 'SELLING', 8, false, 1, 1, NOW(), NOW()),
                                                                                                                     ('COS 리넨 블라우스', 99000, 'STOPPED', 8, false, 1, 1, NOW(), NOW());

-- inventories
INSERT INTO inventories (product_id, quantity, created_at, updated_at) VALUES
                                                                           (1, 100, NOW(), NOW()),
                                                                           (2, 200, NOW(), NOW()),
                                                                           (3, 50, NOW(), NOW()),
                                                                           (4, 150, NOW(), NOW()),
                                                                           (5, 80, NOW(), NOW()),
                                                                           (6, 0, NOW(), NOW());