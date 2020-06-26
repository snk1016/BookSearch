#과제 수행 전략
 - MVVM 패턴을 활용
 - di(의존성 주입)를 이용한 서비스 연결
 - 네트워크 서비스는 retrofit2를 활용
 - 서비스 호출은 Home에서 리스트를 가져올때만 호출
 - 상세페이지는 리스트의 Model Item을 전달하여 활용
 - 서비스 호출에 필요한 Authorization은 string.xml에 선언 후 활용
 - Glide를 이용한 이미지로드 및 캐싱처리
 - 데이터 바인딩을 이용해 처리하되, XML상에서 삼항연산 처리 가능한 부분은 kotlin코드에서 처리(XML에 삼항연산이 들어가면 퍼포먼스 저하 발생)

#과제 수행 방법
 - 도서검색 기능을 Actionbar Menu를 이용해 최상단에 넣어 뷰화면을 최대한 넓게 쓸 수 있도록 처리함.
 - Home에서 ReCycleView를 이용하고, 50개씩 Paging되도록 처리함.
 - ReCycleView의 ItemSelect이벤트도 Home에서 처리하도록 코드 삽입.
 - ReCycleView의 Item을 Click시 Detail로 화면을 전환하며 ActionBar의 Style을 변경함.
 - Detail 페이지에서 통신없이 list에서 받은 데이터로만 화면에 표시.
 - Detail 페이지에 구매하기 버튼을 넣어 URL링크를 브라우저로 띄우도록 처리.
 - Fragment의 Stack기능을 이용하여, Fragment간 이동을 해도 '뒤로가기'버튼을 눌렀을 때 이전 Fragment가 불리도록 설정.


#외부라이브러리 사용 출처
Glide : https://github.com/bumptech/glide