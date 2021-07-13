# About This Project

This project's goal is to be a webapp that allows for easy searching of photos
tagged by category and date.

# Dependencies

This project uses Maven to manage most dependencies. There are, of course, other
dependencies. As written currently, it relies on Tomcat 10 (not any earlier 
version because of the jakarta renaming), and MySQL (support for mariaDB, at a
minimum, is planned).

# Configuration to run

## context.xml

In order to run this app, configuration needs to be placed in 
META-INF/context.xml. What needs to be placed is:
* The URL, username, and password for a database user that has search, update, 
and insert permissions.
* The URL, username, and password for a database user that has search permissions
only.
* The location of the images. The folder put here must have two subfolders: 
fullsize and thumbnails. The fullsize folder must have the fullsize versions of
the photos, and the thumbnails folder must have the photos at thumbnail size.

## Database

Currently, the database needs to be hand created for the app to work. Create table
statements for the necessary tables are in doc.txt. Note, however, that the
tags category enum as well as the photos decade enum will **need** to be 
modified to suit your needs.

Also note that one of the tags must be called "None"; otherwise, photos which
have no tags will not be added to the database.

# Info for Tests

## Unit Tests

Some of the unit tests rely on a database. For these, a database must be created.
It can be pulled from photostest.sql or from the photos in the test folder and
using the update database section of the webapp. Note the .sql file itself isn't 
used in the test it exists for convenience.

A valid database username and password must be specified in the json 
configuration, see below.

## Integration Tests

In order for the integration test to work, the website needs to be deployed so
it is possible to visit it (locally). Other things, like a webdriver for Chrome,
are also required. The configuration for this (as well as for the unit tests) 
needs to be in src/test/TestConfig.json. There is a sample configuration in the 
TestConfigSample.json file.

## TestConfig.json

The fields are:

* testURL: Must be the URL where the test site is. Should be local and end 
in /FamilyPhotos (no trailing slash)
* viewCredential: The value is an object with the keys *username* and *password*.
This is the login that allows a user to view all the photos
* limitedCredential: The value is an object as in above. This is the login that allows
a user to view only certain photos.
* chromeWebDriver: Location of a webdriver for chrome.
* geckoWebDriver: Location of a webdriver for firefox. Having at least one of these
two set is required. If both driver locations are set it will run the test with 
Chrome.
* testDatabaseURL: URL where to locate the test database. Should be loaded from
the photostest.sql file or loaded from the images in the test folder from the
app itself.
* testDatabaseCredential: The value is a credential object. Needs to have read
access to the test database.