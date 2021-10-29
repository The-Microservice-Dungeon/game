Prerequisites:
1. GitHub Pro -> https://education.github.com/pack?utm_source=github+jetbrains
2. IntelliJ IDEA Ultimate -> https://www.jetbrains.com/idea/download/#section=windows
3. Gitkraken -> https://www.gitkraken.com/ (one of the best Git-Gui)
(2-3 free use with GitHub Pro)
4. Docker -> https://www.docker.com/get-started

1-3 not essential but making it so much easier

###

Steps:

After Docker installation:
1. In Terminal:
    docker run -p 3306:3306 --name test -e MYSQL_ROOT_PASSWORD=root -d  mysql:8

Explanation:
-p 3306:3306 -> Open port needed for src...application.properties
--name test -> name of container in Docker
MYSQL_ROOT_PASSWORD=root -> password from src...application.properties

2. Connect to DB (IntelliJ IDEA Ultimate)
3. Run in Console (for given example in src...testingExample):

CREATE DATABASE kotlin_demo;

CREATE TABLE `articles` (
    `article_id` int(11) NOT NULL AUTO_INCREMENT,
    `title` varchar(45) NOT NULL,
    `contents` longtext,
    PRIMARY KEY (`article_id`),
    UNIQUE KEY `id_UNIQUE` (`article_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=big5;

INSERT INTO kotlin_demo.articles(title,contents)
VALUES ('Spring Kotlin demo', 'This Article talks about sample application with spring boot and Kotlin');

4. Stop Container (for example over GUI)
    -> if you restart everything stays saved
    -> if you delete you have to repeat steps 1-4