-- Etapa 7a: dados iniciais (eram o import.sql). Rodam UMA vez, na primeira inicialização.
-- A senha dos dois usuários é 123456 (guardada com BCrypt).

INSERT INTO tb_user (name, email, password) VALUES ('Maria Brown', 'maria@gmail.com', '$2a$10$DJisNkqLMbBoEUp.R6fw.eB2IJPmSLclp57gjcLyJRC8a8L9dLzDG');
INSERT INTO tb_user (name, email, password) VALUES ('Alex Green', 'alex@gmail.com', '$2a$10$DJisNkqLMbBoEUp.R6fw.eB2IJPmSLclp57gjcLyJRC8a8L9dLzDG');
INSERT INTO tb_role (authority) VALUES ('ROLE_CLIENT');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);
