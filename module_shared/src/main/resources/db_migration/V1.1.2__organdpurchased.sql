ALTER TABLE `organization` ADD COLUMN `check_day` INT(11) NOT NULL DEFAULT 1,ADD COLUMN `is_check_pd` bit(1) DEFAULT NULL;

ALTER TABLE `customer_purchased_suite` ADD COLUMN `auth_button_str` VARCHAR (225) COLLATE utf8_unicode_ci DEFAULT NULL;
