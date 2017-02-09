ALTER TABLE `customer_purchased_suite_item`
CHANGE COLUMN `suite_item_id` `suite_item_id` BIGINT(20) NULL,
CHANGE COLUMN `times_left` `times` INT(11) NOT NULL,
ADD COLUMN `sale_category` INT(11) NOT NULL,
ADD COLUMN `cost` DOUBLE NULL,
ADD COLUMN `custom_stock_item_id` BIGINT(20) NOT NULL;

ALTER TABLE `customer_purchased_suite_item`
ADD CONSTRAINT `FK_9bu7tvthrrbu8v0ta1ukcs2ml` FOREIGN KEY (`id`) REFERENCES `sku_item` (`id`);


ALTER TABLE `stock_transfer_order_detail`
ADD COLUMN `before_number` INT(11);