## NEUROlearNING
Dementia Prevention Support Story Game

## 깃헙 사용하기
#### ✅1. 깃허브 플로우
브랜치 관리 방법:

각 팀원은 feature/* 브랜치에서 작업 → develop으로 병합

develop에서 충분히 테스트한 후 main으로 배포

#### ✅(B) 커밋 & PR (Pull Request) 규칙
📌 커밋 메시지 컨벤션 (명확한 기록을 위해!)

feat: 스토리 진행 기능 추가  

fix: UI 버튼 크기 수정  

refactor: 미니게임 로직 최적화  

docs: README 업데이트 
 
#### ✅ 예시 커밋 메시지:
✔ feat: 스토리 시스템에서 선택지 기능 추가

✔ fix: 미니게임 UI 깨지는 버그 수정

#### 📌 Pull Request (PR) 관리 규칙(고민중)
PR 제목은 [작업 내용]을 명확하게 작성

팀원 중 최소 1명이 코드 리뷰 후 승인 (자동 병합 방지)

develop에 머지 후 팀원에게 공유

## 📌 개발 순서 (각 팀원이 해야 할 작업)
### 🎯 1. 새로운 기능 개발할 때
##### 최신 develop 브랜치 가져오기
> git checkout develop
> 
> git pull origin develop


##### 새로운 기능 브랜치 생성
> git checkout -b feature/mini-game1


##### 코드 작성 후 커밋
> git add .
>
> git commit -m "feat: 십자말 풀이 미니게임 기본 로직 추가"


##### 원격 깃허브에 업로드
> git push origin feature/mini-game1

### 🎯 2. PR(Pull Request) 생성(고민중)
GitHub에서 feature/mini-game1 → develop 으로 Pull Request(PR) 생성

팀원이 코드 리뷰 후 승인 → 병합

### 🎯 3. 최신 코드 동기화
##### develop 브랜치 최신 상태 가져오기
> git checkout develop
>
> git pull origin develop

##### 새로운 작업 브랜치 생성 후 반복
> git checkout -b feature/next-task
