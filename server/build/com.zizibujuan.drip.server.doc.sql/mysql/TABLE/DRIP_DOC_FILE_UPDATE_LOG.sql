-- -----------------------------------------------------
-- Table `DRIP_DOC_FILE_UPDATE_LOG` 文档修改历史记录
-- -----------------------------------------------------
DROP TABLE IF EXISTS `DRIP_DOC_FILE_UPDATE_LOG`;

CREATE TABLE IF NOT EXISTS `DRIP_DOC_FILE_UPDATE_LOG` (
  `DBID` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `DOC_FILE_ID` VARCHAR(32) NULL COMMENT '文档标识',
  `UPT_TM` DATETIME NULL COMMENT '修改时间',
  `UPT_USER_ID` BIGINT NOT NULL COMMENT '修改人标识',
  PRIMARY KEY (`DBID`))
ENGINE = InnoDB
COMMENT = '文档修改历史记录';
