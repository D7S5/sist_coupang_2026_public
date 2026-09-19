# 배포 자동화 (Slack 재시작 봇)

## 배경

기존에는 서버 업데이트 시마다 아래 과정을 수동으로 반복했다.

1. Ubuntu 서버(`scym3`)에 SSH 접속
2. `git pull`
3. war 파일 복사
4. `restart.sh` 실행 (Apache/Tomcat 재시작)

이 과정을 Slack 명령(`/restart`)으로 대체하기 위해 Slack Bolt 기반 봇을 구축했다.

## 아키텍처

- 서버: Ubuntu, 실제 로그인 계정은 `root` (홈 디렉터리 `/root`, `ubuntu` 계정은 존재하지 않음)
- 방식: **Slack Bolt(Socket Mode)**
  - 공인 IP/포트포워딩/HTTPS 엔드포인트 불필요 (봇이 Slack으로 아웃바운드 웹소켓 연결만 유지)
  - Slash Command(`/restart`) → 봇 프로세스가 서버의 `restart.sh`를 `subprocess`로 실행 → 결과를 Slack 채널에 응답

## 구성 파일

리포지토리 내 `etc/slack-restart-bot/`에 위치, 서버의 `/root/slack-restart-bot/`에 배포됨.

| 파일 | 역할 |
|---|---|
| `slack_restart_bot.py` | Slack `/restart` 명령을 받아 `restart.sh`를 실행하고 결과를 Slack에 응답하는 봇 본체 |
| `requirements.txt` | Python 의존성 (`slack-bolt`) |
| `slack-restart-bot.service` | systemd 유닛 파일 (상시 실행/자동 재시작) |

`slack_restart_bot.py`는 아래 환경변수를 사용한다 (서버의 `/root/slack-restart-bot/.env`, git에는 커밋하지 않음).

| 변수 | 설명 |
|---|---|
| `SLACK_BOT_TOKEN` | `xoxb-...`, Slack App의 OAuth & Permissions → Install to Workspace 후 발급 |
| `SLACK_APP_TOKEN` | `xapp-...`, Slack App의 Basic Information → App-Level Tokens (`connections:write` 스코프)에서 발급 |
| `RESTART_SCRIPT_PATH` | 실행할 `restart.sh`의 절대 경로 |
| `ALLOWED_SLACK_USER_IDS` | `/restart` 실행을 허용할 Slack 멤버 ID 목록 (콤마 구분). 비우면 누구나 실행 가능하므로 반드시 지정 |
| `ALLOWED_SLACK_CHANNEL_ID` | (선택) 특정 채널에서만 실행 허용 |
| `RESTART_TIMEOUT_SEC` | (선택, 기본 180) 재시작 스크립트 타임아웃 |

## Slack App 설정

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

3. 생성 후 수동으로 진행해야 하는 것 (manifest로 자동화 안 되는 부분):
   - **Basic Information → App-Level Tokens**: `connections:write` 스코프로 토큰 발급 → `SLACK_APP_TOKEN`
   - **OAuth & Permissions → Install to Workspace** → 발급된 Bot Token → `SLACK_BOT_TOKEN`
   - 사용할 채널에 `/invite @restart-bot`

## 서버 배포 절차

```bash
mkdir -p /root/slack-restart-bot
# 리포지토리의 etc/slack-restart-bot/*.py, requirements.txt 를 서버로 복사

cd /root/slack-restart-bot
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt

# .env 파일 생성 (위 환경변수 표 참고), 권한 제한
chmod 600 /root/slack-restart-bot/.env

# systemd 등록
cp etc/slack-restart-bot/slack-restart-bot.service /etc/systemd/system/
systemctl daemon-reload
systemctl enable --now slack-restart-bot
```

## restart.sh (서버에 배치된 실제 스크립트)

`/root/restart.sh` (Slack 봇의 `RESTART_SCRIPT_PATH`가 가리키는 파일)

```bash
#!/bin/bash
set -e

REPO_DIR="/root/git/cdcdtest"
WAR_SRC="$REPO_DIR/goodpang/GoodPang.war"

cd "$REPO_DIR"
git pull

git show

/opt/tomcat/bin/shutdown.sh

for i in $(seq 1 30); do
    if ! pgrep -f "catalina.home=/opt/tomcat" > /dev/null; then
        break
    fi
    sleep 1
done

rm -rf /opt/tomcat/webapps/ROOT
cp "$WAR_SRC" /opt/tomcat/webapps/ROOT.war

sh /opt/tomcat/bin/startup.sh
```

동작 요약:
1. 배포용 저장소 `/root/git/cdcdtest`에서 `git pull`
2. 해당 저장소의 `goodpang/GoodPang.war`(미리 빌드되어 커밋된 war 파일)를 사용
3. Tomcat(`/opt/tomcat`) 종료 → 프로세스 종료 대기(최대 30초) → 기존 `ROOT` 배포 삭제 → 새 war를 `ROOT.war`로 복사 → Tomcat 시작

