-- phpMyAdmin SQL Dump
-- version 3.3.3
-- http://www.phpmyadmin.net
--
-- Host: mysql.iu.edu:3537
-- Generation Time: Mar 12, 2015 at 12:53 PM
-- Server version: 5.0.83
-- PHP Version: 5.3.26

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `builder`
--

-- --------------------------------------------------------

--
-- Table structure for table `player`
--

CREATE TABLE IF NOT EXISTS `player` (
  `playerID` mediumint(9) NOT NULL auto_increment,
  `score` mediumint(9) NOT NULL,
  `password` mediumtext NOT NULL,
  `email` mediumtext NOT NULL,
  `name` mediumtext NOT NULL,
  `shareScore` tinyint(4) default NULL,
  `handedness` tinyint(4) NOT NULL,
  `gender` tinyint(4) NOT NULL,
  `ethnicity` tinyint(4) NOT NULL,
  `race` tinyint(4) NOT NULL,
  `realName` text NOT NULL,
  KEY `playerID` (`playerID`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=15666 ;
