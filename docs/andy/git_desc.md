# Git 브랜치 전략 & PR 승인 규칙

이 프로젝트(`gmake20/sist_coupang_2026`)의 Git 운영 방식 정리.

---

## 1. 핵심 규칙

- **`main` 브랜치는 직접 커밋/푸시 금지** — GitHub의 **Branch protection rule**로 `main`에 설정
- 모든 변경은 각자의 **기능별 브랜치(feature branch)** 에서 작업
- 브랜치 작업이 끝나면 **Pull Request(PR)** 를 올려서 `main`으로 병합을 요청
- 그 PR은 **팀원 3명의 승인(Approve)** 을 받아야만 `main`으로 **merge** 가능

즉 "코드가 main에 들어가려면 최소 3명이 리뷰하고 동의해야 한다"가 이 프로젝트의 기본 원칙입니다.

---

## 2. 왜 이렇게 설정했는가

| 문제 | 이 규칙으로 얻는 효과 |
|---|---|
| 팀원이 실수로 완성 안 된 코드를 `main`에 바로 올림 | `main` 직접 push 자체가 GitHub 단에서 막혀서 원천 차단 |
| 한 사람만 보고 넘어가서 버그가 뒤늦게 발견됨 | 최소 3명이 코드를 봐야 하니, merge 전에 문제를 미리 잡을 확률이 올라감 |
| "누가 언제 무엇을 왜 합쳤는지" 기록이 안 남음 | PR 자체가 변경 이력 + 리뷰 코멘트 + 승인자 기록으로 그대로 남음 |
| 여러 명이 같은 파일을 동시에 고쳐서 충돌 | 브랜치 단위로 나눠서 작업하니 충돌 범위가 줄고, 병합 시점에만 한 번 정리하면 됨 |

---

## 3. 실제 브랜치 구성

이 저장소는 팀원별 담당 도메인에 맞춰 기능 브랜치를 나눠서 운영했습니다.

| 브랜치 | 담당 영역(추정) |
|---|---|
| `feature/scm` | 판매자센터(Vendor) — andy 담당 파트가 여기서 작업됨 |
| `feature/cart` | 장바구니/주문/결제 |
| `feature/index` | 메인페이지/카테고리 |
| `feature/signup` | 회원가입/로그인 |
| `main` | 위 브랜치들이 PR로 병합되는 최종 브랜치 (직접 커밋 불가) |

실제 커밋 로그를 보면 아래처럼 각 기능 브랜치가 PR을 통해 `main`으로 들어온 흔적이 그대로 남아있습니다.

```
Merge pull request #146 from gmake20/feature/index
Merge pull request #145 from gmake20/feature/cart
Merge pull request #144 from gmake20/feature/signup
Merge pull request #143 from gmake20/feature/scm
```

---

## 4. 실제 작업 흐름 (한 기능이 만들어지는 과정)

1. 담당 팀원이 자기 브랜치(예: `feature/scm`)에서 기능을 개발하고 커밋
2. 원격 저장소로 `git push origin feature/scm`
3. GitHub에서 `feature/scm` → `main`으로 **Pull Request 생성**
4. 나머지 팀원들이 PR의 변경 내용(diff)을 확인하고 코멘트/리뷰
5. **팀원 3명이 Approve**를 눌러야 `main`으로 **Merge** 버튼이 활성화됨 (승인 수가 안 채워지면 merge 자체가 GitHub에서 막힘)
6. Merge되면 그 내용이 자동으로 병합 커밋(`Merge pull request #xxx from ...`)으로 `main`에 기록됨
7. 이후 각 팀원은 자기 브랜치에서 `git merge main` 등으로 최신 `main` 내용을 받아와 계속 작업

---

## 5. GitHub 설정 상 의미 (참고)

GitHub Branch protection rule의 다음 항목들이 켜져 있는 것과 같은 효과입니다.

- **Require a pull request before merging** — `main`에 직접 push 금지, 반드시 PR을 거쳐야 함
- **Require approvals** — 값이 `3`으로 설정되어, 승인 3개 미만이면 merge 버튼 비활성화
- (일반적으로 함께 켜는 옵션) **Require branches to be up to date before merging** — PR 브랜치가 최신 `main` 기준이어야 merge 허용, 오래된 브랜치 그대로 합쳐지는 것 방지

---

## 6. 발표용 한 줄 요약

> "`main` 브랜치는 보호 규칙으로 직접 커밋을 막아두고, 모든 변경은 기능별 브랜치에서 작업한 뒤 Pull Request로 올려서 팀원 3명의 승인을 받아야만 병합되는 구조로 운영했습니다. 그 덕분에 검증 안 된 코드가 바로 `main`에 들어가는 걸 막고, 매 변경마다 최소 3명이 함께 검토하는 코드리뷰 프로세스를 자연스럽게 강제할 수 있었습니다."