즉 현재는 **war 파일 자체가 `/root/git/cdcdtest` 저장소에 미리 빌드된 채로 커밋되어 있고**, `restart.sh`는 그 war를 pull 받아 배포/재시작만 담당한다. Tomcat 경로는 `/opt/tomcat`.

## Tomcat 커넥터 설정 (server.xml, git 미관리)

`/opt/tomcat/conf/server.xml`은 war 파일에 포함되지 않는 **Tomcat 컨테이너 설정**이라 git으로 관리되지
않는다. `/restart`(war 재배포)로는 초기화되지 않지만, Tomcat을 새로 설치하거나 서버를 옮기면 이 설정은
날아가므로 여기에 기록해둔다.

- **`maxPostSize`/`maxSwallowSize` 상향 (2026-08-31)**
  - 증상: 상품등록(`vendor-product-write.jsp` → `/vendor/product/write`)에서 옵션 이미지를 여러 장
    첨부하면 브라우저 콘솔에 `TypeError: Failed to fetch`가 뜨고 요청이 아예 실패함.
  - 원인: `VendorProductWriteServlet`의 `@MultipartConfig(maxFileSize=10MB, maxRequestSize=150MB)`는
    넉넉한데, Tomcat 커넥터 자체는 `maxPostSize`/`maxSwallowSize`를 따로 안 정해서 기본값 2MB로
    동작. 업로드 용량이 2MB를 넘으면 Tomcat이 정상 에러 응답을 못 보내고 연결을 끊어버려서, 브라우저는
    응답 자체를 못 받고 `Failed to fetch`로 표시됨.
  - 조치: `/opt/tomcat/conf/server.xml`의 8080 포트 `<Connector>`에 `maxPostSize`/`maxSwallowSize`를
    `@MultipartConfig`와 맞춰(150MB) 추가하고 Tomcat 재시작(`shutdown.sh` → `startup.sh`).

    ```xml
    <Connector connectionTimeout="20000" maxParameterCount="1000" port="8080" protocol="HTTP/1.1"
               redirectPort="8443" maxPostSize="157286400" maxSwallowSize="157286400"/>
    ```

## 신규 유저에게 실행 권한 부여

1. 대상 유저의 Slack 멤버 ID 확인 (프로필 → 더보기 → 멤버 ID 복사)
2. `/root/slack-restart-bot/.env`의 `ALLOWED_SLACK_USER_IDS`에 콤마로 추가
3. `systemctl restart slack-restart-bot`

## 트러블슈팅 기록

- **`Job for slack-restart-bot.service failed because of unavailable resources`**
  - 원인 1: `User=ubuntu`인데 서버에 `ubuntu` 계정이 없음 (`id ubuntu` → no such user). 실제 계정은 `root`.
  - 원인 2: `EnvironmentFile`로 지정한 `.env` 파일이 아직 생성되지 않음 (`Failed to load environment files: No such file or directory`).
  - 조치: `/etc/systemd/system/slack-restart-bot.service`를 `User=root`, `/root/slack-restart-bot/...` 경로로 전체 재작성, `.env` 파일 생성 후 `daemon-reload` + `restart`로 해결.

## 향후 작업 (TODO, 미완료)

war 빌드 자동화: 현재는 war 파일을 수동으로 빌드/복사한다고 가정하고 `restart.sh`가 이를 배포/재시작만 담당하는 것으로 추정 중.

- `project/GoodPang`은 이미 Maven 표준 레이아웃(`src/main/java`, `src/main/webapp`)을 따르고, 의존 jar(gson, lombok, jbcrypt, ojdbc8, jstl 등)가 모두 `WEB-INF/lib`에 커밋되어 있어 Maven 없이 `javac` + `jar` 명령만으로도 war 빌드가 가능함을 확인함.
- Lombok(24개 DTO 클래스에서 사용)도 클래스패스에 `lombok.jar`만 포함하면 최신 javac가 자동으로 어노테이션 프로세싱을 수행하므로 추가 설정 없이 동작할 것으로 예상.
- `restart.sh` 확인 결과, 현재는 war 파일이 `/root/git/cdcdtest/goodpang/GoodPang.war`로 **미리 빌드되어 커밋된 상태**로 pull되고 있고, `restart.sh`는 그 war를 Tomcat(`/opt/tomcat`, `ROOT.war`)에 배포/재시작만 함. 즉 소스 변경 후 war를 새로 빌드해서 `cdcdtest` 저장소에 커밋하는 과정이 여전히 수동임.
- 자동 빌드 스크립트 작성 전 확인 필요:
  - `/root/git/cdcdtest` 저장소 안에 `project/GoodPang`과 같은 Java 소스도 포함되어 있는지, 아니면 빌드된 war만 커밋되어 있는지
  - 소스가 없다면 어느 저장소(`sist_coupang_2026`?)의 소스를 기준으로 빌드해야 하는지, 빌드 후 war를 `cdcdtest` 저장소의 어느 경로에 놓아야 하는지
