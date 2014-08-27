/*
Navicat MySQL Data Transfer

Source Server         : locahost
Source Server Version : 50151
Source Host           : localhost:3306
Source Database       : weibo

Target Server Type    : MYSQL
Target Server Version : 50151
File Encoding         : 65001

Date: 2013-08-29 16:30:04
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for cookie
-- ----------------------------
DROP TABLE IF EXISTS `cookie`;
CREATE TABLE `cookie` (
  `cookie_id` int(11) NOT NULL AUTO_INCREMENT,
  `cookie_user_name` varchar(45) NOT NULL COMMENT '该cookie对应账号的用户名',
  `cookie_user_password` varchar(45) NOT NULL COMMENT '该cookie对应账号的密码',
  `cookie_value_text` text COMMENT '该cookie的内容',
  `update_status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`cookie_id`),
  UNIQUE KEY `cookie_id_UNIQUE` (`cookie_id`),
  UNIQUE KEY `cookie_user_name_UNIQUE` (`cookie_user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for crawl_task
-- ----------------------------
DROP TABLE IF EXISTS `crawl_task`;
CREATE TABLE `crawl_task` (
  `cra_id` int(11) NOT NULL AUTO_INCREMENT,
  `cra_keyword` varchar(255) NOT NULL COMMENT '该任务的关键词',
  `cra_orig_time` datetime DEFAULT '2013-07-01 09:39:49' COMMENT '搜索该关键词的起始时间',
  `cra_gap` int(11) NOT NULL DEFAULT '20' COMMENT '该任务的抓取间隔(分钟)',
  `cra_status` int(11) NOT NULL DEFAULT '1' COMMENT '任务状态，0表示还不能抓取，1表示可以抓取，2表示暂时不能抓取，等待下一轮抓取机会 ',
  `cra_lastcrawl_time` datetime DEFAULT NULL COMMENT '上一轮抓取时间',
  `cra_is_end` int(11) NOT NULL DEFAULT '0' COMMENT '0表示本轮已经结束,1表示正在抓取，本轮还没有结束',
  `start_time` datetime DEFAULT NULL COMMENT '搜索参数时间段开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '搜索参数时间段的结束时间',
  `next_page` int(11) DEFAULT '1' COMMENT '下一页',
  `last_weibo_time` datetime DEFAULT NULL COMMENT '第50页最后一条微博时间',
  `temp_time` datetime DEFAULT NULL COMMENT '临时时间记录',
  `first_wb_time` datetime DEFAULT NULL,
  PRIMARY KEY (`cra_id`),
  UNIQUE KEY `cra_id_UNIQUE` (`cra_id`)
) ENGINE=InnoDB AUTO_INCREMENT=499901 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for test
-- ----------------------------
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test` (
  `id` int(11) NOT NULL,
  `name` varchar(10) DEFAULT NULL,
  `time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for weibo
-- ----------------------------
DROP TABLE IF EXISTS `weibo`;
CREATE TABLE `weibo` (
  `weibo_mid` varchar(45) NOT NULL COMMENT '微博的id，字符串',
  `weibo_user_name` varchar(45) NOT NULL COMMENT '作者名',
  `weibo_user_image` varchar(100) NOT NULL COMMENT '作者头像',
  `weibo_context` varchar(256) NOT NULL COMMENT '微博内容',
  `weibo_url` varchar(256) NOT NULL COMMENT '微博地址',
  `weibo_time` datetime NOT NULL COMMENT '微博时间',
  `weibo_repost_count` int(11) DEFAULT NULL COMMENT '转发数',
  `weibo_comment_count` int(11) DEFAULT NULL COMMENT '微博评论数',
  `weibo_from` varchar(45) DEFAULT NULL COMMENT '微博来源',
  `weibo_domain` varchar(45) NOT NULL COMMENT '该作者的微博主页',
  `weibo_inner_html` varchar(100) DEFAULT NULL COMMENT '该微博的评论数统计信息等',
  `book_id` int(4) NOT NULL DEFAULT '0' COMMENT '图书id',
  `book_title` varchar(255) DEFAULT '' COMMENT '图书标题',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`weibo_mid`),
  UNIQUE KEY `weibo_idstr_UNIQUE` (`weibo_mid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for weibo_tmp
-- ----------------------------
DROP TABLE IF EXISTS `weibo_tmp`;
CREATE TABLE `weibo_tmp` (
  `weibo_mid` varchar(45) NOT NULL COMMENT '微博的id，字符串',
  `weibo_user_name` varchar(45) NOT NULL COMMENT '作者名',
  `weibo_user_image` varchar(100) NOT NULL COMMENT '作者头像',
  `weibo_context` varchar(256) NOT NULL COMMENT '微博内容',
  `weibo_url` varchar(256) NOT NULL COMMENT '微博地址',
  `weibo_time` datetime NOT NULL COMMENT '微博时间',
  `weibo_repost_count` int(11) DEFAULT NULL COMMENT '转发数',
  `weibo_comment_count` int(11) DEFAULT NULL COMMENT '微博评论数',
  `weibo_from` varchar(45) DEFAULT NULL COMMENT '微博来源',
  `weibo_domain` varchar(45) NOT NULL COMMENT '该作者的微博主页',
  `weibo_inner_html` varchar(100) DEFAULT NULL COMMENT '该微博的评论数统计信息等',
  `book_id` int(4) NOT NULL DEFAULT '0' COMMENT '图书id',
  `book_title` varchar(255) DEFAULT '' COMMENT '图书标题',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`weibo_mid`),
  UNIQUE KEY `weibo_idstr_UNIQUE` (`weibo_mid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
