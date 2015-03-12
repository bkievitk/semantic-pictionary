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
-- Table structure for table `models`
--

CREATE TABLE IF NOT EXISTS `models` (
  `modelID` mediumint(9) NOT NULL auto_increment,
  `wordID` mediumint(9) NOT NULL,
  `playerID` mediumint(9) NOT NULL,
  `representation` mediumtext NOT NULL,
  `gameType` text NOT NULL,
  `time` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `timesSolved` int(11) NOT NULL,
  PRIMARY KEY  (`modelID`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=76351 ;
