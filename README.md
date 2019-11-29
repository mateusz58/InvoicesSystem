


![js-standard-style](https://img.shields.io/badge/code%20style-Google_Style-brightgreen.svg?style=flat)
![js-standard-style](https://img.shields.io/badge/coverage-80%25-green)
![js-standard-style](https://img.shields.io/badge/build-passing-green)
![js-standard-style](https://img.shields.io/badge/release-v1.0.0-blue)
![js-standard-style](https://img.shields.io/badge/license-MIT-green)


<img src="https://i.ibb.co/sHqWSB6/logoinvoice2.png" width = 200 align="right" />

# Invoice manager

## Table of Contents
  - [Table of Contents](#table-of-contents)
  - [About The Project](#about-the-project)
  - [Technology stack](#technology-stack)
  - [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
  - [API Reference](#api-reference)
  - [License](#license)
  - [Contact](#contact)
## About The Project

![](https://i.ibb.co/pKWvDx2/Screenshot-5.png)

An open-source invoicing system built with modern spring boot backend technology along with front end created with one of the most popular front-end framework Angular.js. The system allows adding, removing, modifying invoices to the system with the possibility of generating pdf documents based on informations received via REST API and sending pdf documents on given email. Additionally, the system support authentication and authorization of multiple users based on their ascribed roles using tokenization method provided by OAuth 2.0 technology witch each user role having a different kind of permissions of invoices management.

## Technology stack

<img src="https://whirly.pl/wp-content/uploads/2017/05/spring.png" width="200"><img src="https://upload.wikimedia.org/wikipedia/commons/2/2c/Mockito_Logo.png" width="200">
<img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTNkximiwITI1smJcOkn_bx2Zk_RnNKnmDq23Ua26wTVd_YNJcWgw" width="200">
<img src="https://junit.org/junit4/images/junit5-banner.png" width="200">
<img src="https://jules-grospeiller.fr/media/logo_competences/lang/json.png" width="200">
<img src="http://www.postgresqltutorial.com/wp-content/uploads/2012/08/What-is-PostgreSQL.png" width="200">
<img src="https://cdn.bulldogjob.com/system/readables/covers/000/001/571/thumb/27-02-2019.png" width="200">
<img src="https://i2.wp.com/bykowski.pl/wp-content/uploads/2018/07/hibernate-2.png?w=300" width="200">
<img src="https://zdnet3.cbsistatic.com/hub/i/r/2018/02/16/8abdb3e1-47bc-446e-9871-c4e11a46f680/resize/370xauto/8a68280fd20eebfa7789cdaa6fb5eff1/mongo-db-logo.png" width="200"><img src="http://mapstruct.org/images/mapstruct.png" width="200"><img src="https://miro.medium.com/max/494/0*QWNG5EAnPSaUSAHH.png"  width="200">
<img src="https://solidsoft.files.wordpress.com/2014/04/awaitility_logo_red_small.png?w=584"  width="200"><img src="https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcSwSU80GnkMvuNnZxrl2NZQcfaKY9etA5QmMSZHlDSlxWhSpGMb"  width="200"><img src="https://miro.medium.com/max/630/1*j_zP74-cpvXRcs8dM_pkMQ.jpeg"  width="200">
<img src="https://jrebel.com/wp-content/uploads/2017/07/test-containers-java-logo.png"  width="200">
<img src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/cf/Angular_full_color_logo.svg/250px-Angular_full_color_logo.svg.png"  width="200">
<img src="https://i.ibb.co/bzf4Hnv/itext.png"  width="200">



## Getting Started

### Prerequisites

* [java 11](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
* [PostgreSql version 11.5](https://www.postgresql.org/download/)
* [MongoDb](https://docs.mongodb.com/manual/release-notes/4.0/)
* [Maven 3.6.3](https://maven.apache.org/download.cgi)

### Installation

1. Clone the repo
```sh
$ git clone https://github.com/mateusz58/InvoicesSystem.git
$ cd InvoiceSystem
```
2. Set up initial build of project
```sh
$ mvn build
```

3. Modify application.properties value of “server.port” to port on which you wish to launch application
4. Modify application.properties value of “pl.coderstrust.database” to one of the possible databases which project implements which are: in-memory, in-file, hibernate, jdbc, mongo
5. Launch application with the following command
```sh
$ mvn run
```

## API Reference

### Admin panel

  - `admin/*` administration panel for managing users

### invoice related

- `/invoices/`<br> display/add/delete/modify invoices stored in database
- `invoices/{id}`<br> get/delete/modify invoice based on given id
- `invoices/byNumber?=<number>`<br> display/delete/modify invoice based on its number
  
### User management related

- `/users/`<br> get/delete/add users
- `/users/byEmail?=`<br> get/delete/modify user by email

## License

[MIT](https://tldrlegal.com/license/mit-license)

## Contact

  - Email: matp321@gmail.com

- Project Link: [https://github.com/mateusz58/Parking_Server.git](https://github.com/your_username/repo_name)
