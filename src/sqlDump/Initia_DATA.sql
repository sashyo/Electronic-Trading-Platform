-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.5.9-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             11.0.0.5919
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for project
DROP DATABASE IF EXISTS `project`;
CREATE DATABASE IF NOT EXISTS `project` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `project`;

-- Dumping structure for table project.assets
DROP TABLE IF EXISTS `assets`;
CREATE TABLE IF NOT EXISTS `assets` (
  `AssetId` int(11) NOT NULL AUTO_INCREMENT,
  `AssetName` varchar(255) DEFAULT NULL,
  `AssetQuantity` int(11) DEFAULT NULL,
  `OrganisationId` int(11) DEFAULT NULL,
  PRIMARY KEY (`AssetId`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- Dumping data for table project.assets: ~0 rows (approximately)
/*!40000 ALTER TABLE `assets` DISABLE KEYS */;
REPLACE INTO `assets` (`AssetId`, `AssetName`, `AssetQuantity`, `OrganisationId`) VALUES
	(1, 'CPU HOURS', 100, 1);
/*!40000 ALTER TABLE `assets` ENABLE KEYS */;

-- Dumping structure for table project.orders
DROP TABLE IF EXISTS `orders`;
CREATE TABLE IF NOT EXISTS `orders` (
  `OrderID` int(11) NOT NULL AUTO_INCREMENT,
  `OrderType` int(11) NOT NULL,
  `UserID` int(11) NOT NULL,
  `AssetID` int(11) NOT NULL,
  `Quantity` int(11) NOT NULL,
  `Price` int(11) NOT NULL,
  `OrderStatusID` int(11) DEFAULT 0,
  `OrderTimestamp`datetime DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (`OrderID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table project.orders: ~0 rows (approximately)
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;

-- Dumping structure for table project.orderstatus
DROP TABLE IF EXISTS `orderstatus`;
CREATE TABLE IF NOT EXISTS `orderstatus` (
  `OrderStatusId` int(11) NOT NULL,
  `OrderStatusType` char(255) NOT NULL,
  PRIMARY KEY (`OrderStatusId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table project.orderstatus: ~5 rows (approximately)
/*!40000 ALTER TABLE `orderstatus` DISABLE KEYS */;
REPLACE INTO `orderstatus` (`OrderStatusId`, `OrderStatusType`) VALUES
	(0, null),
	(1, 'Unfulfilled'),
	(2, 'Completed');
/*!40000 ALTER TABLE `orderstatus` ENABLE KEYS */;

-- Dumping structure for table project.ordertypes
DROP TABLE IF EXISTS `ordertypes`;
CREATE TABLE IF NOT EXISTS `ordertypes` (
  `OrderTypeID` int(11) NOT NULL AUTO_INCREMENT,
  `OrderTypeName` varchar(255) NOT NULL,
  PRIMARY KEY (`OrderTypeID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table project.ordertypes: ~2 rows (approximately)
/*!40000 ALTER TABLE `ordertypes` DISABLE KEYS */;
REPLACE INTO `ordertypes` (`OrderTypeID`, `OrderTypeName`) VALUES
	(1, 'BUY'),
	(2, 'SELL');
/*!40000 ALTER TABLE `ordertypes` ENABLE KEYS */;

-- Dumping structure for table project.organisations
DROP TABLE IF EXISTS `organisations`;
CREATE TABLE IF NOT EXISTS `organisations` (
  `OrganisationId` int(11) NOT NULL AUTO_INCREMENT,
  `OrganisationName` varchar(255) DEFAULT NULL,
  `Credits` int(11) DEFAULT 0,
  `Pending` int(11) DEFAULT 0,
  PRIMARY KEY (`OrganisationId`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- Dumping data for table project.organisations: ~0 rows (approximately)
/*!40000 ALTER TABLE `organisations` DISABLE KEYS */;
REPLACE INTO `organisations` (`OrganisationId`, `OrganisationName`, `Credits`, `Pending`) VALUES
	(1, 'Computer Cluster', 1000, 0);
/*!40000 ALTER TABLE `organisations` ENABLE KEYS */;

-- Dumping structure for table project.permissions
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE IF NOT EXISTS `permissions` (
  `PermId` int(11) NOT NULL AUTO_INCREMENT,
  `PermType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`PermId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

-- Dumping data for table project.permissions: ~3 rows (approximately)
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
REPLACE INTO `permissions` (`PermId`, `PermType`) VALUES
	(1, 'Administrator'),
	(2, 'User');
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;

-- Dumping structure for table project.transactions
DROP TABLE IF EXISTS `transactions`;
CREATE TABLE IF NOT EXISTS `transactions` (
  `TransactionId` int(11) NOT NULL AUTO_INCREMENT,
  `SellOrderID` int(11) DEFAULT NULL,
  `BuyOrderID` int(11) DEFAULT NULL,
  `TransactionTimeStamp` datetime NOT NULL,
  PRIMARY KEY (`TransactionId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table project.transactions: ~0 rows (approximately)
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;

-- Dumping structure for table project.users
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `UserId` int(11) NOT NULL AUTO_INCREMENT,
  `FirstName` varchar(50) DEFAULT NULL,
  `LastName` varchar(50) DEFAULT NULL,
  `UserName` varchar(60) NOT NULL,
  `PASSWORD` char(128) DEFAULT NULL,
  `OrganisationId` int(11) DEFAULT NULL,
  `PermID` int(11) DEFAULT NULL,
  PRIMARY KEY (`UserId`),
  UNIQUE KEY `UserName` (`UserName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table project.users: ~1 rows (approximately)
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
REPLACE INTO `users` (`FirstName`, `LastName`, `UserName`, `PASSWORD`, `OrganisationId`, `PermID`) VALUES
	('Administrator', 'Account', 'admin','1000:cf5d78c61a7467b7e95901b7d036c55c29170d65e6f62a8b:670195144f3ab07c05267e9c2d871cc7ffcc58e91cb44585', 1, 1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
