#!/bin/bash
# Yesir! Information Agent 启动脚本

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

CLASSPATH="yesir.jar"
for f in lib/*.jar lib/*.zip; do
    [ -f "$f" ] && CLASSPATH="$CLASSPATH:$f"
done

java -classpath "$CLASSPATH" com.semanticwww.yesir.gui.YesirMain "$@"
