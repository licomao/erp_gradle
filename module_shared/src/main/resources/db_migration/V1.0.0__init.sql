/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
DROP TABLE IF EXISTS `agency`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `agency` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `erp_user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_l37dwm772pcvtsvkjpswm4tey` (`erp_user_id`),
  CONSTRAINT `FK_l37dwm772pcvtsvkjpswm4tey` FOREIGN KEY (`erp_user_id`) REFERENCES `erpuser` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `base_set`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `base_set` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `operation_price` double NOT NULL,
  `pos_rate` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `pos_top_rate` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `organization_id` bigint(20) DEFAULT NULL,
  `shop_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_imb6cw9frhy74nkbynw4wclwg` (`organization_id`),
  KEY `FK_gs2tehqarcocpthv44m136fx9` (`shop_id`),
  CONSTRAINT `FK_gs2tehqarcocpthv44m136fx9` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`),
  CONSTRAINT `FK_imb6cw9frhy74nkbynw4wclwg` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `campaign`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `campaign` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `banner_image_url` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `compaign_type` int(11) NOT NULL,
  `distance` double DEFAULT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `on_banner` bit(1) NOT NULL,
  `publish_date` datetime DEFAULT NULL,
  `summary` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `city_id` bigint(20) DEFAULT NULL,
  `shop_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_7tefjxff9cbvryw4idvu48hep` (`city_id`),
  KEY `FK_e6mielu1l11btu34c9haax2mu` (`shop_id`),
  CONSTRAINT `FK_7tefjxff9cbvryw4idvu48hep` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`),
  CONSTRAINT `FK_e6mielu1l11btu34c9haax2mu` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `care_suite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `care_suite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `mileage` int(11) NOT NULL,
  `month` int(11) NOT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `care_suite_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `care_suite_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `care_suite_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_klaikxc989brn5uvy9jejidfe` (`care_suite_id`),
  CONSTRAINT `FK_klaikxc989brn5uvy9jejidfe` FOREIGN KEY (`care_suite_id`) REFERENCES `care_suite` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `care_suite_group_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `care_suite_group_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `for_clazz` int(11) DEFAULT NULL,
  `suite_price` double NOT NULL,
  `sku_item_id` bigint(20) DEFAULT NULL,
  `care_suite_group_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_p15rqk8t82wswlo8qe41ospeg` (`sku_item_id`),
  KEY `FK_lk094k0jyq2vbuuhrpyxikeft` (`care_suite_group_id`),
  CONSTRAINT `FK_lk094k0jyq2vbuuhrpyxikeft` FOREIGN KEY (`care_suite_group_id`) REFERENCES `care_suite_group` (`id`),
  CONSTRAINT `FK_p15rqk8t82wswlo8qe41ospeg` FOREIGN KEY (`sku_item_id`) REFERENCES `sku_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `care_suite_group_item_price`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `care_suite_group_item_price` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `overridden_price` double DEFAULT NULL,
  `care_suite_group_item_id` bigint(20) DEFAULT NULL,
  `organization_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_jqfwmce6no2pq1shix2nr18xa` (`care_suite_group_item_id`),
  KEY `FK_m1oe6w1li11oak5yec67r791m` (`organization_id`),
  CONSTRAINT `FK_jqfwmce6no2pq1shix2nr18xa` FOREIGN KEY (`care_suite_group_item_id`) REFERENCES `care_suite_group_item` (`id`),
  CONSTRAINT `FK_m1oe6w1li11oak5yec67r791m` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `area` int(11) NOT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `comment` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `rating1` int(11) NOT NULL,
  `rating2` int(11) NOT NULL,
  `rating3` int(11) NOT NULL,
  `rating4` int(11) NOT NULL,
  `rating5` int(11) NOT NULL,
  `rating6` int(11) NOT NULL,
  `rating7` int(11) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  `settle_order_id` bigint(20) NOT NULL,
  `shop_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_2cmtlvc6bwscn1bkljd5w18c4` (`customer_id`),
  KEY `FK_et8wylcgla90sknva6nd8b7pn` (`settle_order_id`),
  KEY `FK_7moephcugn6hjrh6b43mu3dce` (`shop_id`),
  CONSTRAINT `FK_2cmtlvc6bwscn1bkljd5w18c4` FOREIGN KEY (`customer_id`) REFERENCES `customer_profile` (`id`),
  CONSTRAINT `FK_7moephcugn6hjrh6b43mu3dce` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`),
  CONSTRAINT `FK_et8wylcgla90sknva6nd8b7pn` FOREIGN KEY (`settle_order_id`) REFERENCES `settle_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `mobile` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `token` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `customer_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer_profile` (
  `profile_type` varchar(31) COLLATE utf8_unicode_ci NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `avatar_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `gender` int(11) DEFAULT NULL,
  `nick_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `bonus` int(11) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `real_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `customer_id` bigint(20) NOT NULL,
  `binding_shop_id` bigint(20) DEFAULT NULL,
  `organization_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ga2lcavjadn2fn5hf090aabfi` (`customer_id`),
  KEY `FK_8clioxkrbucgt5igy9dmn88ae` (`binding_shop_id`),
  KEY `FK_467smnn8map4b5n3a8fqdvkuq` (`organization_id`),
  CONSTRAINT `FK_467smnn8map4b5n3a8fqdvkuq` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`),
  CONSTRAINT `FK_8clioxkrbucgt5igy9dmn88ae` FOREIGN KEY (`binding_shop_id`) REFERENCES `shop` (`id`),
  CONSTRAINT `FK_ga2lcavjadn2fn5hf090aabfi` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `customer_purchased_suite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer_purchased_suite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `enabled` bit(1) NOT NULL,
  `settle_order_id` bigint(20) DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `customer_id` bigint(20) NOT NULL,
  `shop_id` bigint(20) DEFAULT NULL,
  `staff_id` bigint(20) DEFAULT NULL,
  `suite_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_17sl8dqyyi4vylw4nlt4gbdox` (`customer_id`),
  KEY `FK_ly26atvuy7lljxsnm6tlh8fu4` (`shop_id`),
  KEY `FK_9ee2hg2h6oi9kyuqw3rdyai0q` (`staff_id`),
  KEY `FK_37f7c2mpykh96ue3xd0nh0jcy` (`suite_id`),
  CONSTRAINT `FK_17sl8dqyyi4vylw4nlt4gbdox` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `FK_37f7c2mpykh96ue3xd0nh0jcy` FOREIGN KEY (`suite_id`) REFERENCES `suite` (`id`),
  CONSTRAINT `FK_9ee2hg2h6oi9kyuqw3rdyai0q` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`),
  CONSTRAINT `FK_ly26atvuy7lljxsnm6tlh8fu4` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `customer_purchased_suite_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer_purchased_suite_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `times_left` int(11) NOT NULL,
  `used_times` int(11) NOT NULL,
  `purchased_suite_id` bigint(20) DEFAULT NULL,
  `suite_item_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_bhs1gmn3okfcmtw8dt7u55g3c` (`purchased_suite_id`),
  KEY `FK_ha3wnrix5hl7eie72mrxb81tw` (`suite_item_id`),
  CONSTRAINT `FK_bhs1gmn3okfcmtw8dt7u55g3c` FOREIGN KEY (`purchased_suite_id`) REFERENCES `customer_purchased_suite` (`id`),
  CONSTRAINT `FK_ha3wnrix5hl7eie72mrxb81tw` FOREIGN KEY (`suite_item_id`) REFERENCES `suite_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `customer_visit_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer_visit_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `done` bit(1) NOT NULL,
  `note` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `reason` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `type` int(11) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  `shop_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_6lgel45qy5orc4umgp4uichsn` (`customer_id`),
  KEY `FK_b164o2ymkyjj8vxtfafhj882f` (`shop_id`),
  CONSTRAINT `FK_6lgel45qy5orc4umgp4uichsn` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `FK_b164o2ymkyjj8vxtfafhj882f` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `erp_announcement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `erp_announcement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `content` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `publish_date` datetime DEFAULT NULL,
  `publisher` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `title` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `organization_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_kj81c4rrl7jfpth9n49iq417t` (`organization_id`),
  CONSTRAINT `FK_kj81c4rrl7jfpth9n49iq417t` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `erprole`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `erprole` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `authority_mask` bigint(20) NOT NULL,
  `role` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `organization_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_kohk0xejeqkh7iqn5j7ifqkf8` (`role`),
  KEY `FK_nxw3dbju8rr1mlppe6s6hcld8` (`organization_id`),
  CONSTRAINT `FK_nxw3dbju8rr1mlppe6s6hcld8` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `erpuser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `erpuser` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `email` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `enable` bit(1) NOT NULL,
  `finger_print` varchar(2000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `phone` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `real_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `username` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `organization_id` bigint(20) DEFAULT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_juc38r093ek7y5db4342co2xn` (`organization_id`),
  KEY `FK_9yrivnqlve6twr20l7v7wcvpl` (`role_id`),
  CONSTRAINT `FK_9yrivnqlve6twr20l7v7wcvpl` FOREIGN KEY (`role_id`) REFERENCES `erprole` (`id`),
  CONSTRAINT `FK_juc38r093ek7y5db4342co2xn` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `erpuser_shops`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `erpuser_shops` (
  `erpuser_id` bigint(20) NOT NULL,
  `shops_id` bigint(20) NOT NULL,
  KEY `FK_sr876q8tpjciqfptnd6mny188` (`shops_id`),
  KEY `FK_ftanswqwmwgwevycw80btbhm0` (`erpuser_id`),
  CONSTRAINT `FK_ftanswqwmwgwevycw80btbhm0` FOREIGN KEY (`erpuser_id`) REFERENCES `erpuser` (`id`),
  CONSTRAINT `FK_sr876q8tpjciqfptnd6mny188` FOREIGN KEY (`shops_id`) REFERENCES `shop` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `expense`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `expense` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `electric_expense` double NOT NULL,
  `equip_repairs_expense` double NOT NULL,
  `month` int(11) NOT NULL,
  `net_phone_expense` double NOT NULL,
  `other_expense` double NOT NULL,
  `property_expense` double NOT NULL,
  `rent_expense` double NOT NULL,
  `water_expense` double NOT NULL,
  `year` int(11) NOT NULL,
  `shop_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_kvg02gs2fc60bkmegx9943p6f` (`shop_id`),
  CONSTRAINT `FK_kvg02gs2fc60bkmegx9943p6f` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `finger_print_scanner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `finger_print_scanner` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `pid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sensorsn` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `usb_sn` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `vid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `organization_id` bigint(20) DEFAULT NULL,
  `shop_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ig91s2rf3l7hfotgggkcph240` (`organization_id`),
  KEY `FK_ohlm6va2avfhdht91hs08j7kv` (`shop_id`),
  CONSTRAINT `FK_ig91s2rf3l7hfotgggkcph240` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`),
  CONSTRAINT `FK_ohlm6va2avfhdht91hs08j7kv` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `fixed_asset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fixed_asset` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `asset_status` int(11) NOT NULL,
  `model` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `number` int(11) NOT NULL,
  `price` double NOT NULL,
  `shop_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_2mae1xkboylm98nq4sfbqrsdb` (`shop_id`),
  CONSTRAINT `FK_2mae1xkboylm98nq4sfbqrsdb` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `organization_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_mb3m2w02lhklc0bimmdu4oxwp` (`organization_id`),
  CONSTRAINT `FK_mb3m2w02lhklc0bimmdu4oxwp` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `material_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `material_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `order_num` bigint(20) NOT NULL,
  `order_num_view` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `remark` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `use_date` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `erp_user_id` bigint(20) DEFAULT NULL,
  `shop_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_hed4p6pfdujnx9wudmi90g6og` (`erp_user_id`),
  KEY `FK_avh8paxbr6eu5lw965qs6x3g7` (`shop_id`),
  CONSTRAINT `FK_avh8paxbr6eu5lw965qs6x3g7` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`),
  CONSTRAINT `FK_hed4p6pfdujnx9wudmi90g6og` FOREIGN KEY (`erp_user_id`) REFERENCES `erpuser` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `material_order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `material_order_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `cost` double NOT NULL,
  `number` int(11) NOT NULL,
  `custom_stock_item_id` bigint(20) DEFAULT NULL,
  `material_order_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ko2va8cgq1ov0hk1lxxkbndvs` (`custom_stock_item_id`),
  KEY `FK_41fkv3k24nrm5ulgwx0dopgo` (`material_order_id`),
  CONSTRAINT `FK_41fkv3k24nrm5ulgwx0dopgo` FOREIGN KEY (`material_order_id`) REFERENCES `material_order` (`id`),
  CONSTRAINT `FK_ko2va8cgq1ov0hk1lxxkbndvs` FOREIGN KEY (`custom_stock_item_id`) REFERENCES `sku_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `operation_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `operation_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `car_level` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `labor_hours` double NOT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `operation_type` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `operation_item_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `operation_item_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `sum` double NOT NULL,
  `operation_item_id` bigint(20) DEFAULT NULL,
  `settle_order_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_gytf2kqpb6y1ro8qynhfghvde` (`operation_item_id`),
  KEY `FK_4tm34ff8ahx8un3y7pw65q7lr` (`settle_order_id`),
  CONSTRAINT `FK_4tm34ff8ahx8un3y7pw65q7lr` FOREIGN KEY (`settle_order_id`) REFERENCES `settle_order` (`id`),
  CONSTRAINT `FK_gytf2kqpb6y1ro8qynhfghvde` FOREIGN KEY (`operation_item_id`) REFERENCES `operation_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `cost` double NOT NULL,
  `count` int(11) NOT NULL,
  `discount` double NOT NULL,
  `discount_price` double NOT NULL,
  `receivable` double NOT NULL,
  `discount_granter_id` bigint(20) DEFAULT NULL,
  `from_purchased_suite_item_id` bigint(20) DEFAULT NULL,
  `merchandier_id` bigint(20) DEFAULT NULL,
  `ordered_item_id` bigint(20) DEFAULT NULL,
  `ordered_suite_id` bigint(20) DEFAULT NULL,
  `reference_care_suite_id` bigint(20) DEFAULT NULL,
  `processing_order_id` bigint(20) DEFAULT NULL,
  `settle_order_id` bigint(20) DEFAULT NULL,
  `presale_order_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_mrpj3x2lh060yenvh8mx3l90r` (`discount_granter_id`),
  KEY `FK_h410tlrs8hy66xgvpukgcyhjq` (`from_purchased_suite_item_id`),
  KEY `FK_67lr9jb7q06oecssdc8xnbxbe` (`merchandier_id`),
  KEY `FK_qtd7rhjwy4ni3ht4djq5i55hw` (`ordered_item_id`),
  KEY `FK_qiwspqryihlx7iuxifk0flsou` (`ordered_suite_id`),
  KEY `FK_rcydx1d8521txnonqop9atai3` (`reference_care_suite_id`),
  KEY `FK_j1hd9f4q8ki8tfwgtw1r15hol` (`processing_order_id`),
  KEY `FK_99huasyji6pp2uf4rqk998mpw` (`settle_order_id`),
  KEY `FK_lb93q3757alyypsul2l2isauw` (`presale_order_id`),
  CONSTRAINT `FK_67lr9jb7q06oecssdc8xnbxbe` FOREIGN KEY (`merchandier_id`) REFERENCES `staff` (`id`),
  CONSTRAINT `FK_99huasyji6pp2uf4rqk998mpw` FOREIGN KEY (`settle_order_id`) REFERENCES `settle_order` (`id`),
  CONSTRAINT `FK_h410tlrs8hy66xgvpukgcyhjq` FOREIGN KEY (`from_purchased_suite_item_id`) REFERENCES `customer_purchased_suite_item` (`id`),
  CONSTRAINT `FK_j1hd9f4q8ki8tfwgtw1r15hol` FOREIGN KEY (`processing_order_id`) REFERENCES `processing_order` (`id`),
  CONSTRAINT `FK_lb93q3757alyypsul2l2isauw` FOREIGN KEY (`presale_order_id`) REFERENCES `presale_order` (`id`),
  CONSTRAINT `FK_mrpj3x2lh060yenvh8mx3l90r` FOREIGN KEY (`discount_granter_id`) REFERENCES `erpuser` (`id`),
  CONSTRAINT `FK_qiwspqryihlx7iuxifk0flsou` FOREIGN KEY (`ordered_suite_id`) REFERENCES `suite` (`id`),
  CONSTRAINT `FK_qtd7rhjwy4ni3ht4djq5i55hw` FOREIGN KEY (`ordered_item_id`) REFERENCES `sku_item` (`id`),
  CONSTRAINT `FK_rcydx1d8521txnonqop9atai3` FOREIGN KEY (`reference_care_suite_id`) REFERENCES `care_suite` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `organization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `bank_account` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `bank_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `business_license_image_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `contact` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `contact_address` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `contact_phone` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `serial_num` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `shop_quota` int(11) DEFAULT NULL,
  `tax_number` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `tried` bit(1) DEFAULT NULL,
  `valid_date` datetime DEFAULT NULL,
  `agency_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_nudghtf89rc29m6cpq8rn0vjv` (`agency_id`),
  CONSTRAINT `FK_nudghtf89rc29m6cpq8rn0vjv` FOREIGN KEY (`agency_id`) REFERENCES `agency` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `amount` double NOT NULL,
  `app_amount` double NOT NULL,
  `cash_amount` double NOT NULL,
  `cash_movement` double NOT NULL,
  `charge_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `movement` double NOT NULL,
  `pos_amount` double NOT NULL,
  `pos_movement` double NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_nv6gdqo95afgikjngghh6um8p` (`customer_id`),
  CONSTRAINT `FK_nv6gdqo95afgikjngghh6um8p` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `presale_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `presale_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `appointment_date` datetime DEFAULT NULL,
  `cancelled` bit(1) NOT NULL,
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sale_category` int(11) NOT NULL,
  `source` int(11) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  `payment_id` bigint(20) DEFAULT NULL,
  `settle_order_id` bigint(20) DEFAULT NULL,
  `shop_id` bigint(20) NOT NULL,
  `vehicle_info_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_2ox9h9oo4lmckqajslljwjov9` (`customer_id`),
  KEY `FK_i3y1n4gavn7xnxhf3923u64ac` (`payment_id`),
  KEY `FK_ly7oq1oj01yy8ctu6rbeobp8d` (`settle_order_id`),
  KEY `FK_i2f1b58xqp7mjens7gixsq0cx` (`shop_id`),
  KEY `FK_er184yi2p6j0riyla0ljvqfnq` (`vehicle_info_id`),
  CONSTRAINT `FK_2ox9h9oo4lmckqajslljwjov9` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `FK_er184yi2p6j0riyla0ljvqfnq` FOREIGN KEY (`vehicle_info_id`) REFERENCES `vehicle_info` (`id`),
  CONSTRAINT `FK_i2f1b58xqp7mjens7gixsq0cx` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`),
  CONSTRAINT `FK_i3y1n4gavn7xnxhf3923u64ac` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`),
  CONSTRAINT `FK_ly7oq1oj01yy8ctu6rbeobp8d` FOREIGN KEY (`settle_order_id`) REFERENCES `settle_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `presale_order_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `presale_order_images` (
  `presale_order_id` bigint(20) NOT NULL,
  `images` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  KEY `FK_8yg4m8m1qov48hyokxto2hpjr` (`presale_order_id`),
  CONSTRAINT `FK_8yg4m8m1qov48hyokxto2hpjr` FOREIGN KEY (`presale_order_id`) REFERENCES `presale_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `processing_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `processing_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `cancelled` bit(1) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  `presale_order_id` bigint(20) DEFAULT NULL,
  `shop_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_8uyw0bbxu0wj188alx06whlou` (`customer_id`),
  KEY `FK_28gq1q3rdm0m9klj98kelh1wt` (`presale_order_id`),
  KEY `FK_a31d90xcxxjmod52t9e5yrij0` (`shop_id`),
  CONSTRAINT `FK_28gq1q3rdm0m9klj98kelh1wt` FOREIGN KEY (`presale_order_id`) REFERENCES `presale_order` (`id`),
  CONSTRAINT `FK_8uyw0bbxu0wj188alx06whlou` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `FK_a31d90xcxxjmod52t9e5yrij0` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `purchase_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `purchase_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `apply_person` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `in_stock_person` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `order_number` bigint(20) NOT NULL,
  `order_number_view` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `order_status` int(11) NOT NULL,
  `purchase_type` int(11) NOT NULL,
  `remark` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `review_person` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sale_no` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `purchase_shop_id` bigint(20) DEFAULT NULL,
  `sale_shop_id` bigint(20) DEFAULT NULL,
  `supplier_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_2jomjutsqnyrm0uoot83mb6jl` (`purchase_shop_id`),
  KEY `FK_qujwoywgo8fqvyj8nxo6y71pl` (`sale_shop_id`),
  KEY `FK_dgil1aiqhau54y5giflc3806n` (`supplier_id`),
  CONSTRAINT `FK_2jomjutsqnyrm0uoot83mb6jl` FOREIGN KEY (`purchase_shop_id`) REFERENCES `shop` (`id`),
  CONSTRAINT `FK_dgil1aiqhau54y5giflc3806n` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`),
  CONSTRAINT `FK_qujwoywgo8fqvyj8nxo6y71pl` FOREIGN KEY (`sale_shop_id`) REFERENCES `shop` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `purchase_order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `purchase_order_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `bank_number` int(11) NOT NULL,
  `last_price` double NOT NULL,
  `number` int(11) NOT NULL,
  `price` double NOT NULL,
  `custom_stock_item_id` bigint(20) DEFAULT NULL,
  `purchase_order_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_k33rmh5u08vx00s9y6vfe6sng` (`custom_stock_item_id`),
  KEY `FK_ddgwvg20ddyobyml5vnqx37lr` (`purchase_order_id`),
  CONSTRAINT `FK_ddgwvg20ddyobyml5vnqx37lr` FOREIGN KEY (`purchase_order_id`) REFERENCES `purchase_order` (`id`),
  CONSTRAINT `FK_k33rmh5u08vx00s9y6vfe6sng` FOREIGN KEY (`custom_stock_item_id`) REFERENCES `sku_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `recommended_accessory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommended_accessory` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `price` double NOT NULL,
  `sku_item_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_8bv5df8vd8c8shficyodndybj` (`sku_item_id`),
  CONSTRAINT `FK_8bv5df8vd8c8shficyodndybj` FOREIGN KEY (`sku_item_id`) REFERENCES `sku_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `refund_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `refund_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `apply_person` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `order_number` bigint(20) NOT NULL,
  `order_number_view` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `order_status` int(11) NOT NULL,
  `remark` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `refund_shop_id` bigint(20) DEFAULT NULL,
  `supplier_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_r54eu4ymjw45td088y75vbycm` (`refund_shop_id`),
  KEY `FK_byo8ycp7r1r5d5porhuy3q6jo` (`supplier_id`),
  CONSTRAINT `FK_byo8ycp7r1r5d5porhuy3q6jo` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`),
  CONSTRAINT `FK_r54eu4ymjw45td088y75vbycm` FOREIGN KEY (`refund_shop_id`) REFERENCES `shop` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `refund_order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `refund_order_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `bank_number` double NOT NULL,
  `cost` double NOT NULL,
  `number` int(11) NOT NULL,
  `custom_stock_item_id` bigint(20) DEFAULT NULL,
  `refund_order_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ldo9mfw6hkkey2hhbsqyfucia` (`custom_stock_item_id`),
  KEY `FK_p1p09bcp8woa5hq19f81bsqfu` (`refund_order_id`),
  CONSTRAINT `FK_ldo9mfw6hkkey2hhbsqyfucia` FOREIGN KEY (`custom_stock_item_id`) REFERENCES `sku_item` (`id`),
  CONSTRAINT `FK_p1p09bcp8woa5hq19f81bsqfu` FOREIGN KEY (`refund_order_id`) REFERENCES `refund_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sale_shelf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sale_shelf` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `price` double NOT NULL,
  `sale_category` int(11) NOT NULL,
  `organization_id` bigint(20) DEFAULT NULL,
  `sku_item_id` bigint(20) DEFAULT NULL,
  `suite_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_siahj1in0wcppr149aw0cq0kn` (`organization_id`),
  KEY `FK_fi37rmvs9i0u99u02xnycbifb` (`sku_item_id`),
  KEY `FK_8307uuixxmtid434hpg4ul8wv` (`suite_id`),
  CONSTRAINT `FK_8307uuixxmtid434hpg4ul8wv` FOREIGN KEY (`suite_id`) REFERENCES `suite` (`id`),
  CONSTRAINT `FK_fi37rmvs9i0u99u02xnycbifb` FOREIGN KEY (`sku_item_id`) REFERENCES `sku_item` (`id`),
  CONSTRAINT `FK_siahj1in0wcppr149aw0cq0kn` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `secondary_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `secondary_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `addition_rate` float NOT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `root_category` int(11) NOT NULL,
  `organization_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_5nfckjifhr3ny1nastwcsngyu` (`organization_id`),
  CONSTRAINT `FK_5nfckjifhr3ny1nastwcsngyu` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `settle_accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `settle_accounts` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `amount` double NOT NULL,
  `cal_date` datetime DEFAULT NULL,
  `shop_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_mpbj2x60uscnlf2v27u9nq0m8` (`shop_id`),
  CONSTRAINT `FK_mpbj2x60uscnlf2v27u9nq0m8` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `settle_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `settle_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `close` bit(1) NOT NULL,
  `commented` bit(1) NOT NULL,
  `credit` bit(1) NOT NULL,
  `is_finish` bit(1) NOT NULL,
  `sale_category` int(11) NOT NULL,
  `sale_no` bigint(20) DEFAULT NULL,
  `sale_no_view` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `customer_id` bigint(20) DEFAULT NULL,
  `customer_purchased_suite_id` bigint(20) DEFAULT NULL,
  `payment_id` bigint(20) DEFAULT NULL,
  `presale_order_id` bigint(20) DEFAULT NULL,
  `processing_order_id` bigint(20) DEFAULT NULL,
  `receiver_id` bigint(20) DEFAULT NULL,
  `saler_id` bigint(20) DEFAULT NULL,
  `shop_id` bigint(20) NOT NULL,
  `vehicle_info_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_kkfua6rvxmxxo016tf10kjyat` (`customer_id`),
  KEY `FK_sfd85w5x3yiiiw8hcrmr7fbcp` (`customer_purchased_suite_id`),
  KEY `FK_7w7u707bg0nmlmin7vv7pbdlk` (`payment_id`),
  KEY `FK_qakaq1tiyp7nvbe887hsl51xn` (`presale_order_id`),
  KEY `FK_78qitr2htfq1vb1gkdgqni7in` (`processing_order_id`),
  KEY `FK_jt49iux6n0dd9vqt4wpxi6eae` (`receiver_id`),
  KEY `FK_b5sprnf24gu5ng93giwgyoe7w` (`saler_id`),
  KEY `FK_b6cx4xe1y27yvlk6g6wrn98my` (`shop_id`),
  KEY `FK_dfcr8ttj5hcg6ttlci59kng24` (`vehicle_info_id`),
  CONSTRAINT `FK_78qitr2htfq1vb1gkdgqni7in` FOREIGN KEY (`processing_order_id`) REFERENCES `processing_order` (`id`),
  CONSTRAINT `FK_7w7u707bg0nmlmin7vv7pbdlk` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`),
  CONSTRAINT `FK_b5sprnf24gu5ng93giwgyoe7w` FOREIGN KEY (`saler_id`) REFERENCES `erpuser` (`id`),
  CONSTRAINT `FK_b6cx4xe1y27yvlk6g6wrn98my` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`),
  CONSTRAINT `FK_dfcr8ttj5hcg6ttlci59kng24` FOREIGN KEY (`vehicle_info_id`) REFERENCES `vehicle_info` (`id`),
  CONSTRAINT `FK_jt49iux6n0dd9vqt4wpxi6eae` FOREIGN KEY (`receiver_id`) REFERENCES `staff` (`id`),
  CONSTRAINT `FK_kkfua6rvxmxxo016tf10kjyat` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `FK_qakaq1tiyp7nvbe887hsl51xn` FOREIGN KEY (`presale_order_id`) REFERENCES `presale_order` (`id`),
  CONSTRAINT `FK_sfd85w5x3yiiiw8hcrmr7fbcp` FOREIGN KEY (`customer_purchased_suite_id`) REFERENCES `customer_purchased_suite` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `settle_order_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `settle_order_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `is_sign_for` bit(1) NOT NULL,
  `belong_shop_id` bigint(20) DEFAULT NULL,
  `settle_order_id` bigint(20) NOT NULL,
  `shop_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_cvl5axujnrjn56li5m2nvr6s0` (`belong_shop_id`),
  KEY `FK_6d5lmhwceiknny394yogp0ibv` (`settle_order_id`),
  KEY `FK_8fmr2o9th1w3c4tsmwnuh07bf` (`shop_id`),
  CONSTRAINT `FK_6d5lmhwceiknny394yogp0ibv` FOREIGN KEY (`settle_order_id`) REFERENCES `settle_order` (`id`),
  CONSTRAINT `FK_8fmr2o9th1w3c4tsmwnuh07bf` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`),
  CONSTRAINT `FK_cvl5axujnrjn56li5m2nvr6s0` FOREIGN KEY (`belong_shop_id`) REFERENCES `shop` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `shop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `shop` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `address` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `distance` double DEFAULT NULL,
  `image_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `opening_hours` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `phone` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `promotion_tag` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `rating` float NOT NULL,
  `rating_count` bigint(20) NOT NULL,
  `shop_code` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `shop_type` int(11) NOT NULL,
  `city_id` bigint(20) DEFAULT NULL,
  `organization_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_5hrbwn0g2cnevwihfun9d6vrl` (`city_id`),
  KEY `FK_f3oj3hs6rgrj4gmi60nnq9qmb` (`organization_id`),
  CONSTRAINT `FK_5hrbwn0g2cnevwihfun9d6vrl` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`),
  CONSTRAINT `FK_f3oj3hs6rgrj4gmi60nnq9qmb` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `shop_promotion_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `shop_promotion_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `tag_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sku_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sku_item` (
  `sku_type` varchar(31) COLLATE utf8_unicode_ci NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `brand_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `cover_image_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `need_appointment` bit(1) NOT NULL,
  `price` double NOT NULL,
  `root_category` int(11) NOT NULL,
  `accessory_category` int(11) DEFAULT NULL,
  `app_sort` int(11) DEFAULT NULL,
  `bar_code` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `cost` double DEFAULT NULL,
  `is_app_sale` bit(1) DEFAULT NULL,
  `is_distribution` int(11) DEFAULT NULL,
  `param1` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `param2` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `param3` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `param4` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `param5` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `number` int(11) DEFAULT NULL,
  `view_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `stock_item` tinyblob,
  `labor_hours` double DEFAULT NULL,
  `secondary_category_id` bigint(20) DEFAULT NULL,
  `supplier_id` bigint(20) DEFAULT NULL,
  `organization_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ewyhqs4mffbmw92ml76ba7km9` (`secondary_category_id`),
  KEY `FK_8kkg4lwgpx1gxyuxsyq9fxxss` (`supplier_id`),
  KEY `FK_4ntl4ki9fwvsv3l71md0u0a0y` (`organization_id`),
  CONSTRAINT `FK_4ntl4ki9fwvsv3l71md0u0a0y` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`),
  CONSTRAINT `FK_8kkg4lwgpx1gxyuxsyq9fxxss` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`),
  CONSTRAINT `FK_ewyhqs4mffbmw92ml76ba7km9` FOREIGN KEY (`secondary_category_id`) REFERENCES `secondary_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `staff` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `dimission_date` datetime DEFAULT NULL,
  `entry_date` datetime DEFAULT NULL,
  `finger_print` varchar(2000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `identity_card` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `phone` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `probation` double NOT NULL,
  `status` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `work_day` double NOT NULL,
  `job_id` bigint(20) DEFAULT NULL,
  `shop_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_8yva3cqrfj94v8bpevxm9bman` (`job_id`),
  KEY `FK_dpd202yt2j6mu2qh495mkhgrd` (`shop_id`),
  CONSTRAINT `FK_8yva3cqrfj94v8bpevxm9bman` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`),
  CONSTRAINT `FK_dpd202yt2j6mu2qh495mkhgrd` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `staff_attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `staff_attendance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `arrive_date` datetime DEFAULT NULL,
  `leave_date` datetime DEFAULT NULL,
  `work_date` datetime DEFAULT NULL,
  `staff_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_g20qpds1q34dc6yghqjh0d4um` (`staff_id`),
  CONSTRAINT `FK_g20qpds1q34dc6yghqjh0d4um` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `staff_staff_attendances`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `staff_staff_attendances` (
  `staff_id` bigint(20) NOT NULL,
  `staff_attendances_id` bigint(20) NOT NULL,
  UNIQUE KEY `UK_2d9hu9amkmv91bmsyrbtcw5po` (`staff_attendances_id`),
  KEY `FK_mcyjj103e6s0ewerm8s6ikr1k` (`staff_id`),
  CONSTRAINT `FK_2d9hu9amkmv91bmsyrbtcw5po` FOREIGN KEY (`staff_attendances_id`) REFERENCES `staff_attendance` (`id`),
  CONSTRAINT `FK_mcyjj103e6s0ewerm8s6ikr1k` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `stock_transfer_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stock_transfer_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `order_number` bigint(20) NOT NULL,
  `order_number_view` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `remark` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `stock_date` datetime DEFAULT NULL,
  `transfer_date` datetime DEFAULT NULL,
  `transfer_status` int(11) NOT NULL,
  `erp_user_id` bigint(20) DEFAULT NULL,
  `in_shop_id` bigint(20) DEFAULT NULL,
  `out_shop_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_6fmwwaedjdkamlq5afikyr6l7` (`erp_user_id`),
  KEY `FK_odc9p851khccbdp9jumu2mtf0` (`in_shop_id`),
  KEY `FK_a5u2g3o426y13xph0whucepdy` (`out_shop_id`),
  CONSTRAINT `FK_6fmwwaedjdkamlq5afikyr6l7` FOREIGN KEY (`erp_user_id`) REFERENCES `erpuser` (`id`),
  CONSTRAINT `FK_a5u2g3o426y13xph0whucepdy` FOREIGN KEY (`out_shop_id`) REFERENCES `shop` (`id`),
  CONSTRAINT `FK_odc9p851khccbdp9jumu2mtf0` FOREIGN KEY (`in_shop_id`) REFERENCES `shop` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `stock_transfer_order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stock_transfer_order_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `cost` double NOT NULL,
  `number` int(11) NOT NULL,
  `custom_stock_item_id` bigint(20) DEFAULT NULL,
  `stock_transfer_order_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_d328t6qw54eh5e9cekgmiwu2n` (`custom_stock_item_id`),
  KEY `FK_appnwodhsih0dep870jhweip` (`stock_transfer_order_id`),
  CONSTRAINT `FK_appnwodhsih0dep870jhweip` FOREIGN KEY (`stock_transfer_order_id`) REFERENCES `stock_transfer_order` (`id`),
  CONSTRAINT `FK_d328t6qw54eh5e9cekgmiwu2n` FOREIGN KEY (`custom_stock_item_id`) REFERENCES `sku_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `stocking_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stocking_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `order_number` bigint(20) NOT NULL,
  `order_number_view` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `stocking_date` datetime DEFAULT NULL,
  `stocking_status` int(11) NOT NULL,
  `erp_user_id` bigint(20) DEFAULT NULL,
  `shop_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_n05riukyg8lemme5x31u0tn46` (`erp_user_id`),
  KEY `FK_7iuhcp4w8tgq8orey1buws5ob` (`shop_id`),
  CONSTRAINT `FK_7iuhcp4w8tgq8orey1buws5ob` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`),
  CONSTRAINT `FK_n05riukyg8lemme5x31u0tn46` FOREIGN KEY (`erp_user_id`) REFERENCES `erpuser` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `stocking_order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stocking_order_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `calculate_number` int(11) NOT NULL,
  `old_number` int(11) NOT NULL,
  `stock_cost` double NOT NULL,
  `custom_stock_item_id` bigint(20) DEFAULT NULL,
  `stocking_order_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_lk218ai5rhi0tyvxo1ngcq1xq` (`custom_stock_item_id`),
  KEY `FK_lrm3o2mye5ijpgrl1lbcib1n4` (`stocking_order_id`),
  CONSTRAINT `FK_lk218ai5rhi0tyvxo1ngcq1xq` FOREIGN KEY (`custom_stock_item_id`) REFERENCES `sku_item` (`id`),
  CONSTRAINT `FK_lrm3o2mye5ijpgrl1lbcib1n4` FOREIGN KEY (`stocking_order_id`) REFERENCES `stocking_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `suite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `suite` (
  `custom_suite` int(11) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `cover_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `expiation` int(11) NOT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `price` double NOT NULL,
  `suite_type` int(11) NOT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `organization_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ym4ymo3ra5uiichs91j8thhb` (`organization_id`),
  CONSTRAINT `FK_ym4ymo3ra5uiichs91j8thhb` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `suite_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `suite_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `cost` double NOT NULL,
  `sale_category` int(11) NOT NULL,
  `times` int(11) NOT NULL,
  `times_left` int(11) NOT NULL,
  `used_times` int(11) NOT NULL,
  `sku_item_id` bigint(20) DEFAULT NULL,
  `suite_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_o27wv20pc5s2lry8ph571l38e` (`sku_item_id`),
  KEY `FK_smubkhwk9xavybo8h4y16ckhh` (`suite_id`),
  CONSTRAINT `FK_o27wv20pc5s2lry8ph571l38e` FOREIGN KEY (`sku_item_id`) REFERENCES `sku_item` (`id`),
  CONSTRAINT `FK_smubkhwk9xavybo8h4y16ckhh` FOREIGN KEY (`suite_id`) REFERENCES `suite` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supplier` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `contact_info` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `fax` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `organization_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_2g3le5gor28b37od9tgyei589` (`organization_id`),
  CONSTRAINT `FK_2g3le5gor28b37od9tgyei589` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `temp_presale_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `temp_presale_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `appointment_date` datetime DEFAULT NULL,
  `care_suite_id` bigint(20) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  `order_type` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `price` double NOT NULL,
  `sale_category` int(11) NOT NULL,
  `shop_id` bigint(20) NOT NULL,
  `sku_count` int(11) NOT NULL,
  `sku_id` bigint(20) NOT NULL,
  `vehicle_info_uid` binary(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `temp_presale_order_care_suite_group_item_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `temp_presale_order_care_suite_group_item_ids` (
  `temp_presale_order_id` bigint(20) NOT NULL,
  `care_suite_group_item_ids` bigint(20) DEFAULT NULL,
  KEY `FK_3kgjw3xh8jthtd201ag3doyys` (`temp_presale_order_id`),
  CONSTRAINT `FK_3kgjw3xh8jthtd201ag3doyys` FOREIGN KEY (`temp_presale_order_id`) REFERENCES `temp_presale_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `temp_settle_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `temp_settle_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `customer_id` bigint(20) NOT NULL,
  `price` double NOT NULL,
  `shop_id` bigint(20) NOT NULL,
  `suite_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `vehicle_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vehicle_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `engine_displacement` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `last_maintenance_date` datetime DEFAULT NULL,
  `last_maintenance_mileage` int(11) NOT NULL,
  `mileage` int(11) NOT NULL,
  `mileage_updated_date` datetime DEFAULT NULL,
  `obdsn` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `on_road_date` datetime DEFAULT NULL,
  `plate_number` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `tire` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `verified` bit(1) NOT NULL,
  `vin_code` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `vin_image_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `model_id` bigint(20) DEFAULT NULL,
  `profile_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ih7hs831n43vmmwbxcjtf5f2u` (`model_id`),
  KEY `FK_kxklh1gqim8mccxs4i72t3tjs` (`profile_id`),
  CONSTRAINT `FK_ih7hs831n43vmmwbxcjtf5f2u` FOREIGN KEY (`model_id`) REFERENCES `vehicle_model` (`id`),
  CONSTRAINT `FK_kxklh1gqim8mccxs4i72t3tjs` FOREIGN KEY (`profile_id`) REFERENCES `customer_profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `vehicle_model`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vehicle_model` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `uid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_by` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ver` int(11) DEFAULT 0,
  `back_tire` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `brand` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `engine` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `engine_displacement` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `first_letter` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `for_sale` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `front_tire` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `gear_box` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `line` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `manufacturer` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `price` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `produced_year` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `spare_tire` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `version` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

