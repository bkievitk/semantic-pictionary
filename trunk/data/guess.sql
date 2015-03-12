-- phpMyAdmin SQL Dump
-- version 3.3.3
-- http://www.phpmyadmin.net
--
-- Host: mysql.iu.edu:3537
-- Generation Time: Mar 12, 2015 at 12:54 PM
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
-- Table structure for table `guess`
--

CREATE TABLE IF NOT EXISTS `guess` (
  `guesserID` mediumint(9) NOT NULL,
  `modelID` mediumint(9) NOT NULL,
  `guessID` mediumint(9) NOT NULL,
  `direction` tinyint(4) NOT NULL,
  `gameType` text NOT NULL,
  `time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
