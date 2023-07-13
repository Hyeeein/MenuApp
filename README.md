# Lunch Menu Recommendation

### 🥉 인천대학교 컴퓨터공학부 캡스톤디자인 졸업작품 발표회 장려상 수상
- 주제: 학교에서 뭐 먹지? (사용자 기반 메뉴 추천 앱)
- 기간: 2022.09.01 ~ 2023.05.26 (개발 기간: 2023.03.08 ~ 2023.05.26)
- 참여 인원
  - 박혜인(팀장): 추천시스템 알고리즘 개발, 데이터 수집 및 전처리, UI/UX 설계 및 디자인
  - 이윤하: 프론트엔드, UI/UX 설계
  - 정의헌, 서제원: 백엔드, API 서버 배포 및 명세서 작성, 서버 관리, 데이터 수집 및 전처리
- 기술 스택: Python, Sklearn, NLP, Recommendation, AndroidStudio, Django, MySQL, AWSEC2
- 커뮤니케이션: 오프라인 회의, Zoom 화상회의, 팀 Notion

## 1. 프로젝트 목표
* 프로젝트 기획 배경

![image](https://github.com/Hyeeein/MenuApp/assets/81239567/702d372f-c730-4afa-8d29-42d4e07e6aaf)

* 프로젝트 목표

![image](https://github.com/Hyeeein/MenuApp/assets/81239567/b25cd874-b20a-451a-b91d-58a0c86d7a7c)

→ 이렇게 3가지 목표를 기반으로, 매일 점심 어떤 음식을 먹을지 고민하는 사람들을 위한 안드로이드 앱 서비스 개발 진행

## 2. 서비스 소개
* 메인 서비스명 : 학교에서 뭐 먹지?

* 메인 로고 및 캐릭터

![image](https://github.com/Hyeeein/MenuApp/assets/81239567/2320ca8e-8588-447b-b129-55f5a490df4c)

* 서비스 소개
  - GPS 위치 정보와 사용자 정보 입력을 통한 **개인 맞춤형 메뉴 추천 시스템**
  - 메뉴 별 특징 데이터 **텍스트 유사도**를 활용해 사용자 선호 메뉴와 가장 유사한 메뉴 추천
  - 메뉴 별로 간단하게 음식 **칼로리와 영양 정보**를 함께 제공
  - **‘밥풀이’ 캐릭터를 활용**한 사용자 친화적인 앱 서비스

* 서비스 전체 흐름도
  
![image](https://github.com/Hyeeein/MenuApp/assets/81239567/86ec85bb-ec69-46c1-bd86-6c9783e23004)

* 시스템 아키텍처

![image](https://github.com/Hyeeein/MenuApp/assets/81239567/cf8b0ea6-509d-41e9-bb76-407a04a35e94)

## 3. 데이터

* 데이터 수집 및 전처리

![image](https://github.com/Hyeeein/MenuApp/assets/81239567/071faaca-7d83-4711-a93f-758843974c53)

* ERD

![image](https://github.com/Hyeeein/MenuApp/assets/81239567/7830bc75-e123-43ab-8f92-950a2e4bb21d)

→ 데이터는 MySQL과 AWSEC2를 사용하여 저장 및 관리

## 4. 메뉴 추천 알고리즘 ⭐

### (1) 컨텐츠 기반 필터링

* 컨텐츠 기반 필터링
  - 텍스트 기반으로, 문서의 유사도를 측정해 비슷한 다른 컨텐츠를 추천
  - 주로 코사인 유사도를 많이 사용하며, 유사도가 가장 높은 값을 추천

* 메뉴 특징 데이터 자체 생성
  - 메뉴를 설명하는 자료가 따로 없어, 메뉴의 특징을 나타낼 수 있는 텍스트들을 합쳐 메뉴별 특징 데이터 생성 <br>
    (메뉴 소개 데이터를 활용하고자 했으나, 메뉴와 관련없는 용어들이 많아 제외)
  - 음식점 카테고리 값 + 메뉴 이름 + 날씨 + 감정 + 재료 정보, 총 5가지 컬럼을 합쳐서 구성

  ![image](https://github.com/Hyeeein/MenuApp/assets/81239567/36d56972-acdf-4abc-be4a-df0dee219512)

* 선호 메뉴와 가장 유사한 메뉴 리스트업 과정 <br>
  ① 컨텐츠에 대한 텍스트의 **TF-IDF** 방식을 사용하여 Feature Vectorization <br>
  ⑦ 컨텐츠들의 Feature 벡터들 간에 **코사인 유사도**를 사용하여 유사도 행렬을 구함 <br>
  ③ 특정 컨텐츠를 기준으로 그 컨텐츠와 유사도, 가중평점이 가장 높은 순으로 정렬 <br> 
  ④ **특징 데이터 값과 사용자가 선호하는 메뉴의 특징 데이터 간의 유사도가 가장 높은 메뉴 추천** <br>

### (2) 사용자 정보 활용
*여기부터는 앱에서 사용자가 입력한 데이터를 받아와 알고리즘에 적용*

* 사용자가 가입 시 입력한 알러지 정보, 선호 메뉴를 각각의 DB에 저장 후 활용

![image](https://github.com/Hyeeein/MenuApp/assets/81239567/29abca8f-42ed-4651-ad50-b8c2eda12413)

### (3) 사용자의 에산, 기분, 날씨 정보 활용
* 메뉴 추천 전, 사용자의 현재 예산과 날씨, 기분을 입력하여 현재 상태에 가장 어울리는 메뉴 추천에 활용

 ![image](https://github.com/Hyeeein/MenuApp/assets/81239567/b4a0ec17-816b-4294-acce-f4fe2037b4dc)

### (4) 추천 로그, 리뷰 데이터 활용
- 메뉴 추천이 이루어진 후, 추천 받은 메뉴 로그 데이터를 저장하고, 작성한 리뷰를 활용하여 선호 메뉴에 추가

![image](https://github.com/Hyeeein/MenuApp/assets/81239567/99727788-e2d3-4d2f-951f-2ce2b5421fd0)

### (5) 추천 알고리즘 전반적인 흐름도
![image](https://github.com/Hyeeein/MenuApp/assets/81239567/a42206b8-9d3b-4673-9890-19c8eae39e3a)

### (6) 메뉴 추천 Test 과정
*사용자 김점심(24세)는 '달걀' 알러지가 있지만, 튀긴 음식을 좋아하고 매운 음식을 잘 먹는다.*
![image](https://github.com/Hyeeein/MenuApp/assets/81239567/ee7a220e-5bd2-496b-8fd7-3f3c598a9c73)

- (5)의 알고리즘을 적용한 후, 메뉴 유사도는 약 50%로 출력

## 5. 주요 기능 소개 및 구현 과정
* 주요 기능 : 메뉴 추천, 주변 음식점, 마이페이지 부수적인 기능
* 여기서는 메뉴 추천과 관련된 기능 위주로 설명합니다

### (1) 메뉴 추천을 위한 사전 정보 입력
- 회원 가입 시 알레르기 및 음식 취향 정보 입력

![image](https://github.com/Hyeeein/MenuApp/assets/81239567/2f691dc9-c14a-4d58-b95d-b9a8ad156f6b)

### (2) 메인 화면
- 화면에 닉네임, GPS 정보를 통한 현재 위치 표시
- 메뉴 추천, 주변 음식점, 마이페이지 기능을 활용할 수 있는 버튼 활성화 → 메뉴 추천 버튼 클릭

![image](https://github.com/Hyeeein/MenuApp/assets/81239567/36d8cd7b-8c0e-42e4-9aaa-3449cffb804b)

### (3) 메뉴 추천 과정
- 사용자의 예산, 날씨, 기분을 입력하여 사용자와 관련된 추가 데이터 확보 후 메뉴 추천 알고리즘 적용
  
![image](https://github.com/Hyeeein/MenuApp/assets/81239567/121e77cc-8f60-428d-b782-8a4096988445)

- 메뉴 추천 결과에서 '이거 먹기'를 클릭할 경우 확정 팝업 표시 / 그렇지 않을 경우, 재추천 가능

![image](https://github.com/Hyeeein/MenuApp/assets/81239567/0d71cfcc-9da9-4f19-b896-40e11f837d31)

*더 자세한 기능들은 아래의 포트폴리오와 시연 영상 참고 부탁드립니다*
* [포트폴리오 19~29페이지 참고](https://github.com/Hyeeein/MenuApp/blob/master/documents/%5B%ED%8F%AC%ED%8A%B8%ED%8F%B4%EB%A6%AC%EC%98%A4%5D%20%ED%95%99%EA%B5%90%EC%97%90%EC%84%9C%20%EB%AD%90%20%EB%A8%B9%EC%A7%80%20(%EC%A0%90%EB%A9%94%EC%B6%94).pdf)
  - 크게 첫화면/회원가입/로그인, 메뉴 추천, 주변 음식점 출력, 마이페이지 기능으로 세분화
- [**작품 시연 영상**](https://github.com/Hyeeein/MenuApp/blob/master/documents/%EC%9E%91%ED%92%88%20%EC%8B%9C%EC%97%B0%20%EC%98%81%EC%83%81%20final.mp4)
  - 회원가입을 시작으로 메뉴 추천을 받고, 앱을 사용하는 일련의 모습을 담음

## 6. 기대효과 및 발전방향
* 기대효과
  - 학교에서 어떤 메뉴를 먹어야 할지 고민이 될 때, 이 앱을 이용함으로써 메뉴 고민 해결 가능
  - 학교 주변의 음식점에 대한 운영시간, 메뉴를 모두 확인할 수 있어 유용 (매주 메뉴가 바뀌는 학식 1, 2코너, 기숙사 식당은 제외)
  
* 발전방향
  - 사용자들이 남긴 리뷰 데이터를 활용하여 메뉴 추천에 추가 활용 (지금은 리뷰 데이터가 없어서 불가능)
  - 추후 학교의 범위를 넓히면, 보다 많은 대학생들의 점심 메뉴 고민을 덜 수 있음
  - 추천 결과의 신뢰성을 높일 수 있도록, 추천 과정을 시각화해서 알려주거나, 추천된 메뉴에 대한 평가 항목을 추가하여 추가적인 성능 향상 가능

* **기술적인 앱 개선점**
  - NLP 기술을 사용했지만, 메뉴 데이터 자체가 너무 한정적이어서 추천 정확도가 상대적으로 떨어짐 <br>
    따라서, 메뉴 데이터와 관련된 텍스트 데이터를 좀 더 확보하여 진행할 필요가 있음
  - 추가적으로 이미지 인식 기술을 활용하여 사용자가 좋아하는 메뉴의 이미지와 비슷한 메뉴를 추천해주는 방식으로도 개선 가능

## 7. 참고자료

- [**작품 시연 영상**](https://github.com/Hyeeein/MenuApp/blob/master/documents/%EC%9E%91%ED%92%88%20%EC%8B%9C%EC%97%B0%20%EC%98%81%EC%83%81%20final.mp4)
- [발표자료](https://github.com/Hyeeein/MenuApp/blob/master/documents/%5B%EB%B0%9C%ED%91%9C%EC%9E%90%EB%A3%8C%5D%20%ED%95%99%EA%B5%90%EC%97%90%EC%84%9C%20%EB%AD%90%20%EB%A8%B9%EC%A7%80%20(%EC%A0%90%EB%A9%94%EC%B6%94).pdf)
- [포트폴리오](https://github.com/Hyeeein/MenuApp/blob/master/documents/%5B%ED%8F%AC%ED%8A%B8%ED%8F%B4%EB%A6%AC%EC%98%A4%5D%20%ED%95%99%EA%B5%90%EC%97%90%EC%84%9C%20%EB%AD%90%20%EB%A8%B9%EC%A7%80%20(%EC%A0%90%EB%A9%94%EC%B6%94).pdf)
- [메인로고 사진](https://github.com/Hyeeein/MenuApp/blob/master/documents/%EB%A9%94%EC%9D%B8%ED%99%94%EB%A9%B4%20%EB%A1%9C%EA%B3%A0.png)
- [메인캐릭터 밥풀이 사진](https://github.com/Hyeeein/MenuApp/blob/master/documents/%EB%A9%94%EC%9D%B8%ED%99%94%EB%A9%B4%20%EB%B0%A5%ED%92%80.png)
