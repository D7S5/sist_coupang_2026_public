# Slack 기반 서버 재시작/배포

Ubuntu 서버(`scym3`, 계정 `root`)에 SSH로 직접 접속하지 않고, Slack에서 `/restart` 명령으로 최신 소스를
빌드/배포하기 위한 구성이다.

## 전체 플로우

```
로컬에서 GoodPang 소스 수정 → git push (sist_coupang_2026)
        ↓
Slack 채널에서 "/restart" 입력
        ↓
slack_restart_bot.py 가 서버의 restart.sh 실행
        ↓
restart.sh: sist_coupang_2026 git pull
        → project/GoodPang 소스를 javac + jar로 GoodPang.war 빌드
        → Tomcat(/opt/tomcat) 종료 → 기존 ROOT 배포 삭제
        → 새 war를 ROOT.war로 복사 → Tomcat 시작
        ↓
실행 결과(성공/실패 로그)가 Slack 채널에 응답으로 올라옴
```

과거에는 이클립스에서 war를 export해서 war 전용 저장소(`cdcdtest`)에 commit/push하고, 서버가 그 저장소를
pull하는 방식이었다. 지금은 `project/GoodPang`이 Maven 표준 레이아웃(`src/main/java`,
`src/main/webapp`)이고 의존 jar(gson, lombok, jbcrypt, ojdbc8, jstl 등)가 전부 `WEB-INF/lib`에 커밋돼
있어서, **서버에서 소스로 직접 war를 빌드**하도록 바꿔 `cdcdtest`와 이클립스 export 단계를 없앴다.

## 구성 파일

| 파일 | 역할 | 서버 배치 경로 |
|---|---|---|
| `slack-restart-bot/slack_restart_bot.py` | Slack `/restart` 명령을 받아 `restart.sh`를 실행하고 결과를 Slack에 응답하는 봇 본체 | `/root/slack-restart-bot/slack_restart_bot.py` |
| `slack-restart-bot/requirements.txt` | Python 의존성 (`slack-bolt`) | `/root/slack-restart-bot/requirements.txt` |
| `slack-restart-bot/slack-restart-bot.service` | 봇을 상시 실행/자동 재시작하는 systemd 유닛 | `/etc/systemd/system/slack-restart-bot.service` |
| `deploy/restart.sh` | git pull → war 빌드 → Tomcat 재배포를 수행하는 실제 배포 스크립트 | `/root/restart.sh` |

## Slack Bolt(Socket Mode)를 쓰는 이유

공인 IP/포트포워딩/HTTPS 엔드포인트 없이도, 봇 프로세스가 Slack으로 아웃바운드 웹소켓 연결만 유지하면
Slash Command를 받을 수 있다. 방화벽/도메인 설정이 필요 없어 개인 서버 환경에 적합하다.

## `slack_restart_bot.py` 환경변수

서버의 `/root/slack-restart-bot/.env`에 설정한다 (git에는 커밋하지 않는 시크릿 파일).

| 변수 | 설명 |
|---|---|
| `SLACK_BOT_TOKEN` | `xoxb-...`, OAuth & Permissions → Install to Workspace 후 발급되는 Bot Token |
| `SLACK_APP_TOKEN` | `xapp-...`, Basic Information → App-Level Tokens (`connections:write` 스코프)에서 발급 |
| `RESTART_SCRIPT_PATH` | 실행할 스크립트 경로. `/root/restart.sh` |
| `ALLOWED_SLACK_USER_IDS` | `/restart` 실행을 허용할 Slack 멤버 ID 목록(콤마 구분). 비우면 누구나 실행 가능하므로 반드시 지정 |
| `ALLOWED_SLACK_CHANNEL_ID` | (선택) 특정 채널에서만 실행 허용 |
| `RESTART_TIMEOUT_SEC` | (선택, 기본 180) 빌드+재시작 스크립트 타임아웃(초) |

## Slack App 생성

1. https://api.slack.com/apps → **Create New App** → **From a manifest**
2. 아래 manifest 사용:

   ```yaml
   display_information:
     name: restart-bot
   features:
     bot_user:
       display_name: restart-bot
       always_online: true
     slash_commands:
       - command: /restart
         description: 서버를 재시작합니다
         should_escape: false
   oauth_config:
     scopes:
       bot:
         - commands
         - chat:write
   settings:
     socket_mode_enabled: true
     org_deploy_enabled: false
     token_rotation_enabled: false
   ```

3. manifest로 자동화되지 않는 부분(수동 진행):
   - **Basic Information → App-Level Tokens**: `connections:write` 스코프로 토큰 발급 → `SLACK_APP_TOKEN`
   - **OAuth & Permissions → Install to Workspace** → 발급된 Bot Token → `SLACK_BOT_TOKEN`
   - 사용할 채널에 `/invite @restart-bot`

## 서버 배포 절차

```bash
# 1. 봇 배치
mkdir -p /root/slack-restart-bot
# cicd/slack-restart-bot/slack_restart_bot.py, requirements.txt 를 서버로 복사

cd /root/slack-restart-bot
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt

# .env 생성 (위 환경변수 표 참고) 후 권한 제한
chmod 600 /root/slack-restart-bot/.env

# systemd 등록
cp cicd/slack-restart-bot/slack-restart-bot.service /etc/systemd/system/
systemctl daemon-reload
systemctl enable --now slack-restart-bot

# 2. 배포 스크립트 배치
cp cicd/deploy/restart.sh /root/restart.sh
chmod +x /root/restart.sh
```

## `restart.sh` 동작

- `git pull`(최초 실행 시 `git clone`) 대상: `https://github.com/gmake20/sist_coupang_2026.git` → `/root/git/sist_coupang_2026`
- `project/GoodPang/src/main/java`를 `javac`로 컴파일, `-cp`에 `WEB-INF/lib/*`와 Tomcat의 `/opt/tomcat/lib/*`(서블릿 API 등) 포함
- 컴파일 결과 + `src/main/webapp` 내용을 합쳐 `GoodPang.war`로 패키징
- Tomcat 종료(최대 30초 대기) → 기존 `ROOT` 배포 삭제 → 새 war를 `ROOT.war`로 복사 → Tomcat 시작
- `set -e`가 걸려있어 **컴파일 에러가 나면 Tomcat을 내리기 전에 스크립트가 중단**된다. 빌드 실패 시 기존 서비스는 그대로 유지된다.

## 신규 유저에게 `/restart` 권한 부여

1. 대상 유저의 Slack 멤버 ID 확인 (Slack 프로필 → 더보기 → 멤버 ID 복사)
2. `/root/slack-restart-bot/.env`의 `ALLOWED_SLACK_USER_IDS`에 콤마로 추가
3. `systemctl restart slack-restart-bot`

## 트러블슈팅 기록

- **`Job for slack-restart-bot.service failed because of unavailable resources`**
  - 원인 1: 서비스 파일이 `User=ubuntu`로 돼 있었는데 서버에 `ubuntu` 계정이 없음(`id ubuntu` → no such
    user). 실제 로그인 계정은 `root`.
  - 원인 2: `EnvironmentFile`로 지정한 `.env` 파일이 아직 생성되지 않음
    (`Failed to load environment files: No such file or directory`).
  - 조치: 서비스 파일을 `User=root`, `/root/slack-restart-bot/...` 경로로 전체 재작성, `.env` 파일
    생성 후 `daemon-reload` + `restart`로 해결.
