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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

-- Dumping data for table project.assets: ~5 rows (approximately)
/*!40000 ALTER TABLE `assets` DISABLE KEYS */;
REPLACE INTO `assets` (`AssetId`, `AssetName`, `AssetQuantity`, `OrganisationId`) VALUES
	(1, 'CPU HOURS', 90, 1),
	(2, 'Hourly Unlimited Bandwidth ', 100, 2),
	(3, 'Sensor Data', 100, 3),
	(4, 'User Data', 100, 4),
	(5, 'Kilowatt/hour', 1000, 5),
	(6, 'International Calls - hourly', 1000, 6);
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
  `OrderTimestamp` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`OrderID`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

-- Dumping data for table project.orders: ~11 rows (approximately)
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
REPLACE INTO `orders` (`OrderID`, `OrderType`, `UserID`, `AssetID`, `Quantity`, `Price`, `OrderStatusID`, `OrderTimestamp`) VALUES
	(3, 1, 2, 1, 10, 10, 2, '2021-06-05 23:29:19'),
	(4, 2, 1, 1, 10, 10, 2, '2021-06-05 23:29:54'),
	(5, 2, 2, 1, 1, 100, 2, '2021-06-05 23:31:56'),
	(6, 1, 1, 1, 1, 100, 2, '2021-06-05 23:42:41'),
	(7, 1, 1, 1, 100, 200, 1, '2021-06-06 00:25:59'),
	(8, 2, 1, 1, 5, 5, 1, '2021-06-06 00:26:12'),
	(9, 1, 2, 2, 1, 1, 1, '2021-06-06 00:26:41'),
	(10, 1, 2, 2, 100, 100, 0, '2021-06-06 00:34:54');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;

-- Dumping structure for table project.orderstatus
DROP TABLE IF EXISTS `orderstatus`;
CREATE TABLE IF NOT EXISTS `orderstatus` (
  `OrderStatusId` int(11) NOT NULL,
  `OrderStatusType` char(255) NOT NULL,
  PRIMARY KEY (`OrderStatusId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table project.orderstatus: ~6 rows (approximately)
/*!40000 ALTER TABLE `orderstatus` DISABLE KEYS */;
REPLACE INTO `orderstatus` (`OrderStatusId`, `OrderStatusType`) VALUES
	(0, ''),
	(1, 'Unfulfilled'),
	(2, 'Completed');
/*!40000 ALTER TABLE `orderstatus` ENABLE KEYS */;

-- Dumping structure for table project.ordertypes
DROP TABLE IF EXISTS `ordertypes`;
CREATE TABLE IF NOT EXISTS `ordertypes` (
  `OrderTypeID` int(11) NOT NULL AUTO_INCREMENT,
  `OrderTypeName` varchar(255) NOT NULL,
  PRIMARY KEY (`OrderTypeID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

-- Dumping data for table project.organisations: ~2 rows (approximately)
/*!40000 ALTER TABLE `organisations` DISABLE KEYS */;
REPLACE INTO `organisations` (`OrganisationId`, `OrganisationName`, `Credits`, `Pending`) VALUES
	(1, 'Computer Cluster', 600, 300),
	(2, 'Network Cluster', 979, 101),
	(3, 'Sensor Cluster', 1000, 0),
	(4, 'IoT Cluster', 1000, 0),
	(5, 'Energy Cluster', 1000, 0),
	(6, 'Telecommunication Cluster', 1000, 0);
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- Dumping data for table project.transactions: ~0 rows (approximately)
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
REPLACE INTO `transactions` (`TransactionId`, `SellOrderID`, `BuyOrderID`, `TransactionTimeStamp`) VALUES
	(1, 4, 3, '2021-06-05 23:31:39'),
	(2, 5, 6, '2021-06-06 00:18:27');
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

-- Dumping data for table project.users: ~6 rows (approximately)
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
REPLACE INTO `users` (`UserId`, `FirstName`, `LastName`, `UserName`, `PASSWORD`, `OrganisationId`, `PermID`) VALUES
	(1, 'Andrew', 'Wilks', 'aWilks', '1000:06dd90a664774b3a2c826033344cabbce4b9fc822653591f:fd38f6dc50ed31e9d47841ab72fa689b1b636831cebd1e29', 1, 1),
	(2, 'Libby', 'Robinson', 'lRobinson', '1000:bc1543e4a5b5ec8dd93960d884f1949acf9ed175a99048ee:bb21436cc96a48773c8f693f322e0a8324ac2b6df986b3c7', 1, 1),
	(3, 'Ash', 'Phillips', 'APhillips', '1000:0b915acc886a5978a2feb26824c26f58018025dabdf1d968:5ad437b715fab833c8cbc58775a2d9e260743f9e0e298d54', 3, 2),
	(4, 'Sasha', 'Le', 'sLe', '1000:920ded231207802a826954ac6cbbe11972f7ad6d42764b5d:8eb7e0b9c54de524aa7dac32e676c1f71cba7ec07505886c', 4, 2),
	(5, 'Administrator', 'Account', 'Admin', '1000:91d0e058f5e80ec963014e94d611e6bdf223c60f29777f47:079b130f3ef42e4422fbe1e858e7205c37f68f879ff7825e', 5, 1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
