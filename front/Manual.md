파일 종류
 * 디자인 (.xml)
   - activity_ : 앱에서 보여지는 화면
   - item_ : listview를 구성하는 아이템 틀
 * 기능 (.java)
   - ~Activity : activity_ 파일의 요소를 구현하는 메인 파일 (사용자 입력, 버튼, 화면 전환 등)
   - ~Item : 메뉴, 음식점 등의 데이터를 사용하기 위한 클래스
   - ~Adapter : listview로 출력되는 요소들에 서버의 데이터 담기 위함 (이미지 인코딩도 여기)
   - fragment : 메뉴 탭으로 화면 전환하기 위해 만든 클래스. 아직 구현 못함

파일 경로
 * 디자인 파일 (.xml)
   - front\app\src\main\res\layout
 * 이미지 파일 (아이콘 등. 서버 X 다운받은 파일)
   - front\app\src\main\res\drawable
 * 기능 파일 (.java)
   - front\app\src\main\java\com\example\menuapp
  
 
