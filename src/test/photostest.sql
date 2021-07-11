-- MySQL dump 10.13  Distrib 8.0.20, for Win64 (x86_64)
--
-- Host: localhost    Database: photostest
-- ------------------------------------------------------
-- Server version	8.0.20

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `collections`
--

DROP TABLE IF EXISTS `collections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `collections` (
  `id` binary(16) NOT NULL,
  `name` varchar(30) DEFAULT NULL,
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `collections`
--

LOCK TABLES `collections` WRITE;
/*!40000 ALTER TABLE `collections` DISABLE KEYS */;
INSERT INTO `collections` VALUES (_binary '_É½\âU\ëƒ\Ë\È`\00x,','circuit'),(_binary '„U§\âU\ëƒ\Ë\È`\00x,','vertical');
/*!40000 ALTER TABLE `collections` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `photocollections`
--

DROP TABLE IF EXISTS `photocollections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `photocollections` (
  `collectionID` binary(16) NOT NULL,
  `photoID` binary(16) NOT NULL,
  KEY `photoID` (`photoID`),
  KEY `collectionID` (`collectionID`),
  CONSTRAINT `photocollections_ibfk_1` FOREIGN KEY (`photoID`) REFERENCES `photos` (`id`),
  CONSTRAINT `photocollections_ibfk_2` FOREIGN KEY (`collectionID`) REFERENCES `collections` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `photocollections`
--

LOCK TABLES `photocollections` WRITE;
/*!40000 ALTER TABLE `photocollections` DISABLE KEYS */;
INSERT INTO `photocollections` VALUES (_binary '„U§\âU\ëƒ\Ë\È`\00x,',_binary '¹‚”\âU\ëƒ\Ë\È`\00x,'),(_binary '„U§\âU\ëƒ\Ë\È`\00x,',_binary '»9\âU\ëƒ\Ë\È`\00x,'),(_binary '_É½\âU\ëƒ\Ë\È`\00x,',_binary '»\rj\âU\ëƒ\Ë\È`\00x,');
/*!40000 ALTER TABLE `photocollections` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `photos`
--

DROP TABLE IF EXISTS `photos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `photos` (
  `thumbnailPath` varchar(200) DEFAULT NULL,
  `photoPath` varchar(200) DEFAULT NULL,
  `tags` varchar(200) NOT NULL,
  `decade` enum('1950s','1960s','1970s','1980s') DEFAULT NULL,
  `comment` varchar(200) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `photoPath` (`photoPath`),
  UNIQUE KEY `thumbnailPath` (`thumbnailPath`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `photos`
--

LOCK TABLES `photos` WRITE;
/*!40000 ALTER TABLE `photos` DISABLE KEYS */;
INSERT INTO `photos` VALUES ('images/thumbnails/folder2/sorasak-KxCJXXGsv9I-unsplash.jpg','images/fullsize/folder2/sorasak-KxCJXXGsv9I-unsplash.jpg','buildings','1960s',NULL,NULL,_binary '¹‚”\âU\ëƒ\Ë\È`\00x,'),('images/thumbnails/folder2/subfolder1/marliese-streefland-2l0CWTpcChI-unsplash.jpg','images/fullsize/folder2/subfolder1/marliese-streefland-2l0CWTpcChI-unsplash.jpg','animals,dogs',NULL,NULL,NULL,_binary 'º\à\Ï\âU\ëƒ\Ë\È`\00x,'),('images/thumbnails/folder2/umberto-jXd2FSvcRr8-unsplash.jpg','images/fullsize/folder2/umberto-jXd2FSvcRr8-unsplash.jpg','circuits',NULL,NULL,NULL,_binary '»\rj\âU\ëƒ\Ë\È`\00x,'),('images/thumbnails/folder1/laura-college-K_Na5gCmh38-unsplash.jpg','images/fullsize/folder1/laura-college-K_Na5gCmh38-unsplash.jpg','animals','1980s','this is a comment',NULL,_binary '»9\âU\ëƒ\Ë\È`\00x,'),('images/thumbnails/folder1/lily-banse--YHSwy6uqvk-unsplash.jpg','images/fullsize/folder1/lily-banse--YHSwy6uqvk-unsplash.jpg','food',NULL,NULL,'1975-01-01',_binary '»dV\âU\ëƒ\Ë\È`\00x,');
/*!40000 ALTER TABLE `photos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tags`
--

DROP TABLE IF EXISTS `tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tags` (
  `tagName` varchar(20) DEFAULT NULL,
  `displayName` varchar(30) DEFAULT NULL,
  `category` enum('Natural','Manmade') DEFAULT NULL,
  `id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tags`
--

LOCK TABLES `tags` WRITE;
/*!40000 ALTER TABLE `tags` DISABLE KEYS */;
INSERT INTO `tags` VALUES ('buildings','Buildings','Manmade',_binary '\çQV\á\ë…\ó\È`\00x,'),('circuits','Circuits','Manmade',_binary '^^\á\ë…\ó\È`\00x,'),('food','Food','Natural',_binary '0(\ó\á\ë…\ó\È`\00x,'),('dogs','Dogs','Natural',_binary '\\ª\á\ë…\ó\È`\00x,'),('animals','Animals','Natural',_binary '\÷{û\Ì\á\ë…\ó\È`\00x,');
/*!40000 ALTER TABLE `tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tagwhitelist`
--

DROP TABLE IF EXISTS `tagwhitelist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tagwhitelist` (
  `id` binary(16) NOT NULL,
  KEY `id` (`id`),
  CONSTRAINT `tagwhitelist_ibfk_1` FOREIGN KEY (`id`) REFERENCES `tags` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tagwhitelist`
--

LOCK TABLES `tagwhitelist` WRITE;
/*!40000 ALTER TABLE `tagwhitelist` DISABLE KEYS */;
INSERT INTO `tagwhitelist` VALUES (_binary '\çQV\á\ë…\ó\È`\00x,');
/*!40000 ALTER TABLE `tagwhitelist` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-07-11 12:35:53
