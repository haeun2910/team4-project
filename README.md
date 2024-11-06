# 일정 관리 웹 서비스

현대인의 바쁜 일상을 위해 일정과 이동 경로를 효율적으로 관리해주는 서비스를 구현하였습니다. 사용자는 자신의 일정을 입력하고, 최적의 이동 방법과 출발 시간을 추천받을 수 있습니다. 이 서비스는 **KTECH 백엔드 과정의 최종 프로젝트**로, 사용자 편의를 극대화하기 위해 설계되었습니다.

### 팀 멤버

- **[Nguyen Thi Hong Hanh (이하은2699)](https://github.com/haeun2910)**
- **[Nguyen Thi Thuy Dung (원튀영6825)](https://github.com/thuydung44)**
- **[Tran Dang Tien(짠당티엔5899)](https://github.com/TRANDANGTIEN99)**

## 프로젝트 설명
이 시스템은 **Spring Boot**를 사용하여 일정 관리 서비스의 백엔드 API를 구축했습니다. 사용자는 출발지, 도착지, 시간, 이동 수단 등을 입력하여 효율적인 일정 관리와 최적의 출발 시간 추천을 받을 수 있습니다. 네이버 지도 API와 ODsay LAB API를 활용하여 대중교통 정보를 제공하고, 사용자는 이동 경로를 직관적으로 확인할 수 있습니다. 이 프로젝트는 현대인의 바쁜 생활 속에서 더 나은 시간 관리를 제공하기 위해 개발되었습니다.
## 주요 기능

### 1. 사용자 권한 및 인증
- **회원가입**: 기본 정보 (username, password)를 제공하여 회원가입 시 비활성 사용자(`ROLE_INACTIVE`)로 시작.
- **로그인**: 로그인 후 추가 정보 (이메일, 나이, 전화번호, 주소, 프로필 이미지)를 업데이트하면 활성 사용자(`ROLE_ACTIVE`)로 전환.활성 사용자만 서비스의 모든 기능을 사용할 수 있으며, 이는 서비스의 남용을 방지하기 위한 조치입니다.
- **권한 부여**: JWT 기반 인증 시스템을 통해 사용자 권한 부여, 활성 사용자만 서비스 이용 가능.
- **관리자(`ROLE_ADMIN`)**는 일반 사용자 가입 절차를 통해 생성할 수 없으며, 서비스 관리 및 사용자 요청 검토 등의 고정된 역할 수행.

### 2. 일정 등록 및 관리
- **일정 등록**: 사용자가 일정 제목, 날짜, 출발지, 도착지, 이동 수단 등을 입력하여 등록.
- **일정 수정/삭제**: 등록한 일정을 자유롭게 수정 및 삭제할 수 있음.
- **출발 시간 추천**: 이동 수단에 따라 시스템이 최적의 출발 시간을 계산하여 제공.
- **경로 표시**: Naver Map API를 이용하여 등록된 일정 경로를 지도에 표시.

### 3. 대중교통 정보 제공
- **ODsay LAB API**: 대중교통 경로 및 예상 소요 시간을 실시간으로 제공하여 사용자에게 최적의 대중교통 정보를 제공합니다.


## 구현된 기술

### 1. 백엔드 아키텍처
- **Spring Boot**: RESTful API 구축 및 사용자 인증/권한 관리.
- **JWT (JSON Web Token)**: 사용자 인증 및 세션 유지.
- **데이터베이스**: SQLite.
- **지도 및 경로 API**: 네이버 지도 API, Geocoding API, NCloud Directions 5 API, Odsay Api, Naver Search Api.

### 2. 프론트엔드
- **HTML/CSS/JavaScript**: 사용자가 일정을 입력하고 결과를 확인할 수 있는 입력 폼과 결과 화면 제공.

## API 연동
- **네이버 지도 API**: 출발지 및 도착지 설정과 경로 계산.
- **ODsay LAB API**: 대중교통 경로 및 예상 소요 시간 계산.
- **NCloud Directions 5 API**: 자가용, 대중교통, 택시 경로에 따른 이동 시간 및 추천 경로 제공.

## 주요 엔드포인트

- `/views/signup`: 회원가입
- `/views/signin`: 로그인
- `/views`: 사용자 정보 조회
- `/views/update`: 사용자 정보 수정
- `/views/plan-create`: 일정 생성
- `/views/my-plan`: 일정 조회
- `/views/task-create`: task 생성
- `/views/my-task`: task 조회
- `/views/view-plan?{planId}`: 시간 계산
- `/views/pub-trans-route?{planId}`: 대중교통 경로 추천 및 시간 계산
- `/views/car-taxi-route?{planId}`: 개인 차, 택시 경로 추천 및 시간 계산
- `/views/suspend-req`: 서비스 중지 요청

## 기술 스택

- **프레임워크**: Spring Boot 3.3.3
- **데이터베이스**: SQLite
- **의존성**:
    - **Spring Web**: 웹 애플리케이션 구축
    - **Spring Security**: 사용자 인증 및 권한 관리
    - **Thymeleaf**: 서버 측 HTML 템플릿 엔진
    - **Lombok**: 자바 보일러플레이트 코드 간소화
    - **Spring Data JPA**: 데이터베이스 상호작용
    - **네이버 지도 API**: 경로 및 위치 표시
    - **Odsay API**: 대중교통 정보 조회
    - **NCloud Directions 5 API**: 실시간 경로 안내
- **API 테스트 도구**: Postman

## API 문서 및 다이어그램

- [Postman Collection](https://github.com/haeun2910/team4-project/blob/main/schedule/team-4-project.postman_collection.json): 모든 API 엔드포인트 테스트를 위한 Postman 컬렉션.
- [Entity Diagram](https://dbdiagram.io/d/6707218197a66db9a377efae): 데이터베이스 구조 및 관계 다이어그램. 

이 프로젝트는 사용자 일정과 이동 경로 관리에 있어 효율적인 기능과 유연한 확장성을 제공하도록 설계되었습니다.
