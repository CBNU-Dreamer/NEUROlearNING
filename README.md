# 🧠 NEUROlearNING
치매 예방 지원 스토리 게임 <br>
Dementia Prevention Support Story Game
<br>
### ✅ Contact
![mailcontact](https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white) <br>
팀장 배수연 | bsy5640@gmail.com
<br>

### ✅ Team
<table> <tr> <td align="center"> <a href="https://github.com/suyeonb"><img src="https://github.com/suyeonb.png" width="100px;" alt="배수연 프로필"/><br /><sub><b>배수연</b><br />팀장 / BE / DB</sub></a> </td> <td align="center"> <a href="https://github.com/kdm0927"><img src="https://github.com/kdm0927.png" width="100px;" alt="김다민 프로필"/><br /><sub><b>김다민</b><br />FE / BE</sub></a> </td> <td align="center"> <a href="https://github.com/ieeyesoi"><img src="https://github.com/ieeyesoi.png" width="100px;" alt="이예솔 프로필"/><br /><sub><b>이예솔</b><br />디자인 / PM</sub></a> </td> <td align="center"> <a href="https://github.com/jaeyeo0ng"><img src="https://github.com/jaeyeo0ng.png" width="100px;" alt="조재영 프로필"/><br /><sub><b>조재영</b><br />디자인 / FE</sub></a> </td> </tr> </table>

### ✅ Purpose
1. 치매 예방을 위한 인지 훈련 게임 <br>
2. 스토리 기반으로 기억력, 판단력 등 인지 능력 향상 유도 <br>
3. Android 앱으로 제공 <br>

