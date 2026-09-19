#!/bin/bash
set -e

SRC_REPO_URL="https://github.com/gmake20/sist_coupang_2026.git"
SRC_REPO_DIR="/root/git/sist_coupang_2026"
PROJECT_DIR="$SRC_REPO_DIR/project/GoodPang"
BUILD_DIR="$PROJECT_DIR/build"
STAGING_DIR="$BUILD_DIR/war-staging"
WAR_OUT="$BUILD_DIR/GoodPang.war"
TOMCAT_LIB="/opt/tomcat/lib"

if [ -d "$SRC_REPO_DIR/.git" ]; then
    cd "$SRC_REPO_DIR"
    git pull
else
    git clone "$SRC_REPO_URL" "$SRC_REPO_DIR"
    cd "$SRC_REPO_DIR"
fi

git show --stat

cd "$PROJECT_DIR"

rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/classes" "$STAGING_DIR"

find src/main/java -name "*.java" > "$BUILD_DIR/sources.txt"

javac -d "$BUILD_DIR/classes" \
    -cp "src/main/webapp/WEB-INF/lib/*:$TOMCAT_LIB/*" \
    @"$BUILD_DIR/sources.txt"

cp -r src/main/webapp/. "$STAGING_DIR/"
mkdir -p "$STAGING_DIR/WEB-INF/classes"
cp -r "$BUILD_DIR/classes/." "$STAGING_DIR/WEB-INF/classes/"

(cd "$STAGING_DIR" && jar cf "$WAR_OUT" -C . .)

/opt/tomcat/bin/shutdown.sh

for i in $(seq 1 30); do
    if ! pgrep -f "catalina.home=/opt/tomcat" > /dev/null; then
        break
    fi
    sleep 1
done

rm -rf /opt/tomcat/webapps/ROOT
cp "$WAR_OUT" /opt/tomcat/webapps/ROOT.war

sh /opt/tomcat/bin/startup.sh
