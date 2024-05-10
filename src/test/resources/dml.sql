-- Додавання користувача
INSERT INTO users (id, username, password, email, birthday, avatar, role)
VALUES ('5c2a6d9b-8a32-4366-ad16-ca14f387c564', 'user1', 'password1', 'user1@example.com', '1990-01-01', '0x89504E470D0A1A0A0000000D4948445200000030000000300806000000F9B78C0000000970485973000000EC400000EC40195F36F000000017352474200AECE1CE90000000467414D410000B18F0BFC6105000000097048597300000EC400000EC40195F36F000000017352474200000000527443436F6C6F7253706163652E6465660000000049454E44AE426082'
           , 'STUDENT'),
       ('ce8e1de3-ebc1-4332-b696-e2b4d7c94400', 'user2', 'password2', 'user2@example.com', '1992-03-15', '0x89504E470D0A1A0A0000000D4948445200000030000000300806000000F9B78C0000000970485973000000EC400000EC40195F36F000000017352474200AECE1CE90000000467414D410000B18F0BFC6105000000097048597300000EC400000EC40195F36F000000017352474200000000527443436F6C6F7253706163652E6465660000000049454E44AE426082',
        'STUDENT'),
       ('acf647ec-b69c-43cc-86fd-96628a1fb40a', 'user3', 'password3', 'user3@example.com', '1985-07-20', '0x89504E470D0A1A0A0000000D4948445200000030000000300806000000F9B78C0000000970485973000000EC400000EC40195F36F000000017352474200AECE1CE90000000467414D410000B18F0BFC6105000000097048597300000EC400000EC40195F36F000000017352474200000000527443436F6C6F7253706163652E6465660000000049454E44AE426082',
        'TEACHER');

-- Додавання тесту
INSERT INTO tests (id, title, owner_id, create_date)
VALUES ('3552c844-e5dc-4135-a4ff-b0bb148ee2af', 'Test 1', 'acf647ec-b69c-43cc-86fd-96628a1fb40a', '2024-05-01 10:00:00'),
       ('fd1476e6-eed2-45d8-b975-8f713e916d9c', 'Test 2', 'acf647ec-b69c-43cc-86fd-96628a1fb40a', '2024-05-02 10:00:00');

-- Додавання питань
INSERT INTO questions (id, test_id, text)
VALUES ('c9cafa71-bb81-4447-82f4-bbf8a7d74428', '3552c844-e5dc-4135-a4ff-b0bb148ee2af', 'Question 1 for Test 1'),
       ('f8fdc69b-5cde-4057-9c12-41b0b5d9f68d', '3552c844-e5dc-4135-a4ff-b0bb148ee2af', 'Question 2 for Test 1'),
       ('9055bf47-a26f-4eaa-8967-649d43f2f7fc', 'fd1476e6-eed2-45d8-b975-8f713e916d9c', 'Question 1 for Test 2');

-- Додавання відповідей
INSERT INTO answers (id, question_id, text, is_correct)
VALUES ('e92c2eda-a3cc-460a-a94a-62e46687c07e', 'c9cafa71-bb81-4447-82f4-bbf8a7d74428', 'Answer 1 for Question 1', true),
       ('ff14bb98-76cf-421a-a319-65f46d567a40', 'c9cafa71-bb81-4447-82f4-bbf8a7d74428', 'Answer 2 for Question 1', false),
       ('4855cc3b-77e4-4d19-a50c-b39ba6e811f3', 'f8fdc69b-5cde-4057-9c12-41b0b5d9f68d', 'Answer 1 for Question 2', false),
       ('c5663118-dcde-4f47-84a7-45833b7a5043', 'f8fdc69b-5cde-4057-9c12-41b0b5d9f68d', 'Answer 2 for Question 2', true),
       ('11e59648-8235-43f2-82f2-abdc52640ef9', '9055bf47-a26f-4eaa-8967-649d43f2f7fc', 'Answer 1 for Question 3', true),
       ('070bf3c2-7014-4006-b8f7-15406e06528a', '9055bf47-a26f-4eaa-8967-649d43f2f7fc', 'Answer 2 for Question 3', false);

-- Додавання звітів
INSERT INTO reports (id, owner_id, test_id, create_date)
VALUES ('04be57c2-0615-4b02-8218-767ddd91757f', 'acf647ec-b69c-43cc-86fd-96628a1fb40a', '3552c844-e5dc-4135-a4ff-b0bb148ee2af', '2024-05-03 10:00:00'),
       ('b7315922-4c03-4cef-b5a2-9dea45d8aa04', 'acf647ec-b69c-43cc-86fd-96628a1fb40a', 'fd1476e6-eed2-45d8-b975-8f713e916d9c', '2024-05-04 10:00:00');

-- Додавання результатів
INSERT INTO results (id, test_id, owner_id, report_id, mark, create_date)
VALUES ('9e31927b-da7d-4cb3-a22b-d8a4d92cbde5', '3552c844-e5dc-4135-a4ff-b0bb148ee2af', '5c2a6d9b-8a32-4366-ad16-ca14f387c564', '04be57c2-0615-4b02-8218-767ddd91757f', 85, '2024-05-03 12:00:00'),
       ('26b9135c-caea-43ae-9073-a93934896f24', '3552c844-e5dc-4135-a4ff-b0bb148ee2af', 'ce8e1de3-ebc1-4332-b696-e2b4d7c94400', 'b7315922-4c03-4cef-b5a2-9dea45d8aa04', 90, '2024-05-03 12:30:00'),
       ('8d83ed9f-5e7f-4475-a7bb-8a72f6c35bba', 'fd1476e6-eed2-45d8-b975-8f713e916d9c', '5c2a6d9b-8a32-4366-ad16-ca14f387c564', '04be57c2-0615-4b02-8218-767ddd91757f', 75, '2024-05-04 11:00:00');

INSERT INTO tags(id, name)
VALUES ('b4479381-fd3b-4b6f-83ab-e8f1d4bd8de9', 'Математика'),
       ('01e58ad9-b4fb-4cd5-993c-3848f0afe736', 'Українська мова');

INSERT INTO test_tag (test_id, tag_id)
VALUES ('3552c844-e5dc-4135-a4ff-b0bb148ee2af', 'b4479381-fd3b-4b6f-83ab-e8f1d4bd8de9'),
       ('fd1476e6-eed2-45d8-b975-8f713e916d9c', '01e58ad9-b4fb-4cd5-993c-3848f0afe736');
