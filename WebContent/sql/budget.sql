/*
Navicat MySQL Data Transfer

Source Server         : 远程数据库
Source Server Version : 50633
Source Host           : 118.89.140.113:3306
Source Database       : budget

Target Server Type    : MYSQL
Target Server Version : 50633
File Encoding         : 65001

Date: 2017-06-22 09:39:25
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `tb_project`
-- ----------------------------
DROP TABLE IF EXISTS `tb_project`;
CREATE TABLE `tb_project` (
  `project_id` int(11) NOT NULL AUTO_INCREMENT,
  `project_name` varchar(255) NOT NULL COMMENT '项目名',
  `level` int(11) NOT NULL COMMENT '等级，1表示项目，2表示单项工程，3表示单位工程',
  `parent_id` int(11) DEFAULT NULL,
  `enable_flag` int(1) NOT NULL DEFAULT '1',
  `remark` varchar(255) DEFAULT NULL,
  `isleaf` int(1) DEFAULT NULL,
  PRIMARY KEY (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=237 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_project
-- ----------------------------
INSERT INTO `tb_project` VALUES ('224', 'BB', '3', '222', '1', null, '1');
INSERT INTO `tb_project` VALUES ('227', '单项工程1', '2', '1', '1', null, '0');
INSERT INTO `tb_project` VALUES ('228', 'CC', '2', '2', '1', null, '0');
INSERT INTO `tb_project` VALUES ('229', 'AA', '2', '2', '1', null, '0');
INSERT INTO `tb_project` VALUES ('230', 'CC1', '3', '228', '1', null, '1');
INSERT INTO `tb_project` VALUES ('231', 'AA1', '3', '229', '1', null, '1');
INSERT INTO `tb_project` VALUES ('232', 'AA2', '3', '229', '1', null, '1');
INSERT INTO `tb_project` VALUES ('233', '单向工程1', '2', '2', '1', '你好', '0');
INSERT INTO `tb_project` VALUES ('234', '单位工程1', '3', '229', '1', '你好', '1');
INSERT INTO `tb_project` VALUES ('235', '单项工程2', '2', '1', '1', null, '0');
INSERT INTO `tb_project` VALUES ('236', '单位工程', '3', '235', '1', null, '1');

-- ----------------------------
-- Table structure for `th_seq`
-- ----------------------------
DROP TABLE IF EXISTS `th_seq`;
CREATE TABLE `th_seq` (
  `seq_name` varchar(255) DEFAULT NULL,
  `current_val` int(11) DEFAULT NULL,
  `increment_val` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of th_seq
-- ----------------------------
INSERT INTO `th_seq` VALUES ('aaa', '18', '1');
INSERT INTO `th_seq` VALUES ('GJYSF-999999', '7', '1');
INSERT INTO `th_seq` VALUES ('GJCCJ-9999', '1', '1');
INSERT INTO `th_seq` VALUES ('GJCCJ-1111', '1', '1');
INSERT INTO `th_seq` VALUES ('GJCCJ-999999', '1', '1');
INSERT INTO `th_seq` VALUES ('GJCCJ-99999', '1', '1');
INSERT INTO `th_seq` VALUES ('GLCCJ-10110', '1', '1');
INSERT INTO `th_seq` VALUES ('CLYSF-1000', '1', '1');
INSERT INTO `th_seq` VALUES ('YSCLF-00000', '1', '1');
INSERT INTO `th_seq` VALUES ('CLBGF', '1', '1');

-- ----------------------------
-- Table structure for `tm_authority`
-- ----------------------------
DROP TABLE IF EXISTS `tm_authority`;
CREATE TABLE `tm_authority` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `checked` int(11) DEFAULT NULL,
  `expanded` int(11) NOT NULL DEFAULT '0',
  `icon_cls` varchar(20) DEFAULT NULL,
  `leaf` int(11) NOT NULL DEFAULT '0',
  `menu_code` varchar(50) NOT NULL,
  `menu_config` varchar(200) DEFAULT NULL,
  `menu_name` varchar(50) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `sort_order` int(11) NOT NULL,
  `url` varchar(100) DEFAULT NULL,
  `enable_flag` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tm_authority
-- ----------------------------
INSERT INTO `tm_authority` VALUES ('1', null, '1', '', '0', 'BaseDataManage', '', '基础数据', null, '2000', '', '1');
INSERT INTO `tm_authority` VALUES ('2', null, '0', '', '1', 'CostMoneyManage', '', '费用代码维护', '1', '2001', 'baseDataManage.CostMoneyManage', '1');
INSERT INTO `tm_authority` VALUES ('3', null, '0', null, '1', 'ImportCateGoryManage', null, '分类/模块导入', '1', '2002', 'baseDataManage.ImportCateGoryManage', '1');
INSERT INTO `tm_authority` VALUES ('4', null, '0', '', '1', 'CategoryAndModelManage', '', '分类/模块维护', '1', '2002', 'baseDataManage.CategoryAndModelManage', '1');
INSERT INTO `tm_authority` VALUES ('5', null, '1', '', '0', 'BussinessProcess', '', '业务处理', null, '3000', '', '1');
INSERT INTO `tm_authority` VALUES ('6', null, '0', '', '1', 'ProjectSummary', '', '项目汇总', '5', '3001', 'bussinessProcess.ProjectSummary', '1');
INSERT INTO `tm_authority` VALUES ('7', null, '0', '', '1', 'ProjectInfo', '', '新建项目', '5', '3001', 'bussinessProcess.ProjectInfo', '1');
INSERT INTO `tm_authority` VALUES ('8', null, '0', '', '1', 'ReportDown', '', '报表下载', null, '4002', '', '1');
INSERT INTO `tm_authority` VALUES ('9', null, '0', null, '1', 'YCAManage', null, '运材安维护', '1', '2003', 'baseDataManage.YCAManage', '1');

-- ----------------------------
-- Table structure for `tm_looktype`
-- ----------------------------
DROP TABLE IF EXISTS `tm_looktype`;
CREATE TABLE `tm_looktype` (
  `looktype_id` int(11) NOT NULL AUTO_INCREMENT,
  `looktype_code` varchar(255) NOT NULL,
  `looktype_name` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `enable_flag` int(1) DEFAULT '1',
  `list_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`looktype_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tm_looktype
-- ----------------------------
INSERT INTO `tm_looktype` VALUES ('1', 'YSF', '运输费', '运输费', '1', 'COST_CODE');
INSERT INTO `tm_looktype` VALUES ('2', 'CLF', '材料费', '材料费', '1', 'COST_CODE');
INSERT INTO `tm_looktype` VALUES ('3', 'AZF', '安装费', '安装费', '1', 'COST_CODE');
INSERT INTO `tm_looktype` VALUES ('4', 'CSF', '措施费', '措施费', '1', 'COST_CODE');

-- ----------------------------
-- Table structure for `tm_lookvalue`
-- ----------------------------
DROP TABLE IF EXISTS `tm_lookvalue`;
CREATE TABLE `tm_lookvalue` (
  `lookvalue_id` int(11) NOT NULL AUTO_INCREMENT,
  `lookvalue_code` varchar(255) NOT NULL,
  `lookvalue_name` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `seq` double DEFAULT NULL,
  `enable_flag` int(1) NOT NULL DEFAULT '1',
  `looktype_id` int(11) NOT NULL,
  PRIMARY KEY (`lookvalue_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tm_lookvalue
-- ----------------------------
INSERT INTO `tm_lookvalue` VALUES ('7', 'GJCCJ', '构件出厂价', '测试', null, '1', '1');
INSERT INTO `tm_lookvalue` VALUES ('8', 'TYGJCL', '通用构件建筑材料费', null, null, '1', '1');
INSERT INTO `tm_lookvalue` VALUES ('9', 'TYSNDCL', '通用构件水暖电部分材料费', null, null, '1', '1');
INSERT INTO `tm_lookvalue` VALUES ('10', 'FC', '辅材费', null, null, '1', '1');
INSERT INTO `tm_lookvalue` VALUES ('11', 'CLYSF', '材料运输费', null, null, '1', '2');
INSERT INTO `tm_lookvalue` VALUES ('12', 'GJCCRG', '构件出厂部分安装人工', null, null, '1', '3');
INSERT INTO `tm_lookvalue` VALUES ('13', 'TYJZRG', '通用构件建筑部分安装人工', null, null, '1', '3');
INSERT INTO `tm_lookvalue` VALUES ('14', 'TYAZAZ', '通用构件水暖电部分安装人工', null, null, '1', '3');
INSERT INTO `tm_lookvalue` VALUES ('15', 'TYGJAZ', '其他用工人工费', null, null, '1', '3');
INSERT INTO `tm_lookvalue` VALUES ('16', 'GJCCJX', '构件出厂部分机械', null, null, '1', '3');
INSERT INTO `tm_lookvalue` VALUES ('17', 'TYJZJX', '通用构件建筑部分机械', null, null, '1', '3');
INSERT INTO `tm_lookvalue` VALUES ('18', 'TYSNDJX', '通用构件水暖电部分机械', null, null, '1', '3');
INSERT INTO `tm_lookvalue` VALUES ('19', 'QIJX', '其它机械费', null, null, '1', '3');
INSERT INTO `tm_lookvalue` VALUES ('20', '1', '1', '2', null, '1', '2');
INSERT INTO `tm_lookvalue` VALUES ('21', 'CLBGF', '材料保管费', null, null, '1', '2');
INSERT INTO `tm_lookvalue` VALUES ('22', 'HCS', '韩测试', '你好吗', null, '1', '2');
INSERT INTO `tm_lookvalue` VALUES ('23', 'a\'sa\'s', '啊撒起', 'w\'q\'w\'q\'w', null, '1', '1');
INSERT INTO `tm_lookvalue` VALUES ('24', '121', '你的项目', '五千万', null, '1', '1');
INSERT INTO `tm_lookvalue` VALUES ('25', 'sa\'a', 'sa\'s', 'sa\'s', null, '0', '4');

-- ----------------------------
-- Table structure for `tm_unitproject`
-- ----------------------------
DROP TABLE IF EXISTS `tm_unitproject`;
CREATE TABLE `tm_unitproject` (
  `id` int(11) NOT NULL COMMENT '主键',
  `code` varchar(255) DEFAULT NULL COMMENT '代码',
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `type` varchar(255) DEFAULT NULL COMMENT '类别',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `amount` double DEFAULT NULL COMMENT '含量',
  `dtgcl` double DEFAULT NULL COMMENT '工程量',
  `single_price` double DEFAULT NULL COMMENT '合价',
  `price` double DEFAULT NULL COMMENT '综合合价',
  `sumprice` double NOT NULL DEFAULT '0' COMMENT '综合合价',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `parentid` int(11) DEFAULT NULL,
  `leaf` int(11) DEFAULT NULL COMMENT '单位工程子目基础表',
  `bit_project_id` int(11) DEFAULT NULL COMMENT '单位工程ID',
  PRIMARY KEY (`id`,`sumprice`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tm_unitproject
-- ----------------------------
INSERT INTO `tm_unitproject` VALUES ('1', '第一章', '建筑工程', null, 'g', null, null, null, null, '0', null, '0', '0', '235');
INSERT INTO `tm_unitproject` VALUES ('2', '第一节', '方管柱', null, 'g', null, null, null, null, '0', null, '1', '1', null);
INSERT INTO `tm_unitproject` VALUES ('3', '1-1', '首层角柱-高度：3010mm  方管柱规格：200mm*200mm*6mm 榫头182*182*5 高度：675mm', null, 'g', null, null, null, null, '0', null, '2', '1', null);
INSERT INTO `tm_unitproject` VALUES ('4', '第二章', '装饰工程', null, 'g', null, null, null, null, '0', null, '0', '0', '235');
INSERT INTO `tm_unitproject` VALUES ('5', '第一节', '装饰', null, 'g', null, null, null, null, '0', null, '4', '1', null);
INSERT INTO `tm_unitproject` VALUES ('6', '2-1', '装饰 序', null, 'g', null, null, null, null, '0', null, '5', '1', null);
INSERT INTO `tm_unitproject` VALUES ('7', '第一章', '建筑工程A', null, 'g', null, null, null, null, '0', null, '0', '1', '227');
INSERT INTO `tm_unitproject` VALUES ('8', '第一节', '方管柱A', null, 'g', null, null, null, null, '0', null, '7', '1', null);
INSERT INTO `tm_unitproject` VALUES ('9', '1-1', '首层角柱AAA', null, 'g', null, null, null, null, '0', null, '8', '1', null);

-- ----------------------------
-- Table structure for `tm_unitproject_base`
-- ----------------------------
DROP TABLE IF EXISTS `tm_unitproject_base`;
CREATE TABLE `tm_unitproject_base` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `unit` varchar(20) DEFAULT NULL,
  `transportfee` double DEFAULT NULL COMMENT '运输费',
  `materialfee` double DEFAULT NULL COMMENT '物料费',
  `installationfee` double DEFAULT NULL COMMENT '安装费',
  `parentid` int(11) DEFAULT NULL,
  `leaf` int(11) DEFAULT NULL COMMENT '单位工程子目基础表',
  PRIMARY KEY (`id`),
  KEY `TUB_PARENTID_NOR_IDX` (`parentid`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tm_unitproject_base
-- ----------------------------
INSERT INTO `tm_unitproject_base` VALUES ('1', '第一章', '建筑工程', '', null, null, null, null, '0');
INSERT INTO `tm_unitproject_base` VALUES ('2', '第一节', '方管柱', null, null, null, null, '1', '1');
INSERT INTO `tm_unitproject_base` VALUES ('3', '1-1', '首层角柱-高度：3010mm  方管柱规格：200mm*200mm*6mm 榫头182*182*5 高度：675mm', '个', '683.6', '200', '300', '2', '1');
INSERT INTO `tm_unitproject_base` VALUES ('4', '1-2', '首层角柱-高度：3030mm  方管柱规格：200mm*200mm*6mm 榫头182*182*5 高度：575mm', '个', '30', '50', '80', '2', '1');
INSERT INTO `tm_unitproject_base` VALUES ('5', '第二节', '组合梁板', '个', null, null, null, '1', '1');
INSERT INTO `tm_unitproject_base` VALUES ('6', '2-1', '楼层板2040*3850：套筒规格：200*98*6、98*98*6', '个', '12', '16', '36', '5', '1');
INSERT INTO `tm_unitproject_base` VALUES ('7', '第三节', '屋面板', '个', null, null, null, '1', '1');
INSERT INTO `tm_unitproject_base` VALUES ('8', '3-1', '屋面板2040*3850 套筒规格：200*98*6、98*98*6 有次梁', '个', '16', '40', '70', '7', '1');
INSERT INTO `tm_unitproject_base` VALUES ('9', '1-3', '汗行啊发的', 'gg', '0', '683.6', '0', '2', '1');
INSERT INTO `tm_unitproject_base` VALUES ('10', '1-4', '啊对发生的佛', 'kk', '0', '3589.8', '85.5', '2', '1');
INSERT INTO `tm_unitproject_base` VALUES ('11', '3-9', '啊时代发生的发', 'asdfasd', '0', '0', '0', '7', '1');

-- ----------------------------
-- Table structure for `tm_unitproject_detail`
-- ----------------------------
DROP TABLE IF EXISTS `tm_unitproject_detail`;
CREATE TABLE `tm_unitproject_detail` (
  `id` int(11) NOT NULL,
  `unitproject_id` int(11) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL COMMENT '类别',
  `name` varchar(255) DEFAULT NULL,
  `type_info` varchar(255) DEFAULT NULL COMMENT '规格/型号',
  `unit` varchar(20) DEFAULT NULL,
  `content` double(11,0) DEFAULT NULL,
  `single_price` double DEFAULT NULL,
  `Market_price` double DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `orig_count` double DEFAULT NULL COMMENT '原始数量',
  `lookvalueid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tm_unitproject_detail
-- ----------------------------
INSERT INTO `tm_unitproject_detail` VALUES ('1', '3', 'CLYSF-100101', null, '运输费用', null, 'gg', '1', '100', '10', '10', null, '11');
INSERT INTO `tm_unitproject_detail` VALUES ('2', '3', 'FC-2004004', null, '基础地脚螺栓', null, 'kg', '1', '200', '10', '10', null, '7');
INSERT INTO `tm_unitproject_detail` VALUES ('3', '6', 'CLYSF-100101', null, '运输费用', null, 'gg', '1', '300', '10', '10', null, '11');
INSERT INTO `tm_unitproject_detail` VALUES ('4', '6', 'FC-2004004', null, '基础地脚螺栓', null, 'gg', '1', '400', '10', '10', null, '7');
INSERT INTO `tm_unitproject_detail` VALUES ('5', '9', 'CLYSF-100101', null, '运输费用', null, 'gg', '1', '600', '10', '10', '1', '11');
INSERT INTO `tm_unitproject_detail` VALUES ('6', '9', 'FC-2004004', null, '基础地脚螺栓', null, 'gg', '1', '800', '10', '10', null, '7');

-- ----------------------------
-- Table structure for `tm_unitproject_detail_base`
-- ----------------------------
DROP TABLE IF EXISTS `tm_unitproject_detail_base`;
CREATE TABLE `tm_unitproject_detail_base` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `unitproject_id` int(11) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `unit` varchar(20) DEFAULT NULL,
  `content` int(11) DEFAULT NULL COMMENT '量含',
  `amount` double DEFAULT NULL COMMENT '数量',
  `noprice` double DEFAULT NULL COMMENT '不含税单价',
  `price` double DEFAULT NULL COMMENT '含税单价',
  `rate` double DEFAULT NULL COMMENT '税率',
  `sumprice` double DEFAULT NULL COMMENT '含税合价',
  `sumnoprice` double DEFAULT NULL COMMENT '不含税合价',
  `lookvalueid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tm_unitproject_detail_base
-- ----------------------------
INSERT INTO `tm_unitproject_detail_base` VALUES ('1', '3', 'CLYSF-100101', '运输费用', 'kg', '1', '10', '8.55', '10', '17', '100', '85.5', '11');
INSERT INTO `tm_unitproject_detail_base` VALUES ('2', '3', 'FC-2004004', '基础地脚螺栓', 'kg', '1', '20', '3', '3', '0', '60', '60', '10');
INSERT INTO `tm_unitproject_detail_base` VALUES ('14', '3', 'GJCCJ-999999', 'han-燕', 'gg', '1', '20', '17.09', '20', '17', '400', '341.8', '7');
INSERT INTO `tm_unitproject_detail_base` VALUES ('15', '3', 'GJCCJ-999999@1', 'han-燕', 'gg', '1', '20', '17.09', '20', '17', '400', '341.8', '7');
INSERT INTO `tm_unitproject_detail_base` VALUES ('16', '3', 'GJCCJ-99999', '小朋友', 'gg', '1', '20', '17.09', '20', '17', '400', '341.8', '7');
INSERT INTO `tm_unitproject_detail_base` VALUES ('17', '3', 'GJCCJ-99999@1', '小朋友', 'gg', '1', '20', '17.09', '20', '17', '400', '341.8', '7');
INSERT INTO `tm_unitproject_detail_base` VALUES ('18', '3', 'GLCCJ-10110', 'han-唐', 'gg', '1', '20', '17.09', '20', '17', '400', '341.8', '7');
INSERT INTO `tm_unitproject_detail_base` VALUES ('19', '3', 'GLCCJ-10110@1', 'han-唐', 'gg', '1', '20', '17.09', '20', '17', '400', '341.8', '7');
INSERT INTO `tm_unitproject_detail_base` VALUES ('20', '10', 'CLYSF-1000', 'han-夏', 'gg', '1', '40', '34.19', '40', '17', '1600', '1367.6', '11');
INSERT INTO `tm_unitproject_detail_base` VALUES ('21', '10', 'CLYSF-1000@1', 'han-夏', 'gg', '1', '40', '34.19', '40', '17', '1600', '1367.6', '11');
INSERT INTO `tm_unitproject_detail_base` VALUES ('22', '10', 'YSCLF-99999', '你好', 'gg', '1', '10', '8.55', '10', '17', '100', '85.5', '11');
INSERT INTO `tm_unitproject_detail_base` VALUES ('23', '10', 'GJCCAZF-99999', 'adsf', 'gg', '2', '10', '8.55', '10', '17', '100', '85.5', '12');
INSERT INTO `tm_unitproject_detail_base` VALUES ('24', '10', 'YSCLF-00000', 'asdfasdf', 'gg', '1', '20', '17.09', '20', '17', '400', '341.8', '11');
INSERT INTO `tm_unitproject_detail_base` VALUES ('25', '10', 'YSCLF-00000@1', 'asdfasdf', 'gg', '1', '20', '17.09', '20', '17', '400', '341.8', '11');
INSERT INTO `tm_unitproject_detail_base` VALUES ('26', '9', 'CLBGF', 'han-清', 'gg', '1', '20', '17.09', '20', '17', '400', '341.8', '21');
INSERT INTO `tm_unitproject_detail_base` VALUES ('27', '9', 'CLBGF@1', 'han-清', 'gg', '1', '20', '17.09', '20', '17', '400', '341.8', '21');
INSERT INTO `tm_unitproject_detail_base` VALUES ('28', '10', 'CLYSF-121212', 'asdfasd', 'gg', '1', '10', '8.55', '10', '17', '100', '85.5', '11');

-- ----------------------------
-- Table structure for `tm_yca_detail`
-- ----------------------------
DROP TABLE IF EXISTS `tm_yca_detail`;
CREATE TABLE `tm_yca_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '运材安详情',
  `lookvalue_id` int(11) NOT NULL,
  `code` varchar(30) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `unit` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tm_yca_detail
-- ----------------------------
INSERT INTO `tm_yca_detail` VALUES ('1', '7', 'GJCCJ-200101', '韩显示方管柱3010mm', '个');
INSERT INTO `tm_yca_detail` VALUES ('2', '7', 'GJCCJ-200102', '李小龙方管柱3030mm寒天', '个');
INSERT INTO `tm_yca_detail` VALUES ('3', '7', 'GJCCJ-200103', '方管柱3040mmadf', '个');
INSERT INTO `tm_yca_detail` VALUES ('4', '10', 'CLYSF', 'ssssHan', '车JJ而非为氛围');
INSERT INTO `tm_yca_detail` VALUES ('5', '10', 'GJCCRG-100101', 'eee去去去去', '个');
INSERT INTO `tm_yca_detail` VALUES ('6', '13', 'TYGJAZ-100501', 'Hanjin', '个');
INSERT INTO `tm_yca_detail` VALUES ('7', '13', 'GJCCJ-3001', 'han测试', 'g');
INSERT INTO `tm_yca_detail` VALUES ('8', '16', 'GLLFL-9999', 'nihao', 'oo');
INSERT INTO `tm_yca_detail` VALUES ('9', '16', 'asdfasd', 'adsf', '阿道夫');
INSERT INTO `tm_yca_detail` VALUES ('10', '7', 'GCCJ-99999', '韩测试111', 'gg');
INSERT INTO `tm_yca_detail` VALUES ('11', '7', '99999', '222', '222');
INSERT INTO `tm_yca_detail` VALUES ('12', '7', 'GJCCJ-11111', '3333adsf', 'hei');
INSERT INTO `tm_yca_detail` VALUES ('13', '7', 'GJCCJ-199999', 'han', 'kg');

-- ----------------------------
-- Procedure structure for `aaaa`
-- ----------------------------
DROP PROCEDURE IF EXISTS `aaaa`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `aaaa`()
BEGIN
	#Routine body goes here...
	select * from tm_unitproject;
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for `pro_get_unitproject`
-- ----------------------------
DROP PROCEDURE IF EXISTS `pro_get_unitproject`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `pro_get_unitproject`(IN `dxprojectid` integer)
BEGIN
	#Routine body goes here...

/*传入 单项工程 id
去 tm_unitproject 找 bit_project_id 为 单项工程id的数据 idlist，--- 单位工程id    相当于章
在去tm_unitproject 找 parentid = idlist 的数据 idchildlist  ---  相当于 节


在去tm_unitproject 找 parentid = idchildlist 的数据  idDetaillist --- 相当于 序



在去tm_unitproject_detail 里把 unitproject_id = idDetaillist 的数据查出，按照 lookvalueid 分组


第二中情况


tm_unitproject 直接存在 运材安数据， 跟序一个层级，如果lookvalueid存在 在 直接算和*/
select * from tm_unitproject tuc where tuc.parentid in
(select * from tm_unitproject tub where tub.parentid in
(select * from tm_unitproject tua where tua.parentid in
	( select tu.id FROM tm_unitproject tu where tu.bit_project_id = dxprojectid)
));