![android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white) ![googleplay](https://img.shields.io/badge/Google_Play-414141?style=for-the-badge&logo=google-play&logoColor=white)
<br>

### ✅ Tools
![js](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=white) ![html](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white) ![css](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white) ![react](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB) ![figma](https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white)

![java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white) ![flutter](https://img.shields.io/badge/Flutter-02569B?style=for-the-badge&logo=flutter&logoColor=white) ![kotlin](https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white)

![androidstudio](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white) ![unity](https://img.shields.io/badge/Unity-100000?style=for-the-badge&logo=unity&logoColor=white)

![notion](https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white) 👉 Notion 프로젝트 페이지 [notionlink](https://www.notion.so/NEUROlearNING-1a8f0c1d63f28071a1d5c167d4dddf59?pvs=4)
<br>

# 📘 GitHub 협업 전략

본 문서는 우리 팀의 협업 방식, 브랜치 전략, 이슈 관리 방식, 커밋 메시지 규칙 등을 정리한 개발 리드미입니다.

---

## 🔁 작업 흐름: 페이지 단위 병렬 작업

1. **다민 & 재영**
    - 스토리 흐름 및 UI 초안 설계
    - 페이지 단위 세부 명세서 제작
    - feature 브랜치 생성
2. **예솔**
    - 초안 UI에 디자인 적용 (Figma 등 활용)
    - 시니어 맞춤형 인터페이스 설계 및 사용성 테스트 진행
3. **수연**
    - 기능 구현 (DB, 로직, API, 게임 연동 등)
    - 초안/완성된 UI를 기준으로 기능 연결
4. **작업 순서 흐름**
    - 다민 & 재영: 페이지 1 UI 초안 제작
    - 예솔 & 수연: 페이지 1 디자인 및 기능 구현
    - 동시에 다민 & 재영은 페이지 2 초안 제작 시작
5. **페이지 완성 시** → `master` 브랜치 병합
6. **다른 페이지와의 연결까지 완료** → `main` 브랜치 병합

---

## 🌿 브랜치 전략

| 브랜치 | 설명 |
| --- | --- |
| `main` | 전체 연계 및 릴리즈 대상 병합 |
| `master` | 기능 단위 완료된 페이지 저장소 (테스트 통과 기준) |
| `feature/페이지명` | 각 기능/화면 단위 개발 공간 (예: `feature/story1`) |

**TIP:** 브랜치는 항상 `feature/페이지명` 형식으로 명명

```bash
# 예시
feature/story1
feature/login
```

## 📌 중요 협업 원칙

**항상 master에서 새 브랜치를 만듭니다.**

**병합 전에는 항상 pull로 최신화합니다.**

**병합은 feature → master → main 순서로 이뤄집니다.**

**작업이 완료된 브랜치는 필요 시 삭제합니다.**

**이슈는 생성-참조-해결까지 깔끔히 관리합니다.**

---
### 1️⃣ 다민 & 재영 - UI 초안 작업 흐름

1. **원격 브랜치 최신화 및 master 기준 브랜치 생성**
```bash
git fetch origin
git checkout master
git pull origin master
git checkout -b feature/pageX
```
2. **GitHub 이슈 생성**
예: #21 feature/pageX-basicUI

3. **Android Studio에서 XML UI 초안 작업**
4. **작업물 커밋 & 푸시**
```bash
git add .
git commit -m "#21 feat: pageX 초안 UI 구성 완료"
git push origin feature/pageX
```

5. **작업 완료 후 master 병합**
```bash
git checkout master
git pull origin master
git merge feature/pageX
git push origin master
```
6. **이슈 클로즈 & 다음 브랜치로 이동**
```bash
git checkout -b feature/pageY
```
---
### 2️⃣수연 & 예솔 – 기능 및 디자인 작업 흐름

1. **master 최신화**
```bash
git fetch origin
git checkout master
git pull origin master
```
2. **기능 브랜치 가져오기**
```bash
git checkout -b feature/pageX origin/feature/pageX
```
3. **각자 이슈 생성 후 세부 작업**

예솔: #23 feature/pageX-디자인 적용

수연: #24 feature/pageX-DB 연동

4. **작업 완료 후 커밋 & 푸시**
```bash
git add .
git commit -m "#24 feat: DB 연동 기능 구현"
git push origin feature/pageX
```
5. **기능 구현 완료 시 병합 (수연 담당)**
```bash
git checkout master
git pull origin master
git merge feature/pageX
git push origin master
```
6. **브랜치 정리 (완전한 작업 완료 후)**
```bash
git branch -d feature/pageX
git push origin --delete feature/pageX
```
---

## 🗂️ 이슈 관리 전략

### 📌 이슈 분해 기준

- 역할별로 나누기 (PM / UI / 기능)
- 한 작업에 하루 이내 완료 가능할 수준으로 쪼개기
- 작업 완료 여부 관리하기

### 📄 이슈 예시

| 제목 | 담당자 | 설명 |
| --- | --- | --- |
| `[Story1] UI 초안 구조 설계` | 다민, 재영 | 버튼/레이아웃 정의 |
| `[Story1] 디자인 적용` | 예솔 | Figma 기반 디자인 적용 |
| `[Story1] 기능 구현 - 대사 출력` | 수연 | 텍스트 애니메이션, 전환 |
| `[Story1] 기능 구현 - 미니게임 연동` | 수연 | 게임 Activity 연결 |

### ✅ 이슈 제목 형식

```
[페이지명] 작업유형 - 간단한 설명
```

---

## 📝 커밋 메시지 규칙

### 📌 기본 구조

```
#[이슈번호] 작업유형: [페이지] 한 줄 설명
```

### 📋 커밋 유형 예시

| 유형 | 설명 | 예시 |
| --- | --- | --- |
| `feat` | 새로운 기능 추가 | `#12 feat: [Story1] 미니게임 연결 기능 구현` |
| `fix` | 버그 수정 | `#14 fix: [Story2] 대사 흐름 순서 오류 수정` |
| `docs` | 문서 수정 | `#3 docs: README 브랜치 전략 설명 추가` |
| `refactor` | 리팩토링 | `#6 refactor: GameManager 구조 개선` |
| `style` | 코드 스타일 변경 (로직 X) | `#15 style: 들여쓰기 및 변수명 통일` |
| `chore` | 기타 변경사항 | `#2 chore: gradle 버전 업데이트` |

### 📌 문서 또는 리드미 수정 시

```
#1 docs: README 커밋 메시지 전략 설명 추가
```

---

## 📌 GitHub Projects 활용

- `To Do` / `In Progress` / `Done` 칼럼으로 이슈 추적
- PR과 이슈 자동 연결 (`Fixes #11` 등)
- 페이지별 마일스톤 설정 가능 (선택사항)

---
