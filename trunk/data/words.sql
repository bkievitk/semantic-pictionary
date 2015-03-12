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
-- Table structure for table `words`
--

CREATE TABLE IF NOT EXISTS `words` (
  `wordID` mediumint(9) NOT NULL auto_increment,
  `word` tinytext NOT NULL,
  `correctGuesses` mediumint(9) NOT NULL,
  `incorrectGuesses` mediumint(9) NOT NULL,
  `fromDataSet` int(11) NOT NULL,
  PRIMARY KEY  (`wordID`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1040 ;