END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for `proc_adder`
-- ----------------------------
DROP PROCEDURE IF EXISTS `proc_adder`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `proc_adder`(IN a int, IN b int, OUT sum int)
BEGIN
    #Routine body goes here...

    DECLARE c int;
    if a is null then set a = 0; 
    end if;
  
    if b is null then set b = 0;
    end if;

    set sum  = a + b;
END
;;
DELIMITER ;

-- ----------------------------
-- Function structure for `currval`
-- ----------------------------
DROP FUNCTION IF EXISTS `currval`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `currval`(v_seq_name VARCHAR(50)) RETURNS int(11)
begin     
		declare value integer;       
		set value = 0;       
		select current_val into value  from th_seq where seq_name = v_seq_name; 
	   return value; 
	end
;;
DELIMITER ;

-- ----------------------------
-- Function structure for `nextval`
-- ----------------------------
DROP FUNCTION IF EXISTS `nextval`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `nextval`(v_seq_name VARCHAR(50)) RETURNS int(11)
begin
		DECLARE x int;
		set x = 0;   
		select count(1) INTO x from th_seq where seq_name = v_seq_name ;
	
		IF x > 0 THEN	
			update th_seq set current_val = current_val + increment_val  where seq_name = v_seq_name;
		ELSE
			insert th_seq (seq_name, current_val, increment_val) values (v_seq_name, 1, 1);
		end IF;
	
		return currval(v_seq_name);
	end
;;
DELIMITER ;
