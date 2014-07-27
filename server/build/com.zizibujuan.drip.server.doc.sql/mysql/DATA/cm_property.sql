-- doc git仓库的根目录
insert into DRIP_PROPERTY_KEY (DBID,PROPERTY_KEY,GROUP_ID,PROPERTY_TYPE) VALUES (60, 'git.repo.doc.root', 1, 1);
-- 本地测试目录是/home/jzw/drip_data/
insert into DRIP_PROPERTY_VALUE_STRING (KEY_ID, PROPERTY_VALUE,I18n_ID) VALUES (60, '/mnt/drip_data/doc/','zh_cn');