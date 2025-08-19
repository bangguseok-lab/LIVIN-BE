## LIVIN - 안전한 부동산 거래 도우미

----

- 안전하고 투명한 부동산 거래를 위해 매물 위험도 분석, 맞춤형 체크리스트, 거래 가이드를 제공하는 서비스입니다.  
- 사용자는 매물을 등록하고 위험도를 분석할 수 있으며, 관심 매물 관리와 커스터마이징 체크리스트를 통해 더 안전한 거래를 경험할 수 있습니다.

----
## 📆프로젝트 기간 

---
- 2025-07-09 ~ 2025-08-19

## System Architecture

---
![janghyuk ap-northeast-2 (1).png](..%2F..%2F..%2FDownloads%2Fjanghyuk%20ap-northeast-2%20%281%29.png)

##  ERD

---
![LIVIN (2).png](..%2F..%2F..%2FDownloads%2FLIVIN%20%282%29.png)

## 🛠️ 기술 스택

---
### Backend
- Spring Framework 5.3.37
- Spring Security 5.8.13
- MyBatis 3.5.0
- MySQL 8.1.0
- Redis 2.7.18
- Gradle 

### Frontend
- Vue.js 3
- Pinia 3.0.1 (상태 관리) 
- Vite 6.2.1

### Infra & DevOps
- AWS EC2 (배포 서버)
- Docker & Docker Compose
- GitHub Actions (CI/CD)
- Vercel (Frontend 배포)


## 주요 기능

---
<details>
<summary>소셜 로그인</summary>
<div markdown="1">
<li>
 카카오 네이버 OAuth2 소셜 로그인
</li>

</div>
</details>
<br/>
<details>
<summary>매물 등록</summary>
<div markdown="1">
<ul>
<li>
임대인 매물 등록
</li>
</ul>

</div>
</details>
<br/>
<details>
<summary>위험도 분석</summary>
<div markdown="1">
<ul>
    <li>
    근저당권, 소유주와 임대인 일치 여부, 위반 건축물 여부, 전세가율로 매물의 위험도를 분석
    </li>
</ul>

</div>
</details>
<br/>
<details>
<summary>체크리스트</summary>
<div markdown="1">
<ul>
<li>
매물의 확인하고 싶은 사항을 담는 체크리스트 생성
</li>
<li>
나만의 항목을 생성하여 체크리스트 작성
</li>
<li>
특정 체크리스트가 적용된 매물 조회 가능
</li>
</ul>

</div>
</details>
<br/>
<details>
<summary>안심 뱃지</summary>
<div markdown="1">
<ul>
<li>
안심 뱃지 클릭 시 위험도 분석 결과 제공
</li>
</ul>
</div>
</details>
<br/>
<details>
<summary>관심 매물 관리</summary>
<div markdown="1">
<ul>
<li>
매물 즐겨찾기 및 조회
</li>
</ul>
</div>
</details>

## 🚀 배포 구조
- Backend: Docker 이미지 빌드 후 Docker Hub 푸시 → EC2에서 docker-compose 실행
- Frontend: Vercel 자동 배포
- Cache/세션: Redis (Docker)
- 
---


## ⚙️ 설치 및 실행
### Backend (로컬 실행)
```bash
# 프로젝트 클론
git clone https://github.com/LIVIN-ORG/LIVIN-BE.git
cd LIVIN-BE

# Gradle 빌드
./gradlew clean build

# Docker 실행
docker-compose -f docker/docker-compose.yml up -d
```
### Frontend (로컬 실행)
```bash
# 프로젝트 클론
git clone https://github.com/LIVIN-ORG/LIVIN-FE.git
cd LIVIN-FE

npm install
npm run dev
```