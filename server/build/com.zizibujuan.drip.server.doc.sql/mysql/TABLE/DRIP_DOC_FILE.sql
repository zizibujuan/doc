-- -----------------------------------------------------
-- Table `DRIP_DOC_FILE` 维护所有文档信息
-- -----------------------------------------------------
DROP TABLE IF EXISTS `DRIP_DOC_FILE`;

CREATE TABLE IF NOT EXISTS `DRIP_DOC_FILE` (
  `DBID` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `FILE_PATH` VARCHAR(32) NULL COMMENT '文件唯一标识，使用uuid描述',
  `DOC_TITLE` VARCHAR(32) NULL COMMENT '文档标题',
  `CRT_TM` DATETIME NULL COMMENT '创建时间',
  `CRT_USER_ID` BIGINT NOT NULL COMMENT '创建人标识',
  PRIMARY KEY (`DBID`),
  UNIQUE KEY `UK_PROJ_NAME_USER` (`PROJECT_NAME`,`CRT_USER_ID`))
ENGINE = InnoDB
COMMENT = '文档信息';
