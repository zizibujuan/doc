-- -----------------------------------------------------
-- Table `DRIP_USER_GIT_REPO` 维护用户的git仓库列表
-- 一个用户可以有多个git仓库，但是默认会为用户创建一个名为default的仓库
-- 仓库路径为rootPath/userName/default
-- -----------------------------------------------------
DROP TABLE IF EXISTS `DRIP_USER_GIT_REPO`;

CREATE TABLE IF NOT EXISTS `DRIP_DOC_FILE` (
  `DBID` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `REPO_NAME` VARCHAR(32) NULL COMMENT 'git仓库名称',
  `CRT_TM` DATETIME NULL COMMENT '创建时间',
  `CRT_USER_ID` BIGINT NOT NULL COMMENT '创建人标识',
  PRIMARY KEY (`DBID`))
ENGINE = InnoDB
COMMENT = '用户的git仓库列表';
