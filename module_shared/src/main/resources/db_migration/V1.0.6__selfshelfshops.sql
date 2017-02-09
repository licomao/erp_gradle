CREATE TABLE `sale_shelf_shops` (
  `sale_shelf_id` bigint(20) NOT NULL,
  `shops_id` bigint(20) NOT NULL,
  KEY `FK_sr876q8tpjciqfptnd6mny189` (`shops_id`),
  KEY `FK_ftanswqwmwgwevycw80btbhm1` (`sale_shelf_id`),
  CONSTRAINT `FK_ftanswqwmwgwevycw80btbhm1` FOREIGN KEY (`sale_shelf_id`) REFERENCES `sale_shelf` (`id`),
  CONSTRAINT `FK_sr876q8tpjciqfptnd6mny189` FOREIGN KEY (`shops_id`) REFERENCES `shop` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
