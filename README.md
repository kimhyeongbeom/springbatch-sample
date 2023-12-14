이작업은 유투브 https://www.youtube.com/@BoualiAli 에서 강의한 Spring Batch작업을 기반으로 작성된 것입니다.

자세한 내용을 해당 유투브를 보시면 자세히 나와 있습니다.

# Spring Batch 작업 기준
* IDE : 저는 VSCode 로 작업 (이클립스도 상관없음)
* Spring Boot Version : 3.2.0
* JDK : 17
* 프로젝트관리 : maven
* DB : postgreSQL 16


# 제작하는 Sample 내용
students.csv에 들어있는 학생정보를 student table에 등록하는 배치 프로그램을 제작한다.
해당 프로그램 수행방법
1. post방식으로 http://localhost:9090/students 로 접속하면 수행
2. cron으로 매일 13시 30분 58초에 자동으로 수행


2023-12-14 작성
