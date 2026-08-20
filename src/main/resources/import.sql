-- 3 proveedores
INSERT INTO provider (id, cif, name) VALUES (1, 'A12345678', 'TechProvider S.A.');
INSERT INTO provider (id, cif, name) VALUES (2, 'B87654321', 'Furniture & Co.');
INSERT INTO provider (id, cif, name) VALUES (3, 'C11223344', 'Sport & Lifestyle');

-- 5 usuarios
INSERT INTO users (id, username, email, created_at) VALUES (1, 'Juan Pérez', 'juan.perez@gmail.es', '2020-01-15');
INSERT INTO users (id, username, email, created_at) VALUES (2, 'María Gómez', 'maria.gomez@gmail.es', '2021-03-22');
INSERT INTO users (id, username, email, created_at) VALUES (3, 'Carlos López', 'carlos.lopez@gmail.es', '2022-06-10');
INSERT INTO users (id, username, email, created_at) VALUES (4, 'Ana Martínez', 'ana.martinez@gmail.es', '2023-09-05');
INSERT INTO users (id, username, email, created_at) VALUES (5, 'Pedro Sánchez', 'pedro.sanchez@gmail.es', '2024-11-18');

-- 20 productos
INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (1, 'Portátil', 'Un portátil de alto rendimiento para juegos y trabajo.', 999.99, 10, 'Electrónica', 'MarcaGaming', '2023-11-20', 1, 1);
INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (2, 'Teléfono Inteligente', 'Último modelo con características de vanguardia.', 699.99, 50, 'Electrónica', 'MarcaTeléfono', '2024-08-15', 1, 1);
INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (3, 'Silla de Oficina', 'Silla ergonómica con soporte lumbar.', 149.99, 25, 'Muebles', 'MarcaConfort', '2024-09-01', 2, 1);
INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (4, 'Zapatos para Correr', 'Zapatos ligeros para correr a diario.', 79.99, 100, 'Calzado', 'MarcaDeportiva', '2024-10-10', 3, 1);

INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (5, 'Auriculares Inalámbricos', 'Auriculares inalámbricos con cancelación de ruido y larga duración de batería.', 199.99, 40, 'Electrónica', 'MarcaAudio', '2024-11-20', 1, 2);
INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (6, 'Tablet Pro', 'Tablet profesional con pantalla de alta resolución.', 499.99, 30, 'Electrónica', 'MarcaTech', '2025-01-15', 1, 2);
INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (7, 'Monitor 4K', 'Monitor de 27 pulgadas con resolución 4K.', 349.99, 20, 'Electrónica', 'MarcaDisplay', '2025-02-20', 1, 2);
INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (8, 'Teclado Mecánico', 'Teclado mecánico RGB con switches personalizables.', 129.99, 60, 'Electrónica', 'MarcaKeyboard', '2025-03-10', 1, 2);

INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (9, 'Ratón Gaming', 'Ratón gaming con sensor de alta precisión.', 79.99, 80, 'Electrónica', 'MarcaMouse', '2025-04-05', 1, 3);
INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (10, 'Escritorio Moderno', 'Escritorio de madera con diseño moderno.', 299.99, 15, 'Muebles', 'MarcaFurniture', '2025-05-12', 2, 3);
INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (11, 'Lámpara LED', 'Lámpara LED con control de intensidad.', 49.99, 100, 'Iluminación', 'MarcaLight', '2025-06-18', 2, 3);
INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (12, 'Camiseta Deportiva', 'Camiseta transpirable para actividades deportivas.', 29.99, 150, 'Ropa', 'MarcaSport', '2025-07-25', 3, 3);

INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (13, 'Pantalón Deportivo', 'Pantalón cómodo para entrenamiento.', 39.99, 120, 'Ropa', 'MarcaSport', '2025-08-30', 3, 4);
INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (14, 'Mochila Laptop', 'Mochila resistente con compartimento para laptop.', 59.99, 70, 'Accesorios', 'MarcaBag', '2025-09-10', 3, 4);
INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (15, 'Cargador USB-C', 'Cargador rápido USB-C de 65W.', 34.99, 90, 'Electrónica', 'MarcaPower', '2025-10-15', 1, 4);
INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (16, 'Webcam HD', 'Webcam Full HD con micrófono integrado.', 89.99, 50, 'Electrónica', 'MarcaVideo', '2025-11-20', 1, 4);

INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (17, 'Altavoz Bluetooth', 'Altavoz portátil con sonido estéreo.', 69.99, 65, 'Electrónica', 'MarcaSound', '2025-12-25', 1, 5);
INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (18, 'Reloj Inteligente', 'Smartwatch con monitor de actividad física.', 249.99, 35, 'Electrónica', 'MarcaWatch', '2026-01-10', 1, 5);
INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (19, 'Cámara de Seguridad', 'Cámara IP con visión nocturna y detección de movimiento.', 119.99, 25, 'Electrónica', 'MarcaSecurity', '2026-02-15', 1, 5);
INSERT INTO products (id, name, description, price, quantity, category, brand, expiration_date, provider_id, user_id) VALUES (20, 'Router WiFi 6', 'Router de última generación con WiFi 6.', 179.99, 20, 'Electrónica', 'MarcaNetwork', '2026-03-20', 1, 5);
-- Restart identity columns so that new rows do not clash with the preloaded ids
ALTER TABLE provider ALTER COLUMN id RESTART WITH 4;
ALTER TABLE users ALTER COLUMN id RESTART WITH 6;
ALTER TABLE products ALTER COLUMN id RESTART WITH 21;
